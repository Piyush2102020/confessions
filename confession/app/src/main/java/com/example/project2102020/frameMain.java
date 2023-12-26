package com.example.project2102020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class frameMain extends AppCompatActivity {

    BottomNavigationView bnView;
    FloatingActionButton writePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_main);

        bnView=findViewById(R.id.bnView);
        writePost=findViewById(R.id.writePost);




        writePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), writePost.class));
            }
        });
        bnView.setSelectedItemId(R.id.homeTab);
        loadFrag(new home());
        bnView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId=item.getItemId();

                if (itemId==R.id.homeTab){

                    writePost.setVisibility(View.VISIBLE);
                    loadFrag(new home());

                }  else if (itemId==R.id.globeTab) {
                    loadFrag(new globe());
                    writePost.setVisibility(View.GONE);
                }

                else {

                    loadFrag(new user());
                    writePost.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }


    public void loadFrag(Fragment fragment){

        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.frameLay,fragment);
        ft.commit();
    }
}