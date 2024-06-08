package com.example.ktp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
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
import com.example.ktp.PostProcessing.KtpkuProcessing;
import com.example.ktp.R;
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

public class ktpkuCardActivity extends AppCompatActivity {
    private Button ocr;
    private ImageView imageView;
    private Bitmap mImageBitmap;
    private Button reset;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private CardView mCardView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ktpku_card);

        ocr = findViewById(R.id.ocr);
        imageView = findViewById(R.id.imageView);
        reset = findViewById(R.id.reset);

        reset.setText("R"+"\n"+"E"+"\n"+"S"+"\n"+"E"+"\n"+"T");

        mTextView = findViewById(R.id.textView);
        mCardView = findViewById(R.id.info_card_view);

        if (savedInstanceState != null){
            mCurrentPhotoPath = savedInstanceState.getString("CurrentPhotoPath");
            mImageBitmap = CameraUtils.getBitmap(mCurrentPhotoPath);
            imageView.setImageBitmap(mImageBitmap);
            reset.setVisibility(View.VISIBLE);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            mImageBitmap = CameraUtils.getBitmap(mCurrentPhotoPath);
            imageView.setImageBitmap(mImageBitmap);
            reset.setVisibility(View.VISIBLE);
        }
    }

    public void reset(View view) {
        mImageBitmap = null;
        imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kamera_front));
        mTextView.setText(R.string.extracted_info);
        reset.setVisibility(View.GONE);
    }

    private void presentFrontOutput(HashMap<String, String> dataMap){
        if (dataMap != null){
            mTextView.setText(dataMap.get("text"));
        }
    }

    public void extractInfo(View view) {
        if (mImageBitmap != null){
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mImageBitmap);
            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText text) {
                    HashMap<String, String> datMap = new KtpkuProcessing().processText(text, getApplicationContext());
                    presentFrontOutput(datMap);
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
}