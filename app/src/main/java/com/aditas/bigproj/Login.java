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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private SignInButton signBtn;
    private GoogleSignInClient mGoogle;
    private String TAG = "Login";
    EditText mail, pass;
    Button btnIn;
    TextView tvReg;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthList;
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        signBtn = findViewById(R.id.sign_in);
        mail = findViewById(R.id.et_mail);
        pass = findViewById(R.id.et_pass);
        btnIn = findViewById(R.id.btn_in);
        tvReg = findViewById(R.id.tv_reg);
        mAuthList = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFireUser = mAuth.getCurrentUser();
                if(mFireUser != null){
                    Toast.makeText(Login.this, "Access granted", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Login.this, SetUp.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(Login.this, "Access denied", Toast.LENGTH_LONG).show();
                }
            }
        };
        //tanpa username
//        btnIn.setOnClickListener(new View.OnClickListener() {
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
//                    Toast.makeText(Login.this, "Field must be filled", Toast.LENGTH_LONG).show();
//                }
//                else if(!(email.isEmpty() && pwd.isEmpty())){
//                    mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(!task.isSuccessful()){
//                                Toast.makeText(Login.this, "Signup first!!!", Toast.LENGTH_LONG).show();
//                            }
//                            else{
//                                Intent intHome = new Intent(Login.this, Home.class);
//                                startActivity(intHome);
//                            }
//                        }
//                    });
//                }
//                else {
//                    Toast.makeText(Login.this, "Access Denied", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogle = GoogleSignIn.getClient(this, gso);

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intReg = new Intent(Login.this, Regist.class);
                startActivity(intReg);
            }
        });

        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                final ProgressDialog pd = new ProgressDialog(Login.this);
                pd.setMessage("Wait a minute....");
                pd.show();

                String str_email = mail.getText().toString();
                String str_password = pass.getText().toString();

                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(Login.this, "All fields required", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(str_email, str_password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(mAuth.getCurrentUser().getUid());

                                        ref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                pd.dismiss();
                                                Intent i = new Intent(Login.this, Home.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                pd.dismiss();
                                            }
                                        });
                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(Login.this, "Authentification failed!!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void signIn() {
        Intent signInt = mGoogle.getSignInIntent();
        startActivityForResult(signInt, RC_SIGN_IN);
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthList);
    }
}
