package com.example.tripmanager2;

import android.util.Log;

import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BlockManager {
    public BlockManager(){
        tripBeginTime = 0;
        tripEndTime = 96;
        blockList = new ArrayList<>();
    };

    public int tripBeginTime;
    public int tripEndTime;
    public ArrayList<BlockBase> blockList;

    public void AddBlock(BlockBase block){
        blockList.add(block);
        UpdateBlockList();
    }
    public void RemoveBlock(BlockBase _block){
        blockList.remove(_block);
        UpdateBlockList();
    }
    public void SetTripTime(int _begin, int _end){
        tripBeginTime = _begin;
        tripEndTime = _end;
    }
    public BlockBase GetBlockByID(int id){
        for(int i=0; i< blockList.size(); i++){
            if(blockList.get(i).id==id){
                return blockList.get(i);
            }
        }
        return null;
    }

    public void UpdateBlockList(int... _synchronize){
        int synchronize = (_synchronize.length>=1) ? _synchronize[0] : 0;
        Collections.sort(blockList, new Comparator<BlockBase>() {
            @Override
            public int compare(BlockBase b1, BlockBase b2) {
                return b1.beginTime - b2.beginTime;
            }
        });
        if(synchronize>=0){
            SynchronizeDown();
            SynchronizeUp();
        }
        if(synchronize<0){
            SynchronizeUp();
            SynchronizeDown();
        }

        CheckTransValid();
    }
    public void SynchronizeDown(){
        for(int i=0; i<blockList.size(); i++){
            if(blockList.get(i).beginTime<tripBeginTime){
                blockList.get(i).ChangeBeginTime(tripBeginTime);
            }
            if(i>0 && blockList.get(i).beginTime<blockList.get(i-1).endTime){
                blockList.get(i).ChangeBeginTime(blockList.get(i-1).endTime);
            }
        }
    }
    public void SynchronizeUp(){
        for(int i=blockList.size()-1; i>=0; i--){
            if(blockList.get(i).endTime>tripEndTime){
                blockList.get(i).ChangeEndTime(tripEndTime);
            }
            if(i<blockList.size()-1 && blockList.get(i).endTime>blockList.get(i+1).beginTime){
                blockList.get(i).ChangeEndTime(blockList.get(i+1).beginTime);
            }
        }
    }
    void CheckTransValid(){
        for(int i=blockList.size()-1; i>=0; i--){
            if(blockList.get(i).type== BlockBase.BlockType.trans){
                BlockTrans block = (BlockTrans) blockList.get(i);
                if(i==0 || i==blockList.size()-1){
                    blockList.remove(i);
                }else if(!block.from.equals(blockList.get(i-1).name) || !block.to.equals(blockList.get(i+1).name)){
                    blockList.remove(i);
                }
            }
        }
    }
    public BlockDest GetBlockByTime(int time){
        for(BlockBase  block: blockList){
            if(block.type== BlockBase.BlockType.dest){
                if(block.beginTime<=time && block.endTime>time){
                    return (BlockDest) block;
                }
                if(block.beginTime>time){
                    return null;
                }
            }
        }
        return null;
    }
    public BlockDest GetNextBlockByTime(int time){
        for(BlockBase block : blockList){
            if(block.type== BlockBase.BlockType.dest){
                if(block.beginTime>time){
                    return (BlockDest) block;
                }
            }
        }
        return null;
    }

    public BlockDest GetLastDest(){
        for(int i= blockList.size()-1; i>=0; i--){
            if(blockList.get(i).type== BlockBase.BlockType.dest){
                return (BlockDest)blockList.get(i);
            }
        }
        return null;
    }

    public ArrayList<BlockDest> GetDestList(){
        ArrayList<BlockDest> list = new ArrayList<>();
        for(BlockBase block : blockList){
            if(block.type== BlockBase.BlockType.dest){
                list.add((BlockDest) block);
            }
        }
        return list;
    }
}
