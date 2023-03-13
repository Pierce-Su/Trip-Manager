package com.example.tripmanager2;

import static com.example.tripmanager2.FragmentAddDest.REQUEST_CORD;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class FragmentRealTime extends Fragment {

    View mainView;
    MainActivity mainActivity;
    FusedLocationProviderClient fusedLocationProviderClient;

    TextView tvTime;

    TextView tvNowPlace, tvNowBeginTime, tvNowEndTime;
    ProgressBar pbNowTime;

    TextView tvNextInfo;
    Button btnAddTime, btnResetTime, btnRec, mBtnTripMap;

    BlockDest block, nextBlock;
    private double now_lat, now_lng;
    FragmentInfo  toFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        toFragment = mainActivity.infoFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_real_time, container, false);
        tvTime = mainView.findViewById(R.id.tvTime);

        tvNowPlace = mainView.findViewById(R.id.tvNowPlace);
        tvNowBeginTime = mainView.findViewById(R.id.tvNowBeginTime);
        tvNowEndTime = mainView.findViewById(R.id.tvNowEndTime);

        pbNowTime = mainView.findViewById(R.id.pbNowTime);
        pbNowTime.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        tvNextInfo = mainView.findViewById(R.id.tvNextInfo);

        btnAddTime = mainView.findViewById(R.id.btnAddTime);
        btnAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.AddTime();
                UpdateInfo();
            }
        });
        btnResetTime = mainView.findViewById(R.id.btnResetTime);
        btnResetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.ResetTime();
                UpdateInfo();
            }
        });

        findLocation();

        btnRec = mainView.findViewById(R.id.btnRec);
        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.recommendedFromRealTIme = true;
                mainActivity.ChangeFragment(mainActivity.recFrag);
            }
        });

        mBtnTripMap = mainView.findViewById(R.id.btnTripMap);
        mBtnTripMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.ChangeFragment(mainActivity.tripMapFrag);
            }
        });

        UpdateInfo();
        tvNowPlace.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    System.out.println("========now_place_textview_press===============");
                    if(block != null){
                        mainActivity.placeId = block.place_id;
                        mainActivity.blockClickFlag = true;
                        mainActivity.ChangeFragment(toFragment);
                    }
                }
                return false;
            }
        });
        tvNextInfo.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    System.out.println("========next_place_textview_press===============");
                    if(nextBlock!=null){
                        mainActivity.placeId = nextBlock.place_id;
                        mainActivity.blockClickFlag = true;
                        mainActivity.ChangeFragment(toFragment);
                    }
                }
                return false;
            }
        });
        return mainView;
    }

    public void UpdateInfo(){
        int time = mainActivity.realTime;
        tvTime.setText(String.format("%02d : %02d", time/4, time%4*15));
        block = mainActivity.blockManager.GetBlockByTime(time);
        nextBlock = mainActivity.blockManager.GetNextBlockByTime(time);
        if(block!=null){
            tvNowPlace.setText(String.format(block.name));
            tvNowBeginTime.setText(String.format("%02d : %02d", block.beginTime/4, block.beginTime%4*15));
            tvNowEndTime.setText(String.format("%02d : %02d", block.endTime/4, block.endTime%4*15));
            pbNowTime.setProgress(100*(time-block.beginTime)/(block.endTime - block.beginTime));
        }
        else{
            tvNowPlace.setText("無");
            tvNowBeginTime.setText("");
            tvNowEndTime.setText("");
            pbNowTime.setProgress(0);
        }
        if(nextBlock!=null){
            String strNextBeginTime = String.format("%02d:%02d", nextBlock.beginTime/4, nextBlock.beginTime%4*15);
            String strNextEndTime = String.format("%02d:%02d", nextBlock.endTime/4, nextBlock.endTime%4*15);
            tvNextInfo.setText(String.format("%s %s-%s",  nextBlock.name, strNextBeginTime, strNextEndTime));
        }
        else{
            tvNextInfo.setText("無");
        }


        findLocation();
        if (inthespot(0.8) == 0) {
            Dialog dialog = new Dialog("Alert", String.format("請盡快抵達: %s", block.name));
            dialog.show(getActivity().getSupportFragmentManager(), null);
        }
        /*
        if (inthespot(2) == 1) {
            isInSpot.setText("you are in the "+block.name);
        }
        else if (inthespot(2) == 0) {
            isInSpot.setText("you need to be hurry to "+block.name);
        }
        else {
            isInSpot.setText("prepare to "+nextBlock.name);
        }
         */
    }

    public void findLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity);
        if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        Geocoder geocoder = new Geocoder(mainActivity, Locale.getDefault());
                        List<Address> addresseslist = null;
                        try {
                            addresseslist = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            // 自行加入的部分只有這段
                            now_lat = addresseslist.get(0).getLatitude();
                            now_lng = addresseslist.get(0).getLongitude();
                            System.out.println(">>>>>>>>>>>>>>>>>>++++++++++"+Double.toString(addresseslist.get(0).getLatitude())+Double.toString(addresseslist.get(0).getLongitude()).substring(0, 9));
                            mainActivity.currentLat = Double.toString(now_lat).substring(0, 9);
                            mainActivity.currentLng = Double.toString(now_lng).substring(0, 9);
                            return;
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        else{
            AskforPermission();
        }
    }

    private void AskforPermission() {
        ActivityCompat.requestPermissions(mainActivity, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_CORD);
    }

    public int inthespot (double range) {
        int time = mainActivity.realTime;

        block = mainActivity.blockManager.GetBlockByTime(time);
        if (block == null) return -1;
        double dest_lat = Double.parseDouble(block.lat), dest_lng = Double.parseDouble(block.lng);
        double lngdif = now_lng - dest_lng;
        double distance =  60 * 1.1515 * (180/Math.PI) * Math.acos(
                Math.sin(now_lat * (Math.PI/180)) * Math.sin(dest_lat * (Math.PI/180)) +
                        Math.cos(now_lat * (Math.PI/180)) * Math.cos(dest_lat * (Math.PI/180)) * Math.cos(lngdif * (Math.PI/180))
        );
        System.out.println("+-+-+-+the distance>"+distance);
        distance =  distance * 1.609344; // transform to kilometer
        if (distance < range) return 1;
        else return 0;
    }
}