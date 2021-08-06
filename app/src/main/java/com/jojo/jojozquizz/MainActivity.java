package com.jojo.jojozquizz;

import android.app.AlertDialog;
import android.content.Context;
<<<<<<< HEAD
=======
import android.content.DialogInterface;
>>>>>>> ae3c503 (flblblblbl)
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
<<<<<<< HEAD
import android.util.Log;
=======
>>>>>>> ae3c503 (flblblblbl)
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
<<<<<<< HEAD
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.jojo.jojozquizz.databinding.ContentHomeBinding;
=======
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
>>>>>>> ae3c503 (flblblblbl)
import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.dialogs.NiuDialog;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.model.Question;
<<<<<<< HEAD
import com.jojo.jojozquizz.tools.BCrypt;
import com.jojo.jojozquizz.tools.CombineKeys;
import com.jojo.jojozquizz.tools.Global;
=======
>>>>>>> ae3c503 (flblblblbl)
import com.jojo.jojozquizz.tools.PlayersDatabase;
import com.jojo.jojozquizz.tools.QuestionsDatabase;

import org.json.JSONException;
<<<<<<< HEAD

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
=======
import org.json.JSONObject;

import java.util.Locale;
>>>>>>> ae3c503 (flblblblbl)
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NameDialog.NameDialogListener {

	private static final String TAG = "MainActivity";

	private static final int GAME_ACTIVITY_REQUEST_CODE = 30;
	private static final int USERS_ACTIVITY_REQUEST_CODE = 40;

	private static final int START_BUTTON_TAG = 0;
	private static final int USERS_BUTTON_TAG = 1;
	private static final int SELECT_CATEGORIES_BUTTON_TAG = 2;
	private static final int BONUS_BUTTON_TAG = 3;

<<<<<<< HEAD
	private String API_URL;

	private final Context mContext = this;
=======
	private final String API_URL = "https://nextfor.studio/";

	private final Context context = this;
>>>>>>> ae3c503 (flblblblbl)

	private TextView mGreetingText, mNameText;
	private EditText mNumberOfQuestionsInput;
	private Button mStartButton;
	private ImageButton mUsersButton;

	private SharedPreferences mPreferences;

	private boolean isFirstTime;
	private Player mPlayer;

<<<<<<< HEAD
	private RequestQueue mRequestQueue;
	private Cache mCache;
	private BasicNetwork mNetwork;

	private MutableLiveData<Integer> LAST_ID;

	private ContentHomeBinding mHomeBinding;

=======
	private MutableLiveData<Integer> LAST_ID;

>>>>>>> ae3c503 (flblblblbl)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

<<<<<<< HEAD


=======
>>>>>>> ae3c503 (flblblblbl)
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);
<<<<<<< HEAD
		API_URL = getResources().getString(R.string.api_domain);

		mCache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
		mNetwork = new BasicNetwork(new HurlStack());
		mRequestQueue = new RequestQueue(mCache, mNetwork);
		mRequestQueue.start();

=======
>>>>>>> ae3c503 (flblblblbl)

		mGreetingText = findViewById(R.id.activity_main_greeting_text);
		mNameText = findViewById(R.id.text_display_name);
		mUsersButton = findViewById(R.id.button_users);
		mNumberOfQuestionsInput = findViewById(R.id.activity_main_number_questions_input);
		mStartButton = findViewById(R.id.activity_main_start_button);
		Button mSelectCategoriesButton = findViewById(R.id.activity_main_select_categories_button);
		Button mBonusButton = findViewById(R.id.activity_main_bonus_button);

		mStartButton.setTag(START_BUTTON_TAG);
		mUsersButton.setTag(USERS_BUTTON_TAG);
		mSelectCategoriesButton.setTag(SELECT_CATEGORIES_BUTTON_TAG);
		mBonusButton.setTag(BONUS_BUTTON_TAG);

		mUsersButton.setOnClickListener(this);
		mStartButton.setOnClickListener(this);
		mSelectCategoriesButton.setOnClickListener(this);
		mBonusButton.setOnClickListener(this);

		isFirstTime = PlayersDatabase.getInstance(this).PlayersDAO().getAllPlayers().isEmpty();

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
			updateUI(mPlayer);
		}

		checkForUpdates();

		LAST_ID = new MutableLiveData<>();
