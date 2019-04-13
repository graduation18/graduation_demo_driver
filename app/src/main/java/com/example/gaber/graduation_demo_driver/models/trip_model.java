package com.example.gaber.graduation_demo_driver.models;

public class trip_model {
    public String driver_id;
    public double Leave_from_latt,Leave_from_longt,going_to_latt,going_to_longt;
    public String Leave_from_city,going_to_city,Leave_from_address,going_to_address,Leave_from_state,going_to_state;
    public long time;
    public int seats_number,cost;
    public trip_model( String driver_id,double Leave_from_latt,double Leave_from_longt,double going_to_latt,double going_to_longt,
                       String Leave_from_city,String going_to_city,String Leave_from_address,String going_to_address,
                       String Leave_from_state,String going_to_state,long time,int seats_number,int cost){
        this.driver_id=driver_id;
        this.Leave_from_latt=Leave_from_latt;
        this.Leave_from_longt=Leave_from_longt;
        this.going_to_latt=going_to_latt;
        this.going_to_longt=going_to_longt;
        this.Leave_from_city=Leave_from_city;
        this.going_to_city=going_to_city;
        this.Leave_from_address=Leave_from_address;
        this.going_to_address=going_to_address;
        this.time=time;
        this.seats_number=seats_number;
        this.cost=cost;
        this.Leave_from_state=Leave_from_state;
        this.going_to_state=going_to_state;
    }




}
