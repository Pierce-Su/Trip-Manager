package com.example.tripmanager2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.annotations.NonNull;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FragmentInfo extends Fragment {

    MainActivity mainActivity;
    View mainView;

    String strPlaceId, strGetDetails;
    String photoRef, photoUrl;

    private TextView mtv001;
    private ImageView mimg001;
    private Button mbtn701;
    private Button mbtnCmt;

    private static final String DB_FILE = "myMap.db", DB_TABLE01 = "myMap01";
    private MapDBOpenHelper mMapDBOpenHelper;

    private MapDBOpenHelper myMapDB;
    private SQLiteDatabase db;

    String infoLat, infoLng;

    String ratingStr = "N/A", descriptionStr = "N/A", addressStr = "N/A", phoneStr = "N/A", openingHoursStr = "N/A";

    public FragmentInfo() {
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
        mainView = inflater.inflate(R.layout.fragment_info, container, false);
        mtv001 = mainView.findViewById(R.id.tv001);
        mimg001 = mainView.findViewById(R.id.img001);
        mbtn701 = mainView.findViewById(R.id.btn701);
        mbtn701.setOnClickListener(mbtn701OnClick);
        mbtnCmt = mainView.findViewById(R.id.btnCmt);
        mbtnCmt.setOnClickListener(mbtnCmtnClick);

        if(mainActivity.blockClickFlag == true){
            strPlaceId = mainActivity.placeId;
            System.out.println("=================================>  " + strPlaceId);
            strGetDetails = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + strPlaceId + "&language=zh-TW&key=AIzaSyBC_Atvqg7jCeuBZl2p-hGX3m-uL_Yu36M";

        }else{
            System.out.println("Info++++++++++++++++: ");
            String sindex01 = Integer.toString(mainActivity.infoIdx);

            // 開始從DataBase找出資料，並顯示於textView中
            myMapDB = new MapDBOpenHelper(mainActivity, DB_FILE, null, 1);
            db = myMapDB.getReadableDatabase();
            Cursor c = db.query(DB_TABLE01, new String[]{"_id", "name","geometry", "rating", "types", "vicinity", "photos","place_id"}, "_id="+"\'"+sindex01+"\'" , null, null, null, null, null);
            if(c==null) return mainView;
            if(c.getCount()==0) {
                mtv001.setText("");
                Toast.makeText(mainActivity, "NO DATA FOUND", Toast.LENGTH_LONG).show();
            }   else {
                c.moveToFirst();  // 讓cursor指向符合條件資料的第一欄位，即_id欄位
                Integer begin, end;
                begin = c.getString(2).indexOf("{\"location")+1;
                end = c.getString(2).indexOf("viewport")-2;
//                mtv001.append(
//                        c.getString(1) + "\n\n" +
//                                c.getString(2).substring(begin, end).replace("\"", "") + "\n\n" +
//                                c.getString(3) + "\n\n" +
//                                c.getString(4) + "\n\n" +
//                                c.getString(5) + "\n\n" +
//                                c.getString(7));
            }
            System.out.println("string01 =======================>" + c.getString(1));
            System.out.println("string02 =======================>" + c.getString(2));
            System.out.println("string03 =======================>" + c.getString(3));
            System.out.println("string04 =======================>" + c.getString(4));
            System.out.println("string05 =======================>" + c.getString(5));
            System.out.println("string06 =======================>" + c.getString(6));
            System.out.println("string07 =======================>" + c.getString(7));
            strPlaceId = c.getString(7);  // 儲存plaec_id到字串中，供後面的okhttp request使用 (用details API取得business hours)
            System.out.println("=================================>  " + strPlaceId);
            strGetDetails = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + strPlaceId + "&language=zh-TW&key=AIzaSyBC_Atvqg7jCeuBZl2p-hGX3m-uL_Yu36M";
            photoRef = c.getString(6);   // 儲存photo_reference到字串中；供後面取得照片
            photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoRef + "&key=AIzaSyBC_Atvqg7jCeuBZl2p-hGX3m-uL_Yu36M";
            new DownloadImageTask(mimg001).execute(photoUrl);
            c.close();
            myMapDB.close();
        }


        // 先找出place_id，然後發okhttp取得business hours
        OkHttpClient client = new OkHttpClient();
        Request request01 = new Request.Builder()
                .url(strGetDetails)
                .build();

        client.newCall(request01).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response01) throws IOException {
                if (response01.isSuccessful()) {

                    // 從google回傳的string中，parsing取出business hours資訊
                    final String myResponse01 = response01.body().string();
                    System.out.println("+++++++++++++>>>>>>>>>> 接收 : " + myResponse01);
                    int bgidx = myResponse01.indexOf("weekday_text");
                    final String myResponse02 = myResponse01.substring(bgidx+30);  // 先移除前面的多餘字串
                    int endidx = myResponse02.indexOf("]");
                    final String myResponse03 = myResponse02.substring(0, endidx-11);  // 再移除後面的多於文字
                    final String myResponse04 = myResponse03.replace(" ", "");  //清除字串內的空格，以便各行能對齊

//                    mainActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            mtv001.append(
////                                    "BUSINESS HOURS : \n" +
////                                            myResponse04);
//                            mtv001.append(ratingStr + descriptionStr + addressStr + phoneStr + openingHoursStr);
//
//                        }
//                    });
                    if(mainActivity.blockClickFlag == true){
                        int photoIdx = myResponse01.indexOf("photo_reference")+20;
                        int phototEndIdx = photoIdx + 180;
                        photoRef = myResponse01.substring(photoIdx, phototEndIdx);
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@phtotoRef = " + photoRef);
                        photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoRef + "&key=AIzaSyBC_Atvqg7jCeuBZl2p-hGX3m-uL_Yu36M";
                        // 載入照片到 image view 中
                        new DownloadImageTask(mimg001).execute(photoUrl);
                    }

                    JSONObject jsonObj;

                    try {
                        jsonObj = new JSONObject(myResponse01);
                        JSONObject resultObj = jsonObj.getJSONObject("result");
                        JSONArray reviewArray = resultObj.getJSONArray("reviews");
                        mainActivity.comments.clear();
                        for (int i = 0; i < reviewArray.length(); i++) {
                            JSONObject item = reviewArray.getJSONObject(i);
                            // Now you can retrieve the lat
                            CommentClass temp = new CommentClass();
                            temp.author = item.getString("author_name");
                            temp.iconUrl = item.getString("profile_photo_url");
                            temp.rating = item.getString("rating");
                            temp.relativeTime = item.getString("relative_time_description");
                            temp.text = item.getString("text");
                            mainActivity.comments.add(i, temp);
                            System.out.println("================> comment#" + Integer.toString(i) + ": " + mainActivity.comments.get(i).text);
                        }

                        ratingStr = "Rating: \n" + resultObj.optString("rating", "N/A") + "\n\n";

                        JSONObject editorialSummaryObj;

                        if(resultObj.has("editorial_summary")){
                            editorialSummaryObj = resultObj.getJSONObject("editorial_summary");
                            descriptionStr = "Description: \n" + editorialSummaryObj.optString("overview", "N/A") + "\n\n";
                        }else{
                            descriptionStr = "No description.\n\n";
                        }



                        addressStr = "Address: \n" + resultObj.optString("formatted_address", "N/A") + "\n\n";


                        phoneStr = "Telephone number: \n" + resultObj.optString("formatted_phone_number", "N/A") + "\n\n";

                        JSONObject openingHoursObj;
                        if(resultObj.has("current_opening_hours")){
                            openingHoursObj = resultObj.getJSONObject("current_opening_hours");
                            JSONArray weekday_text_Array = openingHoursObj.getJSONArray("weekday_text");
                            openingHoursStr = "Opening hours: \n";
                            for (int i=0; i<weekday_text_Array.length(); i++){
                                openingHoursStr += weekday_text_Array.getString(i) + "\n";
                                //mtv001.append(weekday_text_Array.getString(i) + "\n");
                            }
                            openingHoursStr += "\n";
                        }else{
                            openingHoursStr = "No opening hours information\n";
                        }



                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                            mtv001.append(
//                                    "BUSINESS HOURS : \n" +
//                                            myResponse04);
                                mtv001.append(ratingStr + descriptionStr + addressStr + phoneStr + openingHoursStr);

                            }
                        });

                        //mtv001.append("\n");
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                        // Do something to recover ... or kill the app.
                    }

                }
            }
        });

        //mtv001.append(ratingStr + descriptionStr + addressStr + phoneStr + openingHoursStr);

        return mainView;
    }

    // 負責下載照片的class (整段copy自網路上，不需修改)
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            Response response = null;
            Bitmap mIcon11 = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()) {
                try {
                    mIcon11 = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private View.OnClickListener mbtn701OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mainActivity.blockClickFlag == false){
                mainActivity.fromInfoToDestFrag = true;
                mainActivity.ChangeFragment(mainActivity.destFrag);
            }else{
                // do nothing
            }
            //mainActivity.ChangeFragment(mainActivity.mapDistanceTime);
        }
    };

    private View.OnClickListener mbtnCmtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainActivity.ChangeFragment(mainActivity.commentsFrag);
        }
    };

}