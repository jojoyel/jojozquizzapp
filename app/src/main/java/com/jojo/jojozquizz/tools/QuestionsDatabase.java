package com.jojo.jojozquizz.tools;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jojo.jojozquizz.model.Question;

@Database(entities = {Question.class}, version = 1, exportSchema = false)
public abstract class QuestionsDatabase extends RoomDatabase {

	private static QuestionsDatabase database;
	private static final String DB_NAME = "questions_db";

	public abstract QuestionDAO QuestionDAO();

	public static synchronized QuestionsDatabase getInstance(Context context) {
		if (database == null) {
			database = Room.databaseBuilder(context.getApplicationContext(),
				QuestionsDatabase.class, DB_NAME)
				.allowMainThreadQueries()
				.fallbackToDestructiveMigration()
				.build();
		}
		return database;
	}
}