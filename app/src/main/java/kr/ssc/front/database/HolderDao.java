package kr.ssc.front.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// 모든 CRUD작업은 메인스레드가 아닌 백그라운드로 작업

@Dao
public interface HolderDao {
    @Query("SELECT * FROM holder_table")
    List<Holder> getAll();

    @Insert
    void insert(Holder holder);

    @Update
    void update(Holder holder);

    @Delete
    void delete(Holder holder);

    @Query("DELETE FROM holder_table")
    void deleteAll();
}
