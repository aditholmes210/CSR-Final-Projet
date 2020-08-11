package com.aditas.bigproj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.aditas.bigproj.fragment.AccFragment;
import com.aditas.bigproj.fragment.FavFragment;
import com.aditas.bigproj.fragment.HomeFragment;
import com.aditas.bigproj.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

    //Button btnOut;
    //FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //set defaultmya home fragment
        loadFragment(new HomeFragment());
        //inisialisasi BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bn_main);
        //beri listener pada saat item/menu bottomnav terpilih
        bottomNav.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) this);


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

    //method utk load fragment yg sesuai
    private boolean loadFragment(Fragment homeFragment) {
        if(homeFragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rv_container, homeFragment)
                    .commit();
            return true;
        }
        return false;
    }

    //method listener utk logika pemilihan
    //@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        Fragment frag = null;
        switch (item.getItemId()){
            case R.id.nav_home:
                frag = new HomeFragment();
                break;
            case R.id.nav_src:
                frag = new SearchFragment();
                break;
            case R.id.nav_fav:
                frag = new FavFragment();
                break;
            case R.id.nav_acc:
                frag = new AccFragment();
                break;
            default:
                break;
        }
        return loadFragment(frag);
    }
}