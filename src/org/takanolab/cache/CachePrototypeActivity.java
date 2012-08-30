package org.takanolab.cache;

import java.io.ByteArrayInputStream;
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
	CacheHelper cacheHelper;
	TextView console;
	Button btn_1,btn_2,btn_3,btn_4;

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
		
		cacheHelper = new CacheHelper();
		helper = new DatabaseHelper(this);
		db = helper.getReadableDatabase();

		getPersonalDataAll();
		
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		db.close();
		cacheHelper.OutPutCache();
	}


	/**
	 * データ登録
	 */
	private void setCache(String name){
		
		db = helper.getWritableDatabase();
		db.beginTransaction();

		ContentValues val = new ContentValues();
		try{
			int weight = getPersonalData(name);
			val.put("name", name);
			val.put("weight", weight);
			db.insert(DatabaseHelper.TABLE_NAME, null,val);
			db.setTransactionSuccessful();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
			val.clear();
		}
		db = helper.getReadableDatabase();
	}

	/**
	 * データ登録
	 * @param name
	 */
	private void setCache(String[] name){
		db = helper.getWritableDatabase();
		db.beginTransaction();
		
		db.execSQL("delete from "+ DatabaseHelper.TABLE_NAME);
		
		ContentValues val = new ContentValues();
		try{
			for(int i = 0;name.length > i;i++){
				int weight = getPersonalData(name[i]);
				val.put("name", name[i]);
				val.put("weight", (i + 10));
				db.insert(DatabaseHelper.TABLE_NAME, null,val);
				val.clear();
				console.append(name[i] + " insert\n");
			}
			db.setTransactionSuccessful();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
		db = helper.getReadableDatabase();
	}

	private int getPersonalData(String name){
		Cursor csr = db.rawQuery("select weight from " + DatabaseHelper.TABLE_NAME + "where name = " + name, null);
		if(csr.moveToFirst()){
			return csr.getInt(0);
		}else{
			return 0;
		}
	}

	private void getPersonalDataAll(){
		Cursor csr = db.rawQuery("select name,weight from " + DatabaseHelper.TABLE_NAME + " order by id", null);
		//csr.moveToFirst();
		while(csr.moveToNext()){
			console.append(csr.getString(0) + csr.getInt(1) + "\n");
		}
	}

	/**
	 * キャッシュを取得
	 * 
	 * @param name
	 */
	private void getCache(String name){
		if(cacheHelper.isCacheData(name)){
			SavedCache cache = cacheHelper.getCacheData(name);
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
			setCache("first");
			break;
		case R.id.second :
			setCache("second");
			break;
		case R.id.third :
			setCache("third");
			break;
		case R.id.fourth :
			setCache("fourth");
			break;
		default : 
			break;
		}
		
	}
}