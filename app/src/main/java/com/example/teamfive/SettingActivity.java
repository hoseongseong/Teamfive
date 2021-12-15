package com.example.teamfive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {

    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference db=database.getReference();

    private FirebaseAuth mFirebaseAuth;

    String user_id;

    SeekBar meter_sk;
    TextView meter_tv;

    SeekBar zoom_sk;
    TextView zoom_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    public void init() {

        meter_sk=(SeekBar)findViewById(R.id.meter_seek);
        meter_tv=(TextView)findViewById(R.id.meter_tv);

        zoom_sk=(SeekBar)findViewById(R.id.zoom_seek);
        zoom_tv=(TextView)findViewById(R.id.zoom_tv);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user_id=mFirebaseAuth.getCurrentUser().getUid();


        db.child("Setting").child(user_id).child("meter").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int meter = snapshot.getValue(Integer.class);

                meter_sk.setProgress(meter);
                meter_tv.setText(meter+"m");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db.child("Setting").child(user_id).child("zoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int zoom = snapshot.getValue(Integer.class);

                zoom_sk.setProgress(zoom);
                zoom_tv.setText(""+zoom);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        meter_sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                meter_tv.setText(progress+"m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        zoom_sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                zoom_tv.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void complete(View view) {

        db.child("Setting").child(user_id).child("zoom").setValue(zoom_sk.getProgress());
        db.child("Setting").child(user_id).child("meter").setValue(meter_sk.getProgress());

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}