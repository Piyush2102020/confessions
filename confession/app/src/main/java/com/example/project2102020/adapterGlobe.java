package com.example.project2102020;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.util.ArrayList;

public class adapterGlobe extends RecyclerView.Adapter<adapterGlobe.ViewHolder> {

    Context context;
    ArrayList<globeModel>list;
    public adapterGlobe(Context context, ArrayList<globeModel>list){

        this.context=context;
        this.list=list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.users,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.username.setText("@"+list.get(position).username);
        holder.name.setText(list.get(position).name);
        if (list.get(position).image!=null && !list.get(position).image.isEmpty()){
            Glide.with(context).load(list.get(position).image).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.dp);
        } else if (list.get(position).image.isEmpty() || list.get(position).image==null) {

            holder.dp.setImageResource(R.drawable.backuse);

        } else {
            holder.dp.setImageResource(R.drawable.backuse);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView dp;
        TextView username,name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            name=itemView.findViewById(R.id.name);
            dp=itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position1 = getAdapterPosition();
                    String instagramUrl = list.get(position1).getInstagram();

                    if (instagramUrl != null && !instagramUrl.isEmpty()) {
                        if (!instagramUrl.startsWith("http://") && !instagramUrl.startsWith("https://")) {
                            instagramUrl = "https://" + instagramUrl;
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl));

                        // Find the best app to handle the intent
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        } else {
                            // If no app is available, show an error message
                            Toast.makeText(context, "No app available to handle this action", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });        }


    }}
