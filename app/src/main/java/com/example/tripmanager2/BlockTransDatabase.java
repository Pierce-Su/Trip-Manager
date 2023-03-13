package com.example.tripmanager2;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BlockTrans.class},version = 1,exportSchema = false)
public abstract class BlockTransDatabase extends RoomDatabase {
    public abstract BlockTransDAO getBlockTransDao();
}
