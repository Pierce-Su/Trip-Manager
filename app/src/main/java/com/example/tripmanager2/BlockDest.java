package com.example.tripmanager2;

import androidx.room.Entity;

import java.util.HashMap;
@Entity(tableName = "blockdest")
public class BlockDest extends  BlockBase {
    public BlockDest(){};
    public BlockDest(BlockType _type, String _name, int _id, int _begin, int _end, String _lat,String _lng, String _place_id){//HashMap<String, Float> _coord
        super(_type, _name, _id, _begin, _end);
        lat = _lat;
        lng = _lng;
        place_id = _place_id;
    }

    //public HashMap<String, Float> coordinate;
    public String lat;
    public String lng;
    public String place_id;
}
