package com.example.project2102020;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class user extends Fragment {

    TextView nameP,userNameP;
    ImageView dp;
    Uri selectedImage;
    TextView profileUrl;
    Button save;
    TextView edit;
    ArrayList<model>list;

    SharedPreferences pre;
    SharedPreferences.Editor editor;
    postAdapter adapter;
    RecyclerView myFeel;
    private final int Gallery_Req_code=100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user, container, false);
        dp=view.findViewById(R.id.dpP);
        edit=view.findViewById(R.id.edit);
        nameP=view.findViewById(R.id.nameP);
        profileUrl=view.findViewById(R.id.profileUrl);
        myFeel=view.findViewById(R.id.myFeel);
        myFeel.setLayoutManager(new LinearLayoutManager(getContext()));
        userNameP=view.findViewById(R.id.userNameP);
        list=new ArrayList<>();
        adapter=new postAdapter(getContext(),list);




        try {

            if (getContext()!=null){
                pre= getContext().getSharedPreferences("project2102020",Context.MODE_PRIVATE);
                editor=pre.edit();
            }


        }
catch (RuntimeException e){

            if (getActivity()!=null){

                pre= getActivity().getSharedPreferences("project2102020",Context.MODE_PRIVATE);
                editor=pre.edit();
            }


}
        String uid=pre.getString("uid","");

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name=snapshot.child("name").getValue(String.class);
                String username=snapshot.child("username").getValue(String.class);
                String dpP=snapshot.child("imageUrl").getValue(String.class);
                String instagram=snapshot.child("instagram").getValue(String.class);
                Boolean shown=snapshot.hasChild("shown");

                editor.putString("username",username);
                editor.putString("name",name);
                editor.putString("instagram",instagram);
                editor.putString("image",dpP);


                editor.putBoolean("shown",shown);
                editor.apply();

                if (instagram!=null){
                    profileUrl.setText("Instagram "+instagram);

                }

                else{
                    profileUrl.setVisibility(View.GONE);
                }
                nameP.setText(name);
                userNameP.setText("@"+username);

                if (getContext() != null && isAdded()) {
                    if (dpP != null) {
                        Glide.with(getContext()).load(dpP).diskCacheStrategy(DiskCacheStrategy.ALL).into(dp);
                    } else {
                        dp.setImageResource(R.drawable.backuse);
                    }
                }



                for (DataSnapshot snapshot1:snapshot.child("posted").getChildren()){
                    String id=snapshot1.getKey();


                    reference.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String message=snapshot.child(id).child("content").getValue(String.class);
                                String time=String.valueOf(snapshot.child(id).child("time").getValue(long.class));
                                long like=snapshot.child(id).child("like").getChildrenCount();
                                long comment=snapshot.child(id).child("comment").getChildrenCount();
                                String image=snapshot.child(id).child("imageUrl").getValue(String.class);
                                Boolean isLiked=snapshot.child(id).child("like").hasChild(uid);
                                model model=new model(message,like,comment,time,isLiked,id,image);
                                list.add(0,model);
                                adapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), edit.class);
                startActivity(intent);
            }
        });

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        myFeel.setAdapter(adapter);

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Req_code && resultCode== Activity.RESULT_OK && data!=null){

            selectedImage=data.getData();
            dp.setImageURI(selectedImage);
            uploadImage(selectedImage);

        }
    }


    public void uploadImage(Uri uri){



    }
}