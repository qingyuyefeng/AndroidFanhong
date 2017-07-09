package com.fanhong.cn.synctaskpicture;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2017/5/19.
 */

public class SaveInFile {
    public static char[] chars={'/','\\','?',':','<','>','*','|','\'','"'};

    public static void save(Context context, InputStream is, String url){
        url = filter(url, chars);
        if(!saveInMysqlite(context,url)){
            return;
        }
        OutputStream os=null;
        try {
            os=context.openFileOutput(url,Context.MODE_PRIVATE);
            int c;
            while((c=is.read())!=-1){
                os.write(c);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static InputStream get(Context context, String url){
        try {
            return context.openFileInput(filter(url,chars));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }




    public static String filter(String string,char ...chars){//?????????
        if(string==null)
        {
            return string;
        }
        String str="";
        for(int i=0;i<string.length();i++){
            char c=string.charAt(i);
            boolean canadd=true;
            for(int j=0;j<chars.length;j++){
                if(c==chars[j]){
                    canadd=false;
                    //????????????????
                    continue;  //j=chars.length;
                }
            }
            if(canadd){
                str+=c;
            }
        }

        return str;
    }

    public  static boolean  saveInMysqlite(Context context,String key){
        long value=System.currentTimeMillis();
        MySQLiteHelper helper=new MySQLiteHelper(context,"image",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor c=db.rawQuery("select * from image",null);
        if(c!=null)
            while(c.moveToNext()){
                if(c.getString(c.getColumnIndex("key")).equals(key)){
                    return false;
                }
            }
        String s="'"+key+"'"+","+"'"+value+"'";
        db.execSQL("insert into image (key,value)values(" +s+")");
        return true;
    }
}
