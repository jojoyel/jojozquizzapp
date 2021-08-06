package com.jojo.jojozquizz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.jojo.jojozquizz.model.Player;
import com.jojo.jojozquizz.tools.CategoriesAdapter;
import com.jojo.jojozquizz.tools.CategoriesHelper;
import com.jojo.jojozquizz.tools.PlayersDatabase;

import java.util.ArrayList;
import java.util.List;

public class SelectCategoriesActivity extends AppCompatActivity implements View.OnClickListener, CategoriesAdapter.CategoriesCheckListener {

	private SwitchCompat mSelectAllSwitch;
	private MaterialCheckBox mCheckboxEasy, mCheckboxMedium, mCheckboxHard;
	private MaterialCheckBox[] mCheckBoxes = new MaterialCheckBox[3];

	private RecyclerView mRecyclerView;
	private CategoriesAdapter mCategoriesAdapter; // Adapter for RecyclerView

	private List<String> mOldCategoriesSelected; // What the mPlayer choose
	private List<String> mNewCategoriesSelected; // What the mPlayer chooses

	private List<String> mDifficultiesSelected;

	private CategoriesHelper mCategoriesHelper; //Helper to process categories and more

	private SharedPreferences mPreferences;

	private Player mPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_categories);

		mPreferences = this.getSharedPreferences("com.jojo.jojozquizz", MODE_PRIVATE);
		int userId = mPreferences.getInt("currentUserId", 0);
		mPlayer = PlayersDatabase.getInstance(this).PlayersDAO().getPlayer(userId);

		mCategoriesHelper = new CategoriesHelper(this);

		mOldCategoriesSelected = mCategoriesHelper.getProcessedCategories(mPlayer.getCategoriesSelected());
		mNewCategoriesSelected = mOldCategoriesSelected;

		mSelectAllSwitch = findViewById(R.id.switchSelectAll);
		mCheckboxEasy = findViewById(R.id.activity_select_categories_checkbox_facile);
		mCheckboxMedium = findViewById(R.id.activity_select_categories_checkbox_moyen);
		mCheckboxHard = findViewById(R.id.activity_select_categories_checkbox_difficile);
		mCheckBoxes = new MaterialCheckBox[]{mCheckboxEasy, mCheckboxMedium, mCheckboxHard};

		mCategoriesAdapter = new CategoriesAdapter(this, mCategoriesHelper.getCategories(), mOldCategoriesSelected);
		mRecyclerView = findViewById(R.id.recycler_categories);
		mRecyclerView.setAdapter(mCategoriesAdapter);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		mDifficultiesSelected = mCategoriesHelper.getProcessedDifficulties(mPlayer.getDifficultiesSelected());

		int i = 0;
		for (CheckBox checkBox : mCheckBoxes) {
			checkBox.setText(mCategoriesHelper.getDifficulties()[i]);
			checkBox.setChecked(mDifficultiesSelected.contains(checkBox.getText().toString()));
			i++;
		}

		Button mButtonOk = findViewById(R.id.activity_select_categories_button_ok);
		mButtonOk.setTag(0);
		mButtonOk.setOnClickListener(this);

		mSelectAllSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			mSelectAllSwitch.setText(isChecked ? R.string.deselect_all : R.string.select_all);
			if (isChecked) {
				mNewCategoriesSelected = mCategoriesHelper.getProcessedCategories(mCategoriesHelper.getAllCategoriesProcessed());
			} else {
				mNewCategoriesSelected = mCategoriesHelper.getProcessedCategories(mCategoriesHelper.getNoneCategoriesProcessed());
			}
			mCategoriesAdapter.changeCategoriesChecked(mNewCategoriesSelected);
			mCategoriesAdapter.notifyDataSetChanged();
		});
	}

	@Override
	public void onClick(View v) {
		int buttonTag = (int) v.getTag();

		if (buttonTag == 0) {
			quitActivity();
		}
	}

	@Override
	public void onBackPressed() {
		quitActivity();
	}

	private void quitActivity() {
		if (isGoodToFinish()) {
			PlayersDatabase.getInstance(this).PlayersDAO().setCategories(mPlayer.getId(), mCategoriesHelper.processCategories(mNewCategoriesSelected));
			PlayersDatabase.getInstance(this).PlayersDAO().setDifficulties(mPlayer.getId(), mCategoriesHelper.processDifficulties(convertCheckboxesToNames(mCheckBoxes)));
			finish();
		} else {
			Toast.makeText(this, getString(R.string.select_nothing_checked), Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isGoodToFinish() {
		return !mCategoriesHelper.processCategories(mNewCategoriesSelected).equals(mCategoriesHelper.getNoneCategoriesProcessed()) && !mCategoriesHelper.processDifficulties(convertCheckboxesToNames(new CheckBox[]{mCheckboxEasy, mCheckboxMedium, mCheckboxHard})).equals(mCategoriesHelper.getNoneDifficultiesrocessed());
	}

	@Override
	public void checkChanged(String category, boolean isChecked) {
		if (isChecked && !mNewCategoriesSelected.contains(category)) {
			mNewCategoriesSelected.add(category);
		} else if (!isChecked) {
			mNewCategoriesSelected.remove(category);
		}
		mCategoriesAdapter.changeCategoriesChecked(mNewCategoriesSelected);
	}

	private List<String> convertCheckboxesToNames(CheckBox[] checkBoxes) {
		List<String> valuesToReturn = new ArrayList<>();
		for (CheckBox checkBox : checkBoxes) {
			if (checkBox.isChecked()) {
				valuesToReturn.add(checkBox.getText().toString());
			}
		}
		return valuesToReturn;
	}
}
