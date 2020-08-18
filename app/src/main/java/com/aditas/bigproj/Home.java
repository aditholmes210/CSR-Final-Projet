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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity{

    Button btnOut;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //set defaultmya home fragment
//        loadFragment(new HomeFragment());
        //inisialisasi BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bn_main);
        //beri listener pada saat item/menu bottomnav terpilih
        bottomNav.setOnNavigationItemSelectedListener( navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rv_container, new HomeFragment())
                .commit();


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
                    //return loadFragment(frag);
                }

    //method utk load fragment yg sesuai
//    private boolean loadFragment(Fragment homeFragment) {
//        if(homeFragment != null){
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.rv_container, homeFragment)
//                    .commit();
//            return true;
//        }
//        return false;
    };
}