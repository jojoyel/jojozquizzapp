package com.jojo.jojozquizz.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class QuestionBank {
	private List<Question> mQuestionList;
	public int mNextQuestionIndex;

	public QuestionBank() {
		mQuestionList = new LinkedList<>();
		mNextQuestionIndex = 0;
	}

	public QuestionBank(List<Question> questionList, boolean shuffle) {
		mQuestionList = questionList;
		if (shuffle)
			Collections.shuffle(mQuestionList);
		mNextQuestionIndex = 0;
	}

	public Question getNextQuestion() {
		if (mNextQuestionIndex == mQuestionList.size())
			mNextQuestionIndex = 0;

		return mQuestionList.get(mNextQuestionIndex++);
	}

	public Question getQuestion(int i) {
		return mQuestionList.get(i);
	}

	public List<Question> getAllQuestions() {
		return mQuestionList;
	}

	public void reShuffle() {
		Collections.shuffle(mQuestionList);
	}

	public void addQuestion(Question question) {
		mQuestionList.add(question);
	}

	public void addQuestionsList(List<Question> questions) {
		mQuestionList.addAll(questions);
	}

	public int returnListSize() {
		return mQuestionList.size();
	}
}
