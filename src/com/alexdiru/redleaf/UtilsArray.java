package com.alexdiru.redleaf;

public class UtilsArray {

	/*
	 * Concatenates an array of strings into a single string
	 */
	public static String concatArray(String[] arr, String delimitor, int startIndex) {
		String concatenation = "";
		for (int i = startIndex; i < arr.length; i++) {
			if (i != arr.length - 1)
				concatenation += arr[i] + delimitor;
			else
				concatenation += arr[i];
		}
		return concatenation;
	}
	
	public static String[] trimAll(String[] arr) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = arr[i].trim();
		return arr;
	}
}
