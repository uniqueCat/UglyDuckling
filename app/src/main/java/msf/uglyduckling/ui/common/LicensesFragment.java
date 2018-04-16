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

package msf.uglyduckling.ui.common;

import android.webkit.WebView;

import butterknife.BindView;
import msf.uglyduckling.R;
import msf.uglyduckling.base.BaseFragment;


/**
 * Created by lizhaotailang on 2017/3/17.
 */

public class LicensesFragment extends BaseFragment {


    @BindView(R.id.webView)
    WebView webView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_licenses;
    }

    @Override
    protected void viewCreate() {
        webView.loadUrl("file:///android_asset/license.html");
    }

}
