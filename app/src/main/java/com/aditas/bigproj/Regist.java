package com.aditas.bigproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Regist extends AppCompatActivity {

    EditText mail, pass;
    Button btnUp;
    TextView tvIn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        mAuth = FirebaseAuth.getInstance();
        mail = findViewById(R.id.et_mail);
        pass = findViewById(R.id.et_pass);
        btnUp = findViewById(R.id.btn_up);
        tvIn = findViewById(R.id.tv_in);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getText().toString();
                String pwd = pass.getText().toString();
                if(email.isEmpty()){
                    mail.setError("Email require");
                    mail.requestFocus();
                }
                else if(pwd.isEmpty()){
                    pass.setError("Password require");
                    pass.requestFocus();
                }
                else if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(Regist.this, "Field must be filled", Toast.LENGTH_LONG).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(Regist.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Regist.this, "Signup failed!!!", Toast.LENGTH_LONG).show();
                            }
                            else{
                                startActivity(new Intent(Regist.this, Home.class));

                            }
                        }
                    });
                }
                else {
                    Toast.makeText(Regist.this, "Access Denied", Toast.LENGTH_LONG).show();
                }
            }
        });
        tvIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Regist.this, Login.class);
                startActivity(i);
            }
        });
    }
}
