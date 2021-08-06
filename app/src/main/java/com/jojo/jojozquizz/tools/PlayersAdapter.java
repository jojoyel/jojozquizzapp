package com.jojo.jojozquizz.tools;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jojo.jojozquizz.PlayersInformationActivity;
import com.jojo.jojozquizz.R;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.UsersViewHolder> {

	private Context context;
	private int[] mIds;
	private String[] mName;
	private long[] mScore;

	public PlayersAdapter(Context ct, int[] usersIds, String[] usersNames, long[] usersScores) {
		this.context = ct;
		this.mIds = usersIds;
		this.mName = usersNames;
		this.mScore = usersScores;
	}

	@NonNull
	@Override
	public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.users_recycler_layout, parent, false);
		return new UsersViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
		holder.textName.setText(mName[position]);
		holder.textScore.setText(String.valueOf(mScore[position]));
		holder.mConstraintLayout.setOnClickListener(v -> context.startActivity(new Intent(context, PlayersInformationActivity.class).putExtra("id", mIds[position])));
	}

	@Override
	public int getItemCount() {
		return mName.length;
	}

	public static class UsersViewHolder extends RecyclerView.ViewHolder {

		TextView textId, textName, textScore;
		ConstraintLayout mConstraintLayout;

		public UsersViewHolder(@NonNull View v) {
			super(v);

			mConstraintLayout = v.findViewById(R.id.user_layout);

			textName = v.findViewById(R.id.user_name);
			textScore = v.findViewById(R.id.user_score);
		}
	}
}
