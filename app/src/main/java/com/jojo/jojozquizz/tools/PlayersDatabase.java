package com.jojo.jojozquizz.tools;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jojo.jojozquizz.model.Player;

@Database(entities = {Player.class}, version = 1, exportSchema = false)
public abstract class PlayersDatabase extends RoomDatabase {

	private static PlayersDatabase database;
	private static final String DB_NAME = "players_db";

	public abstract PlayersDAO PlayersDAO();

	public static synchronized PlayersDatabase getInstance(Context context) {
		if (database == null) {
			database = Room.databaseBuilder(context.getApplicationContext(),
				PlayersDatabase.class, DB_NAME)
				.allowMainThreadQueries()
				.fallbackToDestructiveMigration()
				.build();
		}
		return database;
	}
}
