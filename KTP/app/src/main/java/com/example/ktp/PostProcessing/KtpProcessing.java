package com.example.ktp.PostProcessing;

import android.content.Context;
import android.graphics.Rect;
import android.widget.Toast;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class KtpProcessing {

    public HashMap<String, String> processExtractTextForFrontPic(FirebaseVisionText text, Context context){
        List<FirebaseVisionText.Block> blocks = text.getBlocks();

        if (blocks.size() == 0){
            Toast.makeText(context, "Text Tidak Ada :(", Toast.LENGTH_SHORT).show();
            return null;
        }
        TreeMap<String, String> map = new TreeMap<>();
        
        // Mengambil block-block teks dari hasil deteksi teks
        for (FirebaseVisionText.Block block: text.getBlocks()){
            // Mengambil line-line dari setiap block
            for (FirebaseVisionText.Line line: block.getLines()){
                // Mendapatkan pembatas dari setiap line
                Rect rect = line.getBoundingBox();
                assert rect != null;
                // Mendapatkan koordinat tengah y dari kotak pembatas (y-coordinate)
                String y = String.valueOf(rect.exactCenterY());
                // Mendapatkan teks dari setiap line dalam format huruf kecil
                String lineText = line.getText().toLowerCase();
                // Menyimpan nilai y dan lineText ke dalam TreeMap map dengan key y dan value lineText
                map.put(y, lineText);
            }
        }

        List<String> orderedData = new ArrayList<>(map.values());
        HashMap<String, String> dataMap = new HashMap<>();

        int i = 0;
        //mencari nomor ktp (nik)
        String regx = "\\b\\d{16}\\b";
        for (i = 0; i<orderedData.size(); i++){
            if (orderedData.get(i).matches(regx)){
                String nik = orderedData.get(i);
                dataMap.put("nik", nik);
                break;
            }
        }

        //mencari nama
        String regex = ".*, \\d{2}-\\d{2}-\\d{4}";
        for (i=0; i<orderedData.size(); i++) {
            if (orderedData.get(i).contains("nama") || orderedData.get(i).contains("namae")) {
                if (orderedData.get(i+1).matches(regex)){
                    dataMap.put("nama", orderedData.get(i-1));
                    break;
                }
                dataMap.put("nama", orderedData.get(i+1));
                break;
            }
        }

        //searching for tempat tanggal lahir
        for (i=0; i<orderedData.size(); i++) {
            if (orderedData.get(i).matches(regex)) {
                dataMap.put("ttl", orderedData.get(i));
                break;
            }
        }

        //mencari alamat ktp
        for (i = 0; i < orderedData.size(); i++){
            if (orderedData.get(i).contains("alamat")){
                dataMap.put("alamat", orderedData.get(i+1));
                break;
            }
        }

        return dataMap;

    }
}
