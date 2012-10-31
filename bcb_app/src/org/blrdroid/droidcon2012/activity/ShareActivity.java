/*
 * Copyright (C) 2012 Saurabh Minni <http://100rabh.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.blrdroid.droidcon2012.activity;

import org.blrdroid.droidcon2012.DroidconSharedPrefUtils;
import org.blrdroid.droidcon2012.DroidconUtils;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.blrdroid.droidcon2012.R;

public class ShareActivity extends DroidconActivityBaseClass {
	public static final String SHARE_STRING = "Share String";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_screen);
		DroidconUtils.createActionBarOnActivity(this);
		DroidconUtils.addNavigationActions(this);
		((EditText) findViewById(R.id.editText1))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
						((TextView) findViewById(R.id.charsLeftTextView))
								.setText("Chars left: "
										+ (140 - s.length() - 7));
					}
				});
		if (getIntent().hasExtra(SHARE_STRING)) {
			((EditText) findViewById(R.id.editText1)).setText(getIntent()
					.getStringExtra(SHARE_STRING));
		}
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		final PackageManager pm = getPackageManager();
		final Spinner spinner = (Spinner) findViewById(R.id.shareTypeSpinner);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
		String selectedItem = DroidconSharedPrefUtils
				.getShareSettings(getApplicationContext());
		int selectedPos = -1;
		for (ResolveInfo info : matches) {
			adapter.add(info.loadLabel(pm));
			if (selectedItem.equals(info.loadLabel(pm))) {
				selectedPos = matches.indexOf(info);
			}
		}
		spinner.setAdapter(adapter);

		if (selectedPos != -1) {
			spinner.setSelected(true);
			spinner.setSelection(selectedPos);
		}
		((TextView) findViewById(R.id.charsLeftTextView))
				.setText("Chars left: 140");
		((Button) findViewById(R.id.button1))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						int appSelectedPos = spinner.getSelectedItemPosition();
						ResolveInfo info = matches.get(appSelectedPos);
						intent.setClassName(info.activityInfo.packageName,
								info.activityInfo.name);

						DroidconSharedPrefUtils.setShareSettings(
								getApplicationContext(),
								(String) info.loadLabel(pm));
						intent.putExtra(Intent.EXTRA_TEXT,
								((EditText) findViewById(R.id.editText1))
										.getText().toString() + " #Droidcon12");
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						startActivity(intent);
						finish();

					}
				});

	}
}
