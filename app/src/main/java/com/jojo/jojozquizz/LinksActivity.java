package com.jojo.jojozquizz;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jojo.jojozquizz.databinding.ActivityLinksBinding;
import com.jojo.jojozquizz.tools.ClickHandler;

public class LinksActivity extends AppCompatActivity implements ClickHandler {

	ActivityLinksBinding mBinding;

	Fragment mLinksFragment, mThanksFragment;
	FragmentManager mFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_links);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_links);
		mBinding.setHandler(this);

		AdView mAdView = findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		mFragmentManager = getSupportFragmentManager();

		mLinksFragment = new LinksFragment();
		mThanksFragment = new ThanksFragment();
		mFragmentManager.beginTransaction()
			.replace(R.id.frame_layout_links, mLinksFragment)
			.commit();
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		if (id == R.id.links_activity_show_links) {
			if (mLinksFragment == null) {
				mLinksFragment = new LinksFragment();
			}
			mFragmentManager.beginTransaction()
				.replace(R.id.frame_layout_links, mLinksFragment)
				.commit();
		} else if (id == R.id.links_activity_show_thanks) {
			if (mThanksFragment == null) {
				mThanksFragment = new ThanksFragment();
			}
			mFragmentManager.beginTransaction()
				.replace(R.id.frame_layout_links, mThanksFragment)
				.commit();
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {
		return false;
	}
}
