package com.example.tripmanager2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    public FragmentTripList tripFrag;
    public FragmentAddDest destFrag;
    public FragmentAddTrans transFrag;
    public FragmentRecommended recFrag;
    public FragmentInfo infoFrag;
    public FragmentMapRecommended mapRecFrag;
    public FragmentMapDistanceTime mapDistanceTime;
    public MenuFragment menuFrag;
    public FragmentRealTime realTimeFrag;
    public FragmentComment commentsFrag;
    public FragmentTripMap tripMapFrag;

    public BlockManager blockManager;
    public ArrayList<Float> list;

    public String currentLat;
    public String currentLng;

    public String selLat;
    public String selLng;


    public Integer infoIdx;

    public String placeId;
    public boolean blockClickFlag;
    public boolean fromInfoToDestFrag;
    public boolean recommendedFromRealTIme;

    public ArrayList<CommentClass> comments = new ArrayList<CommentClass>();

    public int realTime;

    Button btnEdit, btnRealTime;

    //DAO and database
    BlockDestDAO blockDestDAO;
    BlockTransDAO blockTransDAO;
    BlockDestDatabase blockDestDatabase;
    BlockTransDatabase blockTransDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        //database
        blockManager = new BlockManager();

        blockDestDatabase = Room.databaseBuilder(this,BlockDestDatabase.class,"blockdest_database")
                .allowMainThreadQueries()
                .build();
        blockTransDatabase = Room.databaseBuilder(this,BlockTransDatabase.class,"blocktrans_database")
                .allowMainThreadQueries()
                .build();
        blockDestDAO = blockDestDatabase.getBlockDestDao();
        blockTransDAO = blockTransDatabase.getBlockTransDao();
        ArrayList<BlockBase> blockbases_from_db = new ArrayList<>();
        List<BlockDest> blockDestList = blockDestDAO.getAllBlockDest();
        List<BlockTrans> blockTransList = blockTransDAO.getAllBlockTrans();
        for(int i = 0;i <blockDestList.size();i++){
            BlockBase basedata = (BlockBase) blockDestList.get(i);
            blockbases_from_db.add(basedata);
        }
        for(int i = 0;i <blockTransList.size();i++){
            BlockBase basedata = (BlockBase) blockTransList.get(i);
            blockbases_from_db.add(basedata);
        }
        blockManager.blockList = blockbases_from_db;
        blockManager.UpdateBlockList();
        //end of database
        tripFrag = new FragmentTripList();
        destFrag = new FragmentAddDest();
        transFrag = new FragmentAddTrans();
        recFrag = new FragmentRecommended();
        infoFrag = new FragmentInfo();
        mapRecFrag = new FragmentMapRecommended();
        mapDistanceTime = new FragmentMapDistanceTime();
        menuFrag = new MenuFragment();
        realTimeFrag =new FragmentRealTime();
        commentsFrag = new FragmentComment();
        tripMapFrag = new FragmentTripMap();

        realTime = 0;

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromInfoToDestFrag = false;
                recommendedFromRealTIme = false;
                ChangeFragment(tripFrag);
            }
        });
        btnRealTime = findViewById(R.id.btnRealTime);
        btnRealTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromInfoToDestFrag = false;
                recommendedFromRealTIme = false;
                ChangeFragment(realTimeFrag);
            }
        });

        //ChangeFragment(tripFrag);
        ChangeFragment(realTimeFrag);
    }
    public void ChangeFragment(Fragment to){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.flFragment, to);
        transaction.commit();
    }

    @Override
    protected void onStop(){
        blockTransDAO.deleteAllBlcokTrans();
        blockDestDAO.deleteAllBlcokDests();
        //save blocklist to database
        for(int i= 0;i<blockManager.blockList.size();i++){
            if(blockManager.blockList.get(i).type== BlockBase.BlockType.trans){
                BlockTrans transData = (BlockTrans) blockManager.blockList.get(i);
                blockTransDAO.insertBlockTrans(transData);
            }
            if(blockManager.blockList.get(i).type== BlockBase.BlockType.dest){
                BlockDest destData = (BlockDest) blockManager.blockList.get(i);
                blockDestDAO.insertBlockDest(destData);
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        blockTransDAO.deleteAllBlcokTrans();
        blockDestDAO.deleteAllBlcokDests();
        //save blocklist to database
        for(int i= 0;i<blockManager.blockList.size();i++){
            if(blockManager.blockList.get(i).type== BlockBase.BlockType.trans){
                BlockTrans transData = (BlockTrans) blockManager.blockList.get(i);
                blockTransDAO.insertBlockTrans(transData);
            }
            if(blockManager.blockList.get(i).type== BlockBase.BlockType.dest){
                BlockDest destData = (BlockDest) blockManager.blockList.get(i);
                blockDestDAO.insertBlockDest(destData);
            }
        }
        super.onDestroy();
    }

    public FragmentAddTrans GetAddTrans(){return transFrag; }

    public FragmentMapDistanceTime GetDisTime(){return mapDistanceTime; }

    public void AddTime(){
        realTime = Math.max(0, Math.min(96, realTime+1));
    }
    public void ResetTime(){
        realTime = 0;
    }
}