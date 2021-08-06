package com.jojo.jojozquizz.tools;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jojo.jojozquizz.model.Question;

import java.util.List;

@Dao
public interface QuestionDAO {

	@Query(value = "SELECT * FROM questions")
	List<Question> getAllQuestions();

	@Query(value = "SELECT * FROM questions WHERE categorie = :cat AND difficulty = :diff")
	List<Question> getQuestionsWithCatsAndDiffs(int cat, int diff);

	@Query(value = "SELECT * FROM questions WHERE categorie = :cat")
	List<Question> getQuestionsWithCats(int cat);

	@Query(value = "SELECT * FROM questions WHERE id = :id")
	Question getOneQuestion(int id);

	@Query(value = "SELECT * FROM questions ORDER BY id DESC LIMIT 1")
	Question getLastQuestion();

	@Query("DELETE FROM questions")
	void deleteTable();

	@Insert
	void addQuestion(Question question);

	@Update
	int updateQuestion(Question question);

	@Delete
	int deleteQuestion(Question question);
}
