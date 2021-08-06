package com.jojo.jojozquizz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ThanksActivity extends AppCompatActivity {

	private ConstraintLayout mConstraint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thanks);

		mConstraint = findViewById(R.id.activity_thanks_constraint);

		mConstraint.setOnClickListener(v -> finish());
	}
}
