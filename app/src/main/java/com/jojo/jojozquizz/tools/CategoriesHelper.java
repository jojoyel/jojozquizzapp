package com.jojo.jojozquizz.tools;

import android.content.Context;

import com.jojo.jojozquizz.R;

import java.util.ArrayList;
import java.util.List;

public class CategoriesHelper {

	private Context context;
	private final String[] categories;
	private final String[] difficulties;

	public CategoriesHelper(Context ct) {
		this.context = ct;
		this.categories = ct.getResources().getStringArray(R.array.categories);
		this.difficulties = ct.getResources().getStringArray(R.array.difficulties);
	}

	public String processCategories(List<String> categoriesGiven) {
		StringBuilder valueToReturn = new StringBuilder();

		for (String category : categories) {
			if (categoriesGiven.contains(category))
				valueToReturn.append("1");
			else
				valueToReturn.append("0");
		}

		return valueToReturn.toString();
	}

	public List<String> getProcessedCategories(String processedCategories) {

		String[] array = processedCategories.split("");

		List<String> valuesToReturn = new ArrayList<>();

		int i = 0;

		for (String character : array) {
			if (character.equals("1"))
				valuesToReturn.add(categories[i]);
			i++;
		}

		return valuesToReturn;
	}

	public String processDifficulties(List<String> difficultiesGiven) {

		StringBuilder valueToReturn = new StringBuilder();

		for (String difficulty : difficulties) {
			if (difficultiesGiven.contains(difficulty))
				valueToReturn.append("1");
			else
				valueToReturn.append("0");
		}

		return valueToReturn.toString();
	}

	public List<String> getProcessedDifficulties(String processedDifficulties) {

		String[] array = processedDifficulties.split("");

		List<String> valuesToReturn = new ArrayList<>();

		int i = 0;

		for (String character : array) {
			if (character.equals("1"))
				valuesToReturn.add(difficulties[i]);
			i++;
		}

		return valuesToReturn;
	}

	public String getAllCategoriesProcessed() {
		StringBuilder valueToReturn = new StringBuilder();

		for (String ignored : this.categories) {
			valueToReturn.append("1");
		}

		return valueToReturn.toString();
	}

	public String getNoneCategoriesProcessed() {
		StringBuilder valueToReturn = new StringBuilder();

		for (String ignored : this.categories) {
			valueToReturn.append("0");
		}

		return valueToReturn.toString();
	}

	public String getAllDifficultiesProcessed() {
		StringBuilder valueToReturn = new StringBuilder();

		for (String ignored : this.difficulties) {
			valueToReturn.append("1");
		}

		return valueToReturn.toString();
	}

	public String getNoneDifficultiesrocessed() {
		StringBuilder valueToReturn = new StringBuilder();

		for (String ignored : this.difficulties) {
			valueToReturn.append("0");
		}

		return valueToReturn.toString();
	}

	public String[] getCategories() {
		return this.categories;
	}

	public String[] getDifficulties() {
		return this.difficulties;
	}
}
