package com.aditas.bigproj.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.aditas.bigproj.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DetailFragment extends Fragment {

    String postid;
    private RecyclerView recV;
    private PostAdapt pAdapt;
    private List<Posm> pmList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        SharedPreferences pref = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid = pref.getString("postid", "none");

        recV = v.findViewById(R.id.rec_view);
        recV.setHasFixedSize(true);;
        LinearLayoutManager lineMgr = new LinearLayoutManager(getContext());
        recV.setLayoutManager(lineMgr);

        pmList = new ArrayList<>();
        pAdapt = new PostAdapt(getContext(), pmList);
        recV.setAdapter(pAdapt);

        readPost();
        return v;
    }

    private void readPost(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pmList.clear();
                Posm posm = dataSnapshot.getValue(Posm.class);
                pmList.add(posm);
                pAdapt.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
