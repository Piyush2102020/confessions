package com.example.project2102020;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class globe extends Fragment {

    ArrayList<globeModel>list;
    adapterGlobe adapterGlobe;
    Button call;
    RecyclerView allUsers;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_globe, container, false);
        allUsers=view.findViewById(R.id.allUsers);
        call=view.findViewById(R.id.anonymous);
        allUsers.setLayoutManager(new GridLayoutManager(getContext(),2));
        list=new ArrayList<>();
        adapterGlobe=new adapterGlobe(getContext(),list);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("instagram").keepSynced(true);
        reference.child("instagram").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    String image=snapshot1.child("image").getValue(String.class);
                    String userName=snapshot1.child("username").getValue(String.class);
                    String name=snapshot1.child("name").getValue(String.class);
                    String instagram=snapshot1.child("instagram").getValue(String.class);
                    globeModel globeModel=new globeModel(name,userName,image,instagram);
                    list.add(globeModel);
                    adapterGlobe.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),info.class));
            }
        });
        allUsers.setAdapter(adapterGlobe);
        return view;
    }
}