// CustomAdapter.java
package com.example.ktp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList nik, nama, ttl, alamat;
    private ArrayList<byte[]> signature;

    CustomAdapter(Context context, ArrayList nik, ArrayList nama, ArrayList ttl, ArrayList alamat, ArrayList<byte[]> signature){
        this.context = context;
        this.nik = nik;
        this.nama = nama;
        this.ttl = ttl;
        this.alamat = alamat;
        this.signature = signature;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nik_txt.setText(String.valueOf(nik.get(position)));
        holder.nama_txt.setText(String.valueOf(nama.get(position)));
        holder.ttl_txt.setText(String.valueOf(ttl.get(position)));
        holder.alamat_txt.setText(String.valueOf(alamat.get(position)));
        byte[] signatureBytes = signature.get(position);
        if (signatureBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(signatureBytes, 0, signatureBytes.length);
            holder.ttd.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return nik.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nik_txt, nama_txt, ttl_txt, alamat_txt;
        ImageView ttd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nik_txt = itemView.findViewById(R.id.NIK_txt);
            nama_txt = itemView.findViewById(R.id.nama_txt);
            ttl_txt = itemView.findViewById(R.id.ttl_txt);
            alamat_txt = itemView.findViewById(R.id.alamat_txt);
            ttd = itemView.findViewById(R.id.ttd);
        }
    }
}
