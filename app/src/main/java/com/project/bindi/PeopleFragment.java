package com.project.bindi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.HashMap;

public class PeopleFragment extends Fragment {
    ArrayList<User> allUserInfo;
    FloatingActionButton rewindFab,likeFab,dislikeFab,doubleLikeFab;
    CardStackView cardStackView;
    private static final String TAG = PeopleFragment.class.getSimpleName();
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    DatabaseReference usersDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_people, container, false);

        usersDatabaseReference=FirebaseDatabase.getInstance().getReference("Users");
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
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUserInfo.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    User user1=ds.getValue(User.class);
                    if(user1.isProfileComplete()) {
                        if (!(user1.getUid().equals(Firebaseuser.getUid()))) {
                            if(DashBoardActivity.loggedInUser.getInterestedin().equals("Both")||user1.getGender().equals(DashBoardActivity.loggedInUser.getInterestedin())){
                            allUserInfo.add(user1);
                            if (allUserInfo.size() == 1) {
                                adapter = new CardStackAdapter(getContext(), allUserInfo);
                            } else adapter.notifyItemInserted(allUserInfo.size() - 1);
                            init(view);
                            rewindFab.setEnabled(true);
                            likeFab.setEnabled(true);
                            dislikeFab.setEnabled(true);
                            doubleLikeFab.setEnabled(true);}
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final SwipeAnimationSetting left= new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        final SwipeAnimationSetting right= new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        final SwipeAnimationSetting up= new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Top)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new AccelerateInterpolator())
                .build();
    rewindFab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cardStackView.rewind();
        }
    });
    likeFab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            manager.setSwipeAnimationSetting(right);
            cardStackView.swipe();

        }
    });
    dislikeFab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            manager.setSwipeAnimationSetting(left);
            cardStackView.swipe();
        }
    });
    doubleLikeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manager.setSwipeAnimationSetting(up);
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
                if(CardStackAdapter.mediaPlayer!=null&&CardStackAdapter.mediaPlayer.isPlaying()){
                    CardStackAdapter.mediaPlayer.release();
                    CardStackAdapter.mediaPlayer = null;
                }

                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right) {
                    likeThePerson();
                }

                if (direction == Direction.Left){
                    dislikeThePerson();
                }
                if(direction == Direction.Top){
                    doubleLikeThePerson();
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
//        manager.setCanScrollHorizontal(true);
//        manager.setCanScrollVertical(true);
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);

        manager.setOverlayInterpolator(new LinearInterpolator());

        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

    }

    private void dislikeThePerson() {
        final int topPosition=manager.getTopPosition()-1;
        allUserInfo.get(topPosition).decreaseLikes();
        HashMap<String, Object> results = new HashMap<>();
        results.put("likes", allUserInfo.get(topPosition).getLikes());
        usersDatabaseReference.child(allUserInfo.get(topPosition).getUid()).updateChildren(results)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "you disliked "+allUserInfo.get(topPosition).getName(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void likeThePerson() {

        final int topPosition=manager.getTopPosition()-1;
        allUserInfo.get(topPosition).increaseLikes();
        HashMap<String, Object> results = new HashMap<>();
        results.put("likes", allUserInfo.get(topPosition).getLikes());
        usersDatabaseReference.child(allUserInfo.get(topPosition).getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "you liked "+ allUserInfo.get(topPosition).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void doubleLikeThePerson() {
        final int topPosition=manager.getTopPosition()-1;
        allUserInfo.get(topPosition).doubleIncreaseLike();
        HashMap<String, Object> results = new HashMap<>();
        results.put("likes", allUserInfo.get(topPosition).getLikes());
        usersDatabaseReference.child(allUserInfo.get(topPosition).getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "you double liked "+ allUserInfo.get(topPosition).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent=new Intent(getActivity(), SendMessageActivity.class);
        intent.putExtra("toId",allUserInfo.get(topPosition).getUid());
        startActivity(intent);
      //  Objects.requireNonNull(getActivity()).finish();
    }
}