package com.example.gaber.graduation_demo_driver.models;

public class driver_data_model {
    public String phone,name,image,gender,driving_lis_image,car_image,car_lis_image,id_image,token;
    public int age;
    public driver_data_model(String phone, String name, int age, String image, String gender, String driving_lis_image
            , String car_image, String car_lis_image, String id_image,String token) {
        this.phone=phone;
        this.name=name;
        this.age=age;
        this.image=image;
        this.gender=gender;
        this.driving_lis_image=driving_lis_image;
        this.car_image=car_image;
        this.car_lis_image=car_lis_image;
        this.id_image=id_image;
        this.token=token;

    }
}
