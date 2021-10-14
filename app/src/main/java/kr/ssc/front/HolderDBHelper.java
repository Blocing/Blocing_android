package kr.ssc.front;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HolderDBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "holder_db";
    private final static String TABLE_NAME = "holder_table";
    private final static String COL_ID = "_id";
    private static final String COL_HOLDER_ID = "holder_id";
    private static final String COL_HOLDER_DID = "holder_did";
    private static final String COL_HOLDER_NAME = "holder_name";
    private static final String COL_HOLDER_UNIVERSITY = "holder_university";
    private static final String COL_HOLDER_DEPARTMENT = "holder_department";
    // did,name,university,department
    public HolderDBHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_HOLDER_ID + " TEXT," + COL_HOLDER_DID + " TEXT," + COL_HOLDER_NAME + " TEXT," + COL_HOLDER_UNIVERSITY + " TEXT," + COL_HOLDER_DEPARTMENT + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColHolderId() {
        return COL_HOLDER_ID;
    }
    public static String getColHolderDid() {
        return COL_HOLDER_DID;
    }
    public static String getColHolderName() {
        return COL_HOLDER_NAME;
    }
    public static String getColHolderUniversity() {
        return COL_HOLDER_UNIVERSITY;
    }
    public static String getColHolderDepartment() {
        return COL_HOLDER_DEPARTMENT;
    }
}
