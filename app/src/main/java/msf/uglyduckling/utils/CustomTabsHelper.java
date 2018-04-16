/*
 *  Copyright(c) 2017 lizhaotailang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msf.uglyduckling.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import msf.uglyduckling.R;
import msf.uglyduckling.config.SettingsKey;


/**
 * Created by lizhaotailang on 2017/3/27.
 */

public class CustomTabsHelper {

    public static void openUrl(Context context, String url) {
        try {
            if ((boolean) SPUtils.get(context, SettingsKey.KEY_CUSTOM_TABS, true)) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                builder.build().launchUrl(context, Uri.parse(url));
            } else {
                context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.error_no_browser, Toast.LENGTH_SHORT).show();
        }
    }

}
