package com.jojo.jojozquizz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.objects.Bonus;
import com.jojo.jojozquizz.tools.PlayersDatabase;

import java.util.Arrays;
import java.util.List;

public class BonusActivity extends AppCompatActivity implements View.OnClickListener {

	private RewardedAd bonus1RewardedAd, bonus2RewardedAd, bonus3RewardedAd;
	private ImageButton mButtonAddBonus1, mButtonAddBonus2, mButtonAddBonus3, mComeBackButton, mHelpButton;
	private TextView mNumberTextBonus1, mNumberTextBonus2, mNumberTextBonus3, mBonusOfPlayerText;
	private ProgressBar mProgressBar1, mProgressBar2, mProgressBar3;

	private Bonus mBonus1, mBonus2, mBonus3;

	private SharedPreferences mPreferences;

	private Context mContext = this;

	private Player mPlayer;

	private List<String> mUserBonus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bonus);

		loadAds();

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);

		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 0));

		mUserBonus = Arrays.asList(mPlayer.getBonus().split("-/-"));

		mBonus1 = new Bonus(Integer.parseInt(mUserBonus.get(0)), getString(R.string.bonus_skip));
		mBonus2 = new Bonus(Integer.parseInt(mUserBonus.get(1)), getString(R.string.bonus_2));
		mBonus3 = new Bonus(Integer.parseInt(mUserBonus.get(2)), getString(R.string.bonus_easier));

		mButtonAddBonus1 = findViewById(R.id.button_add_bonus_1);
		mButtonAddBonus2 = findViewById(R.id.button_add_bonus_2);
		mButtonAddBonus3 = findViewById(R.id.button_add_bonus_3);
		mComeBackButton = findViewById(R.id.activity_bonus_button_come_back);
		mHelpButton = findViewById(R.id.button_help);
		mBonusOfPlayerText = findViewById(R.id.bonus_of_player_text);
		mNumberTextBonus1 = findViewById(R.id.text_number_bonus_1);
		mNumberTextBonus2 = findViewById(R.id.text_number_bonus_2);
		mNumberTextBonus3 = findViewById(R.id.text_number_bonus_3);
		mProgressBar1 = findViewById(R.id.progressBarBonus1);
		mProgressBar2 = findViewById(R.id.progressBarBonus2);
		mProgressBar3 = findViewById(R.id.progressBarBonus3);

		mButtonAddBonus1.setTag(0);
		mButtonAddBonus2.setTag(1);
		mButtonAddBonus3.setTag(2);
		mHelpButton.setTag(3);
		mComeBackButton.setTag(99);

		mButtonAddBonus1.setOnClickListener(this);
		mButtonAddBonus2.setOnClickListener(this);
		mButtonAddBonus3.setOnClickListener(this);
		mHelpButton.setOnClickListener(this);
		mComeBackButton.setOnClickListener(this);

		mBonusOfPlayerText.setText(String.format("%s\n%s", mBonusOfPlayerText.getText(), mPlayer.getName()));

		refreshNumber();
	}

	@Override
	public void onClick(View v) {
		int buttonTag = (int) v.getTag();

		if (buttonTag == 0) {
			if (bonus1RewardedAd.isLoaded()) {
				Activity activityContext = BonusActivity.this;
				RewardedAdCallback adCallback = new RewardedAdCallback() {
					@Override
					public void onRewardedAdOpened() {
					}

					@Override
					public void onUserEarnedReward(@NonNull RewardItem reward) {
						mBonus1.setNumber(mBonus1.getNumber() + 1);
						refreshNumber();
					}

					@Override
					public void onRewardedAdFailedToShow(int errorCode) {
						Toast.makeText(BonusActivity.this, getString(R.string.ad_error), Toast.LENGTH_LONG).show();
					}
				};
				bonus1RewardedAd.show(activityContext, adCallback);
			}
		} else if (buttonTag == 1) {
			if (mBonus2.getNumber() <= 5) {
				if (bonus2RewardedAd.isLoaded()) {
					Activity activityContext = BonusActivity.this;
					RewardedAdCallback adCallback = new RewardedAdCallback() {
						@Override
						public void onRewardedAdOpened() {
						}

						@Override
						public void onUserEarnedReward(@NonNull RewardItem reward) {
							mBonus2.setNumber(mBonus2.getNumber() + 1);
							refreshNumber();
						}

						@Override
						public void onRewardedAdFailedToShow(int errorCode) {
							Toast.makeText(BonusActivity.this, getString(R.string.ad_error), Toast.LENGTH_LONG).show();
						}
					};
					bonus2RewardedAd.show(activityContext, adCallback);
				}
			}
		} else if (buttonTag == 2) {
			if (mBonus3.getNumber() <= 5) {
				if (bonus3RewardedAd.isLoaded()) {
					Activity activityContext = BonusActivity.this;
					RewardedAdCallback adCallback = new RewardedAdCallback() {
						@Override
						public void onRewardedAdOpened() {
						}

						@Override
						public void onUserEarnedReward(@NonNull RewardItem reward) {
							mBonus3.setNumber(mBonus3.getNumber() + 1);
							refreshNumber();
						}
					};
					bonus3RewardedAd.show(activityContext, adCallback);
				}
			}
		} else if (buttonTag == 3) {
			ViewGroup viewGroup = findViewById(android.R.id.content);
			View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bonus, viewGroup, false);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(dialogView);
			builder.setPositiveButton("Ok", (dialog, which) -> {

			});
			final AlertDialog alertDialog = builder.create();
			alertDialog.show();
		} else if (buttonTag == 99) {
			quitActivity();
		}
	}

	/**
	 * refreshNumber() permet de modifier le text des EditTexts qui contiennent les valeurs des
	 * bonus de l'utilisateur pour s'actualiser après avoir regardé une pub
	 */
	public void refreshNumber() {
		mNumberTextBonus1.setText(String.valueOf(mBonus1.getNumber()));
		mNumberTextBonus2.setText(String.valueOf(mBonus2.getNumber()));
		mNumberTextBonus3.setText(String.valueOf(mBonus3.getNumber()));
	}


	public void loadAds() {
		AdView mAdView = findViewById(R.id.adView2);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		bonus1RewardedAd = createAndLoadRewardedAd("ca-app-pub-5050054249389989/9324639067", 1);
		bonus2RewardedAd = createAndLoadRewardedAd("ca-app-pub-5050054249389989/9211204402", 2);
		bonus3RewardedAd = createAndLoadRewardedAd("ca-app-pub-5050054249389989/6686582029", 3);
	}

	public RewardedAd createAndLoadRewardedAd(String adUnitId, final int place) {

		RewardedAd rewardedAd = new RewardedAd(mContext, adUnitId);
		RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
			@Override
			public void onRewardedAdLoaded() {
				if (place == 1) {
					mProgressBar1.setVisibility(View.GONE);
					mButtonAddBonus1.setVisibility(View.VISIBLE);
				} else if (place == 2) {
					mProgressBar2.setVisibility(View.GONE);
					mButtonAddBonus2.setVisibility(View.VISIBLE);
				} else if (place == 3) {
					mProgressBar3.setVisibility(View.GONE);
					mButtonAddBonus3.setVisibility(View.VISIBLE);
				}
			}
		};
		rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
		return rewardedAd;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		quitActivity();
	}

	/**
	 * On met en préférences le nombre de chaque bonus qu'a l'utilisateur
	 */
	public void quitActivity() {
		String[] numbers = {String.valueOf(mBonus1.getNumber()), String.valueOf(mBonus2.getNumber()), String.valueOf(mBonus3.getNumber())};
		PlayersDatabase.getInstance(this).PlayersDAO().setBonus(mPlayer.getId(), TextUtils.join("-/-", numbers));
		finish();
	}
}
