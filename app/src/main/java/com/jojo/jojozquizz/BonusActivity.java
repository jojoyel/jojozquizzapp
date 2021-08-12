package com.jojo.jojozquizz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.jojo.jojozquizz.databinding.ActivityBonusBinding;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.PlayersDatabase;

import java.util.ArrayList;

public class BonusActivity extends AppCompatActivity implements ClickHandler {

	RewardedAd mBonus1RewardedAd, mBonus2RewardedAd, mBonus3RewardedAd;
	AdView mAdView;
	ImageButton mButtonAddBonus1, mButtonAddBonus2, mButtonAddBonus3;
	ProgressBar mProgressBar1, mProgressBar2, mProgressBar3;

	ProgressBar[] mProgressBarArray;
	ImageButton[] mButtonAddBonusArray;

	SharedPreferences mPreferences;

	Context mContext = this;

	Player mPlayer;
	ArrayList<Integer> mPlayerBonuses;

	ActivityBonusBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bonus);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bonus);
		mBinding.setHandler(this);

		mAdView = findViewById(R.id.adView2);

		loadAds();

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);

		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 0));
		mPlayerBonuses = new ArrayList<>();
		String[] temp = mPlayer.getBonus().split("-/-");
		for (String t : temp) {
			mPlayerBonuses.add(Integer.valueOf(t));
		}
		mBinding.setPlayer(mPlayer);

		mProgressBar1 = findViewById(R.id.progressBarBonus1);
		mProgressBar2 = findViewById(R.id.progressBarBonus2);
		mProgressBar3 = findViewById(R.id.progressBarBonus3);

		mProgressBarArray = new ProgressBar[]{mProgressBar1, mProgressBar2, mProgressBar3};
		mButtonAddBonusArray = new ImageButton[]{mButtonAddBonus1, mButtonAddBonus2, mButtonAddBonus3};

	}

	public void loadAds() {
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		mBonus1RewardedAd = createAndLoadRewardedAd(getString(R.string.ad_id_bonus1), 1, adRequest);
		mBonus2RewardedAd = createAndLoadRewardedAd(getString(R.string.ad_id_bonus2), 2, adRequest);
		mBonus3RewardedAd = createAndLoadRewardedAd(getString(R.string.ad_id_bonus3), 3, adRequest);

		/*
		mBonus1RewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
			@Override
			public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
				super.onAdFailedToShowFullScreenContent(adError);
				Toast.makeText(BonusActivity.this, getString(R.string.ad_error), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onAdDismissedFullScreenContent() {
				super.onAdDismissedFullScreenContent();
				mBonus1RewardedAd = null;
				mButtonAddBonus1.setVisibility(View.GONE);
				mProgressBar1.setVisibility(View.VISIBLE);
				createAndLoadRewardedAd(getString(R.string.ad_id_bonus1), 1, adRequest);
			}
		});
		mBonus2RewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
			@Override
			public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
				super.onAdFailedToShowFullScreenContent(adError);
				Toast.makeText(BonusActivity.this, getString(R.string.ad_error), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onAdDismissedFullScreenContent() {
				super.onAdDismissedFullScreenContent();
				mBonus2RewardedAd = null;
				mButtonAddBonus2.setVisibility(View.GONE);
				mProgressBar2.setVisibility(View.VISIBLE);
				createAndLoadRewardedAd(getString(R.string.ad_id_bonus2), 2, adRequest);
			}
		});
		mBonus3RewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
			@Override
			public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
				super.onAdFailedToShowFullScreenContent(adError);
				Toast.makeText(BonusActivity.this, getString(R.string.ad_error), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onAdDismissedFullScreenContent() {
				super.onAdDismissedFullScreenContent();
				mBonus3RewardedAd = null;
				mButtonAddBonus3.setVisibility(View.GONE);
				mProgressBar3.setVisibility(View.VISIBLE);
				createAndLoadRewardedAd(getString(R.string.ad_id_bonus3), 3, adRequest);
			}
		});

		 */
	}

	public RewardedAd createAndLoadRewardedAd(String adUnitId, final int place, AdRequest adRequest) {
		final RewardedAd[] toReturn = {null};

		RewardedAd.load(this, adUnitId, adRequest, new RewardedAdLoadCallback() {
			@Override
			public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
				super.onAdLoaded(rewardedAd);

				mProgressBarArray[place - 1].setVisibility(View.GONE);
				mButtonAddBonusArray[place - 1].setVisibility(View.VISIBLE);
				toReturn[0] = rewardedAd;
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				super.onAdFailedToLoad(loadAdError);

				Toast.makeText(BonusActivity.this, getString(R.string.ad_error), Toast.LENGTH_LONG).show();
			}
		});

		return toReturn[0];
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
		String[] bonusValues = {mBinding.textNumberBonus1.getText().toString(), mBinding.textNumberBonus3.getText().toString(), mBinding.textNumberBonus3.getText().toString()};
		String values = TextUtils.join("-/-", bonusValues);
		PlayersDatabase.getInstance(this).PlayersDAO().setBonus(mPlayer.getId(), values);
		finish();
	}

	@Override
	public void onButtonClick(View v) {
		int buttonTag = v.getId();

		if (buttonTag == R.id.button_add_bonus_1) {
			mBonus1RewardedAd.show(this, (@NonNull RewardItem rewardItem) -> {
				int bonuses = mPlayerBonuses.get(0);
				mPlayerBonuses.set(0, bonuses + 1);
			});
		} else if (buttonTag == R.id.button_add_bonus_2) {
			mBonus2RewardedAd.show(this, (@NonNull RewardItem rewardItem) -> {
				int bonuses = mPlayerBonuses.get(1);
				mPlayerBonuses.set(1, bonuses + 1);
			});
		} else if (buttonTag == R.id.button_add_bonus_3) {
			mBonus3RewardedAd.show(this, (@NonNull RewardItem rewardItem) -> {
				int bonuses = mPlayerBonuses.get(2);
				mPlayerBonuses.set(2, bonuses + 1);
			});
		} else if (buttonTag == R.id.button_help) {
			ViewGroup viewGroup = findViewById(android.R.id.content);
			View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bonus, viewGroup, false);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(dialogView);
			builder.setPositiveButton("Ok", (dialog, which) -> {

			});
			final AlertDialog alertDialog = builder.create();
			alertDialog.show();
		} else if (buttonTag == R.id.activity_bonus_button_come_back) {
			quitActivity();
		}
	}
}
