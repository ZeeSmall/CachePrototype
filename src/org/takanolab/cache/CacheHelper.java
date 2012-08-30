/**
 * キャッシュの読み書きをする
 * 
 * @author s0921122
 */

package org.takanolab.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

public class CacheHelper {
	
	// ログ出力用
	private static final String TAG = "CacheHelper";
	// キャッシュファイルの入出力先のパス
	private static final String PATH = "/sdcard/modeldata/modelcache";
	// ファイルから読み込むマップ
	HashMap<String,SavedCache> importCache;
	
	/**
	 * コンストラクタ
	 * すでにキャッシュファイルが存在するとき読み込みます
	 * 
	 * @author s0921122
	 * @version 1.2
	 */
	@SuppressWarnings("unchecked")
	public CacheHelper(){
		File file = new File(PATH);
		if(file.exists()){
			// ファイルが存在する
			if(file.length() > 0){
				// 内容がある
				try{
					// データ読み込み
					importCache = (HashMap<String, SavedCache>) read_object(PATH);
				}catch(Exception e){
					e.printStackTrace();
					importCache = new HashMap<String, SavedCache>();
				}
			}else{
				// 内容がない
				importCache = new HashMap<String, SavedCache>();
			}
		}else{
			// ファイルが存在しない
			try{
				// ファイル作成
				file.createNewFile();
				importCache = new HashMap<String, SavedCache>();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * キャッシュデータをセットする
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @param name 
	 * @param cc 
	 */
	public void setCacheData(String name,SavedCache sc){
		importCache.put(name, sc);
	}
	
	/**
	 * キャッシュデータをセットする
	 * 
	 * @author s0921122
	 * @version 1.1
	 * @param name
	 * @param is
	 */
	public void setCacheData(String name,InputStream is,int weight){
		long time = weight * 1000;
		int priority = weight * 1;
		SavedCache cacheData = new SavedCache(is,time,priority);
		importCache.put(name, cacheData);
	}

	/**
	 * モデルキャッシュを持つクラスを返す
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @param name
	 * @return SavedCache
	 */
	public SavedCache getCacheData(String name){
		if(importCache.containsKey(name)){
			return importCache.get(name);
		}else{
			return null;
		}
	}
	
	/**
	 * キャッシュマップを取り出す
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @return
	 */
	public HashMap<String, SavedCache> getCaheMap(){
		return importCache;
	}
	
	/**
	 * キャッシュマップが存在するか
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @return 
	 */
	public boolean isCacheTable(){
		if(importCache != null){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * キャッシュデータが存在するか
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @param name modelname
	 * @return 
	 */
	public boolean isCacheData(String name){
		if(importCache.containsKey(name)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * キャッシュマップの要素数を返す
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @return
	 */
	public int getSize(){
		return importCache.size();
	}
	
	/**
	 * キャッシュマップのイテレータを返す
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @return
	 */
	public Iterator<String> getMapIterator(){
		return importCache.keySet().iterator();
	}
	
	/**
	 * キャッシュデータを削除
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @param name
	 * @return
	 */
	public boolean DeleteCacheData(String name){
		try{
			importCache.remove(name);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * キャッシュ全体を削除
	 * 
	 * @author s0921122
	 * @version 1.0
	 * 
	 */
	public void clearCacheTable(){
		importCache.clear();
	}

	/**
	 * キャッシュデータをファイルに書き出す
	 * 
	 * @author s0921122
	 * @version 1.0
	 */
	public void OutPutCache(){
		write_object(importCache, PATH);
	}
	
	/**
	 * オブジェクトをファイルに書き出す
	 * 
	 * @version 1.0
	 * @param obj Object
	 * @param file FilePath
	 * @return
	 */
	private static boolean write_object(Object obj,String file){
		try {
			FileOutputStream outFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(outFile);
			out.writeObject(obj);
			out.close();
			outFile.close();
		} catch(Exception e) {
			Log.d(TAG,"FileOutput");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ファイルを読みこみオブジェクトを生成
	 * 
	 * @version 1.0
	 * @param file FilePath
	 * @return
	 */
	private static Object read_object(String file){
		Object obj=new Object();
		try {
			FileInputStream inFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(inFile);
			obj = in.readObject();
			in.close();
			inFile.close();
		} catch(Exception e) {
			Log.d(TAG,"FileInput");
			e.printStackTrace();
		}
		return obj;
	}

}
