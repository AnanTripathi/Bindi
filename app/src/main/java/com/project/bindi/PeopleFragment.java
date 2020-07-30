package com.project.bindi;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeopleFragment extends Fragment {



    //List of info of all users
    ArrayList<User> allUserInfo;
    FloatingActionButton rewindFab,likeFab,dislikeFab,doubleLikeFab;
    CardStackView cardStackView;
    ShowAllUser showAllUserAdapter;
    private static final String TAG = PeopleFragment.class.getSimpleName();
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_people, container, false);
        rewindFab=view.findViewById(R.id.lastFab);
        likeFab=view.findViewById(R.id.likeFab);
        doubleLikeFab=view.findViewById(R.id.doubleLikeFab);
        dislikeFab=view.findViewById(R.id.dislikeFab);
        likeFab.setEnabled(false);
        dislikeFab.setEnabled(false);
        doubleLikeFab.setEnabled(false);
        rewindFab.setEnabled(false);
        allUserInfo = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        final FirebaseUser Firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUserInfo.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    User user1=ds.getValue(User.class);
                    if(!user1.getUid().equals(Firebaseuser.getUid()))
                    {
                        allUserInfo.add(user1);
                        if(allUserInfo.size()==1){
                        adapter = new CardStackAdapter(getContext(),allUserInfo);}
                        else adapter.notifyItemInserted(allUserInfo.size()-1);
                        init(view);
                        rewindFab.setEnabled(true);
                        likeFab.setEnabled(true);
                        dislikeFab.setEnabled(true);
                        doubleLikeFab.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    rewindFab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cardStackView.rewind();
        }
    });
    likeFab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cardStackView.swipe();
        }
    });
    dislikeFab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cardStackView.swipe();
        }
    });
    doubleLikeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStackView.swipe();
            }
        });
        return view;
    }
    private void init(View root) {
        cardStackView = root.findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right){
                    Toast.makeText(getContext(), "Direction Right", Toast.LENGTH_SHORT).show();
                }

                if (direction == Direction.Left){
                    Toast.makeText(getContext(), "Direction Left", Toast.LENGTH_SHORT).show();
                }

                // Paginating
//                if (manager.getTopPosition() == adapter.getItemCount() - 5){
//                    paginate();
//                }

            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        ArrayList<com.yuyakaido.android.cardstackview.Direction> directions=new ArrayList<Direction>() {};
        directions.add(Direction.Top);
        directions.add(Direction.Right);
        directions.add(Direction.Left);
        manager.setDirections(directions);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);

        manager.setOverlayInterpolator(new LinearInterpolator());

        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

    }

}