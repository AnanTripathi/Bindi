package com.project.bindi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.util.ExceptionCatchingInputStream;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class UpdateUserActivity extends AppCompatActivity {
    Spinner genderSpinner,interestedInSpinner;
    String gender,interestedIn;
    FirebaseAuth firebaseAuth;
    EditText nameEt, ageEt, descriptionEt;
    Button save, editProfileImageBn;
    ProgressBar progressBar,imageProgressBar;
    FirebaseUser firebaseUser;
    User userdata;
    private MediaRecorder recorder = null;
    DatabaseReference usersDatabaseReference;
    FirebaseDatabase firebaseDatabase;
    ImageView imageView;
    String imageUri="";
    CardView profileImageHolderCard;
    private ImageButton arrowIbSheet,recordFabIbSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private MaterialCardView sheetCardView;
    private Boolean isRecordBottonPressed=false,startRecording=true;
    private String fileName=null;

    //for image
    StorageReference storageReference;
    String storagePath = "User_Profile_Cover_Images/";

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 500;
    Boolean isImageBeingUploaded=false;
    String cameraPermission[];
    String storagePermission[];
    Uri image_uri;
    Button editVoiceButton;
    String voiceUri="";
    FloatingActionButton playVoiceButton;
    private Toolbar toolbar;
    private String [] audioPermission = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        toolbar=findViewById(R.id.app_bar);
        imageView=findViewById(R.id.profileImage);
        profileImageHolderCard=findViewById(R.id.profileImageHolder);
        try{
            fileName = getExternalCacheDir().getAbsolutePath();}
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        fileName += "/audiorecordtest.mp3";
        //sheet
        sheetCardView=findViewById(R.id.sheetcardview);
        mBottomSheetBehavior=BottomSheetBehavior.from(sheetCardView);
        arrowIbSheet=findViewById(R.id.arrowIb);
        recordFabIbSheet=findViewById(R.id.recordfabIb);

        playVoiceButton=findViewById(R.id.play_voiceButton);
        imageProgressBar=findViewById(R.id.progressImage);
        editVoiceButton=findViewById(R.id.editVoiceButton);
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
        interestedInSpinner=(Spinner) findViewById(R.id.interestedin_spinner);
        save = findViewById(R.id.save);
        save.setEnabled(false);
        recieveIntent();

        final ArrayAdapter<CharSequence> genderadapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        final ArrayAdapter<CharSequence> interestedinadapter = ArrayAdapter.createFromResource(this,
                R.array.interestedin_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        interestedinadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        interestedInSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    interestedIn = "Male";
                } else if(position==1){
                    interestedIn = "Female";
                } else{
                    interestedIn ="Both";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                interestedIn = "Male";
            }
        });
