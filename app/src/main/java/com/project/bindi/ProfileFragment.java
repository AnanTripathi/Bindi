package com.project.bindi;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
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

import java.io.IOException;


public class ProfileFragment extends Fragment {
FloatingActionButton editFab,playVoiceButton;
TextView nameTv,ageTv,genderTv,descriptionTv;
DatabaseReference userReference;
FirebaseUser firebaseUser;
FirebaseAuth firebaseAuth;
User userdata;
ProgressBar progressBar,imageProgressBar;
ImageView profileImageView;
private Boolean startPlaying=true;
private MediaPlayer mediaPlayer=null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        userReference= FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        playVoiceButton=view.findViewById(R.id.playVoiceButton);
        profileImageView=view.findViewById(R.id.profileImage);
        imageProgressBar=view.findViewById(R.id.progressImage);
        nameTv=view.findViewById(R.id.nameTv);
        progressBar=view.findViewById(R.id.indeterminateBar);
        ageTv=view.findViewById(R.id.ageTv);
        genderTv=view.findViewById(R.id.genderTv);
        descriptionTv=view.findViewById(R.id.descriptionTv);
        editFab=view.findViewById(R.id.editFab);
        imageProgressBar.setVisibility(View.VISIBLE);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),UpdateUserActivity.class);
                startActivity(intent);
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query userQuery=userReference.orderByChild("uid").equalTo(firebaseAuth.getUid());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageProgressBar.setVisibility(View.VISIBLE);
                if(!(firebaseAuth.getUid()==null||firebaseAuth.getUid().equals(""))){
                userdata=snapshot.child(firebaseAuth.getUid()).getValue(User.class);}
                if(userdata.isProfileComplete()){
                nameTv.setText(userdata.getName());
                ageTv.setText(userdata.getAge());
                genderTv.setText(userdata.getGender());
                descriptionTv.setText(userdata.getDescription());
            if(!(userdata.getImage()==null||userdata.getImage().equals(""))){
                    try{

                Glide.with(getContext())
                .load(userdata.getImage())
                        .error(R.drawable.broken_image_black)
                        .fallback(R.drawable.broken_image_black)
                        .placeholder(R.drawable.loadinggif)
                        .centerCrop()
                .into(profileImageView);
                imageProgressBar.setVisibility(View.GONE);
                    }
                catch (Exception ignored){
                    imageProgressBar.setVisibility(View.GONE);
                    }
                }
                }
                progressBar.setVisibility(View.GONE);
                imageProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        playVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(startPlaying);
                startPlaying = !startPlaying;

            }
        });
        return view;
    }
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
    private void startPlaying() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            public void run() {


                if (userdata != null && userdata.getAudio() != null && !userdata.getAudio().equals("")) {
                    Uri myUri = Uri.parse(userdata.getAudio()); // initialize Uri here
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );
                    try {
                        mediaPlayer.setDataSource(getContext(), myUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    Toast.makeText(getActivity(), "Please update the audio first", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private void stopPlaying() {
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.release();
            mediaPlayer = null;}
    }

}