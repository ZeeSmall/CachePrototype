/**
 * 3DModelDataのキャッシュを保持するクラス<br>
 * 直列化する都合上モデルデータはバイト配列にする<br>
 * 処理のコストは未知数
 * 
 * @author s0921122
 *
 */

package org.takanolab.cache;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import android.util.Log;


public class SavedCache implements Serializable{


	private static final String TAG = "SavedCache";
	private static final long serialVersionUID = 1L;
	
	// モデルのキャッシュ
	byte[] modelByte;
	// 生存時間
	long survivalTime;
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
		this.survivalTime = time;
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
		this.survivalTime = time;
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
		this.survivalTime = 10000;
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
		return survivalTime;
	}
	/**
	 * 生存時間をセット
	 * 
	 * @param limitTime
	 */
	public void setLimitTime(long limitTime) {
		this.survivalTime = limitTime;
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
