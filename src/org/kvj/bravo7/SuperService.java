package org.kvj.bravo7;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SuperService<T> extends Service{

	protected T controller = null;
	private final IBinder binder = new LocalBinder();
	private Notification notification = null;
	private String title = null;
    private static final int SERVICE_NOTIFY = 100;
	
	public class LocalBinder extends Binder {
		
		public T getController() {
			return controller;
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	protected void initNotification(int icon, String title) {
		notification = new Notification();
		this.title = title;
		notification.icon = icon;
	}
	
	public void raiseNotification(String text, Class<? extends Activity> received) {
		notification.setLatestEventInfo(getApplicationContext(), title, text, 
				PendingIntent.getActivity(getApplicationContext(), 0, 
						new Intent(getApplicationContext(), received), 
						PendingIntent.FLAG_CANCEL_CURRENT));
		startForeground(SERVICE_NOTIFY, notification);
	}
	
	public void hideNotification() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(SERVICE_NOTIFY);
        stopForeground(true);
	}
}
