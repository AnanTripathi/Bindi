package com.project.bindi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShowAllUser extends RecyclerView.Adapter<ShowAllUser.AllUser>{

    Context context;
    ArrayList<User> PublicInfo;

    public ShowAllUser(Context context, ArrayList<User> publicInfo) {
        this.context = context;
        PublicInfo = publicInfo;
    }

    @NonNull
    @Override
    public AllUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater;
        View view = LayoutInflater.from(context).inflate(R.layout.showalluser,parent,false);
        return new AllUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUser holder, int position) {

        Glide.with(context)
                .load(PublicInfo.get(position).getImage())
                .into(holder.publicProfile);
    }

    @Override
    public int getItemCount() {
        return PublicInfo.size();
    }

    public class AllUser extends RecyclerView.ViewHolder {
        ImageView publicProfile;
        public AllUser(@NonNull View itemView) {
            super(itemView);
            publicProfile = itemView.findViewById(R.id.ImagePublic);
        }
    }
}
