package com.project.bindi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateUserActivity extends AppCompatActivity {
    Spinner genderSpinner;
    String gender;
    FirebaseAuth firebaseAuth;
    EditText nameEt,ageEt,descriptionEt;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        firebaseAuth=FirebaseAuth.getInstance();
        nameEt=findViewById(R.id.nameEt);
        ageEt=findViewById(R.id.ageEt);
        descriptionEt=findViewById(R.id.descriptionEt);
        genderSpinner=(Spinner)findViewById(R.id.gender_spinner);
        save=findViewById(R.id.save);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    gender="Male";
                }
                else{
                    gender="Female";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender="Male";
            }
        });
// Apply the adapter to the spinner
        genderSpinner.setAdapter(adapter);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=nameEt.getText().toString().trim();
                String age=ageEt.getText().toString().trim();
                String description=descriptionEt.getText().toString().trim();
                if(name.length()==0||age.length()==0||description.length()==0){
                    Toast.makeText(UpdateUserActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                }
                else{
                    FirebaseUser u=firebaseAuth.getCurrentUser();
                    User user=new User(u.getUid(),u.getEmail(),name,age,gender,description,null);
                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference=firebaseDatabase.getReference("Users");
                    databaseReference.child(u.getUid()).setValue(user);
                    if(user.isProfileComplete()){
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName("true")
                                .build();

                        u.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        }
                                    }
                                });
                        Intent intent=new Intent(UpdateUserActivity.this,DashBoardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }



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
            // profileTv.setText(user.getEmail());
        }
        else{
            startActivity(new Intent(UpdateUserActivity.this,LoginActivity.class));
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