package com.example.tripmanager2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BlockTransDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBlockTrans(BlockTrans... blockTrans);


    @Query("DELETE FROM BLOCKTRANS")
    void deleteAllBlcokTrans();

    @Query("SELECT* FROM BLOCKTRANS")//@Query("SELECT* FROM BLOCKMANAGER ORDER BY manager_id DESC")
    List<BlockTrans> getAllBlockTrans();

}



