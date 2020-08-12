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

        signBtn = findViewById(R.id.sign_in);
        mAuth = FirebaseAuth.getInstance();
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
                    Intent i = new Intent(Login.this, Home.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(Login.this, "Access denied", Toast.LENGTH_LONG).show();
                }
            }
        };
        btnIn.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(Login.this, "Field must be filled", Toast.LENGTH_LONG).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){
                    mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Login.this, "Signup first!!!", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Intent intHome = new Intent(Login.this, Home.class);
                                startActivity(intHome);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(Login.this, "Access Denied", Toast.LENGTH_LONG).show();
                }
            }
        });

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
