package com.example.gaber.graduation_demo_driver.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.models.driver_data_model;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class sign_up2 extends AppCompatActivity {
    ImageView driving_license,car_lisence,car,national_id;
    int PICK_IMAGE_MULTIPLE = 2;
    private StorageReference mStorageRef;
    String phone,name,image,gender,driving_lis_image,car_image,car_lis_image,id_image;
    int age;
    boolean driving_lis_bool,car_lis_bool,car_bool,id_bool;
    Button finish_sign_up;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        phone=getIntent().getStringExtra("phone");
        name=getIntent().getStringExtra("name");
        age=getIntent().getIntExtra("age",18);
        gender=getIntent().getStringExtra("gender");
        image=getIntent().getStringExtra("image");
        driving_license=(ImageView)findViewById(R.id.driving_license);
        car_lisence=(ImageView)findViewById(R.id.car_lisence);
        car=(ImageView)findViewById(R.id.car);
        national_id=(ImageView)findViewById(R.id.national_id);
        finish_sign_up=(Button)findViewById(R.id.finish_sign_up);

        driving_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_images();
                driving_lis_bool=true;
            }
        });
        car_lisence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_images();
                car_lis_bool=true;
            }
        });
        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_images();
                car_bool=true;
            }
        });
        national_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_images();
                id_bool=true;
            }
        });
        finish_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.length()>0&&name.length()>2&&age>18&&image.length()>0&&gender.length()>0&&driving_lis_image.length()>0&&car_image.length()>0&&car_lis_image.length()>0&&id_image.length()>0){
                    sign_up(phone, name, age, image, gender, driving_lis_image, car_image, car_lis_image, id_image);
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
        final StorageReference ref = mStorageRef.child("profiles/"+FirebaseInstanceId.getInstance().getToken() +"/car_info"+audioFilePath);
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
                    final Uri downloadUri = task.getResult();
                    progressDialog.dismiss();
                    if (driving_lis_bool) {
                        Picasso.with(sign_up2.this)
                                .load(downloadUri)
                                .fit()
                                .centerCrop()
                                .into(driving_license, new Callback() {
                            @Override
                            public void onSuccess() {
                                driving_lis_bool=false;
                                driving_lis_image=downloadUri.toString();

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(sign_up2.this, "error loading image", Toast.LENGTH_LONG).show();
                            }
                        });

                    }else if (car_bool){

                        Picasso.with(sign_up2.this)
                                .load(downloadUri)
                                .fit()
                                .centerCrop()
                                .into(car, new Callback() {
                            @Override
                            public void onSuccess() {
                                car_bool=false;
                                car_image=downloadUri.toString();

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(sign_up2.this, "error loading image", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else if (car_lis_bool){

                        Picasso.with(sign_up2.this)
                                .load(downloadUri)
                                .fit()
                                .centerCrop()
                                .into(car_lisence, new Callback() {
                            @Override
                            public void onSuccess() {
                                car_lis_bool=false;
                                car_lis_image=downloadUri.toString();

                            }

                            @Override
                            public void onError() {
                                Toast.makeText(sign_up2.this, "error loading image", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else if (id_bool){

                        Picasso.with(sign_up2.this)
                                .load(downloadUri)
                                .fit()
                                .centerCrop()
                                .into(national_id, new Callback() {
                            @Override
                            public void onSuccess() {
                                id_bool=false;
                                id_image=downloadUri.toString();
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(sign_up2.this, "error loading image", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
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
    private void sign_up(String phone,String name,int age,String image,String gender,String driving_lis_image,
                         String car_image,String car_lis_image,String id_image)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("drivers").push();
        driver_data_model driver=new driver_data_model(phone,name,age,image,gender,driving_lis_image,car_image
                ,car_lis_image,id_image,FirebaseInstanceId.getInstance().getToken());
        myRef.setValue(driver);
        getSharedPreferences("logged_in",MODE_PRIVATE).edit()
                .putString("driver_key", myRef.getKey())
                .commit();
        Intent main=new Intent(sign_up2.this,Mainview.class);
        startActivity(main);
        finish();

    }
}
