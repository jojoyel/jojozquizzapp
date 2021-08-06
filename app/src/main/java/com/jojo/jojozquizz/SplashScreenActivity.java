package com.jojo.jojozquizz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

	private boolean firstTime = true;

	@Override
	protected void onResume() {
		super.onResume();
		if (!firstTime) {
			SplashScreenActivity.this.finish();
			System.exit(0);
		} else firstTime = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		new Handler().postDelayed(() -> startActivity(new Intent(SplashScreenActivity.this, MainActivity.class)), 3000);
	}
}
