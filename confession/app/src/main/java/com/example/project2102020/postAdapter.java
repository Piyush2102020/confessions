package com.example.project2102020;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class postAdapter extends RecyclerView.Adapter<postAdapter.ViewHolder> {

    ArrayList<model>list;
    Context context;
    public  postAdapter(Context context, ArrayList<model>list){
        this.context=context;
        this.list=list;
    }


    private String getTimeAgo(long timestampInMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long timeDifference = currentTimeMillis - timestampInMillis;

        long seconds = timeDifference / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + (days == 1 ? " day ago" : " days ago");
        } else if (hours > 0) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        } else {
            return "just now";
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        SharedPreferences pre= context.getSharedPreferences("project2102020",Context.MODE_PRIVATE);
        String uid=pre.getString("uid","");




        holder.textMessage.setText(list.get(position).message);
        holder.textComments.setText(String.valueOf(list.get(position).comments));
        holder.textLikes.setText(String.valueOf(list.get(position).likes));

        try {
            if (list.get(position).time != null && !list.get(position).time.isEmpty()) {
                holder.textTime.setText(getTimeAgo(Long.parseLong(list.get(position).time)));
            } else {
                holder.textTime.setText(""); // Set an appropriate default value
            }
        } catch (NumberFormatException e) {
            // Handle the case where parsing the time fails
            holder.textTime.setText("Invalid Time");
        }

        if (list.get(position).isLiked){
            holder.imageLike.setImageResource(R.drawable.heart);
        }
        else {
            holder.imageLike.setImageResource(R.drawable.like);
        }

        if (list.get(position).getImage()!=null && !list.get(position).getImage().isEmpty()){
            holder.picPosted.setVisibility(View.VISIBLE);
            holder.cardView.setVisibility(View.VISIBLE);
            Glide.with(context).load(list.get(position).getImage()).placeholder(R.drawable.loading).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(holder.picPosted);
        } else if (list.get(position).image==null || list.get(position).image.isEmpty()) {
            holder.picPosted.setVisibility(View.GONE);
            holder.cardView.setVisibility(View.GONE);

        }

        holder.imageLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(position).isLiked){
                    holder.imageLike.setImageResource(R.drawable.like);
                    holder.textLikes.setText(String.valueOf(list.get(position).likes-1));
                    reference.child("posts").child(list.get(position).id).child("like").child(uid).removeValue();
                    list.get(position).isLiked=false;
                    list.get(position).setLikes(list.get(position).likes-1);
                }
                else {
                    holder.imageLike.setImageResource(R.drawable.heart);
                    holder.textLikes.setText(String.valueOf(list.get(position).likes+1));
                    reference.child("posts").child(list.get(position).id).child("like").child(uid).setValue(true);
                    list.get(position).isLiked=true;
                    list.get(position).setLikes(list.get(position).likes+1);
                }
            }
        });


        holder.imageComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,comment.class);
                intent.putExtra("id",list.get(position).id);
                intent.putExtra("message",list.get(position).message);
                if (list.get(position).time !=null ){
                    intent.putExtra("time",getTimeAgo(Long.parseLong(list.get(position).time)));
                    context.startActivity(intent);
                }

            }
        });


        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("posts").child(list.get(position).getId()).child("report").child(uid).setValue(true);
                Toast.makeText(context, "Reported", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);

        // Clear the Glide loading for the recycled view's ImageView
        Glide.with(context).clear(holder.picPosted);
    }

    @Override
    public long getItemId(int position) {
        return position; // Return a unique ID for each item
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textTime, textLikes, textComments;
        ImageView imageComment,imageLike,picPosted,report;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            report=itemView.findViewById(R.id.report);
            cardView=itemView.findViewById(R.id.card);
            picPosted=itemView.findViewById(R.id.postPic);
            textMessage = itemView.findViewById(R.id.textMessage);
            imageComment=itemView.findViewById(R.id.imageComment);
            imageLike=itemView.findViewById(R.id.imageLike);
            textTime = itemView.findViewById(R.id.textTime);
            textLikes = itemView.findViewById(R.id.textLikes);
            textComments = itemView.findViewById(R.id.textComments);
        }
    }
}
