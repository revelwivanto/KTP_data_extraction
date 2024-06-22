package com.example.ktp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.ktp.PostProcessing.KtpProcessing;
import com.example.ktp.Utils.CameraUtils;
import com.example.ktp.model.FileInfo;
import com.example.ktp.remote.APIUtils;
import com.example.ktp.remote.FileService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * KtpCardActivity adalah sebuah activity yang digunakan untuk mengambil foto KTP dan melakukan proses pemrosesan teks
 * dari gambar tersebut untuk mendapatkan informasi yang diperlukan seperti NIK, nama, tanggal lahir, dan alamat.
 */
public class ktpCardActivity extends AppCompatActivity {

    private ImageView frontImageView;
    private @Nullable ImageView ttdImageView; // ImageView untuk menampilkan gambar KTP (nullable)
    String mCurrentPhotoPath; // String untuk menyimpan path gambar KTP
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    static final int REQUEST_TAKE_PHOTO = 1; // Request code untuk mengambil gambar KTP
    private Bitmap mImageBitmap; // Bitmap untuk menyimpan gambar KTP
    private EditText nik, nama, ttl, alamat; // EditText untuk menginput informasi yang diperlukan
    private Button reset, save; // Button untuk mereset inputan informasi dan gambar KTP
    private FileService fileService;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ktp_card);

        frontImageView = findViewById(R.id.imageView);
        ttdImageView = findViewById(R.id.ttd);
        nama = findViewById(R.id.name_edit_text);
        nik = findViewById(R.id.nik_edit_text);
        ttl = findViewById(R.id.ttl_edit_text);
        alamat = findViewById(R.id.alamat_edit_text);
        reset = findViewById(R.id.reset);
        save = findViewById(R.id.save);

        fileService = APIUtils.getFileService();

        // In ktpCardActivity.java, add a button to navigate to DisplayKTPActivity
        Button viewData = findViewById(R.id.viewData);
        viewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ktpCardActivity.this, DisplayKTPActivity.class);
                startActivity(intent);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(ktpCardActivity.this);

                // Convert ImageView to Bitmap only if ttdImageView is not null
                if (ttdImageView != null) {
                    ttdImageView.setDrawingCacheEnabled(true);
                    ttdImageView.buildDrawingCache();
                    Bitmap signatureBitmap = ((BitmapDrawable) ttdImageView.getDrawable()).getBitmap();
                    myDB.addKTP(Long.valueOf(nik.getText().toString().trim()),  // Changed to Long.valueOf
                            nama.getText().toString().trim(),
                            ttl.getText().toString().trim(),
                            alamat.getText().toString().trim(),
                            signatureBitmap);
                } else {
                    // Handle case where ttdImageView is null
                    myDB.addKTP(Long.valueOf(nik.getText().toString().trim()),  // Changed to Long.valueOf
                            nama.getText().toString().trim(),
                            ttl.getText().toString().trim(),
                            alamat.getText().toString().trim(),
                            null);
                }
            }
        });


        // Menambahkan teks ke Button reset
        reset.setText("R" + "\n" + "E" + "\n" + "S" + "\n" + "E" + "\n" + "T");
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with your actions
            } else {
                Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Mengambil gambar KTP dengan menggunakan kamera
    public void takePicture(View view) {
        // Check if the camera permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Permission is already granted, proceed with taking the picture
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = CameraUtils.createImageFile(this);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURL = FileProvider.getUriForFile(this, "com.example.ktp.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURL);
                startActivityForResult(takePic, REQUEST_TAKE_PHOTO);
            }
        }
    }


    // Melakukan pemrosesan teks dari gambar KTP untuk mendapatkan informasi yang diperlukan
    public void extractInfo(View view) {
        if (mImageBitmap != null) {
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mImageBitmap);
            // Memanggil fungsi detectInImage buat detect text
            // processExtractTextForFrontPic proces gambar dan return HashMap (karena pair)
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
        } else {
            Toast.makeText(this, "Please take the image first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mImageBitmap = CameraUtils.getBitmap(mCurrentPhotoPath);
            frontImageView.setImageBitmap(mImageBitmap);
            reset.setVisibility(View.VISIBLE);
            try {
                prosesKamera(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void prosesKamera(Bitmap bm) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray(); // convert camera photo to byte array

        Date d = new Date();
        CharSequence s = DateFormat.format("yyyyMMdd-hh-mm-ss", d.getTime());
        String nmFile = s.toString() + ".png";

        // Create request body for multipart upload
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", nmFile, requestBody);

        // Enqueue the upload request
        Call<FileInfo> call = fileService.upload(body);
        call.enqueue(new Callback<FileInfo>() {
            @Override
            public void onResponse(retrofit2.Call<FileInfo> call, Response<FileInfo> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ktpCardActivity.this, "Image uploaded successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ktpCardActivity.this, "Failed to upload image", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<FileInfo> call, Throwable t) {
                Log.e("API_CALL_ERROR", "Error: ", t);
                Toast.makeText(ktpCardActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Mengereset inputan informasi yang diperoleh dari pemrosesan teks gambar KTP
    public void reset(View view) {
        mImageBitmap = null;
        frontImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.kamera_front));
        nama.getText().clear();
        nik.getText().clear();
        alamat.getText().clear();
        ttl.getText().clear();
        reset.setVisibility(View.GONE);
    }

    // Menampilkan informasi yang diperoleh dari pemrosesan teks gambar KTP
    private void presentFrontOutput(HashMap<String, String> dataMap) {
        if (dataMap != null) {
            nik.setText(dataMap.get("nik"), TextView.BufferType.EDITABLE);
            nama.setText(dataMap.get("nama"), TextView.BufferType.EDITABLE);
            ttl.setText(dataMap.get("ttl"), TextView.BufferType.EDITABLE);
            alamat.setText(dataMap.get("alamat"), TextView.BufferType.EDITABLE);
        }
    }

}
