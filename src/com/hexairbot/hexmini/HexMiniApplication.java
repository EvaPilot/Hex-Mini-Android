package com.hexairbot.hexmini;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.hexairbot.hexmini.modal.ApplicationSettings;

public class HexMiniApplication 
	extends Application 
	
{   
	private static final String TAG = "HexMiniApplication";
    
	private ApplicationSettings settings;
	
	/*
	static {
		System.loadLibrary("avutil");
		System.loadLibrary("swscale");
		System.loadLibrary("avcodec");
		System.loadLibrary("avfilter");
		System.loadLibrary("avformat");
		System.loadLibrary("avdevice");
		System.loadLibrary("adfreeflight");
	}*/
	
	@SuppressLint("NewApi")
    @Override
	public void onCreate() 
	{
		super.onCreate();
		Log.d(TAG, "OnCreate");

		settings = new ApplicationSettings(this);
	}

	
	@Override
	public void onTerminate() 
	{
		Log.d(TAG, "OnTerminate");
		super.onTerminate();
	}

	
	public ApplicationSettings getAppSettings()
	{
		return settings;
	}

}
