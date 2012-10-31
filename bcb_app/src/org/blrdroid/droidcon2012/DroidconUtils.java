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

package org.blrdroid.droidcon2012;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.blrdroid.droidcon2012.activity.AboutActivity;
import org.blrdroid.droidcon2012.activity.DroidconActivityBaseClass;
import org.blrdroid.droidcon2012.activity.ScheduleActivity;
import org.blrdroid.droidcon2012.activity.ShareActivity;
import org.blrdroid.droidcon2012.activity.UpdateMessagesActivity;
import org.blrdroid.droidcon2012.activity.WebViewActivity;
import org.blrdroid.droidcon2012.data.BarcampBangalore;
import org.blrdroid.droidcon2012.data.BarcampData;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.slidingmenu.lib.SlidingMenuActivity;

public class DroidconUtils {

	private static final String DROIDCON_SCHEDULE_JSON = "http://blog.blrdroid.org/droidcon.json";
	private static final String Droidcon_LOCATION_MAPS_URL = "https://maps.google.com/maps?q=MLR+Convention+Centre++Whitefield&hl=en&ll=12.997982,77.702147&spn=0.009189,0.016512&sll=12.999079,77.701814&sspn=0.009189,0.016512&hq=MLR+Convention+Centre++Whitefield&t=m&z=17";
	protected static final int START_SCHEDULE = 100;
	protected static final int START_ABOUT = 101;
	protected static final int START_SETTINGS = 102;
	protected static final int START_SHARE = 103;
	protected static final int START_Droidcon12_TWEETS = 104;
	protected static final int START_Droidcon_UPDATES = 105;

	public static void createActionBarOnActivity(final Activity activity) {
		createActionBarOnActivity(activity, false);
	}

	public static void createActionBarOnActivity(final Activity activity,
			boolean isHome) {
		// ******** Start of Action Bar configuration
		ActionBar actionbar = (ActionBar) activity
				.findViewById(R.id.actionBar1);
		actionbar.setHomeLogo(R.drawable.home);
		actionbar.setHomeAction(new Action() {
			@Override
			public void performAction(View view) {
				((SlidingMenuActivity) activity).toggle();
			}

			@Override
			public int getDrawable() {
				return R.drawable.home;
			}
		});

		actionbar.setTitle(R.string.app_title_text);
		TextView logo = (TextView) activity.findViewById(R.id.actionbar_title);
		Shader textShader = new LinearGradient(0, 0, 0, logo.getHeight(),
				new int[] { Color.WHITE, 0xff999999 }, null, TileMode.CLAMP);
		logo.getPaint().setShader(textShader);
		actionbar.setOnTitleClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		// ******** End of Action Bar configuration

	}

	public static Action createShareAction(Activity activity) {
		IntentAction shareAction = new IntentAction(activity,
				createShareIntent(activity), R.drawable.share_icon);

		return shareAction;
	}

	public static Intent createShareIntent(Activity activity) {
		Intent intent = new Intent(activity, ShareActivity.class);
		return intent;

	}

	public static PendingIntent createPendingIntentForID(Context context,
			String id, int slot, int session) {
		Intent intent = new Intent(context, SessionAlarmIntentService.class);
		intent.putExtra(SessionAlarmIntentService.SESSION_ID, id);
		intent.putExtra(SessionAlarmIntentService.EXTRA_SLOT_POS, slot);
		intent.putExtra(SessionAlarmIntentService.EXTRA_SESSION_POSITION,
				session);
		int idInt = Integer.parseInt(id);
		PendingIntent pendingIntent = PendingIntent.getService(context, idInt,
				intent, PendingIntent.FLAG_ONE_SHOT);
		return pendingIntent;
	}

	public static Boolean updateContextWithBarcampData(Context context) {
		Boolean retVal = false;
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpGet(DROIDCON_SCHEDULE_JSON);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			BarcampData data = DataProcessingUtils.parseDroidconJSON(page);
			((BarcampBangalore) context).setBarcampData(data);
			if (data != null) {
				retVal = true;
				DroidconSharedPrefUtils.setAllDroidconUpdates(context, page);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (!retVal) {
			updateContextDataFromSharedPreferences(context);
		}
		return retVal;
	}

	public static void updateContextDataFromSharedPreferences(Context context) {
		try {
			String page = DroidconSharedPrefUtils.getAllDroidconUpdates(
					context, null);
			if (page != null) {
				BarcampData data;
				data = DataProcessingUtils.parseDroidconJSON(page);
				((BarcampBangalore) context).setBarcampData(data);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void addNavigationActions(
			final DroidconActivityBaseClass homeActivity) {
		homeActivity.setBehindContentView(R.layout.navigation_menu);
		int offset = 100;
		DisplayMetrics metrics = new DisplayMetrics();
		homeActivity.getWindow().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		offset = ((metrics.widthPixels * 130)) / 480;

		homeActivity.setBehindOffset(offset);
		homeActivity.setBehindScrollScale(0.5f);

		View view = homeActivity.findViewById(R.id.nav_agenda);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, ScheduleActivity.class);
				homeActivity.startActivityForResult(intent, START_SCHEDULE);

			}
		});

		view = homeActivity.findViewById(R.id.nav_about);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, AboutActivity.class);
				homeActivity.startActivityForResult(intent, START_ABOUT);
			}
		});

		view = homeActivity.findViewById(R.id.nav_share);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, ShareActivity.class);
				homeActivity.startActivityForResult(intent, START_SHARE);
			}
		});
		view = homeActivity.findViewById(R.id.nav_tweets);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, WebViewActivity.class);
				intent.putExtra(WebViewActivity.URL,
						"file:///android_asset/droidcon12_updates.html");
				homeActivity.startActivityForResult(intent,
						START_Droidcon12_TWEETS);
			}
		});
		view = homeActivity.findViewById(R.id.nav_update);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity,
						UpdateMessagesActivity.class);
				homeActivity.startActivityForResult(intent,
						START_Droidcon_UPDATES);
			}
		});
		view = homeActivity.findViewById(R.id.nav_venue);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final PackageManager pm = homeActivity.getPackageManager();

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(Droidcon_LOCATION_MAPS_URL));
				final List<ResolveInfo> matches = pm.queryIntentActivities(
						intent, 0);
				for (ResolveInfo info : matches) {
					Log.e("MapPackage", info.loadLabel(pm) + " "
							+ info.activityInfo.packageName + " "
							+ info.activityInfo.name);
					if (info.activityInfo.name
							.equals("com.google.android.maps.MapsActivity")) {
						intent.setClassName("com.google.android.apps.maps",
								"com.google.android.maps.MapsActivity");
					}
				}

				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				homeActivity.startActivity(intent);
			}
		});
		view = homeActivity.findViewById(R.id.nav_Droidcon);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://blrdroid.org"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				homeActivity.startActivity(intent);
			}
		});

	}
}
