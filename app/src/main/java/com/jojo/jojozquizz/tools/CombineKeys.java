package com.jojo.jojozquizz.tools;

public class CombineKeys {

	public static String combineKeys(String k1, String k2) {
		if (!(checkKeyValidity(k1) && checkKeyValidity(k2) && k1.length() == k2.length())) {
			return "invalid keys";
		}

		int serverKeyNumber = Character.getNumericValue(k2.charAt(k2.length() - 2));
		StringBuilder result = new StringBuilder();
		StringBuilder resultKey1 = new StringBuilder();
		StringBuilder resultKey2 = new StringBuilder();

		for (int i = 0; i < k1.length(); i++) {
			char k1Char = k1.charAt(i);
			char k2Char = k2.charAt(i);
			if (Character.isDigit(k1Char)) {
				int calc;
				int intChar = Character.getNumericValue(k1Char);

				calc = intChar + serverKeyNumber;
				if (isEven(serverKeyNumber)) resultKey1.append(calc % 10);
				else result.append(calc % 10);
			} else if (Character.isLetter(k1Char)) {
				char changedChar;
				changedChar = Character.isLowerCase(k1Char) && isEven(serverKeyNumber) ? Character.toUpperCase(k1Char) : Character.toLowerCase(k1Char);
				if (isEven(serverKeyNumber)) resultKey1.append(changedChar);
				else result.append(changedChar);
			}

			if (Character.isDigit(k2Char)) {
				int calc;
				int intChar = Character.getNumericValue(k2Char);

				calc = intChar + serverKeyNumber;
				if (isEven(serverKeyNumber)) resultKey2.append(calc % 10);
				else result.append(calc % 10);
			} else if (Character.isLetter(k2Char)) {
				char changedChar;
				changedChar = Character.isLowerCase(k2Char) && isEven(serverKeyNumber) ? Character.toUpperCase(k2Char) : Character.toLowerCase(k2Char);
				if (isEven(serverKeyNumber)) resultKey2.append(changedChar);
				else result.append(changedChar);
			}
		}

		if (isEven(serverKeyNumber)) {
			resultKey1.deleteCharAt(resultKey1.length() - 1);
			resultKey2.delete(resultKey2.length() - 2, resultKey2.length());
			int i = 0;
			for (int j = resultKey2.length() - 1; j > -1; j--) {
				result.append(resultKey1.charAt(i));
				result.append(resultKey2.charAt(j));
				i++;
			}
			result.append(resultKey1.charAt(i));
		}

		return result.toString();
	}

	private static boolean checkKeyValidity(String k) {
		return k.length() > 10 && k.charAt(k.length() - 1) == 'j';
	}

	private static boolean isEven(int i) {
		return i % 2 == 0;
	}
}