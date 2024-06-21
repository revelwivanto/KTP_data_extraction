package com.example.ktp;

import static com.example.ktp.MyDatabaseHelper.*;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DisplayKTPActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;
    MyDatabaseHelper myDB;
    ArrayList<String> KTP_NIK, KTP_nama, KTP_TTL, KTP_alamat;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database);

        recyclerView = findViewById(R.id.recyclerView);

        myDB = new MyDatabaseHelper(DisplayKTPActivity.this);
        KTP_NIK = new ArrayList<>();
        KTP_nama = new ArrayList<>();
        KTP_TTL = new ArrayList<>();
        KTP_alamat = new ArrayList<>();

        storeDatainArrays();

        customAdapter = new CustomAdapter(DisplayKTPActivity.this, KTP_NIK, KTP_nama,
                KTP_TTL, KTP_alamat);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(DisplayKTPActivity.this));
    }

    private void storeDatainArrays() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                KTP_NIK.add(cursor.getString(0));
                KTP_nama.add(cursor.getString(1));
                KTP_TTL.add(cursor.getString(2));
                KTP_alamat.add(cursor.getString(3));
            }
        }
    }
}
