package com.aditas.bigproj.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aditas.bigproj.Adapter.UserAdapt;
import com.aditas.bigproj.Model.User;
import com.aditas.bigproj.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private RecyclerView recV;
    private UserAdapt adapt;
    private List<User> mUse;

    EditText srcBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        recV = v.findViewById(R.id.recycle);
        recV.setHasFixedSize(true);
        recV.setLayoutManager(new LinearLayoutManager(getContext()));

        srcBar = v.findViewById(R.id.src_bar);
        mUse = new ArrayList<>();
        adapt = new UserAdapt(getContext(), mUse);
        recV.setAdapter(adapt);

        readUsers();
        srcBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }

    private void searchUsers(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUse.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    User user = snap.getValue(User.class);
                    mUse.add(user);
                }

                adapt.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (srcBar.getText().toString().equals("")){
                    mUse.clear();
                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        mUse.add(user);
                    }

                    adapt.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
