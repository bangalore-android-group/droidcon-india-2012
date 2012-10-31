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

import org.blrdroid.droidcon2012.DroidconUtils;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import org.blrdroid.droidcon2012.R;

public class AboutActivity extends DroidconActivityBaseClass {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_screen);

		DroidconUtils.addNavigationActions(this);
		DroidconUtils.createActionBarOnActivity(this);

		((ImageView) findViewById(R.id.imageView1))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent webIntent = new Intent(Intent.ACTION_VIEW);
						webIntent.setData(Uri
								.parse("http://barcampbangalore.org"));
						startActivity(webIntent);

					}
				});

	}

}
