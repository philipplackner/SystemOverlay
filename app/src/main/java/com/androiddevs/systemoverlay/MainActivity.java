package com.androiddevs.systemoverlay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int SYSTEM_OVERLAY_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForPermission();
    }

    private void checkForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // this will open the settings to let the user grant the permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, SYSTEM_OVERLAY_REQUEST_CODE);
        } else {
            init();
        }
    }

    private void init() {
        Button btnStartService = findViewById(R.id.btnStartService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, WindowOverlay.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SYSTEM_OVERLAY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                init();
            } else {
                Toast.makeText(this,
                        "Permission for System Overlay is not granted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
