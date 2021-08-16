package com.jojo.jojozquizz.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class FabAnimation {

	public static void init(final View v) {
		v.setVisibility(View.INVISIBLE);
		v.setTranslationY(v.getHeight());
		v.setAlpha(0f);
	}

	public static void showIn(final View v, int position) {
		v.setVisibility(View.VISIBLE);
		v.setAlpha(0f);
		v.setTranslationY(v.getHeight());
		v.animate()
			.setStartDelay((100L * position) - 100)
			.setDuration(200)
			.translationY(0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
				}
			}).alpha(1f)
			.start();
	}

	public static void showOut(final View v, int position) {
		v.setVisibility(View.VISIBLE);
		v.setAlpha(1f);
		v.setTranslationY(0);
		v.animate()
			.setStartDelay((100L * position) - 100)
			.setDuration(200)
			.translationY(v.getHeight())
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					v.setVisibility(View.INVISIBLE);
					super.onAnimationEnd(animation);
				}
			}).alpha(0f)
			.start();
	}

	public static void fadeAndTurnIn(final View v, int position) {
		v.setVisibility(View.VISIBLE);
		v.setAlpha(0f);
		v.setRotation(360f);
		v.animate()
			.setStartDelay((100L * position) - 100)
			.setDuration(200)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
				}
			}).alpha(1f)
			.rotation(0f)
			.start();
	}

	public static void fadeAndTurnOut(final View v, int position) {
		v.setVisibility(View.VISIBLE);
		v.setAlpha(1f);
		v.setRotation(0f);
		v.animate()
			.setStartDelay((100L * position) - 100)
			.setDuration(200)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					v.setVisibility(View.INVISIBLE);
					super.onAnimationEnd(animation);
				}
			}).alpha(.5f)
			.rotation(360f)
			.start();
	}

	public static boolean rotateFab(final View v, boolean rotate) {
		v.animate().setDuration(200)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
				}
			})
			.rotation(rotate ? 45f : 0);
		return rotate;
	}
}
