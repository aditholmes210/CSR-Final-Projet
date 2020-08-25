package com.aditas.bigproj.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aditas.bigproj.Adapter.PostAdapt;
import com.aditas.bigproj.Model.Posm;
import com.aditas.bigproj.Post;
import com.aditas.bigproj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recycle;
    private PostAdapt adapt;
    private List<Posm> posmList;
    private List<String> followList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(com.aditas.bigproj.R.layout.fragment_home, container, false);
        recycle = v.findViewById(R.id.recycle);
        recycle.setHasFixedSize(true);
        LinearLayoutManager layoutMgr = new LinearLayoutManager(getContext());
        layoutMgr.setReverseLayout(true);
        layoutMgr.setStackFromEnd(true);
        recycle.setLayoutManager(layoutMgr);
        posmList = new ArrayList<>();
        adapt = new PostAdapt(getContext(), posmList);
        recycle.setAdapter(adapt);

        checkFollowing();
        return v;
    }

    private void checkFollowing(){
        followList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followList.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    followList.add(snap.getKey());
                }
                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPost(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnap) {
                posmList.clear();
                for(DataSnapshot snap : dataSnap.getChildren()){
                    Posm post = snap.getValue(Posm.class);
                    for(String id : followList){
                        if(post.getPublish().equals(id)){
                            posmList.add(post);
                        }
                    }
                }
                adapt.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
