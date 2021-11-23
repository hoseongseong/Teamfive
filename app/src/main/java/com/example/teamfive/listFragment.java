package com.example.teamfive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class listFragment extends Fragment {
    private View view;
    private Context context;

    ArrayList<String> placelist;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth mFirebaseAuth;
    String user_id;

    private FirebaseDatabase database;
    private DatabaseReference db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        context = getActivity();
        view = inflater.inflate(R.layout.listfragment_layout, container, false);

        init();

        upload();

        return view;
    }

    public void init() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        user_id=mFirebaseAuth.getCurrentUser().getUid();

        placelist = new ArrayList();

        database=FirebaseDatabase.getInstance();
        db=database.getReference();

        recyclerView = (view).findViewById(R.id.list_rcy);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlaceAdapter(placelist,context,user_id);
        recyclerView.setAdapter(adapter);

    }

    public void upload() {

        db.child("Itemlist").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                placelist.clear();
                try {
                    for (DataSnapshot sn : snapshot.getChildren()) {
                        placelist.add(sn.getKey());
                        adapter.notifyDataSetChanged();
                    }
                } catch(NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}