<<<<<<< HEAD
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
		LAST_ID.observe(this, integer -> {
			if (QuestionsDatabase.getInstance(mContext).QuestionDAO().getAllQuestions().isEmpty() || integer > QuestionsDatabase.getInstance(mContext).QuestionDAO().getLastQuestion().getId()) {
				addQuestions(integer);
=======
		getLastIdFromServer();
		LAST_ID.observe((LifecycleOwner) this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				if (QuestionsDatabase.getInstance(context).QuestionDAO().getAllQuestions().isEmpty() || integer > QuestionsDatabase.getInstance(context).QuestionDAO().getLastQuestion().getId()) {
					addQuestions(integer);
				}
>>>>>>> ae3c503 (flblblblbl)
			}
		});
	}

	private void getLastIdFromServer() {
<<<<<<< HEAD
		String lastIdRoute = getResources().getString(R.string.api_endpoint_getLastId);
		String lang = mPreferences.getString("langage", "EN");
		Log.d(TAG, "getLastIdFromServer: ");

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL + lastIdRoute + lang, null,
			response -> {
				;
				try {
					LAST_ID.setValue(response.getInt("questionId"));
					Log.d(TAG, "onResponse: " + response.getInt("questionId"));
				} catch (JSONException ignore) {
				}
			}, error -> Snackbar.make(findViewById(R.id.constraint_layout_home), getString(R.string.impossible_to_load_questions), Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), new View.OnClickListener() {
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
=======
		String lastIdRoute = "questions/getLastId/";
		String lang = mPreferences.getString("langage", "EN");

		RequestQueue requestQueue = Volley.newRequestQueue(this);
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
				Snackbar.make(findViewById(R.id.constraint_layout_home), getString(R.string.impossible_to_load_questions), Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getLastIdFromServer();
					}
				}).show();
			}
		});
		requestQueue.add(jsonObjectRequest);
>>>>>>> ae3c503 (flblblblbl)
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 1));
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GAME_ACTIVITY_REQUEST_CODE) {
			updateUI(mPlayer);
		} else if (requestCode == USERS_ACTIVITY_REQUEST_CODE) {
			mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 1));
			updateUI(mPlayer);
		}
	}

	@Override
	public void onClick(View v) {
		int buttonTag = (int) v.getTag();
<<<<<<< HEAD

		if (buttonTag == START_BUTTON_TAG && !mNumberOfQuestionsInput.getText().toString().isEmpty()) {
			int mNumberOfQuestionsAsk = Integer.parseInt(mNumberOfQuestionsInput.getText().toString());
			if (mNumberOfQuestionsAsk <= 0) {
				Toast.makeText(mContext, R.string.error_start0, Toast.LENGTH_LONG).show();
			} else if (mNumberOfQuestionsAsk > 75) {
				Toast.makeText(mContext, R.string.error_start1, Toast.LENGTH_LONG).show();
			} else if (QuestionsDatabase.getInstance(this).QuestionDAO().getLastQuestion() == null) {
				Snackbar.make(findViewById(R.id.constraint_layout_home), getString(R.string.no_questions), Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), v1 -> getLastIdFromServer()).show();
			} else {
				startActivityForResult(new Intent(mContext, GameActivity.class).putExtra("userId", mPlayer.getId()).putExtra("numberOfQuestions", mNumberOfQuestionsAsk), GAME_ACTIVITY_REQUEST_CODE);
			}
		} else if (buttonTag == USERS_BUTTON_TAG) {
			startActivityForResult(new Intent(mContext, PlayersActivity.class), USERS_ACTIVITY_REQUEST_CODE);
		} else if (buttonTag == SELECT_CATEGORIES_BUTTON_TAG) {
			startActivity(new Intent(mContext, SelectCategoriesActivity.class));
		} else if (buttonTag == BONUS_BUTTON_TAG) {
			startActivity(new Intent(mContext, BonusActivity.class));
=======
		if (buttonTag == START_BUTTON_TAG) {
			int mNumberOfQuestionsAsk = Integer.parseInt(mNumberOfQuestionsInput.getText().toString());
			if (mNumberOfQuestionsAsk <= 0) {
				Toast.makeText(context, R.string.error_start0, Toast.LENGTH_LONG).show();
			} else if (mNumberOfQuestionsAsk > 75) {
				Toast.makeText(context, R.string.error_start1, Toast.LENGTH_LONG).show();
			} else if (QuestionsDatabase.getInstance(this).QuestionDAO().getLastQuestion() == null) {
				Snackbar.make(findViewById(R.id.constraint_layout_home), getString(R.string.no_questions), Snackbar.LENGTH_LONG).setAction(getString(R.string.all_retry), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getLastIdFromServer();
					}
				}).show();
			} else {
				startActivityForResult(new Intent(context, GameActivity.class).putExtra("userId", mPlayer.getId()).putExtra("numberOfQuestions", mNumberOfQuestionsAsk), GAME_ACTIVITY_REQUEST_CODE);
			}
		} else if (buttonTag == USERS_BUTTON_TAG) {
			startActivityForResult(new Intent(context, PlayersActivity.class), USERS_ACTIVITY_REQUEST_CODE);
		} else if (buttonTag == SELECT_CATEGORIES_BUTTON_TAG) {
			startActivity(new Intent(context, SelectCategoriesActivity.class));
		} else if (buttonTag == BONUS_BUTTON_TAG) {
			startActivity(new Intent(context, BonusActivity.class));
>>>>>>> ae3c503 (flblblblbl)
		}
	}

	public void addQuestions(int lastId) {
<<<<<<< HEAD
=======

>>>>>>> ae3c503 (flblblblbl)
		long lastIdInDatabase;
		if (QuestionsDatabase.getInstance(this).QuestionDAO().getAllQuestions().isEmpty()) {
			lastIdInDatabase = 0;
		} else {
			lastIdInDatabase = QuestionsDatabase.getInstance(this).QuestionDAO().getLastQuestion().getId() + 1;
		}

<<<<<<< HEAD
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
				Snackbar.make(findViewById(R.id.constraint_layout_home), "Impossible de récupérer les questions du serveur, réessayez plus tard", Snackbar.LENGTH_LONG).show();
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
=======
		String apiRoute = "questions/getQuestion/";
		String lang = mPreferences.getString("langage", "EN");

		RequestQueue requestQueue = Volley.newRequestQueue(this);
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
						QuestionsDatabase.getInstance(context).QuestionDAO().addQuestion(question);
					} catch (JSONException ignored) {
					}
				}
			}, null);
			requestQueue.add(jsonObjectRequest);
