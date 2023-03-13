package com.example.tripmanager2;

import androidx.room.Entity;

@Entity(tableName = "blocktrans")
public class BlockTrans extends BlockBase{
    public BlockTrans(){};
    public BlockTrans(BlockType _type, String _name, int _id, int _begin, int _end, String _transType, String _from, String _to){
        super(_type, _name, _id, _begin, _end);
        transportType = _transType;
        from = _from;
        to = _to;
    }

    public String transportType;
    public String from;
    public String to;
}
