package com.example.tripmanager2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Struct;
import java.util.HashMap;
@Entity
public abstract class BlockBase {
    public BlockBase(){};
    public BlockBase(BlockType _type, String _name, int _id, int _begin, int _end){
        type = _type;
        name = _name;
        id = _id;
        beginTime = _begin;
        endTime = _end;
        stayTime = endTime - beginTime;
    };
    @PrimaryKey(autoGenerate = true)
    private int db_idx;
    public BlockType type;
    public String name;
    public int id;
    public int beginTime;
    public int endTime;
    public int stayTime;

    public enum BlockType{
        dest, trans, transRequest
    }

    public void ChangeBeginTime(int _begin){
        beginTime = _begin;
        endTime = beginTime + stayTime;
    }
    public void ChangeEndTime(int _end){
        endTime = _end;
        beginTime = endTime - stayTime;
    }
    public void ChangeStayTime(int _stay){
        stayTime = _stay;
        endTime = beginTime + stayTime;
    }

    public int getDb_idx() {
        return db_idx;
    }

    public void setDb_idx(int db_idx) {
        this.db_idx = db_idx;
    }
}
