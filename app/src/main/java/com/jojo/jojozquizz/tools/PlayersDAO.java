package com.jojo.jojozquizz.tools;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jojo.jojozquizz.model.Player;

import java.util.List;

@Dao
public interface PlayersDAO {

	@Insert
	void addPlayer(Player player);

	@Update
	int updatePlayer(Player player);

	@Query(value = "SELECT * FROM players")
	List<Player> getAllPlayers();

	@Query(value = "SELECT * FROM players WHERE name = :name")
	Player getPlayerFromName(String name);

	@Query(value = "SELECT id FROM players WHERE name = :name")
	int getIdFromName(String name);

	@Query(value = "SELECT * FROM players WHERE id = :id")
	Player getPlayer(int id);

	@Query(value = "SELECT * FROM players LIMIT 1")
	Player getFirstPlayer();

	@Query(value = "UPDATE players SET name = :name WHERE id = :id")
	void changeName(int id, String name);

	@Query(value = "UPDATE players SET lastGame = :lastGame WHERE id = :id")
	void setLastGame(int id, String lastGame);

	@Query(value = "UPDATE players SET score = :score WHERE id = :id")
	void setScore(int id, long score);

	@Query(value = "UPDATE players SET gamesPlayed = :gamesPlayed WHERE id = :id")
	void setGamesPlayed(int id, long gamesPlayed);

	@Query(value = "UPDATE players SET totalQuestions = :questionsAnswered WHERE id = :id")
	void setTotalQuestionsAnswered(int id, long questionsAnswered);

	@Query(value = "UPDATE players SET bestScore = :score WHERE id = :id")
	void setBestScore(int id, long score);

	@Query(value = "UPDATE players SET validatedQuestions = :validatedQuestions WHERE id = :id")
	void setQuestionsValidated(int id, long validatedQuestions);

	@Query(value = "UPDATE players SET categoriesSelected = :categories WHERE id = :id")
	void setCategories(int id, String categories);

	@Query(value = "UPDATE players SET difficultiesSelected = :difficulties WHERE id = :id")
	void setDifficulties(int id, String difficulties);

	@Query(value = "UPDATE players SET bonus = :bonus WHERE id = :id")
	void setBonus(int id, String bonus);

	@Delete
	int deletePlayer(Player player);

	@Query(value = "DELETE FROM players WHERE id = :id")
	void deletePlayerWithId(int id);
}
