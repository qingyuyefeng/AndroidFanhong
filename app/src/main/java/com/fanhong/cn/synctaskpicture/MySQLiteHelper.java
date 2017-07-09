package com.fanhong.cn.synctaskpicture;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/5/19.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    super(context, name, factory, version);
}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table image(_id integer not null primary key autoincrement,key varchar(200) not null,value varchar(200) not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
