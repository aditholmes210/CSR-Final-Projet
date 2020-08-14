package com.aditas.bigproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Regist extends AppCompatActivity {

    EditText uname, mail, pass;
    Button btnR;
    TextView tvIn;

    FirebaseAuth mAuth;
    DatabaseReference ref;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        mAuth = FirebaseAuth.getInstance();
        uname = findViewById(R.id.et_unam);
        mail = findViewById(R.id.et_mail);
        pass = findViewById(R.id.et_pass);
        btnR = findViewById(R.id.btn_reg);
        tvIn = findViewById(R.id.tv_in);
//        btnR.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = mail.getText().toString();
//                String pwd = pass.getText().toString();
//                if(email.isEmpty()){
//                    mail.setError("Email require");
//                    mail.requestFocus();
//                }
//                else if(pwd.isEmpty()){
//                    pass.setError("Password require");
//                    pass.requestFocus();
//                }
//                else if(email.isEmpty() && pwd.isEmpty()){
//                    Toast.makeText(Regist.this, "Field must be filled", Toast.LENGTH_LONG).show();
//                }
//                else if(!(email.isEmpty() && pwd.isEmpty())){
//                    mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(Regist.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(!task.isSuccessful()){
//                                Toast.makeText(Regist.this, "Signup failed!!!", Toast.LENGTH_LONG).show();
//                            }
//                            else{
//                                startActivity(new Intent(Regist.this, Home.class));
//
//                            }
//                        }
//                    });
//                }
//                else {
//                    Toast.makeText(Regist.this, "Access Denied", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
        tvIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Regist.this, Login.class);
                startActivity(i);
            }
        });

        btnR.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(Regist.this);
                pd.setMessage("Cotto Matte Kudasai...");
                pd.show();

                String str_username = uname.getText().toString();
                String str_email = mail.getText().toString();
                String str_password = pass.getText().toString();

                if (TextUtils.isEmpty(str_username) || (TextUtils.isEmpty(str_email) || (TextUtils.isEmpty(str_password)))){
                    Toast.makeText(Regist.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    str_password.length();
                    Toast.makeText(Regist.this, "Password must have at least 6 character", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void regist(String uname, String mail, String pass){
        mAuth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(Regist.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser fUser = mAuth.getCurrentUser();
                            String userid = fUser.getUid();
                            ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            HashMap<String, Object> hash = new HashMap<>();
                            hash.put("id", userid);
                            hash.put("username", userid.toLowerCase());
                            hash.put("bio", "");
                            hash.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/bigproj-c7b8e.appspot.com/o/blank.webp?alt=media&token=9615cbbe-3ec3-4c26-8645-9f040f0877d3");

                            ref.setValue(hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        pd.dismiss();
                                        Intent i = new Intent(Regist.this, Home.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                }
                            });
                        }else{
                            pd.dismiss();
                            Toast.makeText(Regist.this, "You can't register with this email/password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
