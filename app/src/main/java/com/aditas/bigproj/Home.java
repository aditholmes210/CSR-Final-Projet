package com.aditas.bigproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    Button btnOut;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnOut = findViewById(R.id.btn_out);
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intReg = new Intent(Home.this, Regist.class);
                startActivity(intReg);
            }
        });
    }
}
