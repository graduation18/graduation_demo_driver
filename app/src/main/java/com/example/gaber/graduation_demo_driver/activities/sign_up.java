package com.example.gaber.graduation_demo_driver.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gaber.graduation_demo_driver.R;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class sign_up extends AppCompatActivity {
    EditText name,age;
    Button next;
    ImageView photo;
    StickySwitch stickySwitch;
    int PICK_IMAGE_MULTIPLE = 2;
    String image,phone;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name=(EditText)findViewById(R.id.input_name);
        age=(EditText)findViewById(R.id.input_age);
        next=(Button)findViewById(R.id.next);
        photo=(ImageView)findViewById(R.id.photo);
        stickySwitch = (StickySwitch) findViewById(R.id.sticky_switch);
        stickySwitch.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String text) {

            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_images();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference();
        phone=getIntent().getStringExtra("phone");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_name=name.getText().toString();
                int s_age= Integer.parseInt(age.getText().toString());
                String s_image=image;
                String s_gender=stickySwitch.getText();
                Log.w("sddshhjsdjk",s_gender+" "+s_image+" "+s_name+" "+s_age+" "+phone);
                if (s_name.length()>2&&s_age>18&&s_image.length()>0){
                    Intent car_info=new Intent(sign_up.this,sign_up2.class);
                    car_info.putExtra("name",s_name);
                    car_info.putExtra("age",s_age);
                    car_info.putExtra("image",s_image);
                    car_info.putExtra("gender",s_gender);
                    car_info.putExtra("phone",phone);
                    startActivity(car_info);
                }else {
                    Toast.makeText(sign_up.this,"your data is incomplete or wrong",Toast.LENGTH_LONG).show();
                }

            }
        });


    }
    private void select_images()
    {
        Pix.start(this,
                PICK_IMAGE_MULTIPLE,1);
    }
    private void upload_image(String audioFilePath)
    {
        Uri file = Uri.fromFile(new File(audioFilePath));
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        final StorageReference ref = mStorageRef.child("profiles/"+FirebaseInstanceId.getInstance().getToken() +"/"+audioFilePath);
        UploadTask uploadTask = ref.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    progressDialog.dismiss();
                    image=downloadUri.toString();
                    Picasso.with(sign_up.this)
                            .load(downloadUri)
                            .resize(100,100)
                            .centerCrop()
                            .onlyScaleDown()
                            .into(photo, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override public void onError() {
                            Toast.makeText(sign_up.this,"error loading image",Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Handle failures
                    // ...
                    progressDialog.dismiss();

                }
            }

        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage(String.valueOf(progress)+"% Uploaded");
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_MULTIPLE) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for (String uri:returnValue){
                upload_image(uri);
            }
        }

    }
}
