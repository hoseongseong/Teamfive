package com.example.teamfive;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.naver.maps.map.MapView;

public class PlaceInfoFragment extends Fragment {

    private View view;
    private Context context;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference submitProfile;

    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference db=database.getReference();

    private FirebaseAuth mFirebaseAuth;
    String user_id;

    TextView place_name;
    ImageView place_img;

    String place_id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.placeinfofragment, container, false);

        init();

        upload();

        return view;
    }

    public void init() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        place_id=getArguments().getString("place_id");
        user_id=mFirebaseAuth.getCurrentUser().getUid();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        place_name = (view).findViewById(R.id.place_name);
        place_img = (view).findViewById(R.id.place_img);
    }

    public void upload() {
        db.child("Itemlist").child(user_id).child(place_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PlaceItem item=snapshot.getValue(PlaceItem.class);
                String plc_name=item.getName();
                place_name.setText(plc_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        submitProfile = storageReference.child(user_id+"/"+place_id);
        submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(place_img)
                        .load(uri)
                        .into(place_img);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                place_name.setText(""+e);
            }
        });
    }
}
