package com.jojo.jojozquizz.model;

import android.content.Context;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.jojo.jojozquizz.tools.CategoriesHelper;

@Entity(tableName = "players")
public class Player {

	@Ignore
	private CategoriesHelper mCategoriesHelper;

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	private int id;

	@ColumnInfo(name = "name")
	private String name;

	@ColumnInfo(name = "score")
	private long score;

	@ColumnInfo(name = "bestScore")
	private long bestScore;

	@ColumnInfo(name = "gamesPlayed")
	private long gamesPlayed;

	@ColumnInfo(name = "totalQuestions")
	private long totalQuestions;

	@ColumnInfo(name = "validatedQuestions")
	private long validatedQuestions;

	@ColumnInfo(name = "lastGame")
	private String lastGame;

	@ColumnInfo(name = "categoriesSelected")
	private String categoriesSelected;

	@ColumnInfo(name = "difficultiesSelected")
	private String difficultiesSelected;

	@ColumnInfo(name = "bonus")
	private String bonus;

	public Player() {
	}

	@Ignore
	public Player(String firstName, Context ct) {
		mCategoriesHelper = new CategoriesHelper(ct);
		name = firstName;
		score = 0;
		bestScore = 0;
		gamesPlayed = 0;
		totalQuestions = 0;
		validatedQuestions = 0;
		lastGame = "0-/-0";
		categoriesSelected = mCategoriesHelper.getAllCategoriesProcessed();
		difficultiesSelected = mCategoriesHelper.getAllDifficultiesProcessed();
		bonus = "2-/-2-/-2";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String firstName) {
		name = firstName;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public long getBestScore() {
		return bestScore;
	}

	public void setBestScore(long bestScore) {
		this.bestScore = bestScore;
	}

	public long getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(long gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public long getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(long questionsTotalAsked) {
		this.totalQuestions = questionsTotalAsked;
	}

	public long getValidatedQuestions() {
		return validatedQuestions;
	}

	public void setValidatedQuestions(long questionsValidated) {
		this.validatedQuestions = questionsValidated;
	}

	public String getLastGame() {
		return lastGame;
	}

	public void setLastGame(String lastGame) {
		this.lastGame = lastGame;
	}

	public String getCategoriesSelected() {
		return categoriesSelected;
	}

	public void setCategoriesSelected(String categoriesSelected) {
		this.categoriesSelected = categoriesSelected;
	}

	public String getDifficultiesSelected() {
		return difficultiesSelected;
	}

	public void setDifficultiesSelected(String difficultiesSelected) {
		this.difficultiesSelected = difficultiesSelected;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public String getBonus1() {
		return bonus.split("-/-")[0];
	}

	public String getBonus2() {
		return bonus.split("-/-")[1];
	}

	public String getBonus3() {
		return bonus.split("-/-")[2];
	}

	public void incrementBonus1() {

	}
}
