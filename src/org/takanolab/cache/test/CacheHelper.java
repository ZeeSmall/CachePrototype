/**
 * キャッシュを管理するクラス
 * 
 * @author s0921122
 */

package org.takanolab.cache.test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

public class CacheHelper {
	
	// ログ出力用
	private static final String TAG = "CacheHelper";
	// キャッシュファイルの出力先のパス
	private static final String PATH = "/sdcard/modeldata/modelcache.obj";
	// キャッシュの保持のためのハッシュマップ
	HashMap<String,SavedCache> cacheTable;
	
	/**
	 * コンストラクタ
	 * キャッシュファイルが存在するとき読み込みます
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
					cacheTable = (HashMap<String, SavedCache>) read_object(PATH);
				}catch(Exception e){
					e.printStackTrace();
					cacheTable = new HashMap<String, SavedCache>();
				}
			}else{
				// 内容がない
				cacheTable = new HashMap<String, SavedCache>();
			}
		}else{
			// ファイルが存在しない
			try{
				// ファイル作成
				file.createNewFile();
				cacheTable = new HashMap<String, SavedCache>();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * キャッシュマップが存在するか
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @return 
	 */
	public boolean isCacheTable(){
		if(cacheTable != null){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * モデルキャッシュが存在するか
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @param name modelname
	 * @return 
	 */
	public boolean isModelCache(String name){
		if(cacheTable.containsKey(name)){
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
		return cacheTable.size();
	}

	/**
	 * キャッシュデータをセットする
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @param name modelname
	 * @param cc CacheCore
	 */
	public void setModelCache(String name,SavedCache sc){
		cacheTable.put(name, sc);
	}
	
	/**
	 * キャッシュデータをセットする
	 * 
	 * @author s0921122
	 * @version 1.1
	 * @param name
	 * @param is
	 */
	public void setModelCache(String name,InputStream is,int weight){
		long time = weight * 1000;
		int priority = weight * 1;
		SavedCache cacheData = new SavedCache(is,time,priority);
		cacheTable.put(name, cacheData);
	}

	/**
	 * モデルキャッシュを持つクラスを返す
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @param name modelname
	 * @return CacheCore
	 */
	public SavedCache getModelCacheCore(String name){
		if(cacheTable.containsKey(name)){
			return cacheTable.get(name);
		}else{
			return null;
		}
	}
	
	/**
	 * キャッシュデータを削除
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @param name modeldata
	 * @return
	 */
	public boolean DeleteModelCache(String name){
		try{
			cacheTable.remove(name);
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
	public void clearcacheTable(){
		cacheTable.clear();
	}

	/**
	 * キャッシュマップを取り出す
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @return
	 */
	public HashMap<String, SavedCache> getCaheMap(){
		return cacheTable;
	}
	
	/**
	 * キャッシュマップのイテレータを返す
	 * 
	 * @author s0921122
	 * @version 1.0
	 * @return
	 */
	public Iterator<String> getMapIterator(){
		return cacheTable.keySet().iterator();
	}


	/**
	 * キャッシュデータをファイルに書き出す
	 * 
	 * @author s0921122
	 * @version 1.0
	 */
	public void OutPutCache(){
		write_object(cacheTable, PATH);
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
/**
 * キャッシュを保持するクラス<br>
 * 直列化する都合上モデルデータはバイト配列にする<br>
 * 処理のコストは未知数
 * 
 * @author s0921122
 *
 */
class SavedCache implements Serializable{

	private static final String TAG = "SavedCache";
	private static final long serialVersionUID = 1L;
	
	// モデルのキャッシュ
	byte[] modelByte;
	// 生存時間
	long limitTime;
	// 優先度
	int priority;
	
	/**
	 * 初期コンストラクタ
	 * 
	 * @author s0921122
	 */
	public SavedCache(){
		
	}
	/**
	 * 初期値を代入するコンストラクタ
	 * 
	 * @author s0921122
	 * @param modelData
	 * @param time
	 * @param priority
	 */
	public SavedCache(InputStream modelData,long time,int priority){
		this.modelByte = getBytes(modelData);
		this.limitTime = time;
		this.priority = priority;
	}
	/**
	 * 初期値を代入するコンストラクタ
	 * 
	 * @author s0921122
	 * @param modelData
	 * @param time
	 */
	public SavedCache(InputStream modelData,long time){
		this.modelByte = getBytes(modelData);
		this.limitTime = time;
		this.priority = 0;
	}
	/**
	 * 初期値を代入するコンストラクタ
	 * 
	 * @author s0921122
	 * @param modelData
	 */
	public SavedCache(InputStream modelData){
		this.modelByte = getBytes(modelData);
		this.limitTime = 10000;
		this.priority = 5;
	}

	/**
	 * InputStreamのモデルデータを返す
	 * 
	 * @return
	 */
	public InputStream getModelData() {
		return getStream(modelByte);
	}
	/**
	 * InputStremのモデルをセット
	 * @param modelData
	 */
	public void setModelData(InputStream modelData) {
		this.modelByte = getBytes(modelData);
	}
	
	/**
	 * モデルのバイト配列を返す
	 * 
	 * @return
	 */
	public byte[] getModelByte() {
		return modelByte;
	}
	/**
	 * モデルのバイト配列をセット
	 * 
	 * @param modelByte
	 */
	public void setModelByte(byte[] modelByte) {
		this.modelByte = modelByte;
	}
	
	/**
	 * 生存時間を返す
	 * 
	 * @return
	 */
	public long getLimitTime() {
		return limitTime;
	}
	/**
	 * 生存時間をセット
	 * 
	 * @param limitTime
	 */
	public void setLimitTime(long limitTime) {
		this.limitTime = limitTime;
	}
	
	/**
	 * 優先度を返す
	 * 
	 * @return
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * 優先度をセット
	 * 
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/**
     * InputStreamをバイト配列に変換する
     *
     * @param is
     * @return バイト配列
     */
    private byte[] getBytes(InputStream is) {
    	long start = System.currentTimeMillis();
    	Log.d(TAG,"Start : "+ start);
    	
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = new BufferedOutputStream(baos);
        
        int c;
        try {
            while ((c = is.read()) != -1) {
                os.write(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        long end = System.currentTimeMillis();
        Log.d(TAG,"End : "+ end);
        Log.d(TAG,"InputStream → byte : " + (end - start));
        
        return baos.toByteArray();
    }
    
    /**
     * Byte配列からInputStreamに変換する
     * 
     * @param bytes
     * @return InputStream
     */
    private InputStream getStream(byte[] bytes){
    	long start = System.currentTimeMillis();
    	Log.d(TAG,"Start : " + start);
    	
    	InputStream bais = new ByteArrayInputStream(bytes);
    	
    	long end = System.currentTimeMillis();
    	Log.d(TAG,"End : " + end);
    	Log.d(TAG,"Byte → InputStream : " + (end - start));
    	
    	return bais;
    }
}