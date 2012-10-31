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
import org.blrdroid.droidcon2012.SlotItemsListAdapter;
import org.blrdroid.droidcon2012.data.BarcampBangalore;
import org.blrdroid.droidcon2012.data.BarcampData;
import org.blrdroid.droidcon2012.data.Slot;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import org.blrdroid.droidcon2012.R;

public class SlotDetailsActivity extends DroidconActivityBaseClass {

	public static String EXTRA_POS = "pos";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slot_details_list);

		DroidconUtils.createActionBarOnActivity(this);
		DroidconUtils.addNavigationActions(this);

		BarcampData data = ((BarcampBangalore) getApplicationContext())
				.getBarcampData();

		if (data == null) {
			finish();
			return;
		}

		int pos = getIntent().getIntExtra(EXTRA_POS, 0);

		if (data.slotsArray.size() < pos) {
			finish();
			return;
		}
		Slot slot = data.slotsArray.get(pos);
		if (slot.sessionsArray == null || slot.sessionsArray.isEmpty()) {
			(findViewById(R.id.listView1)).setVisibility(View.GONE);
			(findViewById(R.id.no_slot_text_view)).setVisibility(View.VISIBLE);
			return;
		}

		ListView listview = (ListView) findViewById(R.id.listView1);
		listview.setBackgroundResource(R.drawable.b2);
		SlotItemsListAdapter adapter = new SlotItemsListAdapter(this,
				R.layout.session_list_item, slot.sessionsArray);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Intent intent = new Intent(SlotDetailsActivity.this,
						SessionDetailsActivity.class);
				intent.putExtra(SessionDetailsActivity.EXTRA_SESSION_POSITION,
						pos);
				intent.putExtra(SessionDetailsActivity.EXTRA_SLOT_POS,
						getIntent().getIntExtra(EXTRA_POS, 0));
				startActivity(intent);
			}
		});

	}

}
