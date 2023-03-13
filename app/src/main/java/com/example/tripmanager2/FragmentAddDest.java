package com.example.tripmanager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class FragmentAddDest extends Fragment implements NumberPicker.OnValueChangeListener {

    View mainView;
    MainActivity mainActivity;

    EditText etName;
    NumberPicker npStart_H, npStart_M, npEnd_H, npEnd_M;

    Button btnCheck, btnCancel, btnRec, btnTrans;

    //data from autocomplete
    String plname, LatLng, placeId;

    int begin_H, begin_M, end_H, end_M;

    // 使用 FusedLocationProviderClient 的物件來取得目前的位置資訊
    FusedLocationProviderClient fusedLocationProviderClient;

    double myLAT, myLNG;
    String recPlace;
    String strLAT, strLNG;
    String autoStrLAT, autoStrLNG;
    public static final int REQUEST_CORD = 100;


    // DB的相關設定
    private static final String DB_FILE = "myMap.db", DB_TABLE01 = "myMap01";
    private MapDBOpenHelper mMapDBOpenHelper;
    private MapDBOpenHelper myMapDB;
    private SQLiteDatabase db;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        etName.setText(recPlace);
        Log.v("debug", String.format("onStart" + etName.getText().toString()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_add_dest, container, false);

        etName = mainView.findViewById(R.id.etDestName);

        npStart_H = mainView.findViewById(R.id.npDestStart_H);
        InitNP_H(npStart_H);

        npStart_M = mainView.findViewById(R.id.npDestStart_M);
        InitNP_M(npStart_M);

        npEnd_H = mainView.findViewById(R.id.npDestEnd_H);
        InitNP_H(npEnd_H);

        npEnd_M = mainView.findViewById(R.id.npDestEnd_M);
        InitNP_M(npEnd_M);

        btnCheck = mainView.findViewById(R.id.btnCheck);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });

        btnCancel = mainView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cancel();
            }
        });
        recPlace = "";
        if(mainActivity.fromInfoToDestFrag == true){
            String sindex01 = Integer.toString(mainActivity.infoIdx);

            // 開始從DataBase找出資料，並顯示於textView中
            myMapDB = new MapDBOpenHelper(mainActivity, DB_FILE, null, 1);
            db = myMapDB.getReadableDatabase();
            Cursor c = db.query(DB_TABLE01, new String[]{"_id", "name","geometry", "rating", "types", "vicinity", "photos","place_id"}, "_id="+"\'"+sindex01+"\'" , null, null, null, null, null);
            if(c==null) return mainView;
            if(c.getCount()==0) {
                Toast.makeText(mainActivity, "NO DATA FOUND", Toast.LENGTH_LONG).show();
            }   else {
                c.moveToFirst();  // 讓cursor指向符合條件資料的第一欄位，即_id欄位
                Integer begin, end;
                begin = c.getString(2).indexOf("{\"location")+1;
                end = c.getString(2).indexOf("viewport")-2;
                recPlace = String.format(c.getString(1));
            }

            Integer bgIdx01 = c.getString(2).indexOf("lat")+ 5;
            Integer endIdx01 = bgIdx01 + 8;

            Integer bgIdx02 = c.getString(2).indexOf("lng")+ 5;
            Integer endIdx02 = bgIdx02 + 8;

            autoStrLAT = c.getString(2).substring(bgIdx01, endIdx01);
            autoStrLNG = c.getString(2).substring(bgIdx02, endIdx02);
            placeId = c.getString(7);
            System.out.println("string01 =======================>" + c.getString(1));
            System.out.println("string02 =======================>" + c.getString(2));
            System.out.println("string03 =======================>" + c.getString(3));
            System.out.println("string04 =======================>" + c.getString(4));
            System.out.println("string05 =======================>" + c.getString(5));
            System.out.println("string06 =======================>" + c.getString(6));
            System.out.println("string07 =======================>" + c.getString(7));
            c.close();
            myMapDB.close();
        }

        //---------------------------autocomplete----------------------------
        //initialize places api
        Places.initialize(getActivity().getApplicationContext(), "AIzaSyCV8VLYXALJCvb9_zCMvwlYnqitX-AI_a0");

        //set editText non focusable
        etName.setFocusable(false);

        etName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                mainActivity.fromInfoToDestFrag = false;
                //initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        , Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID);
                //create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                        , fieldList).build(getActivity());
                //start activity result
                startActivityForResult(intent, 100);

            }
        });

        //-----------------------------autocomplete---------------------


        btnRec = mainView.findViewById(R.id.btnRec);
        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toRecommendedFragment();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity);
        //FindLocation();

        // =======================================================================
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
                            strLAT = Double.toString(addresseslist.get(0).getLatitude());
                            strLNG = Double.toString(addresseslist.get(0).getLongitude()).substring(0, 9);
                            System.out.println(">>>>>>>>>>>>>>>>>>++++++++++"+strLAT + strLNG);
                            mainActivity.currentLat = strLAT;
                            mainActivity.currentLng = strLNG;
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
        // =======================================================================
        System.out.println(String.format("=====================> " + Geocoder.isPresent()));
        System.out.println(">>>>>>>>>>>>>>>>>>"+strLAT + strLNG);
        Log.v("debug", String.format("onCreateView" + etName.getText().toString()));
        return mainView;
    }




    // 依照網路上的做法不用修改，僅加入顯示於TextView的部分
    private void FindLocation(){
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
                            strLAT = Double.toString(addresseslist.get(0).getLatitude());
                            strLNG = Double.toString(addresseslist.get(0).getLongitude()).substring(0, 9);
                            System.out.println(">>>>>>>>>>>>>>>>>>++++++++++"+strLAT + strLNG);
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
        Log.v("debug", String.format("FindLocation" + etName.getText().toString()));
    }

    //----------------------------autocomplete--------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == getActivity().RESULT_OK){
            //when success
            //initialize place
            Place place = Autocomplete.getPlaceFromIntent(data);
            //set address on edittext
            etName.setText(String.format(place.getName()));
            //set locality name
            //textView1.setText(place.getAddress());
            //set lat/lng
            //textView2.setText(String.valueOf(place.getLatLng()));
            plname = String.format(place.getName());
            LatLng = String.valueOf(place.getLatLng());
            placeId = place.getId();
            System.out.println("%%%%%%%%%%%%%%%%%%%%% Place ID from place.getid(): " + placeId);
            mainActivity.placeId = placeId;

            int bgIndex01 = LatLng.indexOf("(") + 1;
            int endIndex01 = LatLng.indexOf(",");
            autoStrLAT = LatLng.substring(bgIndex01, endIndex01);
            int bgIndex02 = LatLng.indexOf(",") + 1;
            int endIndex02 = LatLng.indexOf(")");
            autoStrLNG = LatLng.substring(bgIndex02, endIndex02);

        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            //when error
            //initialize status
            Status status = Autocomplete.getStatusFromIntent(data);
            //display toast
            Toast.makeText(getActivity().getApplicationContext(), status.getStatusMessage()
                    , Toast.LENGTH_SHORT).show();
        }
        Log.v("debug", String.format("onActivityResult" + etName.getText().toString()));
    }
    //----------------------------autocomplete--------------------------------



    private void AskforPermission() {
        ActivityCompat.requestPermissions(mainActivity, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_CORD);
        Log.v("debug", String.format("AskforPermission" + etName.getText().toString()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CORD){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                FindLocation();
            } else {
                //Toast.makeText(GetMyLocation.this, "Permission Required", Toast.LENGTH_SHORT.show());
            }
        }
        Log.v("debug", String.format("onRequestPermissionsResult" + etName.getText().toString()));
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    void toRecommendedFragment(){
        mainActivity.ChangeFragment(mainActivity.recFrag);
    }

    void InitNP_H(NumberPicker np){
        np.setMinValue(0);
        np.setMaxValue(23);
        np.setOnValueChangedListener(this);
    }
    void InitNP_M(NumberPicker np){
        np.setMinValue(0);
        np.setMaxValue(3);
        np.setDisplayedValues(new String[]{"0", "15", "30", "45"});
        np.setOnValueChangedListener(this);
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        if(numberPicker==npStart_H){
            begin_H = i1;
        }
        if(numberPicker==npStart_M){
            begin_M = i1;
        }
        if(numberPicker==npEnd_H){
            end_H = i1;
        }
        if(numberPicker==npEnd_M){
            end_M = i1;
        }
    }

    void Check() {

        BlockBase.BlockType type = BlockBase.BlockType.dest;
        String name = etName.getText().toString();
        if(name.isEmpty()){
            Dialog dialog = new Dialog("Alert", "請輸入地點");
            dialog.show(getActivity().getSupportFragmentManager(), null);
            return;
        }
        int beginTime = begin_H*4 + begin_M;
        int endTime = end_H*4 + end_M;
        if(beginTime>=endTime) {
            Dialog dialog = new Dialog("Alert", "請輸入正確時間");
            dialog.show(getActivity().getSupportFragmentManager(), null);
            return;
        }

        int id = View.generateViewId();
        BlockDest block;
        if(mainActivity.fromInfoToDestFrag == true){
            block = new BlockDest(type, name, id, beginTime, endTime, autoStrLAT, autoStrLNG, placeId);
        }else{
            block = new BlockDest(type, name, id, beginTime, endTime, autoStrLAT, autoStrLNG, placeId);
        }


        etName.setText("");
        begin_H = 0;
        begin_M = 0;
        end_H = 0;
        end_M = 0;

        mainActivity.blockManager.AddBlock(block);
        mainActivity.fromInfoToDestFrag = false;
        mainActivity.ChangeFragment(mainActivity.tripFrag);
    }


    void Cancel() {
        Log.v("debug", "cancel");
        mainActivity.fromInfoToDestFrag = false;
        mainActivity.ChangeFragment(mainActivity.tripFrag);
    }
}