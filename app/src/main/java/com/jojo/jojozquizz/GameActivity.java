package com.jojo.jojozquizz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.jojo.jojozquizz.databinding.ActivityGameBinding;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.model.Question;
import com.jojo.jojozquizz.model.QuestionBank;
import com.jojo.jojozquizz.objects.Bonus;
import com.jojo.jojozquizz.tools.CategoriesHelper;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.PlayersDAO;
import com.jojo.jojozquizz.tools.PlayersDatabase;
import com.jojo.jojozquizz.tools.QuestionsDatabase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity implements ClickHandler {

	private static final String TAG = "GameActivity";

	private QuestionBank mQuestionBank;
	private Question mCurrentQuestion;
	private TextView mQuestionTextView, mNumberOfQuestionsAnsweredText, mNumberOfBonus1Left, mNumberOfBonus2Left, mNumberOfBonus3Left;
	private Button mAnswerButton1, mAnswerButton2, mAnswerButton3, mAnswerButton4;
	private Button[] mAllAnswerButton;
	private ProgressBar mProgressBar;
	private ImageButton mUseBonus1, mUseBonus2, mUseBonus3;
	private int mNumberOfQuestions, trueIndex;
	private boolean mEnableTouchEvents;
	private Bonus mBonus1, mBonus2, mBonus3;
	private List<String> mCategoriesSelectedProcessed, mDifficultiesSelectedProcessed;

	private int mTotalQuestions; // Number of questions to reach
	private int mQuestionsAnswered = 0; // Number of questions answered by the player
	private int mValidatedQuestions = 0; // Number of questions answered well by the player

	private int mScore = 0;
	private int mCombo = 0;
	private int mLives;

	private InterstitialAd mInterstitialAd;

	private CategoriesHelper mCategoriesHelper;

	private Player mPlayer;
	private List<String> mUserBonus;

	SharedPreferences mPreferences;

	ActivityGameBinding mBinding;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return mEnableTouchEvents && super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_game);
		mBinding.setHandler(this);

		Intent intent = getIntent();
		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(intent.getIntExtra("userId", 1));

		mNumberOfQuestions = intent.getIntExtra("numberOfQuestions", 20);
		mTotalQuestions = mNumberOfQuestions;

		AdRequest adRequest = new AdRequest.Builder().build();
		InterstitialAd.load(this, "ca-app-pub-5050054249389989/7659082720", adRequest, new InterstitialAdLoadCallback() {
			@Override
			public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
				super.onAdLoaded(interstitialAd);
				mInterstitialAd = interstitialAd;
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				super.onAdFailedToLoad(loadAdError);
				mInterstitialAd = null;
			}
		});

		mEnableTouchEvents = false;

		mCategoriesHelper = new CategoriesHelper(this);

		List<String> categories = Arrays.asList(mCategoriesHelper.getCategories()); // All cat from helper
		List<String> difficulties = Arrays.asList(mCategoriesHelper.getDifficulties()); // All diff from helper

		String categoriesSelected = mPlayer.getCategoriesSelected(); // Categories selected by the player, non processed (100110)
		String difficultiesSelected = mPlayer.getDifficultiesSelected(); // Difficulties selected by the player, non processed

		mCategoriesSelectedProcessed = mCategoriesHelper.getProcessedCategories(categoriesSelected); // Categories selected processed, so tranformed to String list
		List<Integer> categoriesIndex = new ArrayList<>(); // List of categories selected into string
		for (String c : mCategoriesSelectedProcessed) {
			categoriesIndex.add(categories.indexOf(c));
		}

		mDifficultiesSelectedProcessed = mCategoriesHelper.getProcessedDifficulties(difficultiesSelected);
		List<Integer> difficultiesIndex = new ArrayList<>();
		for (String d : mDifficultiesSelectedProcessed) {
			difficultiesIndex.add(difficulties.indexOf(d));
		}

		mQuestionBank = new QuestionBank();
		QuestionsDatabase questionsDatabase = QuestionsDatabase.getInstance(this);
		for (int c : categoriesIndex) {
			if (difficultiesIndex.contains(0)) {
				mQuestionBank.addQuestionsList(questionsDatabase.QuestionDAO().getQuestionsWithCatsAndDiffs(c, 0));
			}
			if (difficultiesIndex.contains(1)) {
				mQuestionBank.addQuestionsList(questionsDatabase.QuestionDAO().getQuestionsWithCatsAndDiffs(c, 1));
			}
			if (difficultiesIndex.contains(2)) {
				mQuestionBank.addQuestionsList(questionsDatabase.QuestionDAO().getQuestionsWithCatsAndDiffs(c, 2));
			}
		}

		mQuestionBank.reShuffle();

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);

		int totalQuestionsValue = mTotalQuestions;

		while (totalQuestionsValue % 10 != 0) {
			--totalQuestionsValue;
		}

		if (totalQuestionsValue <= 10) {
			mLives = 3;
		} else {
			mLives = (3 + totalQuestionsValue / 10) - 1;
		}

		mUserBonus = Arrays.asList(mPlayer.getBonus().split("-/-"));
		mBonus1 = new Bonus(Integer.parseInt(mUserBonus.get(0)), getString(R.string.bonus_skip));
		mBonus2 = new Bonus(Integer.parseInt(mUserBonus.get(1)), getString(R.string.bonus_2));
		mBonus3 = new Bonus(Integer.parseInt(mUserBonus.get(2)), getString(R.string.bonus_easier));

		// Initializing GUI
		mQuestionTextView = mBinding.activityGameQuestionText;
		mAnswerButton1 = mBinding.activityGameAnswer1Btn;
		mAnswerButton2 = mBinding.activityGameAnswer2Btn;
		mAnswerButton3 = mBinding.activityGameAnswer3Btn;
		mAnswerButton4 = mBinding.activityGameAnswer4Btn;
		mUseBonus1 = findViewById(R.id.button_use_bonus_1);
		mUseBonus2 = findViewById(R.id.button_use_bonus_2);
		mUseBonus3 = findViewById(R.id.button_use_bonus_3);
		mNumberOfQuestionsAnsweredText = findViewById(R.id.number_of_questions_answered);
		mNumberOfBonus1Left = mBinding.numberOfBonus1Left;
		mNumberOfBonus2Left = mBinding.numberOfBonus2Left;
		mNumberOfBonus3Left = mBinding.numberOfBonus3Left;
		mProgressBar = mBinding.progressBar;

		mNumberOfQuestionsAnsweredText.setText(String.format("1/%s", mTotalQuestions));

		mAllAnswerButton = new Button[]{mAnswerButton1, mAnswerButton2, mAnswerButton3, mAnswerButton4};

		mProgressBar.setMax(mTotalQuestions);
		mNumberOfBonus1Left.setText(String.valueOf(mBonus1.getNumber()));
		mNumberOfBonus2Left.setText(String.valueOf(mBonus2.getNumber()));
		mNumberOfBonus3Left.setText(String.valueOf(mBonus3.getNumber()));

		setStyleDefault();

		mCurrentQuestion = mQuestionBank.getNextQuestion();
		mCurrentQuestion.setChoiceList(Arrays.asList(mCurrentQuestion.getChoices().split("-/-")));
		this.displayQuestion();

		mEnableTouchEvents = true;
	}

	private void calculateScore(boolean isActualQuestionValidated) {
		int scoreToAdd = 0;

		switch (mCurrentQuestion.getDifficulty()) {
			case 0:
				scoreToAdd += isActualQuestionValidated ? +1 : -1;
				break;
			case 1:
				scoreToAdd += isActualQuestionValidated ? +2 : -1;
				break;
			case 2:
				scoreToAdd += isActualQuestionValidated ? +3 : -1;
		}

		if (mCombo == 0 && isActualQuestionValidated) {
			mCombo = 1;
		} else if (mCombo > 0 && isActualQuestionValidated) {
			mCombo++;
		} else if (mCombo < 0 && isActualQuestionValidated) {
			mCombo = 0;
		} else if (mCombo == 0 && !isActualQuestionValidated) {
			mCombo = -1;
		} else if (mCombo > 0 && !isActualQuestionValidated) {
			mCombo = 0;
		} else if (mCombo < 0 && !isActualQuestionValidated) {
			mCombo += mQuestionsAnswered % 2 == 0 ? -1 : 0;
		}

		scoreToAdd = Math.max(scoreToAdd, 0);

		scoreToAdd += mCombo;

		mScore += scoreToAdd;

	}

	private void displayQuestion() {
		Question questionToShow = new Question();
		questionToShow.setQuestion(mCurrentQuestion.getQuestion());
		questionToShow.setStringCategory(mCategoriesHelper.getCategories()[mCurrentQuestion.getCategory()]);
		questionToShow.setStringDifficulty(mCategoriesHelper.getDifficulties()[mCurrentQuestion.getDifficulty()]);

		String valueTrue = mCurrentQuestion.getChoiceList().get(0);
		List<String> mNewChoiceList = new ArrayList<>(mCurrentQuestion.getChoiceList());

		Collections.shuffle(mNewChoiceList);

		int i = 0;
		for (String proposition : mNewChoiceList) {
			if (proposition.equals(valueTrue)) {
				trueIndex = i;
				break;
			}
			i++;
		}

		int index = 0;
		for (Button button : mAllAnswerButton) {
			button.setText(mNewChoiceList.get(index));
			index++;
		}

		questionToShow.setChoiceList(mNewChoiceList);
		mBinding.setQuestion(questionToShow);
		mCurrentQuestion.setAnswerIndex(trueIndex);
	}

	/**
	 * bonusSkipQuestion() correspond au bonus 1, il saute la question actuelle
	 */
	private void bonusSkipQuestion() {
		if (!isBonusUsable(mBonus1)) {
			toastNoBonusLeft();
		} else {
			mBonus1.setNumber(mBonus1.getNumber() - 1);
			mNumberOfBonus1Left.setText(String.valueOf(mBonus1.getNumber()));
			mCurrentQuestion = mQuestionBank.getNextQuestion();
			mCurrentQuestion.setChoiceList(Arrays.asList(mCurrentQuestion.getChoices().split("-/-")));
			displayQuestion();
		}
	}

	/**
	 * bonusClearAnswers() correspond au bonus numéro 2, il cache le bouton d'une des réponses
	 * aléatoirement, sauf celui où il y a la bonne réponse bien sûr ;)
	 */
	private void bonusClearAnswers() {
		if (!isBonusUsable(mBonus2)) {
			toastNoBonusLeft();
		} else {
			mBonus2.setNumber(mBonus2.getNumber() - 1);
			mNumberOfBonus2Left.setText(String.valueOf(mBonus2.getNumber()));
			DecimalFormat df = new DecimalFormat("#");
			df.setRoundingMode(RoundingMode.DOWN);
			String randomString = df.format(Math.random() * 4.0);
			int randomValue = Integer.parseInt(randomString);
			while (randomValue == trueIndex) {
				randomString = df.format(Math.random() * 4.0);
				randomValue = Integer.parseInt(randomString);
			}
			switch (randomValue) {
				case 0:
					mAnswerButton1.setVisibility(View.GONE);
					break;
				case 1:
					mAnswerButton2.setVisibility(View.GONE);
					break;
				case 2:
					mAnswerButton3.setVisibility(View.GONE);
					break;
				case 3:
					mAnswerButton4.setVisibility(View.GONE);
					break;
			}
		}
	}

	private boolean bonusEasier() {
		boolean used = false;
		if (!isBonusUsable(mBonus3))
			toastNoBonusLeft();
		else {
			int mCurrentCategory = mCurrentQuestion.getCategory();
			if (mCurrentQuestion.getDifficulty() == 2) {
				while (mCurrentQuestion.getDifficulty() != 1 || mCurrentQuestion.getCategory() != mCurrentCategory) {
					mCurrentQuestion = mQuestionBank.getNextQuestion();
					mCurrentQuestion.setChoiceList(Arrays.asList(mCurrentQuestion.getChoices().split("-/-")));
				}
				mBonus3.setNumber(mBonus3.getNumber() - 1);
				mNumberOfBonus3Left.setText(String.valueOf(mBonus3.getNumber()));
				used = true;
				displayQuestion();
			} else if (mCurrentQuestion.getDifficulty() == 1) {
				while (mCurrentQuestion.getDifficulty() != 0 || mCurrentQuestion.getCategory() != mCurrentCategory) {
					mCurrentQuestion = mQuestionBank.getNextQuestion();
					mCurrentQuestion.setChoiceList(Arrays.asList(mCurrentQuestion.getChoices().split("-/-")));
				}
				mBonus3.setNumber(mBonus3.getNumber() - 1);
				mNumberOfBonus3Left.setText(String.valueOf(mBonus3.getNumber()));
				used = true;
				displayQuestion();
			} else {
				Toast.makeText(this, getResources().getString(R.string.game_bonus_3_lower_difficulty_case), Toast.LENGTH_LONG).show();
			}
		}
		return used;
	}

	private boolean isBonusUsable(Bonus bonus) {
		return bonus.getNumber() > 0;
	}

	/**
	 * Utilisée pour les fonctions de bonus et pour éviter de réécrire le toast, annonce qu'il
	 * manque le bonus que l'utilisateur demande
	 */
	private void toastNoBonusLeft() {
		Toast.makeText(this, getResources().getString(R.string.game_not_enough_bonus), Toast.LENGTH_LONG).show();
	}

	/**
	 * Utilisée pour les fonctions de bonus et pour éviter de réécrire le toast, annonce que
	 * l'utilisateur a déjà utilisé le bonus dans la partie
	 */
	private void toastBonusAlreadyUsed() {
		Toast.makeText(this, getResources().getString(R.string.game_bonus_already_use_case), Toast.LENGTH_LONG).show();
	}

	/**
	 * suspense change en gris tout les boutons en attendant que la réponse soit affichée lors de la
	 * darnière question
	 */
	private void suspense() {
		mQuestionTextView.setText(R.string.suspense);
		for (Button button : mAllAnswerButton) {
			button.setBackgroundColor(R.drawable.rounded_corners_white_border_dark);
			button.setTextColor(getResources().getColor(R.color.colorPrimary));
		}
	}

	/**
	 * changeButtons va formater les boutons pour afficher la réponse : tout les boutons sont setups
	 * de la même façon, si ce que l'utilisateur a répondu est faux, le bouton virera au noir et la
	 * bonne réponse sera affichée grâce à styleGoodButton()
	 *
	 * @param id
	 */
	private void changeButtons(int id) {
		int[] buttons = {R.id.activity_game_answer1_btn, R.id.activity_game_answer2_btn, R.id.activity_game_answer3_btn, R.id.activity_game_answer4_btn};

		for (Button button : mAllAnswerButton) {
			button.setBackgroundColor(getResources().getColor(R.color.colorLightPrimary));
			button.setTextColor(getResources().getColor(R.color.colorPrimary));
		}
		if (id != buttons[mCurrentQuestion.getAnswerIndex()]) {
			if (id == R.id.activity_game_answer1_btn) {
				mAnswerButton1.setBackgroundColor(R.drawable.rounded_corners_white_border_dark);
				mAnswerButton1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
			} else if (id == R.id.activity_game_answer2_btn) {
				mAnswerButton2.setBackgroundColor(R.drawable.rounded_corners_white_border_dark);
				mAnswerButton2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
			} else if (id == R.id.activity_game_answer3_btn) {
				mAnswerButton3.setBackgroundColor(R.drawable.rounded_corners_white_border_dark);
				mAnswerButton3.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
			} else if (id == R.id.activity_game_answer4_btn) {
				mAnswerButton4.setBackgroundColor(R.drawable.rounded_corners_white_border_dark);
				mAnswerButton4.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
			}
			styleGoodButton();
			mLives--;
			calculateScore(false);
		} else {
			styleGoodButton();
			calculateScore(true);
			mValidatedQuestions++;
		}
	}

	/**
	 * styleGoodButton met le bouton qui contient la bonne réponse en background vert, n'est
	 * utilisée que par cette changeButton
	 */
	private void styleGoodButton() {
		switch (mCurrentQuestion.getAnswerIndex()) {
			case 0:
				mAnswerButton1.setBackgroundColor(getResources().getColor(R.color.green_true));
				mAnswerButton1.setTextColor(getResources().getColor(R.color.colorSecondary));
				break;
			case 1:
				mAnswerButton2.setBackgroundColor(getResources().getColor(R.color.green_true));
				mAnswerButton2.setTextColor(getResources().getColor(R.color.colorSecondary));
				break;
			case 2:
				mAnswerButton3.setBackgroundColor(getResources().getColor(R.color.green_true));
				mAnswerButton3.setTextColor(getResources().getColor(R.color.colorSecondary));
				break;
			case 3:
				mAnswerButton4.setBackgroundColor(getResources().getColor(R.color.green_true));
				mAnswerButton4.setTextColor(getResources().getColor(R.color.colorSecondary));
				break;
		}
	}

	/**
	 * setStyleDefault remet les styles des boutons à leur état d'origine
	 */
	public void setStyleDefault() {
		for (Button button : mAllAnswerButton) {
			button.setVisibility(View.VISIBLE);
		}
		mAnswerButton1.setBackgroundColor(getResources().getColor(R.color.colorLightPrimary));
		mAnswerButton1.setTextColor(getResources().getColor(R.color.colorSecondary));
		mAnswerButton2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
		mAnswerButton2.setTextColor(getResources().getColor(R.color.colorText));
		mAnswerButton3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
		mAnswerButton3.setTextColor(getResources().getColor(R.color.colorText));
		mAnswerButton4.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
		mAnswerButton4.setTextColor(getResources().getColor(R.color.colorPrimaryText));
	}

	/**
	 * Méthode appellée à la fin d'une partie seulement : donc soit par onBackPressed() ou endGame()
	 * Permet d'écrire et modifier toutes les variables sur les statistiques de l'utilisateur telles
	 * que le nombres de parties jouées, le score total, le nombre de questions répondues, le nombre
	 * de questions répondues correctement, le meilleur score et les bonus restants attachés à
	 * l'utilisateur
	 */

	public void manageSaves() {
		if (mQuestionsAnswered > 0) {

			PlayersDAO usersDatabase = PlayersDatabase.getInstance(this).PlayersDAO();

			long gamesPlayed = mPlayer.getGamesPlayed();
			long score = mPlayer.getScore();
			long questionsAnswered = mPlayer.getTotalQuestions();
			long userValidatedQuestions = mPlayer.getValidatedQuestions();

			long bestScore = mPlayer.getBestScore();
			if (mScore > bestScore)
				usersDatabase.setBestScore(mPlayer.getId(), mScore);

			usersDatabase.setGamesPlayed(mPlayer.getId(), gamesPlayed + 1);
			if (mScore > 0)
				usersDatabase.setScore(mPlayer.getId(), score + mScore);
			usersDatabase.setQuestionsValidated(mPlayer.getId(), userValidatedQuestions + mValidatedQuestions);
			usersDatabase.setLastGame(mPlayer.getId(), mValidatedQuestions + "-/-" + mQuestionsAnswered);
			usersDatabase.setTotalQuestionsAnswered(mPlayer.getId(), questionsAnswered + mQuestionsAnswered);

			String[] numbers = {String.valueOf(mBonus1.getNumber()), String.valueOf(mBonus2.getNumber()), String.valueOf(mBonus3.getNumber())};
			usersDatabase.setBonus(mPlayer.getId(), TextUtils.join("-/-", numbers));
		}
	}

	/**
	 * Si l'utilisateur revient en arrière, la MainActivity aura son result : le nombre de questions
	 * auxquelles l'utilisateur a répondu et son score sur ce même nombre de questions au lieu du
	 * nombre de départ auquel il comptait répondre
	 */
	@Override
	public void onBackPressed() {
		manageSaves();
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		super.onBackPressed();
	}

	/**
	 * Quand la dernière question est répondue ou qu'il revient en arrière, l'utilisateur est dans
	 * un premier temps informé du nombre de bonnes réponses, puis la MainActivity aura son result
	 */
	private void stopGame() {
		mEnableTouchEvents = false;
		manageSaves();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.game_game_over))
			.setMessage(getResources().getString(R.string.game_end_game, mValidatedQuestions, mTotalQuestions, mScore))
			.setPositiveButton(getResources().getString(R.string.all_ok), (dialog, which) -> {
				if (mInterstitialAd != null && mLives <= 0) {
					mInterstitialAd.show(this);
				}
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}).setCancelable(false).setOnDismissListener(dialog -> stopGame()).create().show();
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();
		if (id == R.id.activity_game_answer1_btn || id == R.id.activity_game_answer2_btn || id == R.id.activity_game_answer3_btn || id == R.id.activity_game_answer4_btn) {
			mQuestionsAnswered += 1;
			mEnableTouchEvents = false;
			if (--mNumberOfQuestions == 0) {
				suspense();
				new Handler().postDelayed(() -> {
					changeButtons(id);
					new Handler().postDelayed(() -> {
						mEnableTouchEvents = true;
						stopGame();
					}, 1500);
				}, 4000);
			} else {
				changeButtons(id);
				new Handler().postDelayed(() -> {
					mCurrentQuestion = mQuestionBank.getNextQuestion();
					mCurrentQuestion.setChoiceList(Arrays.asList(mCurrentQuestion.getChoices().split("-/-")));
					displayQuestion();
					setStyleDefault();
					String textToShow = (mQuestionsAnswered + 1) + getString(R.string.slash) + mTotalQuestions;
					mNumberOfQuestionsAnsweredText.setText(textToShow);
					mProgressBar.setProgress(mQuestionsAnswered);
					mProgressBar.setProgress(mQuestionsAnswered);
					mEnableTouchEvents = true;
				}, 1500);
			}
		} else if (id == R.id.button_use_bonus_1) {
			if (mBonus1.isAlreadyUse()) toastBonusAlreadyUsed();
			else if (mProgressBar.getProgress() == mTotalQuestions - 1) {
				Toast.makeText(this, getResources().getString(R.string.game_cant_use_bonus_last_question), Toast.LENGTH_SHORT).show();
			} else {
				mBonus1.setAlreadyUse(true);
				bonusSkipQuestion();
				mUseBonus1.setVisibility(View.INVISIBLE);
				mNumberOfBonus1Left.setVisibility(View.INVISIBLE);
			}
		} else if (id == R.id.button_use_bonus_2) {
			if (mBonus2.isAlreadyUse()) toastBonusAlreadyUsed();
			else {
				mBonus2.setAlreadyUse(true);
				bonusClearAnswers();
				mUseBonus2.setVisibility(View.INVISIBLE);
				mNumberOfBonus2Left.setVisibility(View.INVISIBLE);
			}
		} else if (id == R.id.button_use_bonus_3) {
			if (mBonus3.isAlreadyUse()) toastBonusAlreadyUsed();
			else {
				boolean bonusUsed = bonusEasier();
				if (bonusUsed) {
					mBonus3.setAlreadyUse(true);
					mUseBonus3.setVisibility(View.INVISIBLE);
					mNumberOfBonus3Left.setVisibility(View.INVISIBLE);
				}
			}
		}
	}
}
