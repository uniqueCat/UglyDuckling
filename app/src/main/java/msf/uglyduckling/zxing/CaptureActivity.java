package msf.uglyduckling.zxing;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.Result;

import java.io.IOException;

import butterknife.BindView;
import msf.uglyduckling.R;
import msf.uglyduckling.base.BaseActivity;
import msf.uglyduckling.utils.ScreenUtils;
import msf.uglyduckling.widget.NotifyDialog;
import msf.uglyduckling.zxing.camera.CameraManager;
import msf.uglyduckling.zxing.decode.DecodeThread;
import msf.uglyduckling.zxing.utils.BeepManager;
import msf.uglyduckling.zxing.utils.CaptureActivityHandler;
import msf.uglyduckling.zxing.utils.InactivityTimer;

/**
 * Created by Administrator on 2018/4/26.
 */

public class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.capture_preview)
    SurfaceView scanPreview;
    @BindView(R.id.capture_scan_line)
    ImageView scanLine;
    @BindView(R.id.capture_crop_view)
    RelativeLayout scanCropView;
    @BindView(R.id.capture_container)
    RelativeLayout scanContainer;

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private Rect mCropRect = null;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    private boolean isHasSurface = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan;
    }

    @Override
    protected void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(getApplication());
        handler = null;
        if (isHasSurface) {
            initCamera(scanPreview.getHolder());
        } else {
            scanPreview.getHolder().addCallback(this);
        }
        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder holder) {
        if (holder == null) {
            throw new IllegalArgumentException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }

        try {
            cameraManager.openDriver(holder);
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
                initCrop();
            }
        } catch (IOException e) {
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        new NotifyDialog().setTitle(getString(R.string.error))
                .show(this, getString(R.string.unable_to_open_camera));
    }

    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        // Obtain the location information of the scanning frame in layout
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - ScreenUtils.getStatusHeight(this);

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        // Obtain the height and width of layout container.
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        // Compute the coordinate of the top-left vertex x
        // of the final interception rectangle.
        int x = cropLeft * cameraWidth / containerWidth;
        // Compute the coordinate of the top-left vertex y
        // of the final interception rectangle.
        int y = cropTop * cameraHeight / containerHeight;

        // Compute the width of the final interception rectangle.
        int width = cropWidth * cameraWidth / containerWidth;
        // Compute the height of the final interception rectangle.
        int height = cropHeight * cameraHeight / containerHeight;

        // Generate the final interception rectangle.
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {

        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

        Intent resultIntent = new Intent();
        bundle.putInt("width", mCropRect.width());
        bundle.putInt("height", mCropRect.height());
        bundle.putString("result", rawResult.getText());
        resultIntent.putExtras(bundle);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

}
