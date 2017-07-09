package com.fanhong.cn.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库操作类
 * @author zk
 *
 */
public class Cartdb {

	private DatabaseHelper mDbHelper;    //SQLiteOpenHelper实例对象
	private static SQLiteDatabase mDb;    //数据库实例对象        //数据表创建语句


	private static final String CART_CREATE  = "create table cart ("
			+ "id integer primary key autoincrement,goodsid text not null,name text null, detail text null ,price text null,amount interger null,logourl text null,selected interger null);";
	private static final String ADDRESS_CREATE  = "create table address ("
			+ "id integer primary key autoincrement,person text not null,phone text not null, cell text null ,content text null);";

	private static final String DATABASE_NAME = "cart_db";    //数据库表名
	private static final int DATABASE_VERSION = 2;    //数据库版本号
	private final Context mCtx;    //上下文实例
	private static class DatabaseHelper extends SQLiteOpenHelper {    //数据库辅助类
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CART_CREATE);
			db.execSQL(ADDRESS_CREATE);
			inidata(db);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			try{
				db.execSQL("DROP TABLE IF EXISTS CART");
				db.execSQL("DROP TABLE IF EXISTS ADDRESS");
				onCreate(db);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	public Cartdb(Context ctx) {
		this.mCtx = ctx;
	}

	public static void inidata(SQLiteDatabase db){

	}

	public Cartdb open() throws SQLException {
		if(mDbHelper==null)
			mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	public void close() {
		mDbHelper.close();
	}

	public long insertItem(String goodsid, String name, String detail, String price, int amount,String logourl,int selected){
		if(goodsid == null || goodsid.length()==0){
			return 0;
		}
		//  Log.e("hu","**3*******insertItem:goodsid="+goodsid+" amount="+amount);
		if (searchByGoodsid(goodsid)>0) {
			int num = getAmountFromGoodsid(goodsid);
			return updateItem(goodsid,name, detail,price, amount+num,logourl,selected);
		}

		ContentValues values = new ContentValues();
		values.put("goodsid", goodsid);
		values.put("name", name);
		values.put("detail", detail);
		values.put("price", price);
		values.put("amount", amount);
		values.put("logourl", logourl);
		values.put("selected", selected);
		//Log.e("hu","**5*******insertItem:goodsid="+goodsid);
		return mDb.insert("cart", null, values);
	}

	public int searchByGoodsid(String goodsid){
		String sql = "SELECT * FROM cart" +
				" WHERE goodsid = ? ";
		String args[] = new String[]{goodsid};
		Cursor cur = mDb.rawQuery(sql, args);
		int count = cur.getCount();
		cur.close();
		return count;
	}

	public int getAmountFromGoodsid(String goodsid){
		String sql = "SELECT * FROM cart" +
				" WHERE goodsid = ? ";
		String args[] = new String[]{goodsid};
		Cursor cursor = mDb.rawQuery(sql, args);
		cursor.moveToNext();
		int num = 0;
		num = cursor.getInt(5);
		//Log.i("hu","***111*****cursor.getString(1)="+cursor.getString(1)+" cursor.getString(2)="+cursor.getString(2)
		//		+" cursor.getString(3)="+cursor.getString(3)+" cursor.getString(4)="+cursor.getString(4)
		//		+" cursor.getString(5)="+cursor.getString(5));
		cursor.close();
		return num;
	}

	public Cursor getCursonFromGoodsid(String goodsid){
		String sql = "SELECT * FROM cart" +
				" WHERE goodsid = ? ";
		String args[] = new String[]{goodsid};
		Cursor cursor = mDb.rawQuery(sql, args);
		cursor.moveToNext();

		return cursor;
	}

	public void changeAmount(String goodsid,int amount){
		String sql = "SELECT * FROM cart" +
				" WHERE goodsid = ? ";
		String args[] = new String[]{goodsid};
		Cursor cur = mDb.rawQuery(sql, args);
		int count = cur.getCount();
		if(count > 0){
			cur.moveToNext();
			//Log.i("hu","***111*****cursor.getString(1)="+cur.getString(1)+" cursor.getString(2)="+cur.getString(2)
			//		+" cursor.getString(3)="+cur.getString(3)+" cursor.getString(4)="+cur.getString(4)
			//		+" cursor.getString(5)="+cur.getString(5));
			updateItem(goodsid,cur.getString(2), cur.getString(3),cur.getString(4), amount,cur.getString(6),cur.getInt(7));
		}
		cur.close();
	}

	public void confirmSelected(String goodsid,int selected){
		String sql = "SELECT * FROM cart" +
				" WHERE goodsid = ? ";
		String args[] = new String[]{goodsid};
		Cursor cur = mDb.rawQuery(sql, args);
		int count = cur.getCount();
		if(count > 0){
			cur.moveToNext();
			//Log.i("hu","***111*****cursor.getString(1)="+cur.getString(1)+" cursor.getString(2)="+cur.getString(2)
			//		+" cursor.getString(3)="+cur.getString(3)+" cursor.getString(4)="+cur.getString(4)
			//		+" cursor.getString(5)="+cur.getString(5));
			updateItem(goodsid,cur.getString(2), cur.getString(3),cur.getString(4), cur.getInt(5),cur.getString(6),selected);
		}
		cur.close();
	}

	public long updateItem(String goodsid, String name, String detail, String price, int amount,String logourl,int selected){
		if(goodsid == null || goodsid.length()==0){
			return 0;
		}
		ContentValues values = new ContentValues();
		values.put("goodsid", goodsid);
		values.put("name", name);
		values.put("detail", detail);
		values.put("price", price);
		values.put("amount", amount);
		values.put("logourl", logourl);
		values.put("selected", selected);
		String where = "goodsid = ?";
		String[] whereValue = { goodsid };
		//Log.e("hu","**6*******updateItem:goodsid="+goodsid+" amount="+amount);
		return mDb.update("cart",values, where, whereValue);
	}

	public boolean deleteItem(String goodsid) {
		Cursor mCursor = mDb.rawQuery("select count(*) AS mcount from cart where goodsid='" + goodsid+"'", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			int numColumn1=mCursor.getColumnIndex("mcount");
			String smcount=mCursor.getString(numColumn1);
			int mcount =  Integer.valueOf(smcount);
			if(mcount>0){
				return mDb.delete("cart", "goodsid='"+goodsid+"'", null) > 0;
			}
		}
		return true;
	}

	public boolean deleteItem() {
		Cursor mCursor = mDb.rawQuery("select count(*) AS mcount from cart ", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			int numColumn1=mCursor.getColumnIndex("mcount");
			String smcount=mCursor.getString(numColumn1);
			int mcount =  Integer.valueOf(smcount);
			if(mcount>0){
				return mDb.delete("cart", "", null) > 0;
			}
		}
		return true;
	}

	public static int deleteItem(int rowid) {
		String where = "id = ?";
		String[] whereValue = { Integer.toString(rowid) };
		return mDb.delete("cart", where, whereValue);
	}

	public static int deleteSelectedItem() {
		String where = "selected = ?";
		String[] whereValue = { Integer.toString(1) };
		return mDb.delete("cart", where, whereValue);
	}

	public static int deleteAllItem() {
		String where = "id < ?";
		String[] whereValue = { Integer.toString(999999) };
		return mDb.delete("cart", where, whereValue);
	}

	public Cursor selectConversationList() {

		return mDb.query("cart", new String[] { "goodsid", "name",
				"detail", "price", "amount", "logourl","selected"}, null, null, null, null, "rowid DESC");
	}

	public long getCountItem() {
		Cursor cursor = mDb.rawQuery("select count(*) from cart", null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		return count;
	}

	public float getTotalPrice() { //计算选中的商品总价格
		float total = 0.0f;
		String sql = "SELECT * FROM cart";
		Cursor cur = mDb.rawQuery(sql, null);
		int count = cur.getCount();

		while(cur.moveToNext())
		{
			Log.i("hu","***111*****cursor.getString(1)="+cur.getString(1)+" cursor.getString(2)="+cur.getString(2)
					+" cursor.getString(3)="+cur.getString(3)+" cursor.getString(4)="+cur.getString(4)
					+" cursor.getString(5)="+cur.getString(5)+" cursor.getString(6)="+cur.getString(6)
					+" cursor.getString(7)="+cur.getString(7));
			if(cur.getInt(7) == 1){
				int num = cur.getInt(5);
				String price = cur.getString(4);
				float f = Float.parseFloat(price);
				Log.i("hu","***222 num="+num+" f="+f+" total="+total);
				total = total + f*num;
			}
		}
		cur.close();
		return total;
	}

	public long insertAddress(String person, String phone, String cell, String content){
		ContentValues values = new ContentValues();
		values.put("person", person);
		values.put("phone", phone);
		values.put("cell", cell);
		values.put("content", content);
		Log.e("hu","**5*******insertAddress:person="+person);
		return mDb.insert("address", null, values);
	}

	public Cursor queryAddressList() {

		return mDb.query("address", new String[] { "id", "person", "phone",
				"cell", "content"}, null, null, null, null, "rowid DESC");
	}

	public int deleteAddress(int rowid) {
		String where = "id = ?";
		String[] whereValue = { Integer.toString(rowid) };
		return mDb.delete("address", where, whereValue);
	}

	public Cursor getAddressItem(int id){
		String sql = "SELECT * FROM ADDRESS"+
				" WHERE id = ? ";
		String args[] = new String[]{String.valueOf(id)};
		return	mDb.rawQuery(sql, args);
	}

	public static int deleteAllAddressItem() {
		String where = "id < ?";
		String[] whereValue = { Integer.toString(999999) };
		return mDb.delete("address", where, whereValue);
	}
}
