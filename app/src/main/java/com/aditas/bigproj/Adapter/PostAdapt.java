package com.aditas.bigproj.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aditas.bigproj.Model.Posm;
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

public class PostAdapt extends RecyclerView.Adapter<PostAdapt.ViewHolder>{

    public Context mCon;
    public List<Posm> mPosm;
    private FirebaseUser fUser;

    public PostAdapt (Context mCon, List<Posm> mPosm){
        this.mCon = mCon;
        this.mPosm = mPosm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mCon).inflate(R.layout.post_item, parent, false);
        return new PostAdapt.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Posm posm = mPosm.get(i);
        Glide.with(mCon).load(posm.getPostimg()).into(holder.postImg);

        if(posm.getDesc().equals("")){
            holder.desc.setVisibility(View.GONE);
        } else {
            holder.desc.setVisibility(View.VISIBLE);
            holder.desc.setText(posm.getDesc());
        }

        publishInfo(holder.imgProf, holder.user, holder.publish, posm.getPublish());
    }

    @Override
    public int getItemCount() {
        return mPosm.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgProf, postImg, like, com, save;
        public TextView user, likes, publish, desc, coms;

        public ViewHolder (@NonNull View v){
            super(v);

            imgProf = v.findViewById(R.id.img_prof);
            postImg = v.findViewById(R.id.post_img);
            com = v.findViewById(R.id.com);
            like = v.findViewById(R.id.like);
            coms = v.findViewById(R.id.comment);
            save = v.findViewById(R.id.save);
            user = v.findViewById(R.id.tv_usn);
            likes = v.findViewById(R.id.likes);
            publish = v.findViewById(R.id.pub);
            desc = v.findViewById(R.id.desc);
        }
    }

    private void publishInfo(final ImageView imgProf, final TextView uname, final TextView publish, final String userid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User usr = dataSnapshot.getValue(User.class);
                Glide.with(mCon).load(usr.getImgurl()).into(imgProf);
                uname.setText(usr.getUname());
                publish.setText(usr.getUname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
