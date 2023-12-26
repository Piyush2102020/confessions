package com.example.project2102020;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder> {
Context context;
ArrayList<chatModel>list;
String uid1;
    public AdapterChat(Context context, ArrayList<chatModel>list,String uid1){
        this.context=context;
        this.uid1=uid1;
        this.list=list;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType==1){ view= LayoutInflater.from(context).inflate(R.layout.sender,parent,false);}
        else { view= LayoutInflater.from(context).inflate(R.layout.receiver,parent,false);}

        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (list.get(position).getImage()!=null){

            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context).load(list.get(position).getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.loading).centerCrop().into(holder.image);
        }
        holder.messageSent.setText(list.get(position).message);
        holder.userNameC.setText(list.get(position).userName);
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (list.get(position).uid != null && list.get(position).uid.equals(list.get(position).myUid)) {
                return 1;
            } else {
                return 2;
            }
        } catch (NullPointerException e) {
            e.printStackTrace(); // Log the exception for debugging purposes
            return 2; // Default to the second view type in case of exception
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userNameC,messageSent;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.messagePic);
            userNameC=itemView.findViewById(R.id.userNameC);
            messageSent=itemView.findViewById(R.id.messageSent);
        }
    }


}
