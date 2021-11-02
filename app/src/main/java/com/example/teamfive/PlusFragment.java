package com.example.teamfive;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import java.io.InputStream;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class PlusFragment extends AppCompatActivity{

    private final int GALLERY_CODE = 10;
    private FirebaseStorage storage=FirebaseStorage.getInstance();

    private FirebaseAuth mFirebaseAuth;

    ImageView item_img;
    EditText edit_name;
    EditText edit_info;

    Context context;

    private double latitude;
    private double longitude;

    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference db;

    String post_id;
    String post_name;
    String post_info;

    private Button button;

    private String user_id;

    Uri file = Uri.parse("android.resource://com.example/teamfive/drawable/noimg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plusfragment_layout);

        init();

        setOnClick();

    }

    public void init() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        user_id=mFirebaseAuth.getCurrentUser().getUid();

        item_img=(ImageView)findViewById(R.id.item_img);
        edit_name=(EditText)findViewById(R.id.item_name);
        edit_info=(EditText)findViewById(R.id.item_info);

        context=this;

        Intent intent = getIntent();
        latitude=intent.getExtras().getDouble("latitude");
        longitude=intent.getExtras().getDouble("longitude");

        button = findViewById(R.id.plusButton);

    }

    public void setOnClick() {
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                db=database.getReference("Itemlist").child(user_id).push();
                post_id=db.getKey();
                post_name=edit_name.getText().toString();
                post_info=edit_info.getText().toString();
                PlaceItem item = new PlaceItem(post_id,post_name,post_info,latitude,longitude,false);
                db.setValue(item);

                StorageReference storageRef = storage.getReference();
                StorageReference riversRef = storageRef.child(user_id).child(post_id);
                UploadTask uploadTask = riversRef.putFile(file);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlusFragment.this,"업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(PlusFragment.this,"업로드에 성공했습니다!", Toast.LENGTH_SHORT).show();
                    }
                });

                goBack();
            }
        });

        item_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,GALLERY_CODE);
            }
        });
    }

    void goBack() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode,final int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CODE) {
            file = data.getData();
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                item_img.setImageBitmap(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
