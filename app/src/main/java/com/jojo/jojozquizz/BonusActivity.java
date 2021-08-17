package com.jojo.jojozquizz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.jojo.jojozquizz.databinding.ActivityBonusBinding;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.PlayersDatabase;
import com.jojo.jojozquizz.ui.FabAnimation;

import java.util.ArrayList;
import java.util.Arrays;

public class BonusActivity extends AppCompatActivity implements ClickHandler {

	private static final String TAG = "BonusActivity";

	RewardedAd mBonus1RewardedAd, mBonus2RewardedAd, mBonus3RewardedAd;
	AdView mAdView;
	ProgressBar mProgressBar1, mProgressBar2, mProgressBar3;

	ArrayList<ProgressBar> mProgressBarArray;
	ArrayList<ImageButton> mButtonAddBonusArray;

	SharedPreferences mPreferences;

	Context mContext = this;

	AdRequest mAdRequest;

	Player mPlayer;
	ArrayList<Integer> mPlayerBonuses;

	ActivityBonusBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bonus);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bonus);
		mBinding.setHandler(this);

		mAdRequest = new AdRequest.Builder().build();

		mAdView = mBinding.adViewBonus;

		loadAds();

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);

		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(mPreferences.getInt("currentUserId", 0));
		mPlayerBonuses = new ArrayList<>();
		String[] temp = mPlayer.getBonus().split("-/-");
		for (String t : temp) {
			mPlayerBonuses.add(Integer.valueOf(t));
		}
		mBinding.setPlayer(mPlayer);

		mProgressBar1 = mBinding.progressBarBonus1;
		mProgressBar2 = mBinding.progressBarBonus2;
		mProgressBar3 = mBinding.progressBarBonus3;

		mProgressBarArray = new ArrayList<>(Arrays.asList(mProgressBar1, mProgressBar2, mProgressBar3));
		mButtonAddBonusArray = new ArrayList<>(Arrays.asList(mBinding.buttonAddBonus1, mBinding.buttonAddBonus2, mBinding.buttonAddBonus3));

	}

	public void loadAds() {
		mAdView.loadAd(mAdRequest);

		RewardedAd.load(this, getString(R.string.ad_id_bonus1), mAdRequest, new RewardedAdLoadCallback() {
			@Override
			public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
				super.onAdLoaded(rewardedAd);
				mProgressBarArray.get(0).setVisibility(View.GONE);
				mButtonAddBonusArray.get(0).setVisibility(View.VISIBLE);
				mBonus1RewardedAd = rewardedAd;
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				super.onAdFailedToLoad(loadAdError);
				Toast.makeText(BonusActivity.this, getString(R.string.ad_error), Toast.LENGTH_LONG).show();
			}
		});
		RewardedAd.load(this, getString(R.string.ad_id_bonus2), mAdRequest, new RewardedAdLoadCallback() {
			@Override
			public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
				super.onAdLoaded(rewardedAd);
				mProgressBarArray.get(1).setVisibility(View.GONE);
				mButtonAddBonusArray.get(1).setVisibility(View.VISIBLE);
				mBonus2RewardedAd = rewardedAd;
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				super.onAdFailedToLoad(loadAdError);
				Toast.makeText(BonusActivity.this, getString(R.string.ad_error), Toast.LENGTH_LONG).show();
			}
		});
		RewardedAd.load(this, getString(R.string.ad_id_bonus3), mAdRequest, new RewardedAdLoadCallback() {
			@Override
			public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
				super.onAdLoaded(rewardedAd);
				mProgressBarArray.get(2).setVisibility(View.GONE);
				mButtonAddBonusArray.get(2).setVisibility(View.VISIBLE);
				mBonus3RewardedAd = rewardedAd;
			}

			@Override
			public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
				super.onAdFailedToLoad(loadAdError);
				Toast.makeText(BonusActivity.this, getString(R.string.ad_error), Toast.LENGTH_LONG).show();
			}
		});
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
		String[] bonusValues = {mBinding.textNumberBonus1.getText().toString(), mBinding.textNumberBonus2.getText().toString(), mBinding.textNumberBonus3.getText().toString()};
		String values = TextUtils.join("-/-", bonusValues);
		PlayersDatabase.getInstance(this).PlayersDAO().setBonus(mPlayer.getId(), values);
		finish();
	}

	@Override
	public void onButtonClick(View v) {
		int buttonTag = v.getId();

		switch (buttonTag) {
			case R.id.button_add_bonus_1:
				mBonus1RewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
					@Override
					public void onAdDismissedFullScreenContent() {
						super.onAdDismissedFullScreenContent();
						mBonus1RewardedAd = null;
						mBinding.buttonAddBonus1.setVisibility(View.INVISIBLE);
					}
				});
				mBonus1RewardedAd.show(this, (@NonNull RewardItem rewardItem) -> {
					int bonuses = mPlayerBonuses.get(0);
					mPlayerBonuses.set(0, bonuses + 1);
					mBinding.textNumberBonus1.setText(String.valueOf(mPlayerBonuses.get(0)));
				});
				break;
			case R.id.button_add_bonus_2:
				mBonus2RewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
					@Override
					public void onAdDismissedFullScreenContent() {
						super.onAdDismissedFullScreenContent();
						mBonus2RewardedAd = null;
						mBinding.buttonAddBonus2.setVisibility(View.INVISIBLE);
					}
				});
				mBonus2RewardedAd.show(this, (@NonNull RewardItem rewardItem) -> {
					int bonuses = mPlayerBonuses.get(1);
					mPlayerBonuses.set(1, bonuses + 1);
					mBinding.textNumberBonus2.setText(String.valueOf(mPlayerBonuses.get(1)));
				});
				break;
			case R.id.button_add_bonus_3:
				mBonus3RewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
					@Override
					public void onAdDismissedFullScreenContent() {
						super.onAdDismissedFullScreenContent();
						mBonus3RewardedAd = null;
						mBinding.buttonAddBonus3.setVisibility(View.INVISIBLE);
					}
				});
				mBonus3RewardedAd.show(this, (@NonNull RewardItem rewardItem) -> {
					int bonuses = mPlayerBonuses.get(2);
					mPlayerBonuses.set(2, bonuses + 1);
					mBinding.textNumberBonus3.setText(String.valueOf(mPlayerBonuses.get(2)));
				});
				break;
			case R.id.button_help:
				ViewGroup viewGroup = findViewById(android.R.id.content);
				View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bonus, viewGroup, false);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(dialogView);
				builder.setPositiveButton("Ok", (dialog, which) -> {
				});
				final AlertDialog alertDialog = builder.create();
				alertDialog.show();
				break;
			case R.id.activity_bonus_button_come_back:
				quitActivity();
				break;
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {
		return false;
	}
}
