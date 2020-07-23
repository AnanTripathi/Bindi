package com.project.bindi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.ExceptionCatchingInputStream;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class UpdateUserActivity extends AppCompatActivity {
    Spinner genderSpinner;
    String gender;
    FirebaseAuth firebaseAuth;
    EditText nameEt, ageEt, descriptionEt;
    Button save, editProfileImageBn;
    ProgressBar progressBar,imageProgressBar;
    FirebaseUser firebaseUser;
    User userdata;
    DatabaseReference usersDatabaseReference;
    FirebaseDatabase firebaseDatabase;
    ImageView imageView;
    String imageUri="";

    //for image
    StorageReference storageReference;
    String storagePath = "User_Profile_Cover_Images/";

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    Boolean isImageBeingUploaded=false;
    String cameraPermission[];
    String storagePermission[];
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        imageView=findViewById(R.id.profileImage);
        imageProgressBar=findViewById(R.id.progressImage);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        editProfileImageBn = findViewById(R.id.editprofileImageBn);
        progressBar = findViewById(R.id.indeterminateBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        nameEt = findViewById(R.id.nameEt);
        ageEt = findViewById(R.id.ageEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        save = findViewById(R.id.save);
        save.setEnabled(false);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    gender = "Male";
                } else {
                    gender = "Female";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = "Male";
            }
        });
// Apply the adapter to the spinner
        genderSpinner.setAdapter(adapter);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageProgressBar.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                save.setEnabled(false);
                String name = nameEt.getText().toString().trim();
                String age = ageEt.getText().toString().trim();
                String description = descriptionEt.getText().toString().trim();
                if (name.length() == 0 || age.length() == 0 || description.length() == 0) {
                    save.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    imageProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UpdateUserActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                } else {
                    userdata = new User(firebaseUser.getUid(), firebaseUser.getEmail(), name, age, gender, description, imageUri);
                    usersDatabaseReference.child(firebaseUser.getUid()).setValue(userdata);
                    if (userdata.isProfileComplete()) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName("true")
                                .build();

                        firebaseUser.updateProfile(profileUpdates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(UpdateUserActivity.this, DashBoardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateUserActivity.this, "there was exception please try again", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                imageProgressBar.setVisibility(View.GONE);
                                save.setEnabled(true);
                            }
                        });

                    }
                    else{
                        Toast.makeText(UpdateUserActivity.this, "Please complete the profile", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        if(isImageBeingUploaded){}
                        else{
                        imageProgressBar.setVisibility(View.GONE);}
                        save.setEnabled(true);
                    }
                }
            }
        });
        final Query userQuery = usersDatabaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userdata = snapshot.child(firebaseAuth.getUid()).getValue(User.class);
                if (userdata.isProfileComplete()) {
                    imageProgressBar.setVisibility(View.VISIBLE);
                    nameEt.setText(userdata.getName());
                    ageEt.setText(userdata.getAge());
                    int position;
                    if (userdata.getGender().equals("Male")) {
                        position = 0;
                    } else {
                        position = 1;
                    }
                    genderSpinner.setSelection(position);
                    descriptionEt.setText(userdata.getDescription());
                    Glide.with(UpdateUserActivity.this)
                            .load(userdata.getImage())
                            .into(imageView);
                }
                else{
                    Toast.makeText(UpdateUserActivity.this, "Please complete the profile", Toast.LENGTH_SHORT).show();
                }
                imageProgressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                save.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        editProfileImageBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImageBeingUploaded=true;
                imageProgressBar.setVisibility(View.VISIBLE);
                showImagePicDialog();
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

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // profileTv.setText(user.getEmail());
        } else {
            startActivity(new Intent(UpdateUserActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        checkUserStatus();
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_DENIED);
        return !result;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_DENIED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_DENIED);
        return !result && !result1;

    }

    private void showImagePicDialog() {
        String option[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }
    private void pickFromCamera() {
        ContentValues values =new ContentValues();
        values.put(MediaStore.Images.Media.TITLE," Temp pick");
        values.put(MediaStore.Images.Media.DESCRIPTION," Temp Description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        //permission enable
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(this,"Please enable camera & storage permission",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    //  boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if( writeStorageAccepted){
                        //permission enable
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(this,"Please enablestorage permission",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                try{
                image_uri=data.getData();
                uploadProfilePhoto(image_uri);}catch(Exception ignore){
                    Toast.makeText(this, "please try to upload again", Toast.LENGTH_SHORT).show();
                }
            }
            if(requestCode==IMAGE_PICK_CAMERA_CODE){
                uploadProfilePhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void uploadProfilePhoto(final Uri uri) {
        String filePathAndName=storagePath+"profile_"+userdata.getUid();
        StorageReference storageReference2nd=storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUri=uriTask.getResult();
                        if(uriTask.isSuccessful()) {
                            HashMap<String, Object> results = new HashMap<>();
                            try {
                                results.put("image", downloadUri.toString());
                                userdata.setImage(downloadUri.toString());
                                imageUri = downloadUri.toString();
                                usersDatabaseReference.child(userdata.getUid()).updateChildren(results)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                isImageBeingUploaded = false;
                                                Glide.with(UpdateUserActivity.this)
                                                        .load(userdata.getImage())
                                                        .into(imageView);

                                                Toast.makeText(UpdateUserActivity.this, "Image Updated....", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                if (userdata.isProfileComplete()) {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName("true")
                                            .build();

                                    firebaseUser.updateProfile(profileUpdates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UpdateUserActivity.this, "there was exception please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                imageProgressBar.setVisibility(View.GONE);
                            }catch (Exception ignore){
                                Toast.makeText(UpdateUserActivity.this, "Can't upload image internet error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(UpdateUserActivity.this, "error updating image...."+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
