package org.takanolab.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "PersonalDatabase01";
	public static final String TABLE_NAME = "testtable";
	
	public DatabaseHelper(Context con){
		super(con, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		createTable(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	private void createTable(SQLiteDatabase database){
        try{
        	// テーブル作成
        	String sql = "CREATE TABLE " + TABLE_NAME + " ("
        		+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
        		+ "name TEXT UNIQUE NOT NULL,"
        		+ "weight INTEGER"
        		+ ")";
        	database.execSQL(sql);
        }catch(Exception e){
        	// テーブル作成失敗かすでにあるとき
        	System.out.println(e.toString());
        }
	}

}
