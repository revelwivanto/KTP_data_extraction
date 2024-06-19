package com.example.ktp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import java.io.ByteArrayOutputStream;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String DATABASE_NAME = "KTP.db";
    public static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "KTPlib";
    private static final String COLUMN_NIK = "KTP_NIK";
    private static final String COLUMN_NAMA = "KTP_nama";
    private static final String COLUMN_TTL = "KTP.TTL";
    private static final String COLUMN_ALAMAT = "KTP.alamat";
    private static final String COLUMN_SIGNATURE = "KTP.signature";
    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NIK + " INTEGER, " +
                        COLUMN_NAMA + " TEXT, " +
                        COLUMN_TTL + " TEXT, " +
                        COLUMN_ALAMAT + " TEXT, " +
                        COLUMN_SIGNATURE + " BLOB);";  // Corrected syntax and added BLOB type
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addKTP(int NIK, String NAMA, String TTL, String ALAMAT, @Nullable Bitmap SIGNATURE){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NIK, NIK);
        cv.put(COLUMN_NAMA, NAMA);
        cv.put(COLUMN_TTL, TTL);
        cv.put(COLUMN_ALAMAT, ALAMAT);

        if (SIGNATURE != null) {
            // Convert the bitmap to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            SIGNATURE.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] signatureBytes = outputStream.toByteArray();
            cv.put(COLUMN_SIGNATURE, signatureBytes);
        } else {
            cv.putNull(COLUMN_SIGNATURE);
        }

        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

}