package com.example.tripmanager2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.annotations.NonNull;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FragmentMapDistanceTime extends Fragment {

    MainActivity mainActivity;
    View mainView;

    private GoogleMap mMap;
    private TextView mtv802, mtv803, mtv804, mtv805;

    String fromLat, fromLng, toLat, toLng;

    public FragmentMapDistanceTime() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context; //拿mainActivity
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_map_distance_time, container, false);

        mtv802 = mainView.findViewById(R.id.tv802);
        mtv803 = mainView.findViewById(R.id.tv803);
        mtv804 = mainView.findViewById(R.id.tv804);
        mtv805 = mainView.findViewById(R.id.tv805);

        fromLat = mainActivity.currentLat;
        fromLng = mainActivity.currentLng;
        toLat = mainActivity.selLat;
        toLng = mainActivity.selLng;

        sendTransportRequest(1);
        sendTransportRequest(2);
        sendTransportRequest(3);
        sendTransportRequest(4);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map801);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@androidx.annotation.NonNull GoogleMap googleMap) {
                mMap = googleMap;

                //顯示現在所在地點的marker
                LatLng mFromLatLng = new LatLng(Double.valueOf(fromLat),Double.valueOf(fromLng));
                mMap.addMarker(new MarkerOptions().position(mFromLatLng).title("Marker of Origin"));
                //將地圖中心點移到現在所在地點
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mFromLatLng, 12));

                //顯示目的地地點的marker (使用custom marker，即旗標)
                LatLng mToLatLng = new LatLng(Double.valueOf(toLat),Double.valueOf(toLng));
                mMap.addMarker(new MarkerOptions().position(mToLatLng).title("Marker of Destination") // below line is use to add custom marker on our map.
                        .icon(BitmapFromVector(mainActivity, R.drawable.ic_baseline_tour_24)));

            }
        });


        return mainView;
    }

    public void sendTransportRequest(Integer trans){
        OkHttpClient client = new OkHttpClient();

        String transStr;

        if(trans == 1){
            transStr = "driving";
        }else if(trans == 2){
            transStr = "bicycling";
        }else if(trans == 3){
            transStr = "walking";
        }else{
            transStr = "transit";
        }

        //第一個request : 取得 mode=driving 的距離&時間
        String url ="https://maps.googleapis.com/maps/api/distancematrix/json?origins="+fromLat+","+fromLng+"&destinations="+toLat+","+toLng+"&mode="+ transStr +"&key=AIzaSyBC_Atvqg7jCeuBZl2p-hGX3m-uL_Yu36M";

        Request request = new Request.Builder()
                .url(url)
                .build();

        // 執行OKHttp的url
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    //從收到的string (JSON format) 中，擷取出到目的地所需的距離與時間值
                    final String myResponse = response.body().string();
                    System.out.println("$$$$$$$$$$$$$Transport: " + transStr + "\nmyResponse" + myResponse);
                    int bgIndex01 = myResponse.indexOf("distance") + 42;
                    int endIndex01 = myResponse.indexOf("km");
                    String distanceStr = myResponse.substring(bgIndex01, endIndex01);
                    int bgIndex02 = myResponse.indexOf("duration") + 42;
                    int endIndex02 = myResponse.indexOf("min");
                    String durationStr = myResponse.substring(bgIndex02, endIndex02);

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String showText = "Travel Mode = " + transStr + "\n" + "Distance = " + distanceStr + "km\n" + "Duration = " + durationStr + "min";
                            if(trans == 1){
                                mtv802.setText(showText);
                            }else if(trans == 2){
                                mtv803.setText(showText);
                            }else if(trans == 3){
                                mtv804.setText(showText);
                            }else{
                                mtv805.setText(showText);
                            }


                        }
                    });

                }
            }
        });
    }

    public void sendTransportTime(Integer trans,String fLat, String fLng, String tLat, String tLng){

        OkHttpClient client = new OkHttpClient();

        String transStr;

        if(trans == 1){
            transStr = "driving";
        }else if(trans == 2){
            transStr = "bicycling";
        }else if(trans == 3){
            transStr = "walking";
        }else{
            transStr = "transit";
        }

        //第一個request : 取得 mode=driving 的距離&時間
        String url ="https://maps.googleapis.com/maps/api/distancematrix/json?origins="+fLat+","+fLng+"&destinations="+tLat+","+tLng+"&mode="+ transStr +"&key=AIzaSyBC_Atvqg7jCeuBZl2p-hGX3m-uL_Yu36M";

        Request request = new Request.Builder()
                .url(url)
                .build();

        // 執行OKHttp的url
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    //從收到的string (JSON format) 中，擷取出到目的地所需的距離與時間值
                    final String myResponse = response.body().string();
                    //int bgIndex01 = myResponse.indexOf("distance") + 42;
                    //int endIndex01 = myResponse.indexOf("km");
                    //String distanceStr = myResponse.substring(bgIndex01, endIndex01);

                    int bgIndex02 = myResponse.indexOf("duration") + 42;
                    int endIndex02 = myResponse.indexOf("min");
                    String durationStr = myResponse.substring(bgIndex02, endIndex02);

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String showText =  durationStr;
                            if(trans == 1){
                                //mtv802.setText(showText);
                                mainActivity.GetAddTrans().set_msg1(showText);
                            }else if(trans == 2){
                                //mtv803.setText(showText);
                                mainActivity.GetAddTrans().set_msg2(showText);
                            }else if(trans == 3){
                                //mtv804.setText(showText);
                                mainActivity.GetAddTrans().set_msg3(showText);
                            }else{
                                //mtv805.setText(showText);
                                mainActivity.GetAddTrans().set_msg4(showText);
                            }


                        }
                    });


                }
            }
        });

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