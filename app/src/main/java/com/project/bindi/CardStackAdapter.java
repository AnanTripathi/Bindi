package com.project.bindi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    MediaPlayer mediaPlayer=new MediaPlayer();
    boolean isAudioOn=false;

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
        ImageView image;
        TextView name, age;
        FloatingActionButton playButton;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            age = itemView.findViewById(R.id.item_age);
            playButton=itemView.findViewById(R.id.playFab);
        }

        void setData(final User data) {
            Glide.with(context)
                    .load(data.getImage())
                    .centerCrop()
                    .into(image);
            name.setText(data.getName());
            age.setText(data.getAge());
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playAudio(data);

        }


    });
            }
    }

    public List<User> getItems() {
        return items;
    }

    public void setItems(List<User> items) {
        this.items = items;
    }
    private void playAudio(User data) {
        if(isAudioOn){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        else{
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
        }
    }

}


