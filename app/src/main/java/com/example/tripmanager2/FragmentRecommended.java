package com.example.tripmanager2;

import static com.example.tripmanager2.FragmentAddDest.REQUEST_CORD;

import android.Manifest;
import android.app.appsearch.ReportSystemUsageRequest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.annotations.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.manipulation.Ordering;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FragmentRecommended extends Fragment {

    MainActivity mainActivity;
    View mainView;
    private TextView mtv001;
    private Button mbtn001;

    private TextView mtvSel01;
    private TextView mtvSel02;
    private TextView mtvSel03;
    private TextView mtvSel04;
    private TextView mtvSel05;
    private TextView mtvSel06;
    private TextView mtvSel07;
    private TextView mtvSel08;
    private TextView mtvSel09;
    private TextView mtvSel10;
    //private ScrollView mScrollView01;
    private Button mbtnSel01;
    private Button mbtnSel02;
    private Button mbtnSel03;
    private Button mbtnSel04;
    private Button mbtnSel05;
    private Button mbtnSel06;
    private Button mbtnSel07;
    private Button mbtnSel08;
    private Button mbtnSel09;
    private Button mbtnSel10;

    private final String[] str01 = {"", "", "", "", "", "", "", "", "", ""};  // name
    private final String[] str02 = {"", "", "", "", "", "", "", "", "", ""};  // geometry
    private final String[] str03 = {"", "", "", "", "", "", "", "", "", ""};  // rating
    private final String[] str04 = {"", "", "", "", "", "", "", "", "", ""};  // types
    private final String[] str05 = {"", "", "", "", "", "", "", "", "", ""};  // vicinity
    private final String[] str06 = {"", "", "", "", "", "", "", "", "", ""};  // photo (photo_reference)
    private final String[] str07 = {"", "", "", "", "", "", "", "", "", ""};  // place_id

    // SQLite Database 相關設定
    private static final String DB_FILE = "myMap.db", DB_TABLE01 = "myMap01";
    private MapDBOpenHelper mMapDBOpenHelper;

    BlockDest lastBlock;

    // 用來取得nearby search結果的URL
    //public String url= "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=25.0338,121.5646&radius=1000&keyword=%E7%89%9B%E6%8E%92&language=zh-TW&key=AIzaSyBC_Atvqg7jCeuBZl2p-hGX3m-uL_Yu36M";
    public String url = null;

    String strLAT, strLNG;

    // 使用 FusedLocationProviderClient 的物件來取得目前的位置資訊
    FusedLocationProviderClient fusedLocationProviderClient;


    String fromLng, fromLat, toLat, toLng;

    public FragmentRecommended() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context; //拿mainActivity
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_recommended, container, false);
        mtv001 = mainView.findViewById(R.id.tv001);
        mbtn001 = mainView.findViewById(R.id.btn001);

        lastBlock = mainActivity.blockManager.GetLastDest();



        if(lastBlock == null || mainActivity.recommendedFromRealTIme == true){
            System.out.println("???????????????????????????????????????????? recommended from real");
            fromLat = mainActivity.currentLat;
            fromLng = mainActivity.currentLng;
            mainActivity.recommendedFromRealTIme = false;
        }else{
            fromLat = lastBlock.lat;
            fromLng = lastBlock.lng;
        }

        mtv001.setText("LAT: " + fromLat + " ; " + "\n" + "LNG: " + fromLng);
