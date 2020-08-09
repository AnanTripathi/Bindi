package com.project.bindi;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.project.bindi.VoiceMailsFragment.mediaPlayer;
import static com.project.bindi.VoiceMailsFragment.startPlaying;

public class ReceivedMessageAdapter extends RecyclerView.Adapter<ReceivedMessageAdapter.ViewHolder>{
    ArrayList<Message> receivedMessageList;


    private static final DatabaseReference userDatabaseReference= FirebaseDatabase.getInstance().getReference("Users");
    Context context;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    public ReceivedMessageAdapter(ArrayList<Message> receivedMessageList, Context context) {
        this.receivedMessageList = receivedMessageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String senderId=receivedMessageList.get(position).getSenderId();
        final Message message=receivedMessageList.get(position);
        Query userQuery=userDatabaseReference.orderByChild("uid").equalTo(senderId);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User sender=snapshot.child(senderId).getValue(User.class);
                if(sender!=null&&sender.isProfileComplete()){
                    holder.playIb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onPlay(startPlaying,holder.progressBar,sender);
                            startPlaying = !startPlaying;
                        }
                    });
                    holder.nameTv.setText(sender.getName());
                    holder.messageTv.setText(message.getMessage());
                    if(sender.getImage()!=null&&!sender.getImage().equals("")){
                        try {
                            Glide.with(context)
                                    .load(sender.getImage())
                                    .error(R.drawable.broken_image_black)
                                    .fallback(R.drawable.broken_image_black)
                                    .placeholder(R.drawable.loadinggif)
                                    .centerCrop()
                                    .into(holder.circleImageView);
                        } catch (Exception ignored) {

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            startPlaying=!startPlaying;
                            mp.release();
                        }
                    });
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
                        mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                            DashBoardActivity d=(DashBoardActivity) context;
                            d.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
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
            mediaPlayer = null;
        }
    }

    @Override
    public int getItemCount() {
        return receivedMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView messageTv,nameTv;
        ImageButton playIb;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.profile_circular_image);
            nameTv=itemView.findViewById(R.id.NameTv);
            messageTv=itemView.findViewById(R.id.messageTv);
            playIb=itemView.findViewById(R.id.playIb);
            progressBar=itemView.findViewById(R.id.messageProgressBar);
        }
    }
}
