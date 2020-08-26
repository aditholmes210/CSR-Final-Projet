package com.aditas.bigproj.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aditas.bigproj.Home;
import com.aditas.bigproj.Model.Com;
import com.aditas.bigproj.Model.User;
import com.aditas.bigproj.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ComAdapt extends RecyclerView.Adapter<ComAdapt.ViewHolder> {

    private Context mCon;
    private List<Com> mCom;
    private FirebaseUser fUser;

    public ComAdapt(Context mCon, List<Com> mCom){
        this.mCon = mCon;
        this.mCom = mCom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mCon).inflate(R.layout.com_item, parent, false);
        return new ComAdapt.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        final Com com = mCom.get(pos);
        holder.com.setText(com.getComment());
        getUserInfo(holder.imgProf, holder.usern, com.getPublish());

        holder.com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intn = new Intent(mCon, Home.class);
                intn.putExtra("publisherid", com.getPublish());
                mCon.startActivity(intn);
            }
        });
        holder.imgProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intn = new Intent(mCon, Home.class);
                intn.putExtra("publisherid", com.getPublish());
                mCon.startActivity(intn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCom.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgProf;
        public TextView usern, com;

        public ViewHolder(@NonNull View v){
            super(v);

            imgProf = v.findViewById(R.id.img_prof);
            usern = v.findViewById(R.id.username);
            com = v.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView img, final TextView username, String publishid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publishid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User usr = dataSnapshot.getValue(User.class);
                Glide.with(mCon).load(usr.getImgurl()).into(img);
                username.setText(usr.getUname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
