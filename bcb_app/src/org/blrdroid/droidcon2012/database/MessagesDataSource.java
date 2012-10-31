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
package org.blrdroid.droidcon2012.database;

import org.blrdroid.droidcon2012.data.DroidconUpdatesMessage;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class MessagesDataSource {
	// Database fields
	private SQLiteDatabase database;
	private DroidconSQLiteHelper dbHelper;
	private String[] allColumns = { DroidconSQLiteHelper.COLUMN_ID,
			DroidconSQLiteHelper.COLUMN_MESSAGE, DroidconSQLiteHelper.COLUMN_TIMESTAMP };

	public MessagesDataSource(Context context) {
		dbHelper = new DroidconSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public DroidconUpdatesMessage createMessage(String message, String timestamp) {
		ContentValues values = new ContentValues();
		values.put(DroidconSQLiteHelper.COLUMN_MESSAGE, message);
		values.put(DroidconSQLiteHelper.COLUMN_TIMESTAMP, timestamp);
		long insertId = database.insert(DroidconSQLiteHelper.TABLE_MESSAGES, null,
				values);
		Cursor cursor = database.query(DroidconSQLiteHelper.TABLE_MESSAGES,
				allColumns, DroidconSQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		DroidconUpdatesMessage newMessage = cursorToMessage(cursor);
		cursor.close();
		return newMessage;
	}

	public void deleteComment(DroidconUpdatesMessage message) {
		long id = message.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(DroidconSQLiteHelper.TABLE_MESSAGES,
				DroidconSQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<DroidconUpdatesMessage> getAllMessages() {
		List<DroidconUpdatesMessage> comments = new ArrayList<DroidconUpdatesMessage>();

		Cursor cursor = database.query(DroidconSQLiteHelper.TABLE_MESSAGES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DroidconUpdatesMessage comment = cursorToMessage(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return comments;
	}

	private DroidconUpdatesMessage cursorToMessage(Cursor cursor) {
		DroidconUpdatesMessage message = new DroidconUpdatesMessage();
		message.setId(cursor.getLong(0));
		message.setMessage(cursor.getString(1));
		message.setTimestamp(cursor.getString(2));
		return message;
	}
}
