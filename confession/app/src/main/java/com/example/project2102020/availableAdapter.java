package com.example.project2102020;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class availableAdapter extends RecyclerView.Adapter<availableAdapter.ViewHolder> {

    Context context;
    ArrayList<userModel>list;
    public availableAdapter(Context context,ArrayList<userModel>list){

        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.available_user,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        if (list.get(position).gender!=null){

            if (list.get(position).gender.equals("Male")){
                holder.gender.setImageResource(R.drawable.male);
            }

            else {

                holder.gender.setImageResource(R.drawable.female);


            }
        }


        holder.userName.setText(list.get(position).userName);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView gender;
        TextView userName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gender=itemView.findViewById(R.id.gender);
            userName=itemView.findViewById(R.id.usernameAvailable);
            FirebaseAuth mAuth=FirebaseAuth.getInstance();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();

                    if (list.get(pos).getUid().equals(mAuth.getUid())){
                        Toast.makeText(context, "Sorry you cannot call yourself", Toast.LENGTH_SHORT).show();
                    }

                    else {

                        Intent intent1=new Intent(context,chat_activity.class);
                        intent1.putExtra("targetUid",list.get(pos).uid);
                        intent1.putExtra("targetUser",list.get(pos).userName);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                    }


                }
            });
        }
    }
}
