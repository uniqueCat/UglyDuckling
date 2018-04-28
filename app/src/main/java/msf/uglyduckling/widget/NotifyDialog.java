package msf.uglyduckling.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import msf.uglyduckling.R;


/**
 * Created by 马帅发 on 2017/10/30.
 */

public class NotifyDialog {


    private AlertDialog dialog;

    private String positiveButton;

    private String negativeButton;

    private boolean enable = true;

    private String title;

    private String msg = "";

    private View view;


    public void show(@NonNull Context context, String msg) {
        create(context, msg);
        animShow();
    }

    public void show(Context context) {
        create(context, null);
        animShow();
    }

    public void show(String msg) {
        if (dialog == null)
            return;
        dialog.setMessage(msg);
        animShow();
    }

    public void show() {
        if (dialog == null)
            return;
        animShow();
    }

    private void animShow() {
        dialog.show();
        Window window = dialog.getWindow();
        assert window != null;
        window.setWindowAnimations(R.style.dialogWindowAnim);
    }


    public NotifyDialog create(Context context) {
        create(context, null);
        return this;
    }

    private void create(@NonNull Context context, String msg) {
        dialog = new AlertDialog.Builder(context)
                .setTitle(title == null ? "温馨提醒" : title)
                .setCancelable(enable)
                .setNegativeButton(negativeButton == null ? "取消" : negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onCheckListener != null)
                            onCheckListener.isCheck(false);
                        dismmis();
                    }
                }).setPositiveButton(positiveButton != null ? positiveButton : "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onCheckListener != null)
                            onCheckListener.isCheck(true);
                        dismmis();
                    }
                }).create();
        if (!TextUtils.isEmpty(msg))
            dialog.setMessage(msg);
        if (!TextUtils.isEmpty(this.msg))
            dialog.setMessage(this.msg);
        if (view != null)
            dialog.setView(view);
    }

    private void dismmis() {
        if (dialog != null)
            dialog.dismiss();
        dialog = null;
    }

    public NotifyDialog setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public NotifyDialog setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
        return this;
    }

    public NotifyDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public View getView() {
        return view;
    }

    public NotifyDialog setView(View view) {
        this.view = view;
        return this;
    }

    public NotifyDialog setNegativeButton(String negativeButton) {
        this.negativeButton = negativeButton;
        return this;
    }

    public NotifyDialog setPositiveButton(String positiveButton) {
        this.positiveButton = positiveButton;
        return this;
    }

    public NotifyDialog setMessage(String msg) {
        this.msg = msg;
        if (dialog != null)
            dialog.setMessage(msg);
        return this;
    }

    public boolean isShow() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }

    private OnCheckListener onCheckListener;


    public interface OnCheckListener {
        void isCheck(boolean check);
    }

}
