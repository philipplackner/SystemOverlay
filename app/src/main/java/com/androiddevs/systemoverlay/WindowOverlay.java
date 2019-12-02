package com.androiddevs.systemoverlay;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

public class WindowOverlay extends Service implements View.OnClickListener {

    private WindowManager windowManager;
    private View overlayView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnOpenMainActivity:
                // starting the MainActivity and stopping the service
                Intent intent = new Intent(WindowOverlay.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();
                break;
            case R.id.btnCloseWindow:
                // this stops the service
                stopSelf();
                break;
            default:
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // this is the initial position of our window
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        // this adds our overlay to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(overlayView, params);

        Button btnCloseWindow = overlayView.findViewById(R.id.btnCloseWindow);
        Button btnOpenMainActivity = overlayView.findViewById(R.id.btnOpenMainActivity);
        btnCloseWindow.setOnClickListener(this);
        btnOpenMainActivity.setOnClickListener(this);

        // this will make sure we can drag our view
        final ConstraintLayout dragLayout = overlayView.findViewById(R.id.clParent);
        dragLayout.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;

                        // location of our touch
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        windowManager.updateViewLayout(overlayView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // removing the view if the service is destroyed
        if (overlayView != null) {
            windowManager.removeView(overlayView);
        }
    }
}
