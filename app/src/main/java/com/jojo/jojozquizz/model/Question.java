package com.jojo.jojozquizz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "questions")
public class Question implements Serializable {

	@PrimaryKey()
	@ColumnInfo(name = "id")
	private long id;

	@ColumnInfo(name = "question")
	private String mQuestion;

	@Ignore
	private List<String> mChoiceList;

	@ColumnInfo(name = "choices")
	private String mChoices;

	@ColumnInfo(name = "answer_index")
	private int mAnswerIndex;

	@ColumnInfo(name = "categorie")
	private int mCategory;

	@Ignore
	private String mTrueAnswer;

	@ColumnInfo(name = "difficulty")
	private int mDifficulty;

	public Question() {
	}

	public Question(int id, String mQuestion, String mChoices, int mCategorie, int mDifficulty) {
		this.id = id;
		this.mQuestion = mQuestion;
		this.mChoices = mChoices;
		this.mAnswerIndex = 0;
		this.mCategory = mCategorie;
		this.mDifficulty = mDifficulty;
	}

	public Question(int id, String mQuestion, List<String> mChoiceList, int mCategorie, int mDifficulty) {
		this.id = id;
		this.mQuestion = mQuestion;
		this.mChoices = mChoiceList.get(0) + "-/-" + mChoiceList.get(1) + "-/-" + mChoiceList.get(2) + "-/-" + mChoiceList.get(3);
		this.mAnswerIndex = 0;
		this.mCategory = mCategorie;
		this.mDifficulty = mDifficulty;
	}

	public Question(String mQuestion, List<String> mChoiceList, int mCategorie, int mDifficulty) {
		this.mQuestion = mQuestion;
		this.mChoices = mChoiceList.get(0) + "-/-" + mChoiceList.get(1) + "-/-" + mChoiceList.get(2) + "-/-" + mChoiceList.get(3);
		this.mAnswerIndex = 0;
		this.mCategory = mCategorie;
		this.mDifficulty = mDifficulty;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getQuestion() {
		return mQuestion;
	}

	public void setQuestion(String question) {
		mQuestion = question;
	}

	public List<String> getChoiceList() {
		return mChoiceList;
	}

	public void setChoiceList(List<String> choiceList) {
		mChoiceList = choiceList;
	}

	public String getChoices() {
		return mChoices;
	}

	public void setChoices(String choices) {
		mChoices = choices;
	}

	public void setAnswerIndex(int mAnswerIndex) {
		this.mAnswerIndex = mAnswerIndex;
	}

	public int getAnswerIndex() {
		return mAnswerIndex;
	}

	public int getCategory() {
		return mCategory;
	}

	public void setCategory(int categorie) {
		mCategory = categorie;
	}

	public String getTrueAnswer() {
		return mTrueAnswer;
	}

	public void setTrueAnswer(String trueAnswer) {
		mTrueAnswer = trueAnswer;
	}

	public int getDifficulty() {
		return mDifficulty;
	}

	public void setDifficulty(int difficulty) {
		mDifficulty = difficulty;
	}
}
