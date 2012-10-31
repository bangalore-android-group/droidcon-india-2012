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
import org.blrdroid.droidcon2012.MessagesListAdapter;
import org.blrdroid.droidcon2012.R;
import org.blrdroid.droidcon2012.data.DroidconUpdatesMessage;
import org.blrdroid.droidcon2012.database.MessagesDataSource;

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class UpdateMessagesActivity extends DroidconActivityBaseClass {

	public static final String FROM_NOTIFICATION = "fromNotification";
	public static String EXTRA_POS = "pos";
	MessageLoadingTask task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slot_details_list);

		DroidconUtils.createActionBarOnActivity(this);
		DroidconUtils.addNavigationActions(this);
		if (getIntent().getBooleanExtra(FROM_NOTIFICATION, false)) {
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		task = new MessageLoadingTask();
		task.execute();
	}

	class MessageLoadingTask extends
			AsyncTask<Void, Void, List<DroidconUpdatesMessage>> {

		@Override
		protected List<DroidconUpdatesMessage> doInBackground(Void... params) {
			MessagesDataSource ds = new MessagesDataSource(
					UpdateMessagesActivity.this);
			ds.open();
			List<DroidconUpdatesMessage> list = ds.getAllMessages();
			Collections.reverse(list);
			ds.close();
			return list;
		}

		@Override
		protected void onPostExecute(List<DroidconUpdatesMessage> result) {
			super.onPostExecute(result);
			ListView listview = (ListView) findViewById(R.id.listView1);
			listview.setBackgroundResource(R.drawable.b2);
			MessagesListAdapter adapter = new MessagesListAdapter(
					UpdateMessagesActivity.this, R.layout.updates_list_item,
					result);
			listview.setAdapter(adapter);
		}

	}
}
