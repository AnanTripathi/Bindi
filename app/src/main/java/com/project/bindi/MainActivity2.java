package com.project.bindi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = "MainActivity2";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private static String fileName = null;
    Boolean startRecording=true;
    Boolean startPlaying=true;
    Button playBn,recordBn;
    ProgressBar progressBar;
    private MediaRecorder recorder = null;
    FirebaseAuth firebaseAuth;
    private MediaPlayer player = null;
    User userdata;
    DatabaseReference usersDatabaseReference;
    MaterialButton uploadMaterialButton;

    String storagePath = "User_Profile_Cover_Voice/";

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false,permissionToSaveAccepted=false;
    private String [] audioPermission = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    StorageReference audioRef;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if(grantResults.length>0){
                    permissionToRecordAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(!permissionToRecordAccepted){
                        finish();
                    }
                }
                break;
            case REQUEST_STORAGE_PERMISSION:
                permissionToSaveAccepted =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(!permissionToSaveAccepted){
                    finish();
                }
                break;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        progressBar=findViewById(R.id.progress_horizontal);
        playBn=findViewById(R.id.playBn);
        recordBn=findViewById(R.id.recordBn);
        uploadMaterialButton=findViewById(R.id.upload_button);
        uploadMaterialButton.setEnabled(false);
        firebaseAuth=FirebaseAuth.getInstance();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        playBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(startPlaying);
                if (startPlaying) {
                    playBn.setText("Stop playing");
                } else {
                    playBn.setText("Start playing");
                }
                startPlaying = !startPlaying;
            }
        });
        recordBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(startRecording);
                if (startRecording) {
                    recordBn.setText("Stop recording");
                } else {
                    recordBn.setText("Start recording");
                }
                startRecording = !startRecording;
            }
        });
        try{
        fileName = getExternalCacheDir().getAbsolutePath();}
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        fileName += "/audiorecordtest.mp3";

        audioRef= FirebaseStorage.getInstance().getReference();
        ActivityCompat.requestPermissions(this, audioPermission, REQUEST_RECORD_AUDIO_PERMISSION);
        final Query userQuery = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(firebaseAuth.getUid());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                userdata = snapshot.child(firebaseAuth.getUid()).getValue(User.class);}
                catch (Exception e){
                    Toast.makeText(MainActivity2.this, "Error in authentication", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    uploadMaterialButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            uploadAudio();
            progressBar.setVisibility(View.VISIBLE);
            uploadMaterialButton.setEnabled(false);
        }
    });
    }

    private void requestAudioRecordPermission() {
        requestPermissions(audioPermission,REQUEST_RECORD_AUDIO_PERMISSION);
    }


    private boolean checkAudioRecordPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_DENIED);
        return !result;
    }
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void startRecording() {
        if(checkAudioRecordPermission()){
        requestAudioRecordPermission();}
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        uploadMaterialButton.setEnabled(true);
    }

    private void uploadAudio() {
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
                                    Toast.makeText(MainActivity2.this, "Voice Updated....", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    uploadMaterialButton.setEnabled(true);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity2.this, "voice not updated", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            uploadMaterialButton.setEnabled(true);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity2.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                uploadMaterialButton.setEnabled(true);
            }
        });
    }

}