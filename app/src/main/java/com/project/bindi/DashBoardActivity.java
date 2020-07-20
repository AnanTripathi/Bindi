package com.project.bindi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashBoardActivity extends AppCompatActivity {
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        actionBar=getSupportActionBar();
        actionBar.setTitle("People");
        BottomNavigationView bottomNavigationView=findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedlistener);


        PeopleFragment fragment1=new PeopleFragment();
        FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener selectedlistener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_people:
                            actionBar.setTitle("People");
                            PeopleFragment fragment1=new PeopleFragment();
                            FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content,fragment1,"");
                            ft1.commit();
                            return true;
                        case R.id.nav_voice_mails:
                            actionBar.setTitle("Voice Messages");
                            VoiceMailsFragment fragment2=new VoiceMailsFragment();
                            FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,fragment2,"");
                            ft2.commit();
                            return true;
                        case R.id.nav_profile:
                            actionBar.setTitle("Profile");
                            ProfileFragment fragment3=new ProfileFragment();
                            FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content,fragment3,"");
                            ft3.commit();
                            return true;
                    }
                    return false;
                }
            };
}