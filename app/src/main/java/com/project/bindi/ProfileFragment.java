package com.project.bindi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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


public class ProfileFragment extends Fragment {
FloatingActionButton editFab;
TextView nameTv,ageTv,genderTv,descriptionTv;
DatabaseReference userReference;
FirebaseUser firebaseUser;
FirebaseAuth firebaseAuth;
User userdata;
ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        userReference= FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        nameTv=view.findViewById(R.id.nameTv);
        progressBar=view.findViewById(R.id.indeterminateBar);
        ageTv=view.findViewById(R.id.ageTv);
        genderTv=view.findViewById(R.id.genderTv);
        descriptionTv=view.findViewById(R.id.descriptionTv);
        editFab=view.findViewById(R.id.editFab);
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
                userdata=snapshot.child(firebaseAuth.getUid()).getValue(User.class);
                nameTv.setText(userdata.getName());
                ageTv.setText(userdata.getAge());
                genderTv.setText(userdata.getGender());
                descriptionTv.setText(userdata.getDescription());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}