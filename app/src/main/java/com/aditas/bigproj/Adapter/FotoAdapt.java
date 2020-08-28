package com.aditas.bigproj.Adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aditas.bigproj.Model.Posm;
import com.aditas.bigproj.R;
import com.aditas.bigproj.fragment.DetailFragment;
import com.bumptech.glide.Glide;

import java.util.List;

public class FotoAdapt extends RecyclerView.Adapter<FotoAdapt.ViewHolder>{

    private Context con;
    private List<Posm> mPosm;

    public FotoAdapt(Context context, List<Posm> mPosm){
        this.con = context;
        this.mPosm = mPosm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(con).inflate(R.layout.fotos_item, parent, false);
        return new FotoAdapt.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        final Posm posm = mPosm.get(pos);
        Glide.with(con).load(posm.getPostimg()).into(holder.postImg);

        holder.postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor etr = con.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                etr.putString("postid", posm.getPostid());
                etr.apply();

                ((FragmentActivity)con).getSupportFragmentManager().beginTransaction().replace(R.id.frag_con,
                        new DetailFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosm.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView postImg;

        public ViewHolder(@NonNull View v){
            super(v);
            postImg = v.findViewById(R.id.post_img);
        }
    }
}
