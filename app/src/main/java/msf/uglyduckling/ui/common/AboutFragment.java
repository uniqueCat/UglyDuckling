package msf.uglyduckling.ui.common;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import msf.uglyduckling.BuildConfig;
import msf.uglyduckling.R;
import msf.uglyduckling.utils.CustomTabsHelper;
import msf.uglyduckling.widget.NotifyDialog;

/**
 * Created by Administrator on 2018/4/14.
 */

public class AboutFragment extends PreferenceFragmentCompat {

    private Preference prefRate, prefLicenses, prefThx1, prefThx2, prefSourceCode, prefSendAdvices, prefDonate;

    private Preference prefVersion;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.about_prefs);

        initPrefs();

        prefVersion.setSummary(BuildConfig.VERSION_NAME);

        prefRate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    showError();
                }
                return true;
            }
        });

        prefLicenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), PrefsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(PrefsActivity.EXTRA_FLAG, PrefsActivity.FLAG_LICENSES);
                startActivity(intent);
                return true;
            }
        });

        prefThx1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CustomTabsHelper.openUrl(getContext(), getString(R.string.thanks_1_url));
                return true;
            }
        });

        prefThx2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CustomTabsHelper.openUrl(getContext(), getString(R.string.thanks_2_url));
                return true;
            }
        });

        prefSourceCode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CustomTabsHelper.openUrl(getContext(), getString(R.string.source_code_desc));
                return true;
            }
        });

        prefSendAdvices.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Uri uri = Uri.parse(getString(R.string.mail_account));
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_topic));
                    startActivity(intent);
                } catch (Exception e) {
                    showError();
                }
                return true;
            }
        });

        prefDonate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new NotifyDialog()
                        .setTitle(getString(R.string.donate))
                        .setMessage(getString(R.string.donate_msg))
                        .setOnCheckListener(new NotifyDialog.OnCheckListener() {
                            @Override
                            public void isCheck(boolean check) {
                                if (check) {
                                    if (getContext() == null)
                                        return;
                                    ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("text", getString(R.string.donate_account));
                                    if (manager != null)
                                        manager.setPrimaryClip(clipData);
                                }
                            }
                        }).show(getContext());
                return true;
            }
        });
    }

    private void initPrefs() {
        prefVersion = findPreference("version");
        prefRate = findPreference("rate");
        prefLicenses = findPreference("licenses");
        prefThx1 = findPreference("thanks_1");
        prefThx2 = findPreference("thanks_2");
        prefSourceCode = findPreference("source_code");
        prefSendAdvices = findPreference("send_advices");
        prefDonate = findPreference("donate");
    }

    private void showError() {
        if (getView() != null)
            Snackbar.make(getView(), R.string.something_wrong, Snackbar.LENGTH_SHORT).show();
    }
}
