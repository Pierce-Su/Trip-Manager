package com.example.tripmanager2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;


public class FragmentTripList extends Fragment {

    View mainView;
    MainActivity mainActivity;
    ScrollView scrollView;
    int scrollY = 0;

    LinearLayout llTimeList;
    RelativeLayout rlBlockContainer;
    Button btnAddDest;
    int ratio;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
        ratio = (int)Utils.DP_TO_PX(10, context);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_trip_list, container, false);

        mainActivity.blockClickFlag = false;

        btnAddDest = mainView.findViewById(R.id.btnAddDest);

        btnAddDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDestFragment();
            }
        });
        scrollView = mainView.findViewById(R.id.scrollView);


        llTimeList = mainView.findViewById(R.id.llTimeList);


        rlBlockContainer = mainView.findViewById(R.id.blockContainer);
        rlBlockContainer.setMinimumHeight(24*ratio);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                scrollY = i1;
                for(int c=rlBlockContainer.getChildCount()-1; c>=0; c--){
                    ViewBlock child = (ViewBlock)rlBlockContainer.getChildAt(c);
                    child.setY(child.block.beginTime*ratio + 0.5f*ratio - i1);
                }
            }
        });

        ShowBlock();
        return mainView;
    }




    void ToDestFragment(){
        mainActivity.ChangeFragment(mainActivity.destFrag);
    }

    void ShowBlock(){
        rlBlockContainer.removeAllViews();
        for(BlockBase block : mainActivity.blockManager.blockList){

            ViewDraggableBlock draggableBlock = new ViewDraggableBlock(mainActivity, null);
            draggableBlock.SetData(block);
            RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (block.endTime-block.beginTime)*ratio);
            draggableBlock.setLayoutParams(layout);
            draggableBlock.setY(block.beginTime*ratio + 2f*ratio - scrollY);
            rlBlockContainer.addView(draggableBlock);
        }
        CheckTransportRequired();
    }

    void CheckTransportRequired(){
        ArrayList<BlockBase> list = mainActivity.blockManager.blockList;
        for(int i=0; i<list.size()-1; i++){
            if(list.get(i).type== BlockBase.BlockType.dest && list.get(i+1).type== BlockBase.BlockType.dest ){
                ViewTransRequestBlock view = new ViewTransRequestBlock(mainActivity, null);
                int id = View.generateViewId();
                String name = "請選擇交通方式";
                BlockTransRequest block = new BlockTransRequest(BlockBase.BlockType.transRequest, name, id, list.get(i).endTime, Math.max(list.get(i+1).beginTime,list.get(i).endTime + 1), (BlockDest) list.get(i), (BlockDest)list.get(i+1));
                view.SetData(block);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (block.endTime-block.beginTime)*ratio);
                view.setLayoutParams(params);
                view.setY(block.beginTime*ratio + 2f*ratio - scrollY);
                rlBlockContainer.addView(view);
            }
        }
    }
}