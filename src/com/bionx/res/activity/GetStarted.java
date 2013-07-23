package com.bionx.res.activity;

import java.io.IOException;
import java.io.InputStream;

import com.bionx.res.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

//First Time User Activity
public class GetStarted extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_log);

		((Button) findViewById(R.id.okButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});

		loadChangeLog();
	}

	private void loadChangeLog() {
		TextView tv = (TextView) findViewById(R.id.logText);
		StringBuilder sb = new StringBuilder("");

		try {
			InputStream isr = getResources().openRawResource(R.raw.user);
			int c;
			while ((c = isr.read()) != -1)
				sb.append((char) c);
			isr.close();

		} catch (IOException e) {
		}

		tv.setText(sb.toString());
	}
}
