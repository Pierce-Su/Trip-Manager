package com.example.tripmanager2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MapDBOpenHelper extends SQLiteOpenHelper {
    public MapDBOpenHelper(@Nullable Context context,
                           @Nullable String dBName,
                           @Nullable SQLiteDatabase.CursorFactory factory,
                           int dBVersion) {
        super(context, dBName, null, dBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}