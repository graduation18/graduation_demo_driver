package com.example.gaber.graduation_demo_driver.fragments.carpooling;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.custom.DataPassListener;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import java.util.Date;

public class carpooling_date_step3 extends Fragment  {
    View view;
    Button Confirm_Leaving_from;
    DataPassListener mCallback;
    private double Leave_from_latt,Leave_from_longt,going_to_latt,going_to_longt;
    private String Leave_from_state,going_to_state,Leave_from_city,going_to_city,Leave_from_address,going_to_address;
    private long time;
    private TextView back,number_of_notifications,notifications;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (DataPassListener) context;
        Bundle args = getArguments();
        going_to_latt=args.getDouble("going_to_latt");
        going_to_longt=args.getDouble("going_to_longt");
        going_to_city=args.getString("going_to_city");
        going_to_address=args.getString("going_to_address");
        going_to_state=args.getString("going_to_state");
        Leave_from_latt=args.getDouble("Leave_from_latt");
        Leave_from_longt=args.getDouble("Leave_from_longt");
        Leave_from_city=args.getString("Leave_from_city");
        Leave_from_address=args.getString("Leave_from_address");
        Leave_from_state=args.getString("Leave_from_state");
        time=args.getLong("time");


        Log.w("ssss",Leave_from_city+" "+going_to_city);

    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.date_and_tiem, container, false);
        Confirm_Leaving_from=(Button)view.findViewById(R.id.Confirm_Leaving_from);
        back=(TextView)view.findViewById(R.id.back);
        final SingleDateAndTimePicker singleDateAndTimePicker = (SingleDateAndTimePicker) view.findViewById(R.id.single_day_picker);
        singleDateAndTimePicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                Toast.makeText(getActivity(),displayed,Toast.LENGTH_LONG).show();
            }
        });

        Confirm_Leaving_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time=singleDateAndTimePicker.getDate().getTime();
                if (time!=0){
                    Bundle args = new Bundle();
                    args.putDouble("going_to_latt",going_to_latt);
                    args.putDouble("going_to_longt",going_to_longt);
                    args.putString("going_to_city",going_to_city);
                    args.putString("going_to_address",going_to_address);
                    args.putDouble("Leave_from_latt",Leave_from_latt);
                    args.putDouble("Leave_from_longt",Leave_from_longt);
                    args.putString("Leave_from_city",Leave_from_city);
                    args.putString("Leave_from_address",Leave_from_address);
                    args.putString("Leave_from_state",Leave_from_state);
                    args.putString("going_to_state",going_to_state);
                    args.putLong("time",time);
                    mCallback.passData(new carpooling_seats_step4(),args);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putDouble("going_to_latt",going_to_latt);
                args.putDouble("going_to_longt",going_to_longt);
                args.putString("going_to_city",going_to_city);
                args.putString("going_to_state",going_to_state);
                args.putString("going_to_address",going_to_address);
                args.putDouble("Leave_from_latt",Leave_from_latt);
                args.putDouble("Leave_from_longt",Leave_from_longt);
                args.putString("Leave_from_city",Leave_from_city);
                args.putString("Leave_from_address",Leave_from_address);
                args.putString("Leave_from_state",Leave_from_state);
                mCallback.passData(new carpooling_going_to_step2(),args);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Bundle args = new Bundle();
                    args.putDouble("going_to_latt",going_to_latt);
                    args.putDouble("going_to_longt",going_to_longt);
                    args.putString("going_to_city",going_to_city);
                    args.putString("going_to_state",going_to_state);
                    args.putString("going_to_address",going_to_address);
                    args.putDouble("Leave_from_latt",Leave_from_latt);
                    args.putDouble("Leave_from_longt",Leave_from_longt);
                    args.putString("Leave_from_city",Leave_from_city);
                    args.putString("Leave_from_address",Leave_from_address);
                    args.putString("Leave_from_state",Leave_from_state);
                    mCallback.passData(new carpooling_going_to_step2(),args);
                    return true;
                }
                return false;
            }
        });

        return view;
    }


}
