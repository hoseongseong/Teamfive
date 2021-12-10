package com.example.teamfive;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

import java.util.ArrayList;

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
    TextView place_info;
    TextView place_time;

    LinearLayout place_with;

    LinearLayout place_change;

    ArrayList<String> tag = new ArrayList<>();

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
        place_info = (view).findViewById(R.id.place_info);
        place_time = (view).findViewById(R.id.place_time);
        place_with = (view).findViewById(R.id.place_with);

        place_change = (view).findViewById(R.id.change_frame);

        place_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceInfoMapFragment placeInfoFragment = new PlaceInfoMapFragment();
                Bundle bundle = new Bundle();
                bundle.putString("place_id",place_id);
                placeInfoFragment.setArguments(bundle);
                ((MainActivity)context).changeFragment(placeInfoFragment);
            }
        });
    }

    public void upload() {
        db.child("Itemlist").child(user_id).child(place_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PlaceItem item=snapshot.getValue(PlaceItem.class);
                String plc_name=item.getName();
                place_name.setText(plc_name);
                place_info.setText(item.getInfo());
                place_time.setText(item.getTime());

                tag=item.getTag();

                for(int i=0;i<tag.size();i++) {

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.leftMargin = 5;
                    layoutParams.rightMargin = 5;
                    LinearLayout ll = new LinearLayout(context);
                    ll.setBackground(ContextCompat.getDrawable(context, R.drawable.round_background));
                    ll.setPadding(20, 10, 10, 10);
                    ll.setGravity(Gravity.CENTER_VERTICAL);
                    ll.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.setLayoutParams(layoutParams);

                    Typeface typeface = getResources().getFont(R.font.fontlight);

                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    layoutParams2.rightMargin = 5;
                    TextView tv = new TextView(context);
                    tv.setTextColor(Color.BLACK);
                    tv.setTextSize(20);
                    tv.setText(tag.get(i));
                    tv.setLayoutParams(layoutParams2);
                    tv.setTypeface(typeface);


                    ll.addView(tv);

                    place_with.addView(ll);
                }
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
