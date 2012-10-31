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
import org.blrdroid.droidcon2012.R;
import org.blrdroid.droidcon2012.data.BarcampBangalore;
import org.blrdroid.droidcon2012.data.BarcampData;
import org.blrdroid.droidcon2012.data.Session;
import org.blrdroid.droidcon2012.data.Slot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class SessionDetailsActivity extends DroidconActivityBaseClass {
	public final static String EXTRA_SESSION_POSITION = "session_position";
	public final static String EXTRA_SLOT_POS = "slotPosition";
	public static final String EXTRA_SESSION_ID = "sessionID";
	private static final int SHOW_ERROR_DIALOG = 100;

	SimpleDateFormat sdf_hour = new SimpleDateFormat("HH");
	SimpleDateFormat sdf_minute = new SimpleDateFormat("mm");
	SimpleDateFormat sdf_date = new SimpleDateFormat("dd");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_details);
		sdf_hour.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
		sdf_date.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
		sdf_minute.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
		DroidconUtils.createActionBarOnActivity(this);
		DroidconUtils.addNavigationActions(this);
		BarcampData data = ((BarcampBangalore) getApplicationContext())
				.getBarcampData();
		if (data == null) {
			finish();
			return;
		}
		final Slot slot = data.slotsArray.get(getIntent().getIntExtra(
				EXTRA_SLOT_POS, 0));
		final Session session = slot.sessionsArray.get(getIntent().getIntExtra(
				EXTRA_SESSION_POSITION, 0));
		String id = getIntent().getStringExtra(EXTRA_SESSION_ID);
		if (id != null && !id.equals(session.id)) {
			showDialog(SHOW_ERROR_DIALOG);
			finish();
		}

		((TextView) findViewById(R.id.title)).setText(session.title);
		((TextView) findViewById(R.id.time)).setText(session.time);
		((TextView) findViewById(R.id.location)).setText(session.location);
		((TextView) findViewById(R.id.presenter)).setText("By "
				+ session.presenter);
		((TextView) findViewById(R.id.description)).setText(Html
				.fromHtml(session.description));
		((TextView) findViewById(R.id.description))
				.setMovementMethod(LinkMovementMethod.getInstance());

		CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
		checkBox.setChecked(DroidconSharedPrefUtils.getAlarmSettingsForID(this,
				session.id) == DroidconSharedPrefUtils.ALARM_SET);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					DroidconSharedPrefUtils.setAlarmSettingsForID(
							SessionDetailsActivity.this, session.id,
							DroidconSharedPrefUtils.ALARM_SET);
					PendingIntent intent = DroidconUtils
							.createPendingIntentForID(
									SessionDetailsActivity.this,
									session.id,
									getIntent().getIntExtra(EXTRA_SLOT_POS, 0),
									getIntent().getIntExtra(
											EXTRA_SESSION_POSITION, 0));
					AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					Date d = new Date(slot.time);
					Log.d("DROIDCON", "Alarm : " + sdf_date.format(d)
							+ sdf_hour.format(d) + sdf_minute.format(d));
					int date_event = Integer.parseInt(sdf_date.format(d));
					int hour = Integer.parseInt(sdf_hour.format(d));
					int minute = Integer.parseInt(sdf_minute.format(d));
					GregorianCalendar date = new GregorianCalendar(2012,
							Calendar.NOVEMBER, date_event, hour, minute);
					long timeInMills = date.getTimeInMillis() - 300000;
					alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMills,
							intent);
				} else {
					DroidconSharedPrefUtils.setAlarmSettingsForID(
							SessionDetailsActivity.this, session.id,
							DroidconSharedPrefUtils.ALARM_NOT_SET);
					PendingIntent intent = DroidconUtils
							.createPendingIntentForID(
									SessionDetailsActivity.this,
									session.id,
									getIntent().getIntExtra(EXTRA_SLOT_POS, 0),
									getIntent().getIntExtra(
											EXTRA_SESSION_POSITION, 0));
					AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					alarmManager.cancel(intent);
				}
			}
		});

		Intent intent = new Intent(this, ShareActivity.class);
		intent.putExtra(ShareActivity.SHARE_STRING, "I am attending session "
				+ session.title + " by " + session.presenter + " @"
				+ session.location + " between " + session.time);
		IntentAction shareAction = new IntentAction(this, intent,
				R.drawable.share_icon);

		ActionBar actionbar = (ActionBar) findViewById(R.id.actionBar1);
		actionbar.addAction(shareAction);

	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		switch (id) {
		case SHOW_ERROR_DIALOG:
			alertDialog.setCancelable(false);
			alertDialog.setTitle("Error!!!");
			alertDialog
					.setMessage("Error!!! Session got changed. Please check schedule again.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(SHOW_ERROR_DIALOG);
					SessionDetailsActivity.this.finish();
					Intent intent = new Intent(SessionDetailsActivity.this,
							ScheduleActivity.class);
					startActivity(intent);
				}
			});
			break;
		}
		return alertDialog;
	}

}
