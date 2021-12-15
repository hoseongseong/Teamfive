package com.example.teamfive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    //드로우에서 현재 로그인한 이메일 가져오기
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();
        setNavigationBar();

    }



    public void Init() {
        fragmentManager = getSupportFragmentManager();
        mapFragment=new mapFragment();
        plusFragment= new PlusFragment();
        listFragment=new listFragment();

        //drawable_button=(ImageView)findViewById(R.id.drawableButton);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, mapFragment).commitAllowingStateLoss();

        imgGroup = new ImageView[3];
        imgGroup[0] = findViewById(R.id.img1);
        imgGroup[1] = findViewById(R.id.img2);
        imgGroup[2] = findViewById(R.id.img3);

        clickHandler(findViewById(R.id.ll2));

        //-----------여기!!!!------------

        //drawable_button.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_SHORT).show();
        //    }
        //});
        //-----------여기!!!!------------
    }


    //--------------------------------------드로어------------------------------
    public void setNavigationBar(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        navigationView = (NavigationView)findViewById(R.id.navigationView);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_format_list_bulleted_24);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                int id = menuItem.getItemId();
                if(id == R.id.item_logout) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.item_setting) {
                    Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(intent);
                }
                else if(id==R.id.item_manager) {

                }
                return true;
            }
        });


        LinearLayout ll_navigation_container = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.navigation_item, null);
        ll_navigation_container.setBackground(getResources().getDrawable(R.color.main_color));
        ll_navigation_container.setPadding(30, 70, 30, 50);
        ll_navigation_container.setOrientation(LinearLayout.VERTICAL);
        ll_navigation_container.setGravity(Gravity.BOTTOM);
        ll_navigation_container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final String[] username = {""};
        final TextView tv_user_email = new TextView(this);
        tv_user_email.setTextSize(20);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();
        firebaseDatabase.getReference("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    if(dataSnapshot.getKey().equals("name")) {
                        username[0] = dataSnapshot.getValue().toString();
                        tv_user_email.setText(username[0] + " 님"); } }
                        @Override public void onCancelled(@NonNull DatabaseError error) { }
        });

        tv_user_email.setText(firebaseUser.getEmail());

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home :
            {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item); }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                changeFragment(mapFragment);
            }
        }
    }

    public void del(String place_id) {
        Intent intent = new Intent(this,deletepage.class);
        intent.putExtra("place_id",place_id);
        startActivityForResult(intent,1);
    }

}