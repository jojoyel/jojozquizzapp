package com.jojo.jojozquizz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.PlayersAdapter;
import com.jojo.jojozquizz.tools.PlayersDatabase;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayersActivity extends AppCompatActivity implements View.OnClickListener, NameDialog.NameDialogListener {

	private SharedPreferences mPreferences;

	private ImageButton mBackButton, mAddUserButton;
	private RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_players);

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);

		mBackButton = findViewById(R.id.button_back_users_activity);
		mAddUserButton = findViewById(R.id.button_add_user);
		mRecyclerView = findViewById(R.id.recycler_users);

		mAddUserButton.setTag(0);
		mBackButton.setTag(1);

		mAddUserButton.setOnClickListener(this);
		mBackButton.setOnClickListener(this);

		updateUI();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
	}

	protected void updateUI() {
		List<Player> players = PlayersDatabase.getInstance(this).PlayersDAO().getAllPlayers();
		Player[] usersArray = new Player[players.size()];
		usersArray = players.toArray(usersArray);

		int[] usersIds = new int[players.size()];
		String[] usersNames = new String[players.size()];
		long[] usersScore = new long[players.size()];

		int i = 0;
		for (Player u : usersArray) {
			usersIds[i] = u.getId();
			usersNames[i] = u.getName();
			usersScore[i] = u.getScore();
			i++;
		}

		PlayersAdapter playersAdapter = new PlayersAdapter(this, usersIds, usersNames, usersScore);
		mRecyclerView.setAdapter(playersAdapter);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
	}

	@Override
	public void applyText(String name) {
		Pattern pattern = Pattern.compile(NameDialog.REGEX);
		Matcher matcher = pattern.matcher(name);
		if (!matcher.find()) {
			NameDialog nameDialog = new NameDialog();
			nameDialog.setIsNewUser(true);
			nameDialog.show(getSupportFragmentManager(), "name dialog usersactivity");
			Toast.makeText(this, getResources().getString(R.string.invalid_name), Toast.LENGTH_SHORT).show();
		} else {
			Player player = new Player(name, this);
			PlayersDatabase.getInstance(this).PlayersDAO().addPlayer(player);

			Player newPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayerFromName(player.getName());
			mPreferences.edit().putInt("currentUserId", newPlayer.getId()).apply();

			updateUI();
		}
	}

	@Override
	public void onClick(View v) {
		int tag = (int) v.getTag();

		if (tag == 0) {
			NameDialog nameDialog = new NameDialog();
			nameDialog.setIsNewUser(true);
			nameDialog.setIsCancelable(true);
			nameDialog.show(getSupportFragmentManager(), "name dialog usersactivity");

		} else if (tag == 1) {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}