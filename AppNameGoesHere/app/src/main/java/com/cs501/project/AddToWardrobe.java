package com.cs501.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.internal.compat.ImageWriterCompat;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class AddToWardrobe extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private Button takePhoto, redo, confirm, addAnother;
    private FrameLayout container;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_wardrobe);

        takePhoto = (Button) findViewById(R.id.takePhoto);
        redo = (Button) findViewById(R.id.retake);
        confirm = (Button) findViewById(R.id.confirm);
        container = (FrameLayout) findViewById(R.id.container);
        imageView = (ImageView) findViewById(R.id.imageView);
        addAnother = (Button) findViewById(R.id.addAnother);

        imageView.setVisibility(View.INVISIBLE);
        redo.setVisibility(View.INVISIBLE);
        confirm.setVisibility(View.INVISIBLE);
        addAnother.setVisibility(View.INVISIBLE);

        androidx.camera.view.PreviewView pre = (androidx.camera.view.PreviewView) findViewById(R.id.previewView);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, pre);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageCapture.takePicture(Executors.newSingleThreadExecutor(), new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);
                        System.out.println("SUCCESS");
                        photoTaken(image);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                        System.out.println("ERROR: " +exception);
                    }
                });
//                ImageCapture.OutputFileOptions outputFileOptions =
//                        new ImageCapture.OutputFileOptions.Builder(new File(getApplicationContext().getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".png")).build();
//                imageCapture.takePicture(outputFileOptions, Executors.newSingleThreadExecutor(),
//                        new ImageCapture.OnImageSavedCallback() {
//                            @Override
//                            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
//                                System.out.println("SUCCESS: " + outputFileResults);
//                                //SAVES FILE TO data/data/com.cs501.project/cache
//                            }
//                            @Override
//                            public void onError(ImageCaptureException error) {
//                                System.out.println("ERROR: "+error);
//                            }
//                        }
//                );
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redoPhoto(false);
            }
        });

        addAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redoPhoto(true);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send to new activity with image information (or stack of images) in intent
            }
        });
    }

    void photoTaken(ImageProxy ip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                redo.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
                addAnother.setVisibility(View.VISIBLE);
                takePhoto.setVisibility(View.INVISIBLE);
                container.setVisibility(View.INVISIBLE);

                imageView.setImageBitmap(getBitmap(ip));
                imageView.setVisibility(View.VISIBLE);

                ip.close();
            }
        });
    }

    void redoPhoto(boolean another) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(another) {
                    //add current photo to some stack which will be send to next activity
                }

                redo.setVisibility(View.INVISIBLE);
                confirm.setVisibility(View.INVISIBLE);
                addAnother.setVisibility(View.INVISIBLE);
                takePhoto.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
            }
        });
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider, androidx.camera.view.PreviewView previewView) {
        Preview preview = new Preview.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(Surface.ROTATION_0)
                        .build();

//        OrientationEventListener orientationEventListener = new OrientationEventListener((Context) this) {
//            @Override
//            public void onOrientationChanged(int orientation) {
//                int rotation;
//
//                // Monitors orientation values to determine the target rotation value
//                if (orientation >= 45 && orientation < 135) {
//                    rotation = Surface.ROTATION_90;
//                } else if (orientation >= 135 && orientation < 225) {
//                    rotation = Surface.ROTATION_180;
//                } else if (orientation >= 225 && orientation < 315) {
//                    rotation = Surface.ROTATION_270;
//                } else {
//                    rotation = Surface.ROTATION_0;
//                }
//
//                imageCapture.setTargetRotation(rotation);
//            }
//        };
//
//        orientationEventListener.enable();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageCapture, preview);
    }

    private Bitmap getBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        byte[] clonedBytes = bytes.clone();

        Bitmap bitmapOrg = BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix, true);

        return rotatedBitmap;
    }
}