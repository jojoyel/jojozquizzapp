package com.jojo.jojozquizz.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.jojo.jojozquizz.R;

public class NameDialog extends AppCompatDialogFragment {

	private EditText mNewName;

	public static String REGEX = "^[a-zA-Z0-9]{1}[a-zA-Z0-9-\\s]{1,14}$";

	private boolean isNewUser;
	private boolean isCancelable = false;

	private NameDialogListener mainListener;

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);

		try {
			mainListener = (NameDialogListener) context;
		} catch (Exception e) {
			throw new ClassCastException(context.toString() + " you should implement NameDialogListener");
		}
	}

	public void setIsNewUser(boolean is) {
		isNewUser = is;
	}

	public void setIsCancelable(boolean is) {
		isCancelable = is;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_name, null);

		setCancelable(isCancelable);

		TextView title = view.findViewById(R.id.dialog_progress_title);

		title.setText(isNewUser ? R.string.new_user_name_dialog_title : R.string.whats_your_name);

		builder.setView(view)
			.setTitle(isNewUser ? R.string.string_welcome : R.string.change_title_name_dialog)
			.setPositiveButton(R.string.text_confirm, (dialog, which) -> {
				String name = mNewName.getText().toString();
				mainListener.applyText(name);
			})
			.setIcon(R.drawable.plus_icon)
			.setCancelable(isCancelable);

		mNewName = view.findViewById(R.id.dialog_name_input);

		return builder.create();
	}

	public interface NameDialogListener {
		void applyText(String name);
	}
}