>>>>>>> ae3c503 (flblblblbl)
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
		switch (item.getItemId()) {
			case R.id.menu_niu:
				new NiuDialog().showDialog(this);
				break;
			case R.id.menu_links:
<<<<<<< HEAD
				startActivity(new Intent(mContext, LinksActivity.class));
				break;
			case R.id.menu_settings:
				startActivity(new Intent(mContext, SettingsActivity.class));
=======
				startActivity(new Intent(context, LinksActivity.class));
				break;
			case R.id.menu_settings:
				startActivity(new Intent(context, SettingsActivity.class));
>>>>>>> ae3c503 (flblblblbl)
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

<<<<<<< HEAD
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
=======
		String url = "https://nextfor.studio/version/getLastVersion";

		RequestQueue requestQueue = Volley.newRequestQueue(this);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.getInt("version") > currentCode) {
						AlertDialog.Builder builder = new AlertDialog.Builder(context);

						builder.setTitle(R.string.update_available);
						builder.setMessage(R.string.update_available_text);
						builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								final String appPackageName = getPackageName();
								try {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
								} catch (android.content.ActivityNotFoundException activityNotFoundException) {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
								}
							}
						});
						builder.setNegativeButton(R.string.later, null);
						AlertDialog dialog = builder.create();
						dialog.show();
					}
				} catch (JSONException ignored) {
				}
			}
		}, null);
		requestQueue.add(jsonObjectRequest);
>>>>>>> ae3c503 (flblblblbl)
	}

	private void updateUI(Player player) {
		mNameText.setText(player.getName());

		String[] lastGame = player.getLastGame().split("-/-");
		int lastGameValidatedQuestions = Integer.parseInt(lastGame[0]);
		int lastGameTotalQuestions = Integer.parseInt(lastGame[1]);

		String textToShow;

		if (lastGameTotalQuestions > 0) {
			textToShow = getResources().getQuantityString(R.plurals.string_welcome_again, lastGameValidatedQuestions, player.getName(), lastGameValidatedQuestions, lastGameTotalQuestions);
			mNumberOfQuestionsInput.setText(String.valueOf(lastGameTotalQuestions));
		} else {
			textToShow = getString(R.string.string_welcome);
			mNumberOfQuestionsInput.setText(String.valueOf(20));
		}
		mGreetingText.setText(textToShow);

		mStartButton.setEnabled(true);
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
			updateUI(player);
		}
	}
}