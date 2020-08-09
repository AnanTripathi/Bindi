package com.project.bindi;
import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {
    Context context;
    private List<User> items;
    public static MediaPlayer mediaPlayer=new MediaPlayer();
    boolean isAudioOn=false;
    private Boolean startPlaying=true;

    public CardStackAdapter(Context context,List<User> items) {
        this.context=context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;
        ImageView image;
        TextView name, age;
        FloatingActionButton playButton;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.progressBar);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            age = itemView.findViewById(R.id.item_age);
            playButton=itemView.findViewById(R.id.playFab);
        }

        void setData(final User data) {
            Glide.with(context)
                    .load(data.getImage())
                    .error(R.drawable.broken_image_black)
                    .fallback(R.drawable.broken_image_black)
                    .placeholder(R.drawable.loadinggif)
                    .centerCrop()
                    .into(image);
            name.setText(data.getName());
            age.setText(data.getAge());
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlay(startPlaying,progressBar,data);
                    startPlaying = !startPlaying;

        }


    });
            }
    }
    private void onPlay(boolean start,ProgressBar progressBar,User userdata) {
        if (start) {
            startPlaying(progressBar,userdata);
        } else {
            stopPlaying();
        }
    }
    private void startPlaying(final ProgressBar progressBar, final User userdata) {
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
                        mediaPlayer.setDataSource(context, myUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    DashBoardActivity d=(DashBoardActivity) context;
                    d.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    DashBoardActivity d=(DashBoardActivity) context;
                    d.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    Toast.makeText(context, "Please update the audio first", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private void stopPlaying() {
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.release();
            mediaPlayer = null;}
    }

    public List<User> getItems() {
        return items;
    }

    public void setItems(List<User> items) {
        this.items = items;
    }
    private void playAudio(User data) {
        if(isAudioOn){
           stop(data);
        }
        else{
            play(data);
        }
    }
    private void stop(User data){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        isAudioOn=true;}
    }
    private void play(User data){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            isAudioOn=false;
        }
        mediaPlayer=new MediaPlayer();
        try{
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );}
        catch (NullPointerException e) {
            Toast.makeText(context, "mediaplayer:-"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            mediaPlayer.setDataSource(data.getAudio());
        } catch (IOException e) {
            Toast.makeText(context, "mediaplayer:-"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
            isAudioOn=true;
        } catch (IOException e) {
            Toast.makeText(context, "mediaplayer:-"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        isAudioOn=false;
    }
}


