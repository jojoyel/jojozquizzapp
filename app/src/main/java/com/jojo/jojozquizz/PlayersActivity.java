package com.jojo.jojozquizz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jojo.jojozquizz.databinding.ActivityPlayersBinding;
import com.jojo.jojozquizz.dialogs.NameDialog;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.ClickHandler;
import com.jojo.jojozquizz.tools.PlayersAdapter;
import com.jojo.jojozquizz.tools.PlayersDatabase;
import com.jojo.jojozquizz.ui.FabAnimation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayersActivity extends AppCompatActivity implements NameDialog.NameDialogListener, ClickHandler {

	private SharedPreferences mPreferences;

	private ImageButton mBackButton;
	private RecyclerView mRecyclerView;

	FloatingActionButton mFloatingActionButtonRemove, mFloatingActionButtonAdd, mFloatingActionButtonAddFromServer;
	ExtendedFloatingActionButton mFloatingActionButton;

	ActivityPlayersBinding mBinding;

	boolean isMainFabRotate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_players);

		mBinding = DataBindingUtil.setContentView(this, R.layout.activity_players);
		mBinding.setHandler(this);

		mFloatingActionButton = mBinding.floatingActionButtonUsers;
		mFloatingActionButtonAdd = mBinding.floatingActionButtonChildAdd;
		mFloatingActionButtonAddFromServer = mBinding.floatingActionButtonChildAddFromServer;
		mFloatingActionButtonRemove = mBinding.floatingActionButtonChildRemove;

		FabAnimation.init(mFloatingActionButtonRemove);
		FabAnimation.init(mFloatingActionButtonAdd);
		FabAnimation.init(mFloatingActionButtonAddFromServer);

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);

		mBackButton = findViewById(R.id.button_back_users_activity);
		mRecyclerView = findViewById(R.id.recycler_users);

		mBackButton.setTag(1);

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
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	public void onButtonClick(View v) {
		int id = v.getId();

		switch (id) {
			case R.id.floatingActionButtonUsers:
				mFloatingActionButton.shrink();
				isMainFabRotate = FabAnimation.rotateFab(v, !isMainFabRotate);
				if (isMainFabRotate) {
					FabAnimation.showIn(mFloatingActionButtonAdd, 1);
					FabAnimation.showIn(mFloatingActionButtonAddFromServer, 2);
					FabAnimation.showIn(mFloatingActionButtonRemove, 3);
				} else {
					FabAnimation.showOut(mFloatingActionButtonAdd, 3);
					FabAnimation.showOut(mFloatingActionButtonAddFromServer, 2);
					FabAnimation.showOut(mFloatingActionButtonRemove, 1);
					mFloatingActionButton.extend();
				}
				break;
			case R.id.floatingActionButtonChildAdd:
				NameDialog nameDialog = new NameDialog();
				nameDialog.setIsNewUser(true);
				nameDialog.setIsCancelable(true);
				nameDialog.show(getSupportFragmentManager(), "name dialog usersactivity");
				break;
			case R.id.button_back_users_activity:
				finish();
				break;
		}
	}

	@Override
	public boolean onLongButtonClick(View v) {

		return false;
	}

}