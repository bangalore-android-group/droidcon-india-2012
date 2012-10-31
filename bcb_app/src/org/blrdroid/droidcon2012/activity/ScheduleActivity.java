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

import static org.blrdroid.droidcon2012.gcm.CommonUtilities.SENDER_ID;

import java.util.List;

import org.blrdroid.droidcon2012.DroidconSharedPrefUtils;
import org.blrdroid.droidcon2012.DroidconUtils;
import org.blrdroid.droidcon2012.R;
import org.blrdroid.droidcon2012.SlotsListAdapter;
import org.blrdroid.droidcon2012.data.BarcampBangalore;
import org.blrdroid.droidcon2012.data.BarcampData;
import org.blrdroid.droidcon2012.data.Slot;
import org.blrdroid.droidcon2012.gcm.GCMUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

public class ScheduleActivity extends DroidconActivityBaseClass {

	private FetchScheduleAsyncTask task = null;
	private List<Slot> slotsArray;
	private SlotsListAdapter adapter;
	private static final int SHOW_ERROR_DIALOG = 100;
	private static final String Droidcon_DATA = "DroidconData";
	private static final String LIST_POS = "ListPos";
	public static final String FROM_NOTIFICATION = "FromNotification";
	public ListView listView;
	public View header;
	AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		listView = (ListView) findViewById(R.id.listView1);
		DroidconUtils.createActionBarOnActivity(this);
		DroidconUtils.addNavigationActions(this);
		// Log.d("DROIDCON", "test");
		// String[] zones = TimeZone.getAvailableIDs();
		// for (int i = 0; i < zones.length; i++) {
		// Log.d("DROIDCON", zones[i]);
		// }
		BarcampData data = ((BarcampBangalore) getApplicationContext())
				.getBarcampData();

		if (data == null && savedInstanceState != null
				&& savedInstanceState.containsKey(Droidcon_DATA)) {
			((BarcampBangalore) getApplicationContext())
					.setBarcampData((BarcampData) savedInstanceState
							.getSerializable(Droidcon_DATA));
		}

		ActionBar actionbar = (ActionBar) findViewById(R.id.actionBar1);
		actionbar.addAction(new Action() {

			@Override
			public void performAction(View arg0) {
				if (task == null) {
					findViewById(R.id.spinnerLayout)
							.setVisibility(View.VISIBLE);
					findViewById(R.id.infoText).setVisibility(View.GONE);
					findViewById(R.id.listView1).setVisibility(View.GONE);
					task = new FetchScheduleAsyncTask();
					task.execute();
				}
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		}, 0);

		if (!GCMUtils.isRegistered(this)) {
			Intent registrationIntent = new Intent(
					"com.google.android.c2dm.intent.REGISTER");
			// sets the app name in the intent
			registrationIntent.putExtra("app",
					PendingIntent.getBroadcast(this, 0, new Intent(), 0));
			registrationIntent.putExtra("sender", SENDER_ID);
			startService(registrationIntent);
		}

		if (getIntent().getBooleanExtra(FROM_NOTIFICATION, false)) {
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void addScheduleItems(List<Slot> slotsArray) {
		Log.d("DROIDCON", "slotsarray size- : " + slotsArray.size());
		listView.setBackgroundResource(R.drawable.b3);
		adapter = new SlotsListAdapter(this, slotsArray);
		this.slotsArray = slotsArray;
		LayoutInflater mInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		header = (LinearLayout) mInflater.inflate(R.layout.header, null);
		listView.addHeaderView(header);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				pos--;
				Log.d("DROIDCON", "position - : " + pos);
				if (ScheduleActivity.this.slotsArray.get(pos).type
						.equals(Slot.SESSION)) {
					Intent intent = new Intent(ScheduleActivity.this,
							SlotDetailsActivity.class);
					intent.putExtra(SlotDetailsActivity.EXTRA_POS, pos);
					startActivity(intent);
				}
			}
		});
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		switch (id) {
		case SHOW_ERROR_DIALOG:
			alertDialog.setCancelable(false);
			alertDialog.setTitle("Error!!!");
			alertDialog
					.setMessage("Connection Error!! Check if you have internet connectivity.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(SHOW_ERROR_DIALOG);
				}
			});
			break;
		}
		return alertDialog;
	}

	// protected void onStop()

	public class SlotItemClickListener implements OnClickListener {

		Slot slot;
		Activity context;

		public SlotItemClickListener(Activity context, Slot slot) {
			this.slot = slot;
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, SlotDetailsActivity.class);
			intent.putExtra(SlotDetailsActivity.EXTRA_POS, slot.pos);
			context.startActivity(intent);
		}
	}

	class FetchScheduleAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			return DroidconUtils
					.updateContextWithBarcampData(getApplicationContext());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!isCancelled()) {
				BarcampData data = ((BarcampBangalore) getApplicationContext())
						.getBarcampData();
				if (data != null) {
					updateViews(data);
				}
				if (!result) {
					// failure
					showDialog(SHOW_ERROR_DIALOG);
					findViewById(R.id.progressBar1).setVisibility(View.GONE);
				}
			}
			task = null;
		}

	}

	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(Droidcon_DATA,
				((BarcampBangalore) getApplicationContext()).getBarcampData());
		ListView listView = (ListView) findViewById(R.id.listView1);
		outState.putParcelable(LIST_POS, listView.onSaveInstanceState());
	}

	@Override
	protected void onStop() {
		if (this.task != null) {
			task.cancel(false);
		}
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(false);
		}

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		((ListView) findViewById(R.id.listView1))
				.onRestoreInstanceState(savedInstanceState
						.getParcelable(LIST_POS));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DroidconSharedPrefUtils.getScheduleUpdated(this)) {
			((BarcampBangalore) getApplicationContext()).setBarcampData(null);
			DroidconSharedPrefUtils.setScheduleUpdated(this, false);
		}
		BarcampData data = ((BarcampBangalore) getApplicationContext())
				.getBarcampData();
		if (listView.getHeaderViewsCount() > 0) {
			listView.removeHeaderView(header);
			listView.setAdapter(null);
		}
		if (data == null) {
			findViewById(R.id.spinnerLayout).setVisibility(View.VISIBLE);
			findViewById(R.id.listView1).setVisibility(View.GONE);
			findViewById(R.id.infoText).setVisibility(View.GONE);
			task = new FetchScheduleAsyncTask();

			task.execute();
		} else {
			updateViews(data);
		}
	}

	private void updateViews(BarcampData data) {
		findViewById(R.id.spinnerLayout).setVisibility(View.GONE);
		if (TextUtils.isEmpty(data.status)) {
			findViewById(R.id.listView1).setVisibility(View.VISIBLE);
			addScheduleItems(data.slotsArray);
			findViewById(R.id.infoText).setVisibility(View.GONE);
		} else {
			TextView infoText = ((TextView) findViewById(R.id.infoText));
			infoText.setMovementMethod(LinkMovementMethod.getInstance());
			infoText.setText(Html.fromHtml(data.status));
			infoText.setVisibility(View.VISIBLE);
			findViewById(R.id.listView1).setVisibility(View.GONE);
		}
	}
}
