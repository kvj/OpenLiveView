package net.sourcewalker.olv.service;

import java.util.Set;

import org.kvj.bravo7.ApplicationContext;
import org.kvj.bravo7.SuperService;

import net.sourcewalker.olv.R;
import net.sourcewalker.olv.data.Prefs;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * This service hosts and controls the thread communicating with the LiveView
 * device.
 * 
 * @author Robert &lt;xperimental@solidproject.de&gt;
 */
public class LiveViewService extends SuperService<LVController> {

    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
	private static final String TAG = "LiveViewService";

    private BroadcastReceiver btReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
	        Prefs prefs = new Prefs(context);
	        String address = prefs.getDeviceAddress();
	        if (address == null) {
	            Log.w(TAG, "No device configured!");
	        } else {
	            Log.d(TAG, "Device address: " + address);
	            String action = intent.getAction();
	            if (intent.getExtras() != null) {
	                BluetoothDevice device = (BluetoothDevice) intent.getExtras()
	                        .get(BluetoothDevice.EXTRA_DEVICE);
	                Log.d(TAG, "Received broadcast: " + action);
	                if (device != null && address.equals(device.getAddress())) {
	                    if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
	                        Log.d(TAG, "Connected -> Start thread: "+device.getAddress());
	                        controller.startThread(device.getAddress());
	                    }
	                    if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED
	                            .equals(action)
	                            || BluetoothDevice.ACTION_ACL_DISCONNECTED
	                                    .equals(action)) {
	                        Log.d(TAG, "Disconnected -> OK");
	                    }
	                }
	            }
	        }
		}
	};

    @Override
    public void onCreate() {
    	super.onCreate();
    	ApplicationContext context = ApplicationContext.getInstance(getApplicationContext());
    	controller = new LVController(this);
    	initNotification(R.drawable.icon, "OpenLiveView");
    	Log.i(TAG, TAG+" service started");
		getApplicationContext().registerReceiver(btReceiver, 
				new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
		getApplicationContext().registerReceiver(btReceiver, 
				new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
		getApplicationContext().registerReceiver(btReceiver, 
				new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
		try {
	        Prefs prefs = new Prefs(getApplicationContext());
	        String address = prefs.getDeviceAddress();
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			Set<BluetoothDevice> devices = adapter.getBondedDevices();
			for (BluetoothDevice device : devices) {
				Log.i(TAG, "Device: "+device.getName()+"::"+device.getAddress()+": "+
						device.getBondState());
				if (device.getAddress().equals(address) && device.getBondState() == device.BOND_BONDED) {
					Log.i(TAG, "Immediately start thread");
					controller.startThread(address);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    @Override
    public void onDestroy() {
    	Log.w(TAG, "Stopping service...");
    	controller.stopThread();
    	super.onDestroy();
    }
    
    @Override
    protected void finalize() throws Throwable {
    	controller.stopThread();
    	super.finalize();
    }

}
