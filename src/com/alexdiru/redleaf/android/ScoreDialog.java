package com.alexdiru.redleaf.android;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.alexdiru.redleaf.R;
import com.alexdiru.redleaf.Utils;
import com.alexdiru.redleaf.UtilsString;

public abstract class ScoreDialog {

	public static void show(final int score) {
		Utils.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(Utils.getActivity());
				
				builder.setNeutralButton(Utils.getActivity().getString(R.string.game_scoredialog_ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Utils.getActivity().finish();
					}
				});
				
				
				//Set the title and body of the dialog
				StringBuilder sb = UtilsString.getStringBuilder();
				sb.append( Utils.getActivity().getString(R.string.game_scoredialog_message));
				UtilsString.appendInteger(score);
				
				builder.setTitle(Utils.getActivity().getString(R.string.game_scoredialog_title));
				builder.setMessage(sb.toString());
				
				//Create and show the dialog
				AlertDialog dialog = builder.create();
				
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}
		});
	}
}
