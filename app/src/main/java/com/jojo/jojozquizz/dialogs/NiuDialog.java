package com.jojo.jojozquizz.dialogs;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import com.jojo.jojozquizz.R;

public class NiuDialog {
	public void showDialog(Activity activity) {
		ViewGroup viewGroup = activity.findViewById(android.R.id.content);
		View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_niu, viewGroup, false);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(dialogView);
		builder.setPositiveButton(activity.getResources().getString(R.string.all_ok), (dialog, which) -> {
		});
		final AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
}
