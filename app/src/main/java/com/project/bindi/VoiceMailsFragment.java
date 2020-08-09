package com.project.bindi;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoiceMailsFragment} factory method to
 * create an instance of this fragment.
 */
public class VoiceMailsFragment extends Fragment {

private RecyclerView sentMessageRv,receivedMessagesRv;
SentMessageAdapter sentMessageAdapter;
ReceivedMessageAdapter receivedMessageAdapter;
DatabaseReference messageDatabaseReference;
FirebaseAuth firebaseAuth;
ArrayList<Message> sentMessageArrayList=new ArrayList<>(),receivedMessageArrayList=new ArrayList<>();
TextView emptySentTextView,emptyReceivedTextView;
ImageView emptyReceivedImageView,emptySentImageView;
    public static Boolean startPlaying=true;
    public static MediaPlayer mediaPlayer=new MediaPlayer();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_voice_mails, container, false);
        sentMessageRv= view.findViewById(R.id.sentRecyclerView);
        emptyReceivedImageView=view.findViewById(R.id.emptyRecievedIv);
        emptySentImageView=view.findViewById(R.id.emptySentIv);
        emptyReceivedTextView=view.findViewById(R.id.emptyRecievedTextview);
        emptySentTextView=view.findViewById(R.id.emptySentTextview);
        firebaseAuth=FirebaseAuth.getInstance();
        receivedMessagesRv=view.findViewById(R.id.receivedRecyclerView);
        messageDatabaseReference= FirebaseDatabase.getInstance().getReference(Message.parentLocation);
        messageDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message=snapshot.getValue(Message.class);
                if(message.getSenderId().equals(firebaseAuth.getUid())){
                    sentMessageArrayList.add(message);
                    if(sentMessageArrayList.size()!=1){
                        sentMessageAdapter.notifyItemInserted(sentMessageArrayList.size()-1);
                    }
                    else{
                        emptySentImageView.setVisibility(View.GONE);
                        emptySentTextView.setVisibility(View.GONE);
                        initSentMessageRecyclerView();
                    }
                }
                else if(message.getReceiverId().equals(firebaseAuth.getUid())){
                    receivedMessageArrayList.add(message);
                    if(receivedMessageArrayList.size()!=1){
                        receivedMessageAdapter.notifyItemInserted(receivedMessageArrayList.size()-1);
                    }
                    else{
                        emptyReceivedImageView.setVisibility(View.GONE);
                        emptyReceivedTextView.setVisibility(View.GONE);
                        initReceivedMessageRecyclerView();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
    private void initSentMessageRecyclerView(){
        if(sentMessageArrayList.size()==1)
        {
            sentMessageAdapter=new SentMessageAdapter(sentMessageArrayList,getContext());
            sentMessageRv.setAdapter(sentMessageAdapter);
            sentMessageRv.addItemDecoration(new DividerItemDecoration(sentMessageRv.getContext(),DividerItemDecoration.VERTICAL));
            sentMessageRv.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }
    private void initReceivedMessageRecyclerView(){
        if(receivedMessageArrayList.size()==1)
        {
            receivedMessageAdapter=new ReceivedMessageAdapter(receivedMessageArrayList,getContext());
            receivedMessagesRv.setAdapter(receivedMessageAdapter);
            receivedMessagesRv.addItemDecoration(new DividerItemDecoration(receivedMessagesRv.getContext(), DividerItemDecoration.VERTICAL));
            receivedMessagesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }
}