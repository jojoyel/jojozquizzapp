package com.jojo.jojozquizz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.PlayersDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayersInformationActivity extends AppCompatActivity implements View.OnClickListener, NameDialog.NameDialogListener {

	private TextView mNameText, mInformationText;
	private ImageButton mBackButton, mEditButton, mEraseButton, mShareButton;
	private Button mUseUserButton;

	private SharedPreferences mPreferences;

	private Context mContext;

	int userId;
	Player mPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_information);

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);

		if (getIntent().hasExtra("id")) {
			userId = getIntent().getIntExtra("id", 0);
		} else {
			finishActivity(0);
		}
		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(userId);

		mNameText = findViewById(R.id.information_user_name);
		mInformationText = findViewById(R.id.information_user_information);
		mBackButton = findViewById(R.id.information_back_arrow);
		mEditButton = findViewById(R.id.information_button_edit_user);
		mEraseButton = findViewById(R.id.information_button_delete_user);
		mShareButton = findViewById(R.id.information_share_button);
		mUseUserButton = findViewById(R.id.button_use_user);

		if (mPreferences.getInt("currentUserId", 0) != userId) {
			mUseUserButton.setTag(3);
			mUseUserButton.setOnClickListener(this);
		} else {
			mUseUserButton.setVisibility(View.GONE);
		}

		mBackButton.setTag(-1);
		mEditButton.setTag(0);
		mEraseButton.setTag(1);
		mShareButton.setTag(2);

		mBackButton.setOnClickListener(this);
		mEditButton.setOnClickListener(this);
		mEraseButton.setOnClickListener(this);
		mShareButton.setOnClickListener(this);

		mNameText.setText(mPlayer.getName());

		mInformationText.setText(getString(R.string.user_stats, mPlayer.getScore(), mPlayer.getBestScore(), mPlayer.getGamesPlayed(), mPlayer.getValidatedQuestions(), mPlayer.getTotalQuestions()));
	}


	public void share() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getQuantityString(R.plurals.score_to_send, (int) mPlayer.getGamesPlayed(), mPlayer.getValidatedQuestions(), mPlayer.getTotalQuestions(), mPlayer.getGamesPlayed()));
		sendIntent.setType("text/plain");

		Intent shareIntent = Intent.createChooser(sendIntent, null);
		startActivity(shareIntent);
	}

	@Override
	public void onClick(View v) {
		int tag = (int) v.getTag();

		if (tag == -1) {
			finish();
		} else if (tag == 0) {
			NameDialog nameDialog = new NameDialog();
			nameDialog.setIsCancelable(true);
			nameDialog.show(getSupportFragmentManager(), "information name dialog");
		} else if (tag == 1) {
			if (PlayersDatabase.getInstance(this).PlayersDAO().getAllPlayers().size() <= 1) {
				Toast.makeText(this, getString(R.string.delete_user_error), Toast.LENGTH_SHORT).show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getString(R.string.delete_user))
					.setCancelable(true)
					.setIcon(getResources().getDrawable(R.drawable.trash_icon))
					.setMessage(R.string.delete_user_message)
					.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PlayersDatabase.getInstance(mContext).PlayersDAO().deletePlayerWithId(userId);
							mPreferences.edit().putInt("currentUserId", PlayersDatabase.getInstance(mContext).PlayersDAO().getFirstPlayer().getId()).apply();
							finish();
						}
					})
					.setNegativeButton(getString(R.string.all_cancel), null);
				builder.show();
			}
		} else if (tag == 2) {
			share();
		} else if (tag == 3) {
			mPreferences.edit().putInt("currentUserId", mPlayer.getId()).apply();
			mUseUserButton.setVisibility(View.GONE);
		}
	}

	@Override
	public void applyText(String name) {
		Pattern pattern = Pattern.compile(NameDialog.REGEX);
		Matcher matcher = pattern.matcher(name);
		if (!matcher.find()) {
			NameDialog nameDialog = new NameDialog();
			nameDialog.show(getSupportFragmentManager(), "information name dialog");
			Toast.makeText(this, getResources().getString(R.string.invalid_name), Toast.LENGTH_SHORT).show();
		}
		PlayersDatabase.getInstance(this).PlayersDAO().changeName(userId, name);
		mNameText.setText(name);
	}
}