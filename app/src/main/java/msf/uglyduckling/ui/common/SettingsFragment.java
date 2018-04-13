package msf.uglyduckling.ui.common;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import msf.uglyduckling.R;
import msf.uglyduckling.config.SettingsKey;
import msf.uglyduckling.utils.SPUtils;

/**
 * PreferenceFragment 首选项设置，界面元素来自xml的PreferenceScreen布局， 其中用preferences开头的组，preference结尾的控件
 * PreferenceFragmentCompat继承自v4的fragment，PreferenceFragment继承自app的fragment；用法应该差不多，pfc兼容同一风格的
 * 需要导"com.android.support:preference-v14:27.0.1"包，使用PreferenceFragmentCompat 必须将此容器activity的theme做响应设置；
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference prefStartTime, prefsEndTime, prefAlert;
    private Preference prefNotificationInterval, prefNavigationBar;

    private int startHour, startMinute;
    private int endHour, endMinute;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_prefs);

        initPrefs();

        startHour = (int) SPUtils.get(getContext(), SettingsKey.KEY_DO_NOT_DISTURB_MODE_START_HOUR, 23);
        startMinute = (int) SPUtils.get(getContext(), SettingsKey.KEY_DO_NOT_DISTURB_MODE_START_MINUTE, 0);
        prefStartTime.setSummary(formatTimeIntToString(startHour, startMinute));

        endHour = (int) SPUtils.get(getContext(), SettingsKey.KEY_DO_NOT_DISTURB_MODE_END_HOUR, 6);
        endMinute = (int) SPUtils.get(getContext(), SettingsKey.KEY_DO_NOT_DISTURB_MODE_END_MINUTE, 0);
        prefsEndTime.setSummary(formatTimeIntToString(endHour, endMinute));

        prefStartTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //一个第三方时间选择器
                TimePickerDialog dialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        SPUtils.put(getContext(), SettingsKey.KEY_DO_NOT_DISTURB_MODE_START_HOUR, hourOfDay);
                        SPUtils.put(getContext(), SettingsKey.KEY_DO_NOT_DISTURB_MODE_START_MINUTE, minute);
                        prefStartTime.setSummary(formatTimeIntToString(hourOfDay, minute));
                    }
                }, startHour, startMinute, true);
                if (getActivity() != null && getActivity().getFragmentManager() != null)
                    dialog.show(getActivity().getFragmentManager(), "StartTimePickerDialog");

                return true;
            }
        });

        prefsEndTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TimePickerDialog dialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        if (startHour == endHour && startMinute == endMinute) {
                            if (getActivity() != null && getActivity().getWindow() != null)
                                Snackbar.make(getActivity().getWindow().getDecorView(), R.string.set_end_time_error, Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        SPUtils.put(getContext(), SettingsKey.KEY_DO_NOT_DISTURB_MODE_END_HOUR, hourOfDay);
                        SPUtils.put(getContext(), SettingsKey.KEY_DO_NOT_DISTURB_MODE_END_MINUTE, minute);
                        prefsEndTime.setSummary(formatTimeIntToString(hourOfDay, minute));
                    }
                }, endHour, endMinute, true);
                if (getActivity() != null && getActivity().getFragmentManager() != null)
                    dialog.show(getActivity().getFragmentManager(), "StartTimePickerDialog");

                return true;
            }
        });

        prefAlert.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean value = (boolean) newValue;
                if (value) {
                    //开启接受服务
                } else {
                    //关闭接受服务
                }
                return true;
            }
        });

        prefNotificationInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SPUtils.put(getContext(), SettingsKey.KEY_NOTIFICATION_INTERVAL, newValue);
                return true;
            }
        });

        prefNavigationBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (getActivity() != null && getActivity().getWindow() != null)
                    Snackbar.make(getActivity().getWindow().getDecorView(), R.string.navigation_bar_restart_msg, Snackbar.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    private void initPrefs() {
        prefStartTime = findPreference("do_not_disturb_mode_start");
        prefsEndTime = findPreference("do_not_disturb_mode_end");
        prefAlert = findPreference("alert");
        prefNotificationInterval = findPreference("notification_interval");
        prefNavigationBar = findPreference("navigation_bar_tint");
    }

    private String formatTimeIntToString(int hour, int minute) {
        StringBuilder buffer = new StringBuilder(16);
        if (hour <= 9) {
            buffer.append("0");
        }
        buffer.append(hour).append(":");
        if (minute <= 9) {
            buffer.append("0");
        }
        buffer.append(minute);

        return buffer.toString();
    }
}