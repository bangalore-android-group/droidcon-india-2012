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

package org.blrdroid.droidcon2012.data;

import android.app.Application;

public class BarcampBangalore extends Application {

	private static BarcampBangalore irApplicationContext;

	private BarcampData barcampData;

	public BarcampBangalore() {
		if (irApplicationContext == null) {
			irApplicationContext = this;
		}
	}

	public BarcampData getBarcampData() {
		return barcampData;
	}

	public void setBarcampData(BarcampData data) {
		barcampData = data;
	}

}
