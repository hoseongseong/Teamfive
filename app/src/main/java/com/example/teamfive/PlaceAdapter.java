package com.example.teamfive;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    ArrayList<String> placelist;
    Context context;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference submitProfile;

    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference db=database.getReference();

    String user_id;

    public PlaceAdapter(ArrayList<String> placelist, Context context,String user_id) {
        this.placelist = placelist;
        this.context = context;
        this.user_id = user_id;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.placexml,parent,false);
        PlaceAdapter.PlaceViewHolder holder = new PlaceAdapter.PlaceViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        db.child("Itemlist").child(user_id).child(placelist.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PlaceItem item=snapshot.getValue(PlaceItem.class);
                String plc_name=item.getName();
                holder.name_place.setText(plc_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        submitProfile = storageReference.child(user_id+"/"+placelist.get(position));
        submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.img_place)
                        .load(uri)
                        .into(holder.img_place);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.name_place.setText(""+e);
            }
        });

        holder.ll_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceInfoFragment placeInfoFragment = new PlaceInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("place_id",placelist.get(position));
                placeInfoFragment.setArguments(bundle);
                ((MainActivity)context).changeFragment(placeInfoFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (placelist !=null ? placelist.size() : 0);
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_place;
        TextView name_place;
        ImageView img_place;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ll_place=itemView.findViewById(R.id.place_ll);
            this.name_place=itemView.findViewById(R.id.place_name);
            this.img_place=itemView.findViewById(R.id.place_img);
        }
    }
}