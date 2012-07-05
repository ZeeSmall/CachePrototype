package org.takanolab.cache.test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.takanolab.database.DatabaseHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class CachePrototypeActivity extends Activity {
	DatabaseHelper helper;
	SQLiteDatabase db;
	CacheHelper cachetable;
	TextView console;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		console = (TextView)findViewById(R.id.Console);
		cachetable = new CacheHelper();
		helper = new DatabaseHelper(this);
		db = helper.getReadableDatabase();

		String[] name = {"first","second","third"};

		//cachetable.clearcacheTable();
		
		//setTestTable(name);
		//setTestCache(name);
		getTestCache(name);

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		db.close();
		cachetable.OutPutCache();
	}


	private void setTestTable(){
		db = helper.getWritableDatabase();
		db.beginTransaction();

		ContentValues val = new ContentValues();
		try{
			val.clear();
			val.put("name", "first");
			val.put("weight", 1);
			db.insert(DatabaseHelper.TABLE_NAME, null,val);
			db.setTransactionSuccessful();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
		db = helper.getReadableDatabase();
	}

	private void setTestTable(String[] name){
		db = helper.getWritableDatabase();
		db.beginTransaction();
		
		db.execSQL("delete from "+ DatabaseHelper.TABLE_NAME);
		
		ContentValues val = new ContentValues();
		try{
			for(int i = 0;name.length > i;i++){
				val.clear();
				val.put("name", name[i]);
				val.put("weight", (i+1));
				db.insert(DatabaseHelper.TABLE_NAME, null,val);
			}
			db.setTransactionSuccessful();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
		db = helper.getReadableDatabase();
	}

	private void setTestCache(){
		Cursor csr = db.rawQuery("select weight from " + DatabaseHelper.TABLE_NAME + " order by id", null);
		csr.moveToFirst();
		int weight = csr.getInt(0);
		cachetable.setModelCache("first", new ByteArrayInputStream(new String("first").getBytes()), weight);
	}

	private void setTestCache(String[] name){
		Cursor csr = db.rawQuery("select weight from " + DatabaseHelper.TABLE_NAME + " order by id", null);
		//csr.moveToFirst();
		int i = 0;
		int weight = 0;
		while(csr.moveToNext()){
			weight = csr.getInt(0);
			cachetable.setModelCache(name[i], new ByteArrayInputStream(name[i].getBytes()), weight);
			i++;
		}
	}

	private void getTestCache(){
		String name = "first";
		if(cachetable.isModelCache(name)){
			SavedCache temp = cachetable.getModelCacheCore(name);

			String getmodel="";
			String namecreate="";
			try {
				getmodel = new String(temp.getModelByte(),"UTF-8");
				namecreate = new String(name.getBytes(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			console.setText(namecreate + "\n");
			console.append(getmodel + "\n");
			console.append(temp.getLimitTime() + "\n");
			if(getmodel.equals(namecreate)){
				console.append("true");
			}else{
				console.append("false");
			}
		}
	}

	private void getTestCache(String[] name){
		console.setText("");
		Iterator<String> itr = cachetable.getMapIterator();
		String key;
		while(itr.hasNext()){
			key = itr.next();
			console.append(key + "\n");
		}
		
		for(int i = 0;name.length > i;i++){
			if(cachetable.isModelCache(name[i])){
				SavedCache temp = cachetable.getModelCacheCore(name[i]);

				String getmodel="";
				String namecreate="";
				try {
					getmodel = new String(temp.getModelByte(),"UTF-8");
					namecreate = new String(name[i].getBytes(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				console.append("\n" + namecreate + " : ");
				console.append(getmodel + " = ");
				if(getmodel.equals(namecreate)){
					console.append("true");
				}else{
					console.append("false");
				}
				console.append("\ntime = " + temp.getLimitTime() + " : priority = " + temp.getPriority() + "\n");

			}
		}
	}
}