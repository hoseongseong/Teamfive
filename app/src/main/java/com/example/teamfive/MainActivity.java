package com.example.teamfive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference db = firebaseDatabase.getReference();

    private mapFragment mapFragment;
    private PlusFragment plusFragment;
    private listFragment listFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private ImageView[] imgGroup;

    private ImageView drawable_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();

    }

    public void Init() {
        fragmentManager = getSupportFragmentManager();
        mapFragment=new mapFragment();
        plusFragment= new PlusFragment();
        listFragment=new listFragment();

        drawable_button=(ImageView)findViewById(R.id.drawableButton);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, mapFragment).commitAllowingStateLoss();

        imgGroup = new ImageView[3];
        imgGroup[0] = findViewById(R.id.img1);
        imgGroup[1] = findViewById(R.id.img2);
        imgGroup[2] = findViewById(R.id.img3);

        clickHandler(findViewById(R.id.ll2));

        //-----------여기!!!!------------
        drawable_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //-----------여기!!!!------------
    }

    public void clickHandler(View view) {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.ll1:
                SetView(0);
                Intent intent = new Intent(this,plusmapActivity.class);
                startActivity(intent);
                break;
            case R.id.ll2:
                fragmentTransaction.replace(R.id.flFragment, mapFragment).commitAllowingStateLoss();
                SetView(1);
                break;
            case R.id.ll3:
                fragmentTransaction.replace(R.id.flFragment, listFragment).commitAllowingStateLoss();
                SetView(2);
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void SetView(int targetI) {
        for(int i = 0; i<3; i++){
            if(i==targetI) {
                imgGroup[i].setColorFilter(Color.parseColor("#4F4F4F"));
            }
            else {
                imgGroup[i].setColorFilter(Color.parseColor("#BDBDBD"));
            }
        }
    }
    public void changeFragment(Fragment fragment){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment).commitAllowingStateLoss();
    }

}