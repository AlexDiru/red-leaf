package com.alexdiru.redleaf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public abstract class UtilsStepmaniaConvertor {
	

	public static DataSong stepmaniaToRedLeaf(String inputFilePath, DataSong.DataSongDifficulty difficulty) {
		ArrayList<String> lines = UtilsFileIO.readAllLines(inputFilePath);
		DataSong convertedSong = new DataSong();

		int currentDifficulty = 0;
		boolean notesMode = false;
		
		// Notes for each difficulty (3 difficulties)
		ArrayList<DataNote>[] notes = new ArrayList[3];
		notes[0] = new ArrayList<DataNote>();
		notes[1] = new ArrayList<DataNote>();
		notes[2] = new ArrayList<DataNote>();
		
		//BPM conversion
		Map<Float, Float> beatsToBPM = new HashMap<Float, Float>();
		float currentBPM = 60;
		int currentTimeMS = 0;
		int noteGroupCount = 0;

		float beatsPerMillisecond = (currentBPM / 60) * 1000;
		
		for (int i  =0; i <lines.size(); i++) {
			String line = lines.get(i);
			
			if (line == null)
				continue;
			
			if (i == 49)
				Log.d("n","n");
			
			try {
				if (notesMode) {
					
					
					ArrayList<String> noteLines = new ArrayList<String>();
					
					//Read until comma
					while (!line.equals("") && line.charAt(0) != ','){
						
						
						if (line.contains(";"))
						{
							notesMode = false;
							break;
						}
						
						if (!line.contains("/"))
							noteLines.add(line);
						line = lines.get(++i);
					}
					
					if (!notesMode)
						continue;
				
					for (int n = 0; n < noteLines.size(); n++)
					{
						for (int c = 0; c < noteLines.get(n).length(); c++) {
							switch (noteLines.get(n).charAt(c)){
								default:
								case '0':
									break;
								case '1':
								case '2':
									notes[currentDifficulty].add(new DataNote(currentTimeMS + (int)(((beatsPerMillisecond * n)/noteLines.size())), 0, 0, c ));
									break;
							}
						}
					}
					
					noteGroupCount++;
					
					currentTimeMS += (60/currentBPM)*1000;
					
					continue;
				}
			
				switch (line.charAt(0)) {
				case '#':
					if (line.equals("#NOTES:"))
						notesMode = true;
					else if (line.substring(0, 6).equals("#TITLE"))
						convertedSong.mSongName = line.substring(6, line.length() - 1);
					else if (line.substring(0, 7).equals("#ARTIST"))
						convertedSong.mArtistName = line.substring(7, line.length() - 1);
					else if (line.substring(0, 6).equals("#MUSIC"))
						convertedSong.mMusicFile = line.substring(6, line.length() - 1);
					else if (line.substring(0, 5).equals("#BPMS")) {
						/* Example text 
						 * #BPMS:0.000000=181.699997
							,44.000000=363.399994
							,47.000000=726.799988
							,48.000000=181.699997
							,300.000000=363.399994
							,304.000000=181.699997
							;
						 */
						beatsToBPM.clear();
						noteGroupCount = 0;
						String bpmCSV = line.substring(7, line.length());

						line = lines.get(++i);
						
						//Read until hash detected
						while (line.equals("") || line.charAt(0) != '#'){
							bpmCSV += line;
						}
						
						//Remove semicolon
						bpmCSV.replace(";", "").replace("\n", "");
						
						String[] bpmValues = bpmCSV.split("\\,");
						 
						for (String bpmValue : bpmValues) {
							String[] equationSides = bpmValue.split("\\=");
							
							beatsToBPM.put(Float.parseFloat(equationSides[0]), Float.parseFloat(equationSides[1]));
							currentBPM = Float.parseFloat(equationSides[1]);
						}
						
						//Return to previous line
						i--;
						continue;
					}
					else if (line.substring(0, 11).equals("#DIFFICULTY")) {
						currentTimeMS = 0;
						String difficultyName = line.substring(12, line.length() - 1);
						if (difficultyName.equals("Easy"))
							currentDifficulty = 0;
						else if (difficultyName.equals("Hard"))
							currentDifficulty = 2;
						else
							currentDifficulty = 1;
					} 
					else if (line.substring(0, 9).equals("#NOTEDATA"))
						notesMode = false;

					break;
				case '/':
					break;
				case ';':
					break;
				case ',':
					break;
				default:
					break;
				}
			} catch (Exception ex) {
			}
		}
		
		convertedSong.mNotes = notes[difficulty.ordinal()];
		convertedSong.mMusicFile = "New Game.mp3";
		return convertedSong;
	}

}
