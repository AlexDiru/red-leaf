package com.alexdiru.redleaf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class UtilsFileIO {

	public static ArrayList<String> readAllLines(String filePath) {
		BufferedReader br;

		try {
			br = new BufferedReader(new InputStreamReader(Utils.getActivity().getAssets().open(filePath)));

			ArrayList<String> lines = new ArrayList<String>();
			String line;

			while ((line = br.readLine()) != null)
				lines.add(line);

			br.close();

			return lines;
		} catch (IOException ex) {
			return null;
		}
	}

	public static byte[] getByteStream(String filePath) {
		try {
			InputStream input = Utils.getActivity().getAssets().open(filePath);

			byte[] stream = new byte[input.available()];
			input.read(stream);
			input.close();

			return stream;
		} catch (IOException ex) {
			return null;
		}
	}
	
	public static String getFileType(String filename) {
		String type = "";
		
		for (int i = filename.length() - 1; i >= 0; i--)
			if (filename.charAt(i) == '.')
				break;
			else
				type += filename.charAt(i);
		
		return new StringBuffer(type).reverse().toString();
	}

	/**
	 * Returns a list of all the mp3 files in the assets folder
	 */
	public static ArrayList<String> getMp3Assets() {
		ArrayList<String> mp3Files = new ArrayList<String>();
		
		String[] fileList;
		try {
			fileList = Utils.getActivity().getAssets().list("");
		} catch (IOException e) {
			return mp3Files;
		}
		
		for (String file : fileList)
			if (getFileType(file).equals("mp3"))
				mp3Files.add(file);
		
		return mp3Files;
	}
}
