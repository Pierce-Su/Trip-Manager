package com.example.tripmanager2;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BlockDest.class},version = 1,exportSchema = false)
public abstract class BlockDestDatabase extends RoomDatabase{
    public abstract BlockDestDAO getBlockDestDao();
}



