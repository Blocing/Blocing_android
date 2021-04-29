package kr.ssc.front.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "holder_table")
public class Holder {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "holder_id")
    public String holder_id;
}
