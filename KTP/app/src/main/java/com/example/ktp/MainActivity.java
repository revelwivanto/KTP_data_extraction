package com.example.ktp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openRelevantActivity(View view) {
        int id = view.getId();
        if (id == R.id.ktpCardView) {
            Intent aIntent = new Intent(this, ktpCardActivity.class);
            startActivity(aIntent);
        } else if (id == R.id.ktpkuCardView) {
            Intent aIntent = new Intent(this, ktpkuCardActivity.class);
            startActivity(aIntent);
        }
    }
}