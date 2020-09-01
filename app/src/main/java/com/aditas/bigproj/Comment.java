package com.aditas.bigproj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aditas.bigproj.Adapter.ComAdapt;
import com.aditas.bigproj.Model.Com;
import com.aditas.bigproj.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment extends AppCompatActivity {

    private RecyclerView recyV;
    private ComAdapt cAdapt;
    private List<Com> comList;

    EditText addCom;
    ImageView imgProf;
    TextView post;
    String postid, publishid;
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

        recyV = findViewById(R.id.rec_view);
        recyV.setHasFixedSize(true);
        LinearLayoutManager lineMgr = new LinearLayoutManager(this);
        recyV.setLayoutManager(lineMgr);
        comList = new ArrayList<>();
        cAdapt = new ComAdapt(this, comList);
        recyV.setAdapter(cAdapt);

        addCom = findViewById(R.id.add_com);
        imgProf = findViewById(R.id.img_prof);
        post = findViewById(R.id.post);
        Intent i = getIntent();
        postid = i.getStringExtra("postid");
        publishid = i.getStringExtra("publishid");
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
        readCom();
    }

    private void addComs(){
        String text = addCom.getText().toString();
        String mCom = fUser.getUid();
        Map<String,Object> map = new HashMap<>();
        map.put("text",addCom.getText().toString());
        map.put("mrComment",fUser.getUid());
        Com com = new Com(text, mCom);
        FirebaseFirestore.getInstance()
                .collection("comments")
                .document(postid)
                .collection(postid)
                .add(com)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(Comment.this, "Comment Berhasil Di Kirim", Toast.LENGTH_SHORT).show();
                        addCom.setText("");
                    }
                });
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

    private void readCom(){
        FirebaseFirestore.getInstance().collection("comments").document(postid).collection(postid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        for (DocumentSnapshot snapshot : value){
                            Com com = snapshot.toObject(Com.class);
                            comList.add(com);
                        }
                        cAdapt.notifyDataSetChanged();
                    }
                });
    }
}
