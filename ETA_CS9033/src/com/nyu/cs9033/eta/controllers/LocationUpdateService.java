package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.models.Gps;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
/**
 * This background intent Service would periodically update user's location data
 * to google app engine
 * @author Shiyin
 *
 */
public class LocationUpdateService extends Service {

	 private Gps gps=null;
	 private boolean threadDisable=false; 
	 private final static String TAG=LocationUpdateService.class.getSimpleName();
	 
	 @Override
	 public void onCreate() {
	  super.onCreate();
	   
	  gps=new Gps(LocationUpdateService.this);
	   
	  new Thread(new Runnable(){
	   @Override
	   public void run() {
	    while (!threadDisable) { 
	     try {
	      Thread.sleep(1000);
	     } catch (InterruptedException e) {
	      e.printStackTrace();
	     }
	      
	     if(gps!=null){ //当结束服务时gps为空
	      //获取经纬度
	      Location location=gps.getLocation();
	      //如果gps无法获取经纬度，改用基站定位获取
	      if(location==null){
	       Log.v(TAG, "gps location null"); 
	        
	      }
	       
	      //发送广播
	      Intent intent=new Intent();
	      intent.putExtra("lat", location==null?"":location.getLatitude()+""); 
	      intent.putExtra("lon", location==null?"":location.getLongitude()+""); 
	      intent.putExtra("dtime", location==null?"":location.getTime()+"");
	      intent.setAction("com.nyu.cs9033.eta.controllers.LocationUpdateService"); 
	      sendBroadcast(intent); 
	     }
	 
	    }
	   }
	  }).start();
	   
	 }
     
	 @Override
	 public void onDestroy() {
		  threadDisable=true;
		   
		  if(gps!=null){
		   gps.closeLocation();
		   gps=null;
		  }
		  super.onDestroy();
	 }
	 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	 

}
