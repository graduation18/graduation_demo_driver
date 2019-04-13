package com.example.gaber.graduation_demo_driver.models;

public class user_data_model {
    public static final String TABLE_NAME = "signup_users";
    public String phone_number;
    public String user_token;
    public String name;
    public String email;
    public String password ;
    public user_data_model()
    {

    }
    public user_data_model(String phone_number,String user_token,String name,String email,String password) {
        this.phone_number= phone_number;
        this.name=name;
        this.user_token=user_token;
        this.email = email;
        this.password =password;

    }

}
