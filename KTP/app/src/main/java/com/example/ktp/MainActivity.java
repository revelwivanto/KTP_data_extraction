package com.example.ktp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    Intent aIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(aIntent);
                    return true;
                } else if (item.getItemId() == R.id.ktp1) {
                    Intent aIntent = new Intent(MainActivity.this, ktpCardActivity.class);
                    startActivity(aIntent);
                    return true;
                }
                else if (item.getItemId() == R.id.ktp2) {
                    Intent aIntent = new Intent(MainActivity.this, ktpkuCardActivity.class);
                    startActivity(aIntent);
                    return true;
                }
                return false;
            }
        });
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