// Apply the adapter to the spinner
        genderSpinner.setAdapter(genderadapter);
        interestedInSpinner.setAdapter(interestedinadapter);
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
                    Toast.makeText(UpdateUserActivity.this, "Please fill name,age and description", Toast.LENGTH_SHORT).show();
                } else {
                    userdata =new User(firebaseUser.getUid(), firebaseUser.getEmail(), name, age, gender, description,interestedIn, imageUri,voiceUri,0);
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
                        progressBar.setVisibility(View.GONE);
                        if(isImageBeingUploaded){
                            Toast.makeText(UpdateUserActivity.this, "Please wait while image uploads", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(userdata.getImage().length()==0)
                            Toast.makeText(UpdateUserActivity.this, "Please upload image", Toast.LENGTH_SHORT).show();
                        imageProgressBar.setVisibility(View.GONE);
                            }
                        if(userdata.getAudio().length()==0)
                            Toast.makeText(UpdateUserActivity.this, "Please upload your voice", Toast.LENGTH_SHORT).show();
                        if(userdata.isProfileComplete()){
                        save.setEnabled(true);}
                    }
                }
            }
        });
        final Query userQuery = usersDatabaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid());
        userQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userdata = snapshot.child(Objects.requireNonNull(firebaseAuth.getUid())).getValue(User.class);

                if (userdata != null) {
                    imageUri = userdata.getImage();
                    if (userdata.getAudio()!=null&&!userdata.getAudio().equals(""))
                        voiceUri = userdata.getAudio();

                    if (userdata.getImage()!=null&&!userdata.getImage().equals("")) {

                        imageProgressBar.setVisibility(View.VISIBLE);
                        Glide.with(UpdateUserActivity.this)
                                .load(userdata.getImage())
                                .error(R.drawable.broken_image_black)
                                .fallback(R.drawable.broken_image_black)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(imageView);
                        save.setEnabled(true);
                    }
                    if(userdata.getName()!=null&&userdata.getAge()!=null&&userdata.getGender()!=null&&userdata.getInterestedin()!=null&&!userdata.getName().equals("")&&!userdata.getGender().equals("")&&!userdata.getInterestedin().equals("")) {
                        nameEt.setText(userdata.getName());
                        ageEt.setText(userdata.getAge());
                        int genderPosition, interestedPosition;
                        if (userdata.getGender().equals("Male")) {
                            genderPosition = 0;
                        } else {
                            genderPosition = 1;
                        }
                        if (userdata.getInterestedin().equals("Male")) {
                            interestedPosition = 0;
                        } else if (userdata.getInterestedin().equals("Female")) {
                            interestedPosition = 1;
                        } else {
                            interestedPosition = 2;
                        }

                        genderSpinner.setSelection(genderPosition);
                        interestedInSpinner.setSelection(interestedPosition);
                        descriptionEt.setText(userdata.getDescription());
                    }
                    }
                else {
                        Toast.makeText(UpdateUserActivity.this, "Please complete the profile", Toast.LENGTH_SHORT).show();
                    }
                    imageProgressBar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    if(userdata.isProfileComplete()){
                    save.setEnabled(true);
                    }

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
                save.setEnabled(false);
            }
        });
        playVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userdata != null && userdata.getAudio() != null && !userdata.getAudio().equals("")) {
                    Uri myUri = Uri.parse(userdata.getAudio()); // initialize Uri here
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(), myUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();

                }
                else{
                    Toast.makeText(UpdateUserActivity.this, "Please update the audio first", Toast.LENGTH_SHORT).show();
                }
                
            }
        });
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                arrowIbSheet.setRotation(slideOffset * 180);
            }
        });
        editVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        recordFabIbSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecordBottonPressed){
                    mBottomSheetBehavior.setHideable(false);
                    recordFabIbSheet.setBackgroundTintList(getResources().getColorStateList(R.color.on_press));
                    //set color red an start recording
                    if (!checkRecordPermission()) {
                        requestRecordPermission();
                    } else {
                        startAudioRecording();
                    }

                }
                else{
                    startAudioRecording();
                    recordFabIbSheet.setBackgroundTintList(getResources().getColorStateList(R.color.on_re_press));
                    mBottomSheetBehavior.setHideable(true);
                    //set color blue and stop recording
                }
                isRecordBottonPressed=!isRecordBottonPressed;
            }
        });
    }

    private void recieveIntent() {
//        ActionBar actionBar=getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
        Bundle extras = getIntent().getExtras();
        String parentActivity="";

        if (extras != null) {
            parentActivity = extras.getString("parent");
            Toast.makeText(this, "The parent activity was "+parentActivity , Toast.LENGTH_SHORT).show();
            // and get whatever type user account id is
        }

//        if(parentActivity!=null){
//        if(parentActivity.equals("RegisterActivity")||parentActivity.equals("LoginActivity")){
//            toolbar.collapseActionView();
//        }
//        else if(parentActivity.equals("DashBoardActivity")){
//            toolbar.collapseActionView();
//        }}
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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
    private boolean checkRecordPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == (PackageManager.PERMISSION_DENIED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_DENIED);
        return !result && !result1;
    }
    private void requestRecordPermission() {
        requestPermissions(audioPermission, REQUEST_RECORD_AUDIO_PERMISSION);
    }
    private void showImagePicDialog() {
        String option[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                save.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });
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
            case REQUEST_RECORD_AUDIO_PERMISSION:{
                if(grantResults.length>0){
                    boolean audioAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(audioAccepted && writeStorageAccepted){
                        //permission enable
                        startAudioRecording();
                    }
                    else{
                        isRecordBottonPressed=!isRecordBottonPressed;
                        recordFabIbSheet.setBackgroundTintList(getResources().getColorStateList(R.color.on_re_press));
                        mBottomSheetBehavior.setHideable(true);
                        Toast.makeText(this,"Please enable audio and storage",Toast.LENGTH_SHORT).show();
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

    private void startAudioRecording() {
        onRecord(startRecording);
        startRecording = !startRecording;
    }
    private void onRecord(boolean start) {
        if (start) {
            startRecordingAudio();
        } else {
            stopRecording();
        }
    }
    private void startRecordingAudio() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("UpdateUserActivity", "prepare() failed");
        }
        recorder.start();
    }
    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        uploadAudio();
    }
    private void uploadAudio() {
        progressBar.setVisibility(View.VISIBLE);
        final ProgressDialog uploadProgressDialog=new ProgressDialog(UpdateUserActivity.this);
        uploadProgressDialog.setMessage("Audio is uploading");
        uploadProgressDialog.setCancelable(true);
        uploadProgressDialog.create();
        uploadProgressDialog.show();
        Uri uri=Uri.fromFile(new File(fileName));
        String filePathAndName=storagePath+"profile_"+firebaseAuth.getUid();
        StorageReference storageReference2nd= FirebaseStorage.getInstance().getReference().child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while(!task.isSuccessful());
                if (task.isSuccessful()) {
                    Uri uri=task.getResult();
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("audio",uri.toString());
                    usersDatabaseReference.child(userdata.getUid()).updateChildren(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.GONE);
                                    uploadProgressDialog.dismiss();
                                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    Toast.makeText(UpdateUserActivity.this, "Voice Uploaded", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            uploadProgressDialog.dismiss();
                            Toast.makeText(UpdateUserActivity.this, "Voice Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                uploadProgressDialog.dismiss();
                Toast.makeText(UpdateUserActivity.this, "voice upload failure"+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
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
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                                editProfileImageBn.setEnabled(true);
                                                save.setEnabled(true);
                                                Toast.makeText(UpdateUserActivity.this, "Image Updated....", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        editProfileImageBn.setEnabled(true);
                                        save.setEnabled(true);
                                        Toast.makeText(UpdateUserActivity.this, "Network error \n you might need to re-upload image", Toast.LENGTH_SHORT).show();
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
                                save.setEnabled(true);
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
