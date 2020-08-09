package com.project.bindi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DashBoardActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private String Uid;
    static public User loggedInUser;
    private DatabaseReference usersDatabaseReference;
    private static final String TAG = "DashBoardActivity";
    PeopleFragment fragment1;
    FragmentTransaction ft;
    VoiceMailsFragment fragment2;
    ProfileFragment fragment3;
    FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        firebaseAuth=FirebaseAuth.getInstance();
        usersDatabaseReference=FirebaseDatabase.getInstance().getReference("Users");

        checkUserStatus();
        actionBar=getSupportActionBar();
        actionBar.setTitle("People");
        BottomNavigationView bottomNavigationView=findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedlistener);

        if(fm==null){
        fm=getSupportFragmentManager();

            if(fm.findFragmentByTag("one") != null) {
                //if the fragment exists, show it.
                fm.beginTransaction().show(fm.findFragmentByTag("one")).commit();
            } else {
                //if the fragment does not exist, add it to fragment manager.
                fm.beginTransaction().add(R.id.content, new PeopleFragment(), "one").commit();
            }
            if(fm.findFragmentByTag("two") != null){
                //if the other fragment is visible, hide it.
                fm.beginTransaction().hide(fm.findFragmentByTag("two")).commit();
            }
            if(fm.findFragmentByTag("three") != null){
                //if the other fragment is visible, hide it.
                fm.beginTransaction().hide(fm.findFragmentByTag("three")).commit();
            }
        }

        final Query userQuery = usersDatabaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid());
        userQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                loggedInUser = snapshot.child(firebaseAuth.getUid()).getValue(User.class);}
                catch (NullPointerException exception){
                    Toast.makeText(DashBoardActivity.this, "Network issue", Toast.LENGTH_SHORT).show();
                    Log.e(TAG,""+exception.getMessage());
                }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private BottomNavigationView.OnNavigationItemSelectedListener selectedlistener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_people:
                            actionBar.setTitle("People");
                            if(fm.findFragmentByTag("one") != null) {
                                //if the fragment exists, show it.
                                fm.beginTransaction().show(fm.findFragmentByTag("one")).commit();
                            } else {
                                //if the fragment does not exist, add it to fragment manager.
                                fm.beginTransaction().add(R.id.content, new PeopleFragment(), "one").commit();
                            }
                            if(fm.findFragmentByTag("two") != null){
                                //if the other fragment is visible, hide it.
                                fm.beginTransaction().hide(fm.findFragmentByTag("two")).commit();
                            }
                            if(fm.findFragmentByTag("three") != null){
                                //if the other fragment is visible, hide it.
                                fm.beginTransaction().hide(fm.findFragmentByTag("three")).commit();
                            }
                            return true;
                        case R.id.nav_voice_mails:
                            actionBar.setTitle("Voice Messages");
                            if(fm.findFragmentByTag("two") != null) {
                                //if the fragment exists, show it.
                                fm.beginTransaction().show(fm.findFragmentByTag("two")).commit();
                            } else {
                                //if the fragment does not exist, add it to fragment manager.
                                fm.beginTransaction().add(R.id.content, new VoiceMailsFragment(), "two").commit();
                            }
                            if(fm.findFragmentByTag("one") != null){
                                //if the other fragment is visible, hide it.
                                fm.beginTransaction().hide(fm.findFragmentByTag("one")).commit();
                            }
                            if(fm.findFragmentByTag("three") != null){
                                //if the other fragment is visible, hide it.
                                fm.beginTransaction().hide(fm.findFragmentByTag("three")).commit();
                            }
                            return true;
                        case R.id.nav_profile:
                            actionBar.setTitle("Profile");
                            if(fm.findFragmentByTag("three") != null) {
                                //if the fragment exists, show it.
                                fm.beginTransaction().show(fm.findFragmentByTag("three")).commit();
                            } else {
                                //if the fragment does not exist, add it to fragment manager.
                                fm.beginTransaction().add(R.id.content, new ProfileFragment(), "three").commit();
                            }
                            if(fm.findFragmentByTag("one") != null){
                                //if the other fragment is visible, hide it.
                                fm.beginTransaction().hide(fm.findFragmentByTag("one")).commit();
                            }
                            if(fm.findFragmentByTag("two") != null){
                                //if the other fragment is visible, hide it.
                                fm.beginTransaction().hide(fm.findFragmentByTag("two")).commit();
                            }
                            return true;
                    }
                    return false;
                }
            };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_logout:
                firebaseAuth.signOut();
                checkUserStatus();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    private void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            if(user.getDisplayName()!=null&&user.getDisplayName().equals("false")){
                Intent intent=new Intent(DashBoardActivity.this,UpdateUserActivity.class);
                intent.putExtra("parent",TAG);
                startActivity(intent);
                finish();
            }
        }
        else{
            startActivity(new Intent(DashBoardActivity.this,LoginActivity.class));
            finish();
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        checkUserStatus();

    }
}