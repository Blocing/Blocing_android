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

    @ColumnInfo(name = "holder_did")
    public String holder_did;

    @ColumnInfo(name = "holder_name")
    public String holder_name;

    @ColumnInfo(name = "holder_university")
    public String holder_university;

    @ColumnInfo(name = "holder_department")
    public String holder_department;
}
