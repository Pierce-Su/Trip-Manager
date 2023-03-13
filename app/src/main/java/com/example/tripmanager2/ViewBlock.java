package com.example.tripmanager2;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class ViewBlock extends androidx.appcompat.widget.AppCompatTextView {

    protected MainActivity mainActivity;
    protected FragmentTripList fragment;
    public BlockBase block;

    public ViewBlock(Context context, AttributeSet attrs){
        super(context, attrs);
        mainActivity = (MainActivity) context;
        fragment = mainActivity.tripFrag;

    }

    public void SetData(BlockBase _block){
        block = _block;
        if(block.type== BlockBase.BlockType.dest){
            setBackground(getResources().getDrawable(R.drawable.dest_block_view, null));
        }
        if(block.type== BlockBase.BlockType.trans){
            setBackground(getResources().getDrawable(R.drawable.trans_block_view, null));
        }
        if(block.type== BlockBase.BlockType.transRequest){
            setBackground(getResources().getDrawable(R.drawable.trans_request_block_view, null));
        }
        setText(block.name);
        setPadding(30, 10,0,0);

    }

}
