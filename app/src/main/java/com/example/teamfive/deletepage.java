package com.example.teamfive;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@RequiresApi(api = Build.VERSION_CODES.O)
public class deletepage extends Activity {



    private FirebaseDatabase firebase;
    private DatabaseReference db;

    private FirebaseAuth mFirebaseAuth;

    String user_id;
    String place_id;

    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_deletepage);

        text=(TextView)findViewById(R.id.text);

        firebase=FirebaseDatabase.getInstance();
        db=firebase.getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();

        user_id=mFirebaseAuth.getCurrentUser().getUid();

        Intent intent = getIntent();

        place_id=intent.getStringExtra("place_id");

    }

    public void del_okay(View v)
    {
        db.child("Itemlist").child(user_id).child(place_id).removeValue();


        Intent it = new Intent();
        setResult(RESULT_OK, it);
        finish();
    }
    public void del_notokay(View v)
    {
        finish();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {

        return;
    }
}