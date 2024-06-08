package com.example.ktp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ktp.PostProcessing.KtpProcessing;
import com.example.ktp.Utils.CameraUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ktpCardActivity extends AppCompatActivity {

    private ImageView frontImageView;
    String mCurrentPhotoPath;

    static final int REQUEST_TAKE_PHOTO = 1;
    private Bitmap mImageBitmap;
    private EditText nik, nama, ttl, alamat;
    private Button reset;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ktp_card);

        frontImageView = (ImageView)findViewById(R.id.imageView);
        nama = (EditText) findViewById(R.id.name_edit_text);
        nik = (EditText) findViewById(R.id.nik_edit_text);
        ttl = (EditText) findViewById(R.id.ttl_edit_text);
        alamat = (EditText) findViewById(R.id.alamat_edit_text);
        reset = (Button) findViewById(R.id.reset);

        reset.setText("R"+"\n"+"E"+"\n"+"S"+"\n"+"E"+"\n"+"T");
    }

    public void takePicture(View view) {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = CameraUtils.createImageFile(this);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex){
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null){
                Uri photoURL = FileProvider.getUriForFile(this, "com.example.ktp.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURL);
                startActivityForResult(takePic, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void extractInfo(View view) {
        if (mImageBitmap != null){
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mImageBitmap);
            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    HashMap<String, String> dataMap = new KtpProcessing().processExtractTextForFrontPic(firebaseVisionText, getApplicationContext());
                    presentFrontOutput(dataMap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        else {
            Toast.makeText(this, "Please take the image first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            mImageBitmap = CameraUtils.getBitmap(mCurrentPhotoPath);
            frontImageView.setImageBitmap(mImageBitmap);
            reset.setVisibility(View.VISIBLE);
        }
    }

    public void reset(View view) {
        mImageBitmap = null;
        frontImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kamera_front));
        nama.getText().clear();
        nik.getText().clear();
        alamat.getText().clear();
        ttl.getText().clear();
        reset.setVisibility(View.GONE);
    }

    private void presentFrontOutput(HashMap<String, String> dataMap){
        if (dataMap != null){
            nik.setText(dataMap.get("nik"), TextView.BufferType.EDITABLE);
            nama.setText(dataMap.get("nama"), TextView.BufferType.EDITABLE);
            ttl.setText(dataMap.get("ttl"), TextView.BufferType.EDITABLE);
            alamat.setText(dataMap.get("alamat"), TextView.BufferType.EDITABLE);
        }
    }

}