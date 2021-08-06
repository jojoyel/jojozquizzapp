package com.jojo.jojozquizz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class LinksActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_links);

		AdView mAdView = findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		TextView mTextDiscord = findViewById(R.id.activity_links_discord_text);
		TextView mTextInstagram = findViewById(R.id.activity_links_instagram_text);
		TextView mTextYoutube = findViewById(R.id.activity_links_youtube_text);
		TextView mTextTwitter = findViewById(R.id.activity_links_twitter_text);
		TextView mTextReddit = findViewById(R.id.activity_links_reddit_text);
		TextView mTextThanksfulls = findViewById(R.id.activity_links_thanksfull);

		mTextDiscord.setClickable(true);
		mTextInstagram.setClickable(true);
		mTextYoutube.setClickable(true);
		mTextTwitter.setClickable(true);
		mTextReddit.setClickable(true);

		mTextDiscord.setMovementMethod(LinkMovementMethod.getInstance());
		mTextInstagram.setMovementMethod(LinkMovementMethod.getInstance());
		mTextYoutube.setMovementMethod(LinkMovementMethod.getInstance());
		mTextTwitter.setMovementMethod(LinkMovementMethod.getInstance());
		mTextReddit.setMovementMethod(LinkMovementMethod.getInstance());

		mTextDiscord.setText(Html.fromHtml("<a href='https://discord.gg/fntMgg7'>Serveur Discord</a>"));
		mTextInstagram.setText(Html.fromHtml("<a href='https://instagram.com/nextfor.dev'>Instagram</a>"));
		mTextYoutube.setText(Html.fromHtml("<a href='https://www.youtube.com/channel/UCU9_Y3nu76BgZqxbfr8lX1Q'>Youtube</a>"));
		mTextTwitter.setText(Html.fromHtml("<a href='https://twitter.com/nextfordev'>Twitter</a>"));
		mTextReddit.setText(Html.fromHtml("<a href='https://reddit.com/r/jojoz'>Reddit</a>"));

		mTextThanksfulls.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LinksActivity.this, ThanksActivity.class));
			}
		});
	}
}
