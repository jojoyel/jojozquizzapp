package com.jojo.jojozquizz.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.jojo.jojozquizz.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

	private final Context mContext;
	private final String[] mCategories;
	private List<String> mCategoriesSelected;

	private final String[] mCategoriesChecked;

	private CategoriesCheckListener mCheckListener;

	public CategoriesAdapter(Context ct, String[] categories, List<String> categoriesSelected) {
		this.mContext = ct;
		this.mCategories = categories;
		this.mCategoriesSelected = categoriesSelected;
		mCategoriesChecked = this.mCategories;
	}

	public void changeCategoriesChecked(List<String> newCategories) {
		mCategoriesSelected = newCategories;
	}

	@NonNull
	@Override
	public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.categories_recycler_layout, parent, false);

		mCheckListener = (CategoriesCheckListener) mContext;
		return new CategoriesViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
		MaterialCheckBox currentCheckbox = holder.mCheckBox;

		currentCheckbox.setText(mCategories[position]);
		currentCheckbox.setButtonTintList(ContextCompat.getColorStateList(mContext, position % 2 == 0 ? R.color.colorSecondary : R.color.colorSecondaryDark));
		currentCheckbox.setChecked(mCategoriesSelected.contains(currentCheckbox.getText().toString()));

		holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			String text = String.valueOf(buttonView.getText());
			mCheckListener.checkChanged(text, isChecked);
		});
	}

	@Override
	public int getItemCount() {
		return mCategories.length;
	}

	public static class CategoriesViewHolder extends RecyclerView.ViewHolder {
		MaterialCheckBox mCheckBox;
		ConstraintLayout mConstraintLayout;

		public CategoriesViewHolder(@NonNull View v) {
			super(v);

			mConstraintLayout = v.findViewById(R.id.categories_layout);
			mCheckBox = v.findViewById(R.id.checkbox);
		}
	}

	public interface CategoriesCheckListener {
		void checkChanged(String category, boolean isChecked);
	}
}
