package org.takanolab.cache;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.takanolab.cache.test.R;
import org.takanolab.database.DatabaseHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class CachePrototypeActivity extends Activity implements OnItemClickListener{
	private static String TAG = "CachePrototype";
	DatabaseHelper helper;
	SQLiteDatabase db;
	ContentValues val;
	CacheHelper cacheHelper;
	TextView console,history;
	GridView gView;
	String[] lists;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		
		console = (TextView)findViewById(R.id.console);
		history = (TextView)findViewById(R.id.history);
		history.setText("history:\n");
		
		gView = (GridView)findViewById(R.id.gridView1);
		
		cacheHelper = new CacheHelper();
		helper = new DatabaseHelper(this);
		db = helper.getReadableDatabase();	
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		lists = new String[] {"first","second","third","forth","fifth","six","delete"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item, lists);
		gView.setAdapter(adapter);
		gView.setOnItemClickListener(this);
		
		getPersonalDataAll();
		
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		db.close();
		cacheHelper.OutPutCache();
	}

	private void deleteTableData(){
		db = helper.getWritableDatabase();
		db.beginTransaction();
		
		db.execSQL("delete from " + DatabaseHelper.TABLE_NAME);
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	/**
	 * キャッシュとデータベースの内容を全部削除
	 */
	private void clearDatabaseAndCache(){
		cacheHelper.clearCacheTable();
		deleteTableData();
	}
	
	/**
	 * データ登録
	 */
	private void insertData(String name){
		Log.d(TAG,"insertData");
		db = helper.getWritableDatabase();
		db.beginTransaction();

		val = new ContentValues();
		try{
			val.put("name", name);
			val.put("weight", 10);
			db.insert(DatabaseHelper.TABLE_NAME, null,val);
			db.setTransactionSuccessful();		
			setCache(name, 10);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
			val.clear();
			getPersonalDataAll();
		}
	}

	/**
	 * キャッシュ作成
	 * @param name
	 */
	private void setCache(String name,int priority){
		if(cacheHelper.isCacheData(name)){
			cacheHelper.addPriority(name, priority);
		}else{			
			InputStream is = new ByteArrayInputStream(name.getBytes());
			cacheHelper.setCacheData(name, is, priority);
		}
	}
	
	/**
	 * データ更新
	 * @param name
	 * @param weight
	 */
	private void updateData(String name,int weight){
		Log.d(TAG,"updateData");
		db = helper.getWritableDatabase();
		db.beginTransaction();
		
		val = new ContentValues();
		try{
			val.put("weight", weight);
			db.update(DatabaseHelper.TABLE_NAME, val, "name = '" + name + "'", null);
			db.setTransactionSuccessful();
			setCache(name, weight);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				db.endTransaction();
				getPersonalDataAll();
			}
	}

	/**
	 * データベースに存在するかによって実行する関数が変わる
	 * 
	 * @param name
	 */
	private void checkExist(String name){
		db = helper.getReadableDatabase();
		Cursor csr = db.rawQuery("select weight from " + DatabaseHelper.TABLE_NAME + " where name = '" + name + "'", null);
		if(csr.moveToFirst()){
			updateData(name,csr.getInt(0));
		}else{
			insertData(name);
		}
	}

	private void getPersonalDataAll(){
		db = helper.getReadableDatabase();
		Cursor csr = db.rawQuery("select name,weight from " + DatabaseHelper.TABLE_NAME + " order by id", null);
		//csr.moveToFirst();
		console.setText("cache:\n");
		while(csr.moveToNext()){
			//console.append(csr.getString(0) + "  " + csr.getInt(1) + "\n");
			getCache(csr.getString(0));
		}
	}

	/**
	 * キャッシュを取得
	 * 
	 * @param name
	 */
	private void getCache(String name){
		if(cacheHelper.isCacheData(name)){
			SavedCache cache = cacheHelper.getCacheClass(name);
			try {
				String temp = new String(cache.getModelByte(),"UTF-8");
				console.append(temp + " " + cache.priority + "\n");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			//console.append(name + " no match\n");
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
		TextView tex = (TextView)v;
		if(tex.getText().toString().equals("delete")){
			clearDatabaseAndCache();
			history.setText("history:\n");
			getPersonalDataAll();
		}else{
			checkExist(tex.getText().toString());
			history.append(tex.getText().toString() + "\n");
		}
	}
}