//        url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
//                fromLat + "," + fromLng +
//                "&radius=3000&keyword=%E6%97%85%E9%81%8A%E6%99%AF%E9%BB%9E&language=zh-TW&key=AIzaSyBC_Atvqg7jCeuBZl2p-hGX3m-uL_Yu36M";

        url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                fromLat + "," + fromLng +
                "&radius=3000&type=tourist_attraction&language=zh-TW&key=AIzaSyBC_Atvqg7jCeuBZl2p-hGX3m-uL_Yu36M";


        mtvSel01 = mainView.findViewById(R.id.tvSel01);
        mtvSel02 = mainView.findViewById(R.id.tvSel02);
        mtvSel03 = mainView.findViewById(R.id.tvSel03);
        mtvSel04 = mainView.findViewById(R.id.tvSel04);
        mtvSel05 = mainView.findViewById(R.id.tvSel05);
        mtvSel06 = mainView.findViewById(R.id.tvSel06);
        mtvSel07 = mainView.findViewById(R.id.tvSel07);
        mtvSel08 = mainView.findViewById(R.id.tvSel08);
        mtvSel09 = mainView.findViewById(R.id.tvSel09);
        mtvSel10 = mainView.findViewById(R.id.tvSel10);
        mbtnSel01 = mainView.findViewById(R.id.btnSel01);
        mbtnSel02 = mainView.findViewById(R.id.btnSel02);
        mbtnSel03 = mainView.findViewById(R.id.btnSel03);
        mbtnSel04 = mainView.findViewById(R.id.btnSel04);
        mbtnSel05 = mainView.findViewById(R.id.btnSel05);
        mbtnSel06 = mainView.findViewById(R.id.btnSel06);
        mbtnSel07 = mainView.findViewById(R.id.btnSel07);
        mbtnSel08 = mainView.findViewById(R.id.btnSel08);
        mbtnSel09 = mainView.findViewById(R.id.btnSel09);
        mbtnSel10 = mainView.findViewById(R.id.btnSel10);

        mbtnSel01.setOnClickListener(mbtnSel01OnClick);
        mbtnSel02.setOnClickListener(mbtnSel02OnClick);
        mbtnSel03.setOnClickListener(mbtnSel03OnClick);
        mbtnSel04.setOnClickListener(mbtnSel04OnClick);
        mbtnSel05.setOnClickListener(mbtnSel05OnClick);
        mbtnSel06.setOnClickListener(mbtnSel06OnClick);
        mbtnSel07.setOnClickListener(mbtnSel07OnClick);
        mbtnSel08.setOnClickListener(mbtnSel08OnClick);
        mbtnSel09.setOnClickListener(mbtnSel09OnClick);
        mbtnSel10.setOnClickListener(mbtnSel10OnClick);
        mbtn001.setOnClickListener(mbtn001OnClick);


        // SQLite database 檢查資料庫是否存在，如果資料庫尚未建立，則建立新的資料庫
        mMapDBOpenHelper = new MapDBOpenHelper(mainActivity, DB_FILE, null, 1);
        SQLiteDatabase myMapDB = mMapDBOpenHelper.getWritableDatabase();
        Cursor cursor = myMapDB.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DB_TABLE01 + "'", null);

        // 每次進入此activity時，先清除此table，以避免上次的資料累積，index也歸零
        myMapDB.execSQL("DROP TABLE IF EXISTS " + DB_TABLE01);
        if (cursor != null) {
            if (cursor.getCount() == 0) {  // 如果此table不存在，就建立一個
                myMapDB.execSQL("CREATE TABLE " + DB_TABLE01 + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "geometry TEXT," +
                        "rating TEXT," +
                        "types TEXT," +
                        "vicinity TEXT," +
                        "photos TEXT," +
                        "place_id TEXT)");
            }
            cursor.close();
        }

        //以下這段被要求必須包在try裡面
        try {
            run();  // 主要的工作: (1) 執行okHttp url (2)
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mainView;
    }

    void run() throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 執行okhttp的url request
        client.newCall(request).enqueue(new Callback() {  // 網路上抄來的固定格式，未修改
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }  // 網路上抄來的固定格式，未修改

            JSONObject jsonObj01 = null;
            JSONArray jsonAry01 = null;  // 收到的json object中，可能有一些array，在此是有一個名為"results"的array

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();   // 發接收okhttp之後，收到的Json format string

                try {
                    // 將收到的 Google Place-nearby JSON格式 string 轉成 JSON object
                    jsonObj01 = new JSONObject(myResponse);

                    // 計算JSON array中有多少element
                    JSONArray selections = null;
                    try {
                        selections = jsonObj01.getJSONArray("results");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int totalCount = selections.length();

                    // 將總資料數量限制在10筆以內
                    if (totalCount > 10) {
                        totalCount = 10;
                    }

                    // 選取 JSON檔中的 "results" array
                    jsonAry01 = jsonObj01.getJSONArray("results");

                    // 讀取 "results" array 中的第 index01 筆資料的 "name", "geometry", "rating", types", "vicinity"，"photo" 等欄位string
                    SQLiteDatabase myMapDB = mMapDBOpenHelper.getWritableDatabase();
                    for (int index01 = 0; index01 < totalCount; index01++) {
                        str01[index01] = jsonAry01.getJSONObject(index01).getString("name");
                        str02[index01] = jsonAry01.getJSONObject(index01).getString("geometry");
                        str03[index01] = jsonAry01.getJSONObject(index01).getString("rating");
                        str04[index01] = jsonAry01.getJSONObject(index01).getString("types");
                        str05[index01] = jsonAry01.getJSONObject(index01).getString("vicinity");
                        try{
                            str06[index01] = jsonAry01.getJSONObject(index01).getString("photos");
                        } catch (JSONException e) {
                            str06[index01] = "";
                        }
                        str07[index01] = jsonAry01.getJSONObject(index01).getString("place_id");

                        //創建一個ContentValues的物件"newRow"，並填入該row的資料
                        ContentValues newRow = new ContentValues();
                        newRow.put("name", str01[index01]);
                        newRow.put("geometry", str02[index01]);
                        newRow.put("rating", str03[index01]);
                        newRow.put("types", str04[index01]);
                        newRow.put("vicinity", str05[index01]);
                        String photoStr;
                        if(str06[index01] != "") {   // 當此筆資料中沒有photo (photo_reference) 時的處理
                            // 由於JON string中的photo_reference欄位中，有許多額外的文字內容，需從中擷取出真正的photo_reference字串，然後才能存入database中
                            int bgIndex = str06[index01].indexOf("photo_reference") + 18;   // 在string中找出photo_reference這段文字
                            int endIndex = bgIndex + 180;
                            photoStr = str06[index01].substring(bgIndex, endIndex);
                            System.out.println("=======================> not null photo reference: "+Integer.toString(index01));
                        } else {
                            System.out.println("=======================> null photo reference: "+Integer.toString(index01));
                            photoStr = ""; }

                        newRow.put("photos", photoStr);
                        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Loop Number = " + String.valueOf(index01) + "  ;  photoStr = " + photoStr);
                        newRow.put("place_id", str07[index01]);

                        // 將整個row資料存入database中
                        myMapDB.insert(DB_TABLE01, null, newRow);

                    }
                    myMapDB.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mainActivity.runOnUiThread(new Runnable() {   // 網路上抄來的固定格式，未修改
                    @Override
                    public void run() {
                        // 在 TextView 中顯示10筆 "name" 的資料
                        int index02;   // 選出其中一筆資料來顯示在textView中
                        mtvSel01.setText(str01[0]);
                        mtvSel02.setText(str01[1]);
                        mtvSel03.setText(str01[2]);
                        mtvSel04.setText(str01[3]);
                        mtvSel05.setText(str01[4]);
                        mtvSel06.setText(str01[5]);
                        mtvSel07.setText(str01[6]);
                        mtvSel08.setText(str01[7]);
                        mtvSel09.setText(str01[8]);
                        mtvSel10.setText(str01[9]);
                    }
                });
            }
        });
    }

    // 點選各景點按鍵時，跳往並傳值到info01頁
    public void changeMainSelLatLng(int index09) {

        //取得Latitude及Longitude值 (從String中parsing出來)
        String cordStr = str02[index09-1].replace("{\"location\":{\"lat\":", "");  // 將讀取到的值的前頭多餘文字移除
        int idx01 = cordStr.indexOf(",\"lng\"");
        mainActivity.selLat = cordStr.substring(0, idx01);  // 取得lat值 (string)
        int idx02 = cordStr.indexOf("},");
        mainActivity.selLng = cordStr.substring(idx01+7, idx02); // 取得lng值 (string)

    };


    private final View.OnClickListener mbtnSel01OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainActivity.infoIdx = 1;
            changeMainSelLatLng(1);
            mainActivity.ChangeFragment(mainActivity.infoFrag);
        }
    };
    private final View.OnClickListener mbtnSel02OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainActivity.infoIdx = 2;
            changeMainSelLatLng(2);
            mainActivity.ChangeFragment(mainActivity.infoFrag);
        }
    };
    private final View.OnClickListener mbtnSel03OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mainActivity.infoIdx = 3;
            changeMainSelLatLng(3);
            mainActivity.ChangeFragment(mainActivity.infoFrag);

        }
    };
    private final View.OnClickListener mbtnSel04OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mainActivity.infoIdx = 4;
            changeMainSelLatLng(4);
            mainActivity.ChangeFragment(mainActivity.infoFrag);
        }
    };
    private final View.OnClickListener mbtnSel05OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mainActivity.infoIdx = 5;
            changeMainSelLatLng(5);
            mainActivity.ChangeFragment(mainActivity.infoFrag);
        }
    };
    private final View.OnClickListener mbtnSel06OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mainActivity.infoIdx = 6;
            changeMainSelLatLng(6);
            mainActivity.ChangeFragment(mainActivity.infoFrag);
        }
    };
    private final View.OnClickListener mbtnSel07OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mainActivity.infoIdx = 7;
            changeMainSelLatLng(7);
            mainActivity.ChangeFragment(mainActivity.infoFrag);
        }
    };
    private final View.OnClickListener mbtnSel08OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mainActivity.infoIdx = 8;
            changeMainSelLatLng(8);
            mainActivity.ChangeFragment(mainActivity.infoFrag);
        }
    };
    private final View.OnClickListener mbtnSel09OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mainActivity.infoIdx = 9;
            changeMainSelLatLng(9);
            mainActivity.ChangeFragment(mainActivity.infoFrag);
        }
    };
    private final View.OnClickListener mbtnSel10OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mainActivity.infoIdx = 10;
            changeMainSelLatLng(10);
            mainActivity.ChangeFragment(mainActivity.infoFrag);
        }
    };
    // 點擊See Map按鍵時，跳往並傳值到MapRecommend頁，以便在地圖上顯示所有推薦景點的marker
    private final View.OnClickListener mbtn001OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainActivity.ChangeFragment(mainActivity.mapRecFrag);
        }
    };

    private void AskforPermission() {
        ActivityCompat.requestPermissions(mainActivity, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_CORD);
    }

    void toInfoFragment(){
        mainActivity.ChangeFragment(mainActivity.infoFrag);
    }

}