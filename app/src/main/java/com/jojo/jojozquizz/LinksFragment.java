package com.jojo.jojozquizz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LinksFragment extends Fragment {
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_links, container, false);

		TextView mTextDiscord = view.findViewById(R.id.activity_links_discord_text);
		TextView mTextInstagram = view.findViewById(R.id.activity_links_instagram_text);
		TextView mTextYoutube = view.findViewById(R.id.activity_links_youtube_text);
		TextView mTextTwitter = view.findViewById(R.id.activity_links_twitter_text);
		TextView mTextReddit = view.findViewById(R.id.activity_links_reddit_text);

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

		return view;
	}
}