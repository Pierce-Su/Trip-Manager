package com.example.tripmanager2;

public class BlockTransRequest extends BlockBase{
    public BlockTransRequest(){};
    public BlockTransRequest(BlockType _type, String _name, int _id, int _begin, int _end, BlockDest _from, BlockDest _to){
        super(_type, _name, _id, _begin, _end);
        from = _from;
        to = _to;
    }
    public BlockDest from;
    public BlockDest to;
}
