package com.project.bindi;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.project.bindi.VoiceMailsFragment.isButtonClicked;
import static com.project.bindi.VoiceMailsFragment.mediaPlayer;
import static com.project.bindi.VoiceMailsFragment.startPlaying;
import de.hdodenhof.circleimageview.CircleImageView;

public class SentMessageAdapter extends RecyclerView.Adapter<SentMessageAdapter.ViewHolder> {

    ArrayList<Message> sentMessageList;
    private static final DatabaseReference userDatabaseReference=FirebaseDatabase.getInstance().getReference("Users");
    Context context;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    public SentMessageAdapter(ArrayList<Message> sentMessageList, Context context) {
        this.sentMessageList = sentMessageList;
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
        final String recieverId=sentMessageList.get(position).getReceiverId();
        final Message message=sentMessageList.get(position);
        Query userQuery=userDatabaseReference.orderByChild("uid").equalTo(recieverId);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User reciever=snapshot.child(recieverId).getValue(User.class);
                if(reciever!=null) {
                    if (recieverId.equals(reciever.getUid())&&message.getSenderId().equals(firebaseAuth.getUid())) {
                        if (reciever.isProfileComplete()) {
                            holder.playIb.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(!isButtonClicked){
                                    onPlay(startPlaying,holder.progressBar,message);
                                    startPlaying = !startPlaying;}
                                    isButtonClicked=!isButtonClicked;
                                }
                            });
                            holder.nameTv.setText(reciever.getName());
                            holder.messageTv.setText(message.getMessage());
                            if (!(reciever.getImage() == null || reciever.getImage().equals(""))) {
                                try {
                                    Glide.with(context)
                                            .load(reciever.getImage())
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void onPlay(boolean start,ProgressBar progressBar,Message message) {
        if (start) {
            startPlaying(progressBar,message);
        } else {
            stopPlaying();
        }
    }
    private void startPlaying(final ProgressBar progressBar, final Message message) {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            public void run() {


                if (message != null && message.getVoiceMessageLink() != null && !message.getVoiceMessageLink().equals("")) {
                    Uri myUri = Uri.parse(message.getVoiceMessageLink()); // initialize Uri here
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isButtonClicked=!isButtonClicked;
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
                    isButtonClicked=!isButtonClicked;
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
        try{
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.release();
            mediaPlayer = null;
        }}catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return sentMessageList.size();
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
