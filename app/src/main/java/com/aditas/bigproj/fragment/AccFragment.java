package com.aditas.bigproj.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aditas.bigproj.Adapter.FotoAdapt;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccFragment extends Fragment {

    ImageView imgProf, opt;
    TextView posts, flr, flg, fulln, bio, usern;
    Button editProf;
    FirebaseUser fUser;
    String profid;
    ImageButton myPic, savePic;
    RecyclerView recV;
    FotoAdapt fAdapt;
    List<Posm> posList;

    private List<String> mySave;
    RecyclerView recVSave;
    FotoAdapt fAdaptSave;
    List<Posm> posListSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_acc, container, false);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences pref = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profid = pref.getString("profileid", "none");

        imgProf = v.findViewById(R.id.img_prof);
        opt = v.findViewById(R.id.option);
        posts = v.findViewById(R.id.tot_posts);
        flr = v.findViewById(R.id.tot_followers);
        flg = v.findViewById(R.id.tot_followings);
        fulln = v.findViewById(R.id.fullname);
        bio = v.findViewById(R.id.bio);
        usern = v.findViewById(R.id.username);
        editProf = v.findViewById(R.id.btn_update);
        myPic = v.findViewById(R.id.my_pics);
        savePic = v.findViewById(R.id.my_save);

        recV = v.findViewById(R.id.recP);
        recV.setHasFixedSize(true);
        LinearLayoutManager layMgr = new GridLayoutManager(getContext(), 3);
        recV.setLayoutManager(layMgr);
        posList = new ArrayList<>();
        fAdapt = new FotoAdapt(getContext(), posList);
        recV.setAdapter(fAdapt);

        recVSave = v.findViewById(R.id.recM);
        recVSave.setHasFixedSize(true);
        LinearLayoutManager layMgrS = new GridLayoutManager(getContext(), 3);
        recVSave.setLayoutManager(layMgrS);
        posListSave = new ArrayList<>();
        fAdaptSave = new FotoAdapt(getContext(), posListSave);
        recVSave.setAdapter(fAdaptSave);

        recV.setVisibility(View.VISIBLE);
        recVSave.setVisibility(View.GONE);

        userInfo();
        getFollow();
        getNrPosts();
        myPic();
        mySave();

        if (profid.equals(fUser.getUid())){
            editProf.setText("Edit Profile");
        } else {
            checkFollow();
            savePic.setVisibility(View.GONE);
        }

        editProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = editProf.getText().toString();
                if(btn.equals("Edit Profile")){
                    //go to profile
                } else if (btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("following").child(profid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profid)
                            .child("followers").child(fUser.getUid()).setValue(true);
                } else if (btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("following").child(profid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profid)
                            .child("followers").child(fUser.getUid()).removeValue();
                }
            }
        });
        myPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recV.setVisibility(View.VISIBLE);
                recVSave.setVisibility(View.GONE);
            }
        });

        savePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recV.setVisibility(View.GONE);
                recVSave.setVisibility(View.VISIBLE);
            }
        });

        return v;
    }

    private void userInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                .child(profid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImgurl()).into(imgProf);
                usern.setText(user.getUname());
                fulln.setText(user.getFname());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(fUser.getUid()).child("following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profid).exists()){
                    editProf.setText("following");
                } else {
                    editProf.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollow(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profid).child("followers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flr.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference refs = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profid).child("following");
        refs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flr.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Posm posm = snap.getValue(Posm.class);
                    if (posm.getPublish().equals(profid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myPic(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posList.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Posm posm = snap.getValue(Posm.class);
                    if (posm.getPublish().equals(profid)){
                        posList.add(posm);
                    }
                }
                Collections.reverse(posList);
                fAdapt.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mySave(){
        mySave = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Saves")
                .child(fUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    mySave.add(snap.getKey());
                }
                readSave();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSave(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posListSave.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Posm posm = snap.getValue(Posm.class);
                    for (String id : mySave){
                        if (posm.getPostid().equals(id)){
                            posListSave.add(posm);
                        }
                    }
                }
                fAdaptSave.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
