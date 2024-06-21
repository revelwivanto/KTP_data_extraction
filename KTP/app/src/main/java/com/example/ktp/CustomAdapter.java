package com.example.ktp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList KTP_NIK, KTP_nama, KTP_TTL, KTP_alamat;

    CustomAdapter( Context context, ArrayList KTP_NIK, ArrayList KTP_nama, ArrayList KTP_TTL,
                  ArrayList KTP_alamat){
        this.activity = activity;
        this.context = context;
        this.KTP_NIK = KTP_NIK;
        this.KTP_nama = KTP_nama;
        this.KTP_TTL = KTP_TTL;
        this.KTP_alamat = KTP_alamat;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.KTP_NIK_txt.setText(String.valueOf(KTP_NIK.get(position)));
        holder.KTP_nama_txt.setText(String.valueOf(KTP_nama.get(position)));
        holder.KTP_TTL_txt.setText(String.valueOf(KTP_TTL.get(position)));
        holder.KTP_alamat_txt.setText(String.valueOf(KTP_alamat.get(position)));

    }

    @Override
    public int getItemCount() {
        return KTP_NIK.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView KTP_NIK_txt, KTP_nama_txt, KTP_TTL_txt, KTP_alamat_txt;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            KTP_NIK_txt = itemView.findViewById(R.id.KTP_NIK_txt);
            KTP_nama_txt = itemView.findViewById(R.id.KTP_nama_txt);
            KTP_TTL_txt = itemView.findViewById(R.id.KTP_TTL_txt);
            KTP_alamat_txt = itemView.findViewById(R.id.KTP_alamat_txt);
        }

    }

}