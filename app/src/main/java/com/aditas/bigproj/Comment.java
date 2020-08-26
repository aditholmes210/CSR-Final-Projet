package com.aditas.bigproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.aditas.bigproj.Model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Comment extends AppCompatActivity {

    EditText addCom;
    ImageView imgProf;
    TextView post;
    String postid;
    String publishid;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar tlb = findViewById(R.id.toolbar);
        setSupportActionBar(tlb);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tlb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addCom = findViewById(R.id.add_com);
        imgProf = findViewById(R.id.img_prof);
        post = findViewById(R.id.post);
        Intent intn = getIntent();
        postid = intn.getStringExtra("postid");
        publishid = intn.getStringExtra("publishid");
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addCom.getText().toString().equals("")){
                    Toast.makeText(Comment.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
                } else {
                    addComs();
                }
            }
        });
        getImg();
    }

    private void addComs(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postid);
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("comment", addCom.getText().toString());
        hash.put("publish", fUser.getUid());
        ref.push().setValue(hash);
        addCom.setText("");
    }

    private void getImg(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                .child(fUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User usr = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(usr.getImgurl()).into(imgProf);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}