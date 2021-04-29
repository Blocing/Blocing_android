package kr.ssc.front.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Holder.class}, version = 1)
public abstract class holder_db extends RoomDatabase {
    private static holder_db INSTANCE;

    public abstract HolderDao HolderDao();

    // 디비객체생성 가져오기
    public static holder_db getAppDatabase(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, holder_db.class, "holder_db")
                    .build();
        }
        return INSTANCE;
    }

    public static void destoryInstance() {
        INSTANCE = null;
    }
}
