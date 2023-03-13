package com.example.tripmanager2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ViewDraggableBlock extends ViewBlock {

    BlockDest data;
    MainActivity mainActivity;
    FragmentInfo  toFragment;
    public ViewDraggableBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        mainActivity = (MainActivity) context;
        toFragment = mainActivity.infoFrag;
    }

    @Override
    public void SetData(BlockBase _block) {
        super.SetData(_block);

        //data = (BlockDest) _block;

        if(_block.type== BlockBase.BlockType.trans){
            BlockTrans transData = (BlockTrans) _block;
            setText(String.format("%s:%s %s -> %s", transData.name, transData.transportType, transData.from, transData.to));
        }else{
            data = (BlockDest) _block;
        }

        setOnTouchListener(new OnTouchListener() {
            float y=0, dy=0;
            float x=0, dx=0;
            int initY, initX;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //System.out.println("ACTION_DOWN");
                        y = event.getY();
                        initY = (int)view.getY();
                        initX = (int)view.getX();
                        x = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //System.out.println("ACTION_MOVE");
                        if(dx==0 && initX==(int)view.getX()){
                            dy = event.getY() - y;
                            view.setY(view.getY() + dy);
                        }

                        if (dy == 0 && initY==(int)view.getY()) {
                            dx = event.getX() - x;
                            view.setX(view.getX() + dx);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if(dx!=0){
                            if(Math.abs(view.getX() -x)>200){
                                Log.v("debug", "delete");
                                mainActivity.blockManager.RemoveBlock(block);
                            }
                            fragment.ShowBlock();
                            break;
                        }

                        if(_block.type== BlockBase.BlockType.trans){
                            if((int)view.getY()==initY){
                                System.out.println("click");
                                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$ Transportation block clicked !!!!!!!!");
                            }
                            else {
                                System.out.println("drag");
                                block.ChangeBeginTime(block.beginTime + (int) ((view.getY() - initY) / fragment.ratio));
                                mainActivity.blockManager.UpdateBlockList((int) view.getY() - initY);
                                fragment.ShowBlock();
                            }
                        }else{
                            if((int)view.getY()==initY){
                                System.out.println("click");
                                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$ Destination block clicked !!!!!!!!");
                                System.out.println("########################## placeId of data " + data.place_id);
                                mainActivity.placeId = data.place_id;
                                System.out.println("########################## placeId of main " + mainActivity.placeId);
                                mainActivity.blockClickFlag = true;
                                mainActivity.ChangeFragment(toFragment);
                            }
                            else{
                                System.out.println("drag");
                                block.ChangeBeginTime(block.beginTime + (int)((view.getY()-initY)/fragment.ratio));
                                mainActivity.blockManager.UpdateBlockList((int)view.getY()-initY);
                                fragment.ShowBlock();
                            }
                        }

                        break;
                    default:break;
                }
                return true;
            }
        });
    }
}
