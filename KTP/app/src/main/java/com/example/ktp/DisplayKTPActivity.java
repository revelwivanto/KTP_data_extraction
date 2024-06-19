package com.example.ktp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class DisplayKTPActivity extends AppCompatActivity {

    private TextView nikTextView;
    private TextView namaTextView;
    private TextView ttlTextView;
    private TextView alamatTextView;
    private ImageView ttdImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_row);

        // Initialize the TextViews and ImageView
        nikTextView = findViewById(R.id.NIK_txt);
        namaTextView = findViewById(R.id.nama_txt);
        ttlTextView = findViewById(R.id.ttl_txt);
        alamatTextView = findViewById(R.id.alamat_txt);
        ttdImageView = findViewById(R.id.ttd);

        // Get the Intent that started this activity and extract the string
        String nik = getIntent().getStringExtra("nik");
        String nama = getIntent().getStringExtra("nama");
        String ttl = getIntent().getStringExtra("ttl");
        String alamat = getIntent().getStringExtra("alamat");

        // Set the values to the TextViews
        nikTextView.setText(nik);
        namaTextView.setText(nama);
        ttlTextView.setText(ttl);
        alamatTextView.setText(alamat);

        // If you need to dynamically set the image based on intent data, you can do that here
        // For now, it uses the static image provided in the XML
    }
}
