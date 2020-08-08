package com.project.bindi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SendMessage extends AppCompatActivity {
    private ImageButton sendVoiceIb;
    private MaterialButton sendMessageBn;
    private EditText messageEt;
    private String recieverId="";
    private MediaRecorder recorder;
    private FirebaseAuth firebaseAuth;
    private String fileName=null;
    String storagePath = "Message/";
    private String audioUri="";
    private TextInputLayout messageTil;
    private Boolean isRecordBottonPressed=false,startRecording=true;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 500;
    private String [] audioPermission = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        recieverIntent();
        firebaseAuth=FirebaseAuth.getInstance();
        sendVoiceIb=findViewById(R.id.sendVoiceIb);
        messageTil=findViewById(R.id.messageInputTil);
        sendMessageBn=findViewById(R.id.sendMb);
        sendMessageBn.setEnabled(false);
        messageEt=findViewById(R.id.messageEt);
        try{
            fileName = getExternalCacheDir().getAbsolutePath();}
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        fileName += "/audiorecordtest.mp3";
        sendVoiceIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecordBottonPressed){
                    sendVoiceIb.setBackgroundTintList(getResources().getColorStateList(R.color.on_press));
                    //set color red an start recording
                    if (!checkRecordPermission()) {
                        requestRecordPermission();
                    } else {
                        startAudioRecording();
                    }

                }
                else{
                    startAudioRecording();
                    sendVoiceIb.setBackgroundTintList(getResources().getColorStateList(R.color.on_re_press));
                    //set color blue and stop recording
                }
                isRecordBottonPressed=!isRecordBottonPressed;
            }
        });
        sendMessageBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageEt.getText().toString().trim().equals("")){
                    messageTil.setFocusable(View.FOCUSABLE);
                    Toast.makeText(SendMessage.this, "Please write a message", Toast.LENGTH_SHORT).show();
                }
                else{
                    Message message=new Message(firebaseAuth.getUid()+recieverId,firebaseAuth.getUid(),recieverId,messageEt.getText().toString().trim(),audioUri);
                    DatabaseReference messageDatabaseReference=FirebaseDatabase.getInstance().getReference(Message.parentLocation);
                    messageDatabaseReference.child(message.getMessageId()).setValue(message);
                }
                startActivity(new Intent(SendMessage.this,DashBoardActivity.class));
            }
        });
    }

    private void recieverIntent() {
        Intent intent=getIntent();
        recieverId=intent.getStringExtra("toId");
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
        try {
            recorder.stop();
        } catch(RuntimeException stopException) {
            // handle cleanup here
        }
        recorder.release();
        recorder = null;
        uploadAudio();
    }
    private void uploadAudio() {
        final ProgressDialog uploadProgressDialog=new ProgressDialog(SendMessage.this);
        sendVoiceIb.setEnabled(false);
        uploadProgressDialog.setMessage("Audio is uploading");
        uploadProgressDialog.setCancelable(true);
        uploadProgressDialog.create();
        uploadProgressDialog.show();
        Uri uri=Uri.fromFile(new File(fileName));
        String filePathAndName=Message.parentLocation+"/message_"+firebaseAuth.getUid();
        StorageReference storageReference2nd= FirebaseStorage.getInstance().getReference().child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while(!task.isSuccessful());
                if (task.isSuccessful()) {
                    Uri uri=task.getResult();
                    if (uri != null) {
                        audioUri=uri.toString();
                    }
                    sendVoiceIb.setEnabled(true);
                    sendMessageBn.setEnabled(true);
                    uploadProgressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendVoiceIb.setEnabled(true);
                uploadProgressDialog.dismiss();
                Toast.makeText(SendMessage.this, "voice upload failure"+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:{
                if(grantResults.length>0){
                    boolean audioAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(audioAccepted && writeStorageAccepted){
                        startAudioRecording();
                    }
                    else{
                        isRecordBottonPressed=!isRecordBottonPressed;
                        sendVoiceIb.setBackgroundTintList(getResources().getColorStateList(R.color.on_re_press));
                        Toast.makeText(this,"Please enable audio and storage",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}