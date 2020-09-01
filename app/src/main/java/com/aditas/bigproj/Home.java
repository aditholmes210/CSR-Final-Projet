package com.aditas.bigproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.aditas.bigproj.fragment.AccFragment;
import com.aditas.bigproj.fragment.FavFragment;
import com.aditas.bigproj.fragment.HomeFragment;
import com.aditas.bigproj.fragment.SearchFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity{

    Button btnOut;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthList;
    FirebaseUser firebaseUser;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //set defaultmya home fragment
//        loadFragment(new HomeFragment());
        //inisialisasi BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bn_main);
        //beri listener pada saat item/menu nav terpilih
        bottomNav.setOnNavigationItemSelectedListener( navigationItemSelectedListener);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(String.valueOf(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        Bundle intn = getIntent().getExtras();
        if(intn != null){
            String publish = intn.getString("publishid");
            SharedPreferences.Editor etr = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            etr.putString("profileid", publish);
            etr.apply();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rv_container, new AccFragment())
                    .commit();
        } else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rv_container, new HomeFragment())
                    .commit();
        }

//        btnOut = findViewById(R.id.btn_out);
//        btnOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intReg = new Intent(Home.this, Regist.class);
//                startActivity(intReg);
//            }
//        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                //method listener utk logika pemilihan
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    Fragment frag = null;
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            frag = new HomeFragment();
                            break;
                        case R.id.nav_src:
                            frag = new SearchFragment();
                            break;
                        case R.id.nav_add:
                            frag = null;
                            startActivity(new Intent (Home.this, Post.class));
                            break;
                        case R.id.nav_fav:
                            frag = new FavFragment();
                            break;
                        case R.id.nav_acc:
                            SharedPreferences.Editor edit = getSharedPreferences("PREF", MODE_PRIVATE).edit();
                            edit.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            edit.apply();;
                            frag = new AccFragment();
                            break;
                        default:
                            break;
                    }
                    if(frag != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.rv_container, frag)
                                .commit();
                    }
                    return true;
                }
    };

    private void updateUI(FirebaseUser user){
        if (user==null){
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        }
    }
}