package com.cs501.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.User_settings;
import com.google.common.util.concurrent.ListenableFuture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class AddToWardrobe extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private Button takePhoto, redo, confirm, addAnother;
    private FrameLayout container;
    private ImageView imageView;
    private ArrayList<String> fileNames;
    private boolean camReady = false;
    private FireBaseManager fb_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_wardrobe);


        ActivityResultLauncher<String> requestPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if(result == false) {
                Toast toast = Toast.makeText(AddToWardrobe.this, "Cannot take photos with permissions from device", Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(AddToWardrobe.this, MainActivity.class);
                startActivity(intent);
            }
        });

        requestPermissionRequest.launch(Manifest.permission.CAMERA); //request location permissions
        fileNames = new ArrayList<>();

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

        fb_manager = FireBaseManager.getInstance();

        androidx.camera.view.PreviewView pre = (androidx.camera.view.PreviewView) findViewById(R.id.previewView);

        //from documentation
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

                if(camReady) {
                    takePhoto.setEnabled(false);

                    User_settings uSettings = fb_manager.getUser().getUserSettings();
                    String flashMode = uSettings.getFlashMode();
                    System.out.println(flashMode);
                    if(flashMode.equals("On")){
                        imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
                    } else if(flashMode.equals("Off")){
                        imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                    } else if(flashMode.equals("Auto")){
                        imageCapture.setFlashMode(ImageCapture.FLASH_MODE_AUTO);
                    }

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
                            System.out.println("ERROR: " + exception);
                        }
                    });
                }
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

                confirm.setEnabled(false);
                try {
//                    ConfirmToWardrobe.images = images;
                    Intent i = new Intent(AddToWardrobe.this, ConfirmToWardrobe.class);
                    i.putStringArrayListExtra("fileNames", fileNames);
                    finish();
                    startActivity(i);
                } catch (Exception e) {
                    System.out.println(e);
                }
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

//                images.add(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                saveBitmap(getBitmap(ip));

                ip.close();
            }
        });
    }

    //https://stackoverflow.com/questions/63410194/how-to-save-multiple-bitmaps-fastly-in-android-studio
    public void saveBitmap(Bitmap output){
        String filepath = getApplicationContext().getFilesDir().toString();
        //String filepath = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(filepath);
        if(!dir.exists()){
            dir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        System.out.println("Saved to " + filepath + "/" + fileName);
        fileNames.add(filepath + "/" + fileName);
        File image = new File(dir, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(image);
            output.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("AddToWardrobe", "Error:" + e);
        }
    }

    void redoPhoto(boolean another) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                takePhoto.setEnabled(true);
                if(!another) {
                    //add current photo to some stack which will be send to next activity
                    File file = new File(fileNames.get(fileNames.size() -1));
                    System.out.println(file.delete());
                    fileNames.remove(fileNames.size() -1);
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

    //from Android Studio documentation
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

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageCapture, preview);

        camReady = true;
//        if ( camera.getCameraInfo().hasFlashUnit() ) {
//            camera.getCameraControl().enableTorch(true); // or false
//        }
    }

    //https://stackoverflow.com/questions/56772967/converting-imageproxy-to-bitmap
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
        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth()/2, rotatedBitmap.getHeight()/2, true);
        return rotatedBitmap;
    }
}