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

        for (FirebaseVisionText.Block block: text.getBlocks()){
            for (FirebaseVisionText.Line line: block.getLines()){
                Rect rect = line.getBoundingBox();
                assert rect != null;
                String y = String.valueOf(rect.exactCenterY());
                String lineText = line.getText().toLowerCase();
                map.put(y, lineText);
            }
        }

        List<String> orderedData = new ArrayList<>(map.values());
        HashMap<String, String> dataMap = new HashMap<>();

        int i = 0;
        //mencari nomor ktp (nik)
        String regx = "\\b\\w{16}\\b";
        for (i = 0; i<orderedData.size(); i++){
            if (orderedData.get(i).matches(regx)){
                String nik = orderedData.get(i).replace('l', '6');
                dataMap.put("nik", nik);
                break;
            }
        }

        //mencari nama
        for (i=0; i<orderedData.size(); i++) {
            if (orderedData.get(i).contains("nama")) {
                dataMap.put("nama", orderedData.get(i+1));
                break;
            }
        }

        //searching for tempat tanggal lahir
        String regex = ".*, \\d{2}-\\d{2}-\\d{4}";
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
