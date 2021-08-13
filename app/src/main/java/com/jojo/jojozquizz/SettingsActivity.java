package com.jojo.jojozquizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.jojo.jojozquizz.model.Question;
import com.jojo.jojozquizz.tools.BCrypt;
import com.jojo.jojozquizz.tools.CombineKeys;
import com.jojo.jojozquizz.tools.Global;
import com.jojo.jojozquizz.tools.QuestionsDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

	private static final String TAG = "SettingsActivity";

	private Context mContext;
	private SharedPreferences mPreferences;

	private Button mReloadButton, mPrivacyButton, mTermsButton;
	private RadioButton mFrButton, mEnButton;

	private String API_URL;

	private RequestQueue mRequestQueue;
	private Cache mCache;
	private BasicNetwork mNetwork;

	private MutableLiveData<Integer> LAST_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		mContext = this;

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);
		API_URL = getResources().getString(R.string.api_domain);

		mCache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
		mNetwork = new BasicNetwork(new HurlStack());
		mRequestQueue = new RequestQueue(mCache, mNetwork);
		mRequestQueue.start();

		mReloadButton = findViewById(R.id.settings_reload_questions);
		mFrButton = findViewById(R.id.settings_radio_fr);
		mEnButton = findViewById(R.id.settings_radio_en);
		mPrivacyButton = findViewById(R.id.privacy_policy);
		mTermsButton = findViewById(R.id.terms_of_service);

		mReloadButton.setTag(1);
		mPrivacyButton.setTag(2);
		mTermsButton.setTag(3);
		mReloadButton.setOnClickListener(this);
		mPrivacyButton.setOnClickListener(this);
		mTermsButton.setOnClickListener(this);

		String langage = mPreferences.getString("langage", "EN");
		switch (langage) {
			case "FR":
				mFrButton.setChecked(true);
				break;
			default:
				mEnButton.setChecked(true);
				break;
		}

		LAST_ID = new MutableLiveData<>();
		if (((Global) this.getApplication()).getProcessedKey() == null) {
			Log.d(TAG, "onCreate: ouais ouais c'est bien nul");
			String serverKeyRoute = getResources().getString(R.string.api_endpoint_getServerKey);

			JsonObjectRequest serverKeyRequest = new JsonObjectRequest(Request.Method.GET, API_URL + serverKeyRoute, null,
				response -> {
					try {
						String serverKey = response.getString("key");
						String combinedKey = CombineKeys.combineKeys(getResources().getString(R.string.application_key), serverKey);
						((Global) mContext.getApplicationContext()).setProcessedKey(combinedKey);
						Log.d(TAG, "onResponse: " + serverKey);
						getLastIdFromServer();
					} catch (JSONException ignore) {
					}
				}, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));
			mRequestQueue.add(serverKeyRequest);
		} else {
			getLastIdFromServer();
		}
		LAST_ID.observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				if (QuestionsDatabase.getInstance(mContext).QuestionDAO().getAllQuestions().isEmpty() || integer > QuestionsDatabase.getInstance(mContext).QuestionDAO().getLastQuestion().getId()) {
					addQuestions(integer);
				}
			}
		});

	}

	@Override
	protected void onStop() {
		String selectedLang = mPreferences.getString("langage", "EN");
		if (mFrButton.isChecked()) {
			if (selectedLang.equals("EN")) {
				QuestionsDatabase.getInstance(mContext).QuestionDAO().deleteTable();
			}
			mPreferences.edit().putString("langage", "FR").apply();
		} else if (mEnButton.isChecked()) {
			if (selectedLang.equals("FR"))
				QuestionsDatabase.getInstance(mContext).QuestionDAO().deleteTable();
			mPreferences.edit().putString("langage", "EN").apply();
		}

		super.onStop();
	}

	private void getLastIdFromServer() {
		String lastIdRoute = getResources().getString(R.string.api_endpoint_getLastId);
		String lang = mPreferences.getString("langage", "EN");

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL + lastIdRoute + lang, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					LAST_ID.setValue(response.getInt("questionId"));
				} catch (JSONException ignore) {
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Snackbar.make(findViewById(R.id.settings_constraint_layout), getString(R.string.impossible_to_load_questions), Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getLastIdFromServer();
					}
				}).show();
			}
		});
		mRequestQueue.add(jsonObjectRequest);
	}

	public void addQuestions(int lastId) {

		long lastIdInDatabase;
		if (QuestionsDatabase.getInstance(this).QuestionDAO().getAllQuestions().isEmpty()) {
			lastIdInDatabase = 0;
		} else {
			lastIdInDatabase = QuestionsDatabase.getInstance(this).QuestionDAO().getLastQuestion().getId() + 1;
		}

		String apiRoute = getResources().getString(R.string.api_endpoint_getQuestion);
		String lang = mPreferences.getString("langage", "EN");

		for (long i = lastIdInDatabase; i < lastId + 1; i++) {
			String fullRoute = API_URL + apiRoute + lang + "/" + i;

			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullRoute, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					try {
						int id = response.getInt("questionId");
						String q = response.getString("question");
						String choices = response.getString("choices");
						int category = response.getInt("category");
						int difficulty = response.getInt("difficulty");
						Question question = new Question(id, q, choices, category, difficulty);
						QuestionsDatabase.getInstance(mContext).QuestionDAO().addQuestion(question);
					} catch (JSONException ignored) {
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					//TODO: Translate
					Snackbar.make(findViewById(R.id.drawer_layout), "Impossible de récupérer les questions du serveur, réessayez plus tard", Snackbar.LENGTH_LONG).show();
				}
			}) {
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					HashMap<String, String> headers = new HashMap<>();
					String key = ((Global) mContext.getApplicationContext()).getAuthKey();
					String salt = BCrypt.gensalt();
					headers.put("app-auth", BCrypt.hashpw(key, salt));
					return headers;
				}
			};
			mRequestQueue.add(jsonObjectRequest);
		}
	}

	@Override
	public void onClick(View v) {
		int buttonTag = (int) v.getTag();

		if (buttonTag == 1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.settings_rewrite_database))
				.setMessage(getResources().getString(R.string.settings_rewrite_database_confirmation))
				.setNegativeButton(getResources().getString(R.string.all_cancel), null)
				.setPositiveButton(getString(R.string.rewrite), (dialog, which) -> {
					QuestionsDatabase.getInstance(mContext).QuestionDAO().deleteTable();
					getLastIdFromServer();
				});
			builder.show();

		} else if (buttonTag == 2) {
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nextfor.studio/html/jojozquizz/privacy_policy/" + mPreferences.getString("langage", "EN")));
			startActivity(launchBrowser);
		} else if (buttonTag == 3) {
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nextfor.studio/html/jojozquizz/terms_of_service/" + mPreferences.getString("langage", "EN")));
			startActivity(launchBrowser);
		}
	}
}