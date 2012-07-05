package org.takanolab.cache.test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

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
	String name = "first";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        console = (TextView)findViewById(R.id.Console);
        cachetable = new CacheHelper();
        helper = new DatabaseHelper(this);
        db = helper.getReadableDatabase();
        
        //cachetable.clearcacheTable();
        //setTestTable();
        //setTestCache();
        
        getTestCache();
        
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

    	try{
    		ContentValues val = new ContentValues();
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
    
    private void setTestCache(){
    	Cursor csr = db.rawQuery("select weight from " + DatabaseHelper.TABLE_NAME, null);
    	csr.moveToFirst();
    	int weight = csr.getInt(0);
    	cachetable.setModelCache("first", new ByteArrayInputStream(new String("first").getBytes()), weight);
    }
    
    private void getTestCache(){
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
    		
    		console.setText(name + "\n" + namecreate + "\n");
    		console.append(getmodel);
    		console.append("\n" + temp.getLimitTime() + "\n");
    		if(getmodel.equals(namecreate)){
    			console.append("true");
    		}else{
    			console.append("false");
    		}
    	}
    	
    }
}