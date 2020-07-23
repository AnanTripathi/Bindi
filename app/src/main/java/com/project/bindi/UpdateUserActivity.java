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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UpdateUserActivity extends AppCompatActivity {
    Spinner genderSpinner;
    String gender;
    FirebaseAuth firebaseAuth;
    EditText nameEt,ageEt,descriptionEt;
    Button save;
    ProgressBar  progressBar;
    FirebaseUser firebaseUser;
    User userdata;
    DatabaseReference usersDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        usersDatabaseReference=FirebaseDatabase.getInstance().getReference("Users");
        progressBar=findViewById(R.id.indeterminateBar);
        firebaseAuth=FirebaseAuth.getInstance();
        nameEt=findViewById(R.id.nameEt);
        ageEt=findViewById(R.id.ageEt);
        descriptionEt=findViewById(R.id.descriptionEt);
        genderSpinner=(Spinner)findViewById(R.id.gender_spinner);
        save=findViewById(R.id.save);
        save.setEnabled(false);
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
                progressBar.setVisibility(View.VISIBLE);
                save.setEnabled(false);
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
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent=new Intent(UpdateUserActivity.this,DashBoardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateUserActivity.this, "there was exception please try again", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                save.setEnabled(true);
                            }
                        });

                    }
                }
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query userQuery=usersDatabaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              userdata=snapshot.child(firebaseAuth.getUid()).getValue(User.class);
              if(userdata.isProfileComplete()){
                nameEt.setText(userdata.getName());
                ageEt.setText(userdata.getAge());
                int position;
                if(userdata.getGender().equals("Male")){
                    position=0;
                }
                else{
                    position=1;
                }
                genderSpinner.setSelection(position);
                descriptionEt.setText(userdata.getDescription());
               }
                progressBar.setVisibility(View.GONE);
                save.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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