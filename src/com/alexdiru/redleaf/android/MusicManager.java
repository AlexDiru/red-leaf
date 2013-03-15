package com.alexdiru.redleaf.android;

import com.alexdiru.redleaf.Utils;

import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

public class MusicManager implements OnPreparedListener {

	public MediaPlayer mMediaPlayer;
	public int mPauseTime;
	public boolean mStarted;
	private long mSongLength;

	public MusicManager() {
		
		String encryptedFilePath = Utils.getCurrentSong().mMusicFile;
		Utils.getCurrentSong().mMusicManager = this;
		
		byte[] decryptedMusicBytes;
		// Decrypt the music
		try {
			/* decryptedMusicBytes = HelperCrypto.decryptFile(encryptedFilePath); File tempMp3 =
			 * File.createTempFile("DATA", "mp3", Helper.getActivity().getCacheDir());
			 * tempMp3.deleteOnExit(); FileOutputStream stream = new FileOutputStream(tempMp3);
			 * stream.write(decryptedMusicBytes); stream.close();
			 * 
			 * mMediaPlayer = new MediaPlayer(); mMediaPlayer.setDataSource(stream.getFD());
			 * mMediaPlayer.prepare(); mMediaPlayer.start(); */

			mMediaPlayer = new MediaPlayer();

			AssetFileDescriptor descriptor = Utils.getActivity().getAssets().openFd(encryptedFilePath);

			mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();

			mStarted = false;
			mPauseTime = 0;
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			return;
		}
	}

	public int getPlayPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	public void play() {
		mMediaPlayer.start();
		mStarted = true;
	}

	public void pause() {
		if (mMediaPlayer != null)
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				mPauseTime = mMediaPlayer.getCurrentPosition();
			}
	}

	/** Resumes the media player if it is already playing */
	public void resume() {
		if (mStarted && mMediaPlayer != null) {
			mMediaPlayer.seekTo(mPauseTime);
			mMediaPlayer.start();
		}
	}

	/** Garbage collects the contents of this class */
	public void cleanup() {
		if (mMediaPlayer != null) {
			mPauseTime = 0;
			mStarted = false;
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d("MM", "onprepared");
		mp.start();
		mp.seekTo(mPauseTime);
		mStarted = true;
	}

	public int getLength() {
		return mMediaPlayer.getDuration();
	}

}
