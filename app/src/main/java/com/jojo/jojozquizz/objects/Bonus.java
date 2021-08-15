package com.jojo.jojozquizz.objects;

public class Bonus {

	private String name;

	private int number;

	private boolean alreadyUse;

	public Bonus() {
	}

	public Bonus(int number, String type) {
		this.number = number;
		this.name = type;
		setAlreadyUse(false);
	}

	public String getName() {
		return name;
	}

	public void setName(String type) {
		this.name = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setAlreadyUse(boolean alreadyUse) {
		this.alreadyUse = alreadyUse;
	}

	public boolean isAlreadyUse() {
		return alreadyUse;
	}
}
