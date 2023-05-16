package com.djordjeratkovic.harmony.ui.alarm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.djordjeratkovic.harmony.R;
import com.djordjeratkovic.harmony.ui.alarm.service.AlarmService;
import com.google.zxing.Result;

public class CameraActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 101;
    private CodeScannerView codeScannerView;
    private CodeScanner codeScanner;

    private Context context;

//    private ResetPlanners resetPlanners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        context = getApplicationContext();

//        resetPlanners = new ResetPlanners(context);

        codeScannerView = findViewById(R.id.codeScannerView);

        requestCamera();
        codeScanner = new CodeScanner(this, codeScannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        if (result.getText().equals(getString(R.string.qr_code_check))) {
                            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                            getApplicationContext().stopService(intentService);
                            //TODO: set planner notf
//                            resetPlanners.addPlans();
                            //TODO: schedule another alarm
//                            resetPlanners.scheduleAnotherAlarm();
                            finish();
                            finishAndRemoveTask();
                        } else {
                            Toast.makeText(CameraActivity.this, "Wrong QR", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}