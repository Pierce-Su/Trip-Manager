package com.example.tripmanager2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BlockDestDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBlockDest(BlockDest... blockDests);


    @Query("DELETE FROM BLOCKDEST")
    void deleteAllBlcokDests();

    @Query("SELECT* FROM BLOCKDEST")//@Query("SELECT* FROM BLOCKMANAGER ORDER BY manager_id DESC")
    List<BlockDest> getAllBlockDest();


}
