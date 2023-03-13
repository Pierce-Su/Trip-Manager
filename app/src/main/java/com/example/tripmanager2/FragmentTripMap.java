package com.example.tripmanager2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.icu.util.ICUUncheckedIOException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class FragmentTripMap extends Fragment {

    MainActivity mainActivity;
    View mainView;

    private GoogleMap mMap;
    LatLng temp01;

    String str01 = "";
    String str02 = "";
    String str03 = "";
    String str04 = "";
    String str05 = "";

    //private ActivityMapMyLocationBinding binding;


    double[] finalLat = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    double[] finalLng = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    String[] nameStr = {"","","","","","","","","",""};
    int dataCount;

    ArrayList<BlockDest> myTripDests;
    BlockDest currentDest;



    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;



            //顯示出發地地點的marker
//            Double frmLat = Double.parseDouble(mainActivity.currentLat);
//            Double frmLng = Double.parseDouble(mainActivity.currentLng);
//            System.out.println("===========================> Lng: "+mainActivity.currentLng+" LAT: "+mainActivity.currentLat);
//            LatLng fromLatLng = new LatLng(frmLat, frmLng);
//            mMap.addMarker(new MarkerOptions().position(fromLatLng).title("目前位置"));
//            System.out.println("=====================>Data count: " + Integer.toString(dataCount));
//            //顯示目的地地點的marker (使用custom marker，即旗標)
//            for (int i=0; i<dataCount; i++) {
//                temp01 = new LatLng(finalLat[i], finalLng[i]);
//                System.out.println("===========================> Lng: "+Double.toString(finalLat[i])+" LAT: "+Double.toString(finalLng[i]));
//                mMap.addMarker(new MarkerOptions().position(temp01).title(nameStr[i])     // below line is use to add custom marker on our map.
//                        .icon(BitmapFromVector(mainActivity, R.drawable.ic_baseline_tour_24)));
//            }
//
//            mMap.getUiSettings().setZoomControlsEnabled(true);  // 必須加這行才能讓zoom生效
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp01, 13));
        }
    };


    @Override
    public void onAttach(@com.android.annotations.NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context; //拿mainActivity
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_map_recommended, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                mMap = googleMap;

                //顯示出發地地點的marker

                Double frmLat;
                Double frmLng;



                frmLat = Double.parseDouble(mainActivity.currentLat);
                frmLng = Double.parseDouble(mainActivity.currentLng);


                LatLng fromLatLng = new LatLng(frmLat, frmLng);
                mMap.addMarker(new MarkerOptions().position(fromLatLng).title("目前位置"));

                //顯示目的地地點的marker (使用custom marker，即旗標)
                for (int i=0; i<dataCount; i++) {
                    temp01 = new LatLng(Double.parseDouble(myTripDests.get(i).lat), Double.parseDouble(myTripDests.get(i).lng));

                    if(i < myTripDests.indexOf(currentDest)){
                        mMap.addMarker(new MarkerOptions().position(temp01).title(myTripDests.get(i).name)     // below line is use to add custom marker on our map.
                                .icon(BitmapFromVector(mainActivity, R.drawable.ic_baseline_tour_24)));
                    }else if(i == myTripDests.indexOf(currentDest)){
                        mMap.addMarker(new MarkerOptions().position(temp01).title(myTripDests.get(i).name)     // below line is use to add custom marker on our map.
                                .icon(BitmapFromVector(mainActivity, R.drawable.ic_baseline_flag_circle_24)));
                    }else{
                        mMap.addMarker(new MarkerOptions().position(temp01).title(myTripDests.get(i).name)     // below line is use to add custom marker on our map.
                                .icon(BitmapFromVector(mainActivity, R.drawable.ic_baseline_outlined_flag_24)));
                    }
                }

                mMap.getUiSettings().setZoomControlsEnabled(true);  // 必須加這行才能讓zoom生效
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp01, 13));

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude+":"+latLng.longitude);
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 14
                        ));
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
        });


        myTripDests = mainActivity.blockManager.GetDestList();
        dataCount = myTripDests.size();
        currentDest = mainActivity.blockManager.GetBlockByTime(mainActivity.realTime);
        if(currentDest == null){
            currentDest = mainActivity.blockManager.GetNextBlockByTime(mainActivity.realTime);
        }
//        // 找出DB中所有的資料，取得每筆資料中的Latitude及Longitude
//        Cursor cur = null;
//        dataCount = 0;
//        for (int index01 = 0; index01<10; index01++){
//            String index01Str = Integer.toString(index01+1);  // DB中的_id是從1開始，因此要加1
//            //System.out.println(" #########    indexStr = " + index01Str + "    #########");
//            cur = db.query(true, DB_TABLE01, new String[]{"_id", "name", "geometry", "rating", "types", "vicinity", "photos", "place_id"}, "_id="+"\""+index01Str+"\"", null, null, null, null, null);
//            //System.out.println(" #########   DataBase has been assigned to cur   #########");
//            if(cur==null) {
//                cur.close();
//                myMapDB.close();
//                return mainView;
//            }
//            if(cur.getCount()==0) {
//                Toast.makeText(mainActivity, "NO DATA FOUND", Toast.LENGTH_LONG).show();
//                cur.close();
//                myMapDB.close();
//                return mainView;
//            }
//            else {
//                cur.moveToFirst();  // 讓cursor指向符合條件資料中的第一筆資料
//                //System.out.println(" #########    cursor moves to first data    #########");
//            }
//            //取得Latitude及Longitude值 (從String中parsing出來)
//            String cordStr01 = cur.getString(2);  // 讀取geometry欄位內容值
//            String cordStr02 = cordStr01.replace("{\"location\":{\"lat\":", "");  // 將讀取到的值的前頭多餘文字移除
//            int indx01 = cordStr02.indexOf(",\"lng\"");
//            String lat = cordStr02.substring(0, indx01);  // 取得lat值 (string)
//            //System.out.println("LAT of "+index01+" = "+ lat);
//            int indx02 = cordStr02.indexOf("},");
//            String lng = cordStr02.substring(indx01+7, indx02); // 取得lng值 (string)
//            //System.out.println("LNG of "+index01+" = "+ lng);
//            double dLat = Double.parseDouble(lat);
//            double dLng = Double.parseDouble(lng);
//            //System.out.print(">>>>>>>>>>>>>> dLat = ");
//            //System.out.println(dLat);
//            //System.out.print(">>>>>>>>>>>>>> dLng = ");
//            //System.out.println(dLng);
//            LatLng tmpLatLng = new LatLng(dLat,dLng);
//
//            finalLat[index01] = dLat;
//            finalLng[index01] = dLng;
//
//            //取得景點名稱:name值 (從String中parsing出來)
//            nameStr[index01] = cur.getString(1);  // 讀取name欄位內容值
//
//            dataCount++;
//        }


        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    // 此段單純是要處理custom marker，從網路上抄下來完全不需修改 (https://www.geeksforgeeks.org/how-to-add-custom-marker-to-google-maps-in-android/)
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);
        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);
        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}