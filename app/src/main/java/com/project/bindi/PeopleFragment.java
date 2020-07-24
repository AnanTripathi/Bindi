package com.project.bindi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PeopleFragment extends Fragment {

    // This is the gesture detector compat instance.
    private GestureDetectorCompat gestureDetectorCompat = null;

    //List of info of all users
    ArrayList<User> allUserInfo;

    RecyclerView recyclerView;
    ShowAllUser showAllUserAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_people, container, false);

        allUserInfo = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewAllUserInfo);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        final FirebaseUser Firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUserInfo.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    User user1=ds.getValue(User.class);
                    if(user1.getUid() != Firebaseuser.getUid())
                    {
                        allUserInfo.add(user1);
                    }
                    showAllUserAdapter = new ShowAllUser(getContext(),allUserInfo);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
                    recyclerView.setAdapter(showAllUserAdapter);
                    showAllUserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DetectSwipeGestureListener listener = new DetectSwipeGestureListener(getActivity());
        // Create the gesture detector with the gesture listener.
        gestureDetectorCompat = new GestureDetectorCompat(getActivity(), listener);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Pass activity on touch event to the gesture detector.
                gestureDetectorCompat.onTouchEvent(event);
                // Return true to tell android OS that event has been consumed,
                // do not pass it to other event listeners.
                return true;
            }
        });

        return view;
    }
}