package com.jojo.jojozquizz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.jojo.jojozquizz.databinding.ActivityMainBinding;
import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.dialogs.NiuDialog;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.model.Question;
import com.jojo.jojozquizz.tools.BCrypt;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.CombineKeys;
import com.jojo.jojozquizz.tools.Global;
import com.jojo.jojozquizz.tools.PlayersDatabase;
import com.jojo.jojozquizz.tools.QuestionsDatabase;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NameDialog.NameDialogListener, ClickHandler {

	static final String TAG = "MainActivity";

	static final int GAME_ACTIVITY_REQUEST_CODE = 30;
	static final int USERS_ACTIVITY_REQUEST_CODE = 40;

	String API_URL;

	final Context mContext = this;

	EditText mNumberOfQuestionsInput;

	SharedPreferences mPreferences;

	boolean isFirstTime;
	Player mPlayer;

	RequestQueue mRequestQueue;
	Cache mCache;
	BasicNetwork mNetwork;

	MutableLiveData<Integer> LAST_ID;

	ActivityMainBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		mBinding.setHandler(this);

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);
		API_URL = getResources().getString(R.string.api_domain);

		mCache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
		mNetwork = new BasicNetwork(new HurlStack());
		mRequestQueue = new RequestQueue(mCache, mNetwork);
		mRequestQueue.start();

		mNumberOfQuestionsInput = mBinding.activityMainNumberQuestionsInput;

		isFirstTime = PlayersDatabase.getInstance(this).PlayersDAO().getAllPlayers().isEmpty();

		mBinding.activityMainStartButton.setOnLongClickListener((View v) -> {
			startActivity(new Intent(this, LinksActivity.class));
			return false;
		});

		if (isFirstTime) {
			String lang;
			switch (Locale.getDefault().getCountry()) {
				default:
					lang = "FR";
					break;
			}
			mPreferences.edit().putString("langage", lang).apply();
			askUsernameDialog();
		} else {
			mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 1));
			mBinding.setPlayer(mPlayer);
			mBinding.setPlayer(mPlayer);
		}

		checkForUpdates();

		LAST_ID = new MutableLiveData<>();
		if (((Global) this.getApplication()).getProcessedKey() == null) {
			String serverKeyRoute = getResources().getString(R.string.api_endpoint_getServerKey);

			JsonObjectRequest serverKeyRequest = new JsonObjectRequest(Request.Method.GET, API_URL + serverKeyRoute, null,
				response -> {
					try {
						String serverKey = response.getString("key");
						String combinedKey = CombineKeys.combineKeys(getResources().getString(R.string.application_key), serverKey);
						((Global) mContext.getApplicationContext()).setProcessedKey(combinedKey);
						getLastIdFromServer();
					} catch (JSONException ignore) {
					}
				}, error -> Log.d(TAG, "onErrorResponse: " + error.getMessage()));
			mRequestQueue.add(serverKeyRequest);
		} else {
			getLastIdFromServer();
		}
		LAST_ID.observe(this, integer -> {
			if (QuestionsDatabase.getInstance(mContext).QuestionDAO().getAllQuestions().isEmpty() || integer > QuestionsDatabase.getInstance(mContext).QuestionDAO().getLastQuestion().getId()) {
				addQuestions(integer);
			}
		});
	}

	private void getLastIdFromServer() {
		String lastIdRoute = getResources().getString(R.string.api_endpoint_getLastId);
		String lang = mPreferences.getString("langage", "EN");
		Log.d(TAG, "getLastIdFromServer: ");

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL + lastIdRoute + lang, null,
			response -> {
				try {
					LAST_ID.setValue(response.getInt("questionId"));
					Log.d(TAG, "onResponse: " + response.getInt("questionId"));
				} catch (JSONException ignore) {
				}
			}, error -> Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.impossible_to_load_questions), Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getLastIdFromServer();
			}
		}).show()) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = new HashMap<String, String>();
				String key = ((Global) mContext.getApplicationContext()).getAuthKey();
				String salt = BCrypt.gensalt();
				headers.put("app-auth", BCrypt.hashpw(key, salt));
				return headers;
			}
		};
		mRequestQueue.add(jsonObjectRequest);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 1));
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GAME_ACTIVITY_REQUEST_CODE) {
			mBinding.setPlayer(mPlayer);
		} else if (requestCode == USERS_ACTIVITY_REQUEST_CODE) {
			mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 1));
			mBinding.setPlayer(mPlayer);
		}
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

			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fullRoute, null, response -> {
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
			}, error -> {
				//TODO: Translate
				Snackbar.make(findViewById(R.id.drawer_layout), "Impossible de récupérer les questions du serveur, réessayez plus tard", Snackbar.LENGTH_LONG).show();
			}) {
				@Override
				public Map<String, String> getHeaders() {
					HashMap<String, String> headers = new HashMap<String, String>();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_niu) {
			new NiuDialog().showDialog(this);
		} else if (itemId == R.id.menu_links) {
			startActivity(new Intent(mContext, LinksActivity.class));
		} else if (itemId == R.id.menu_settings) {
			startActivity(new Intent(mContext, SettingsActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	private void askUsernameDialog() {
		NameDialog nameDialog = new NameDialog();
		nameDialog.setIsNewUser(true);
		nameDialog.setIsCancelable(false);
		nameDialog.show(getSupportFragmentManager(), "name dialog");
	}

	private void checkForUpdates() {
		int currentCode = BuildConfig.VERSION_CODE;

		String url = getResources().getString(R.string.api_endpoint_getCurrentVersion);

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL + url, null, response -> {
			try {
				if (response.getInt("version") > currentCode) {
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

					builder.setTitle(R.string.update_available);
					builder.setMessage(R.string.update_available_text);
					builder.setPositiveButton(R.string.update, (dialog, which) -> {
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
						} catch (android.content.ActivityNotFoundException activityNotFoundException) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
						}
					});
					builder.setNegativeButton(R.string.later, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			} catch (JSONException ignored) {
			}
		}, null);
		mRequestQueue.add(jsonObjectRequest);
	}

	@Override
	public void applyText(String name) {
		Pattern pattern = Pattern.compile(NameDialog.REGEX);
		Matcher matcher = pattern.matcher(name);
		if (!matcher.find()) {
			askUsernameDialog();
		} else {
			Player player = new Player(name, this);
			PlayersDatabase.getInstance(this).PlayersDAO().addPlayer(player);
			mPreferences.edit().putInt("currentUserId", PlayersDatabase.getInstance(this).PlayersDAO().getIdFromName(name)).apply();
			mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 1));

			isFirstTime = false;
			mBinding.setPlayer(player);
		}
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.activity_main_start_button) {
			if (!mNumberOfQuestionsInput.getText().toString().isEmpty()) {
				int mNumberOfQuestionsAsk = Integer.parseInt(mNumberOfQuestionsInput.getText().toString());
				if (mNumberOfQuestionsAsk <= 0) {
					Toast.makeText(mContext, R.string.error_start0, Toast.LENGTH_LONG).show();
				} else if (mNumberOfQuestionsAsk > 75) {
					Toast.makeText(mContext, R.string.error_start1, Toast.LENGTH_LONG).show();
				} else if (QuestionsDatabase.getInstance(this).QuestionDAO().getLastQuestion() == null) {
					Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.no_questions), Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), v1 -> getLastIdFromServer()).show();
				} else {
					startActivityForResult(new Intent(mContext, GameActivity.class).putExtra("userId", mPlayer.getId()).putExtra("numberOfQuestions", mNumberOfQuestionsAsk), GAME_ACTIVITY_REQUEST_CODE);
				}
			}
		} else if (id == R.id.button_users) {
			startActivityForResult(new Intent(mContext, PlayersActivity.class), USERS_ACTIVITY_REQUEST_CODE);
		} else if (id == R.id.activity_main_select_categories_button) {
			startActivity(new Intent(mContext, SelectCategoriesActivity.class));
		} else if (id == R.id.activity_main_bonus_button) {
			startActivity(new Intent(mContext, BonusActivity.class));
		}
	}
}