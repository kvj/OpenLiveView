package org.kvj.bravo7;

import org.kvj.bravo7.SuperService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class ControllerConnector<T extends Object, S extends SuperService<T>> implements ServiceConnection {
	
	private static final String TAG = "ControllerConnector";
	private Activity activity = null;
	private ControllerReceiver receiver = null;
	private LocalBinder localBinder = null;
	
	public ControllerConnector(Activity activity, ControllerReceiver receiver) {
		this.activity = activity;
		this.receiver = receiver;
	}
	
	public interface ControllerReceiver<T> {
		public void onController(T controller);
	}
	
    @Override
    public void onServiceDisconnected(ComponentName arg0) {
	    Log.i(TAG, "["+activity.getClass().getSimpleName()+"]service disconnected");
    	localBinder = null;
    	receiver.onController(null);
    }

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
	    Log.i(TAG, "["+activity.getClass().getSimpleName()+"]service connected: "+service);
        localBinder = (LocalBinder) service;
        receiver.onController(getController());
	}
	
	public void connectController(Class<S> cl) {
	    Intent intent = new Intent(activity, cl);
//	    Log.i(TAG, "["+activity.getClass().getSimpleName()+"]Binding to service...");
        activity.bindService(intent, this, Context.BIND_AUTO_CREATE);
//	    Log.i(TAG, "["+activity.getClass().getSimpleName()+"]Binding to service done");
	}
	
	public void disconnectController() {
		if (localBinder != null) {
			activity.unbindService(this);
		}
	}
	
	public T getController() {
		if (localBinder == null) {
			return null;
		}
		return (T) localBinder.getController();
	}
}
