package com.example.expressiondemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;



import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'expressiondemo' library on application startup.
    /*
    static {
        System.loadLibrary("expressiondemo");
    }
     */

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(
                this,
                new String[] {Manifest.permission.CAMERA},
                1001
        );


        previewView = (PreviewView) findViewById(R.id.previewView);

        setContentView(R.layout.activity_main);
        startCameraPreview();

    }

    void startCameraPreview() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
        previewView.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);

    }



    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        try{
            cameraProvider.unbindAll();
            Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
            preview.setSurfaceProvider(previewView.getSurfaceProvider());
            camera.getCameraControl().setZoomRatio(2);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A native method that is implemented by the 'expressiondemo' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}