package com.example.tripmanager2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FragmentAddTrans extends Fragment {

    MainActivity mainActivity;
    View mainView;
    BlockDest from;
    BlockDest to;

    Button btnExample;
    Button btnBack;
    Button btnTran1;
    Button btnTran2;
    Button btnTran3;
    Button btnTran4;
    Button btnTrans;

    TextView dst1;
    TextView dst2;
    TextView time1;
    TextView time2;


    private TextView msg1;
    private TextView msg2;
    private TextView msg3;
    private TextView msg4;

    int valid = 0;
    int valid2 = 0;
    int dur=1, add_time=0, final_add_time;
    int t1=1, t2=1, t3=1, t4=1;
    int my_color = 0xff007bff;
    int default_purple = 0xff7b1fa2;
    String trans;

    ArrayList<Integer> intArr = new ArrayList<Integer>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context; //拿mainActivity
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_add_trans, container, false);
        btnExample = mainView.findViewById(R.id.btnExample);

        dst1 = mainView.findViewById(R.id.textViewDst1);
        dst2 = mainView.findViewById(R.id.textViewDst2);
        time1 = mainView.findViewById(R.id.textViewTime1);
        time2 = mainView.findViewById(R.id.textViewTime2);

        msg1 = mainView.findViewById(R.id.textViewTrans1);
        msg2 = mainView.findViewById(R.id.textViewTrans2);
        msg3 = mainView.findViewById(R.id.textViewTrans3);
        msg4 = mainView.findViewById(R.id.textViewTrans4);

        valid = 0;
        valid2 = 0;

        btnExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(valid != 0 && valid2 !=0){
                    ChooseExample();
                }
                else if(valid == 0 && valid2 != 0){
                    Toast.makeText(getActivity(),"choose a transform",Toast.LENGTH_SHORT).show();
                }
                else if(valid2 == 0){
                    Toast.makeText(getActivity(),"press for time!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack = mainView.findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.ChangeFragment(mainActivity.tripFrag);
            }
        });


        btnTran1 = mainView.findViewById(R.id.button1);
        btnTran1.setOnClickListener( view -> chooseOne() );

        btnTran2 = mainView.findViewById(R.id.button2);
        btnTran2.setOnClickListener( view -> chooseTwo() );

        btnTran3 = mainView.findViewById(R.id.button3);
        btnTran3.setOnClickListener( view -> chooseThree() );

        btnTran4 = mainView.findViewById(R.id.button4);
        btnTran4.setOnClickListener( view -> chooseFour() );


        dst1.setText(from.name);
        dst2.setText(to.name);
        SetTimeString(from.endTime, to.beginTime);

        btnTrans = mainView.findViewById(R.id.button);
        btnTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(valid2 == 0){
                    Toast.makeText(getActivity(),"choose a transform",Toast.LENGTH_SHORT).show();
                }

                TransTime();
                valid2 = 1;
            }
        });

        return mainView;
    }

    public void TransTime() {
        sendTransTime(1, from.lat, from.lng, to.lat, to.lng);
        sendTransTime(2, from.lat, from.lng, to.lat, to.lng);
        sendTransTime(3, from.lat, from.lng, to.lat, to.lng);
        sendTransTime(4, from.lat, from.lng, to.lat, to.lng);
    }

    public void SetData(BlockDest _from, BlockDest _to){
        from = _from;
        to = _to;
    }

    void ChooseExample(){
        BlockBase.BlockType type = BlockBase.BlockType.trans;
        String name = "交通";
        int id = View.generateViewId();
        BlockTrans transBlock = new BlockTrans(type, name, id, from.endTime, from.endTime+final_add_time, trans , from.name,to.name); //建構transBlock
        mainActivity.blockManager.AddBlock(transBlock); //把transBlock加進blockList
        mainActivity.ChangeFragment(mainActivity.tripFrag); //切回trip fragment
    }

    public void sendTransTime(Integer trans,String fLat, String fLng, String tLat, String tLng){

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
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+fLat+","+fLng+"&destinations="+tLat+","+tLng+"&mode="+ transStr +"&key=AIzaSyBC_Atvqg7jCeuBZl2p-hGX3m-uL_Yu36M";

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
                    int bgIndex02 = myResponse.indexOf("duration") + 42;
                    int endIndex02 = myResponse.indexOf("min")-1;
                    String durationStr;
                    if(bgIndex02 < 0 || endIndex02 < 0){
                        durationStr = "0";
                    }else {
                        durationStr = myResponse.substring(bgIndex02, endIndex02);
                    }

                    IntFromString(durationStr);

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(trans == 1){
                                msg1.setText(durationStr);
                                if(intArr.size() == 2){
                                    t1 = intArr.get(0)*60 + intArr.get(1);;
                                }else{
                                    t1 = intArr.get(0);
                                }
                                System.out.println("%%%%%%%%%%%%%%%%%t1: " + Integer.toString(t1));
                            }else if(trans == 2){
                                msg2.setText(durationStr);
                                if(intArr.size() == 2){
                                    t2 = intArr.get(0)*60 + intArr.get(1);;
                                }else{
                                    t2 = intArr.get(0);
                                }
                                System.out.println("%%%%%%%%%%%%%%%%%t2: " + Integer.toString(t2));
                            }else if(trans == 3){
                                msg3.setText(durationStr);
                                if(intArr.size() == 2){
                                    t3 = intArr.get(0)*60 + intArr.get(1);;
                                }else{
                                    t3 = intArr.get(0);
                                }
                                System.out.println("%%%%%%%%%%%%%%%%%t3: " + Integer.toString(t3));
                            }else{
                                msg4.setText(durationStr);
                                if(intArr.size() == 2){
                                    t4 = intArr.get(0)*60 + intArr.get(1);;
                                }else{
                                    t4 = intArr.get(0);
                                }
                                System.out.println("%%%%%%%%%%%%%%%%%t4: " + Integer.toString(t4));
                            }

                        }
                    });


                }
            }
        });

    }

    //設置時間
    public void set_msg1(String m1){
        msg1.setText(m1);
        dur = Integer.parseInt(m1);
    }
    public void set_msg2(String m2){
        msg2.setText(m2);
        dur = Integer.parseInt(m2);
    }
    public void set_msg3(String m3){
        msg3.setText(m3);
        dur = Integer.parseInt(m3);
    }
    public void set_msg4(String m4){
        msg4.setText(m4);


        dur = Integer.parseInt(m4);
    }

    public void chooseOne(){
        add_time = t1/15;
        final_add_time = add_time;

        if(t2%15!=0){
            final_add_time = add_time + 1;
        }
        if(final_add_time != 0){
            valid = 1;
        }
        else {
            valid = 0;
            Toast.makeText(getActivity(),"invalid time is 0!",Toast.LENGTH_SHORT).show();
        }

        if(valid2 == 0){
            Toast.makeText(getActivity(),"press for time!",Toast.LENGTH_SHORT).show();
            valid = 0;
        }


        btnTran1.setBackgroundColor(my_color);

        btnTran2.setBackgroundColor(default_purple);
        btnTran3.setBackgroundColor(default_purple);
        btnTran4.setBackgroundColor(default_purple);

        trans = "drive";
    }
    public void chooseTwo(){
        add_time = t2/15;
        final_add_time = add_time;

        if(t2%15!=0){
            final_add_time = add_time + 1;
        }
        if(final_add_time != 0){
            valid = 1;
        }
        else {
            valid = 0;
            Toast.makeText(getActivity(),"invalid time is 0!",Toast.LENGTH_SHORT).show();
        }

        if(valid2 == 0){
            Toast.makeText(getActivity(),"press for time!",Toast.LENGTH_SHORT).show();
            valid = 0;
        }

        btnTran2.setBackgroundColor(my_color);

        btnTran1.setBackgroundColor(default_purple);
        btnTran3.setBackgroundColor(default_purple);
        btnTran4.setBackgroundColor(default_purple);
        trans = "bike";
    }
    public void chooseThree(){
        add_time = t3/15;
        final_add_time = add_time;

        if(t3%15!=0){
            final_add_time = add_time + 1;
        }
        if(final_add_time != 0){
            valid = 1;
        }
        else {
            valid = 0;
            Toast.makeText(getActivity(),"invalid time is 0!",Toast.LENGTH_SHORT).show();
        }

        if(valid2 == 0){
            Toast.makeText(getActivity(),"press for time!",Toast.LENGTH_SHORT).show();
            valid = 0;
        }

        btnTran3.setBackgroundColor(my_color);

        btnTran1.setBackgroundColor(default_purple);
        btnTran2.setBackgroundColor(default_purple);
        btnTran4.setBackgroundColor(default_purple);

        trans = "walk";
    }
    public void chooseFour(){
        add_time = t4/15;
        final_add_time = add_time;

        if(t4%15!=0){
            final_add_time = add_time + 1;
        }
        if(final_add_time != 0){
            valid = 1;
        }
        else {
            valid = 0;
            Toast.makeText(getActivity(),"invalid time is 0!",Toast.LENGTH_SHORT).show();
        }

        if(valid2 == 0){
            Toast.makeText(getActivity(),"press for time!",Toast.LENGTH_SHORT).show();
            valid = 0;
        }

        btnTran4.setBackgroundColor(my_color);

        btnTran1.setBackgroundColor(default_purple);
        btnTran3.setBackgroundColor(default_purple);
        btnTran2.setBackgroundColor(default_purple);

        trans = "transit";
    }

    public void IntFromString(String s) {

        intArr.clear();
        //Creating a pattern to identify floats
        Pattern pat = Pattern.compile("[-]?[0-9]*\\.?[0-9]+");
        //matching the string with the pattern
        Matcher m = pat.matcher(s);
        //extracting and storing the float values
        while(m.find()) {
            intArr.add(Integer.parseInt(m.group()));
        }
        //printing the float values
        System.out.println("The float values from the string are:");
        for(int i =0; i<intArr.size(); i++) {
            System.out.println(intArr.get(i));
        }
    }

    public void SetTimeString(int f, int t){
        int h1 = 0 , h2 = 0;
        int m1 = 0, m2 = 0;
        h1 = f/4;
        m1 = (f%4)*15;
        h2 = t/4;
        m2 = (t%4)*15;
        time1.setText(h1 + " : " + m1);
        time2.setText(h2 + " : " + m2);

    }




}