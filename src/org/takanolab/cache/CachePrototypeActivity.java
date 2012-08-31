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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CachePrototypeActivity extends Activity implements OnClickListener{
	DatabaseHelper helper;
	SQLiteDatabase db;
	ContentValues val;
	CacheHelper cacheHelper;
	TextView console;
	Button btn_1,btn_2,btn_3,btn_4,btn_all;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		console = (TextView)findViewById(R.id.Console);
		console.setText("");
		
		btn_1 = (Button)findViewById(R.id.first);
		btn_1.setOnClickListener(this);
		btn_2 = (Button)findViewById(R.id.second);
		btn_2.setOnClickListener(this);
		btn_3 = (Button)findViewById(R.id.third);
		btn_3.setOnClickListener(this);
		btn_4 = (Button)findViewById(R.id.fourth);
		btn_4.setOnClickListener(this);
		btn_all = (Button)findViewById(R.id.all);
		btn_all.setOnClickListener(this);
		
		cacheHelper = new CacheHelper();
		helper = new DatabaseHelper(this);
		db = helper.getReadableDatabase();	
		
		cacheHelper.clearCacheTable();
		deleteTableData();
		
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
	 * データ登録
	 */
	private void insertData(String name){
		db = helper.getWritableDatabase();
		db.beginTransaction();

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
		}
	}

	/**
	 * キャッシュ作成
	 * @param name
	 */
	private void setCache(String name,int weight){
		if(cacheHelper.isCacheData(name)){
			cacheHelper.addPriority(name, weight);
		}else{			
			InputStream is = new ByteArrayInputStream(name.getBytes());
			cacheHelper.setCacheData(name, is, weight);
		}
	}
	
	/**
	 * データ更新
	 * @param name
	 * @param weight
	 */
	private void updateData(String name,int weight){
		db = helper.getWritableDatabase();
		db.beginTransaction();
		
		val = new ContentValues();
		try{
			val.put("weight", weight);
			db.update(DatabaseHelper.TABLE_NAME, val, "name = " + name, null);
			db.setTransactionSuccessful();
			setCache(name, weight);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				db.endTransaction();
			}
	}

	/**
	 * データベースに存在するかによって実行する関数が変わる
	 * 
	 * @param name
	 */
	private void checkExist(String name){
		db = helper.getReadableDatabase();
		Cursor csr = db.rawQuery("select weight from " + DatabaseHelper.TABLE_NAME + "where name = " + name, null);
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
		while(csr.moveToNext()){
			console.append(csr.getString(0) + csr.getInt(1) + "\n");
			getCache(csr.getString(0) + "\n");
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
				console.append(name + temp + "\n");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			console.append(name + " no match\n");
		}
	}

	@Override
	public void onClick(View v) {

		switch(v.getId()){
		case R.id.first :
			checkExist("first");
			break;
		case R.id.second :
			checkExist("second");
			break;
		case R.id.third :
			checkExist("third");
			break;
		case R.id.fourth :
			checkExist("fourth");
			break;
		case R.id.all :
			getPersonalDataAll();
		default : 
			break;
		}
		
	}
}