package net.sourcewalker.olv;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kvj.bravo7.ControllerConnector;
import org.kvj.bravo7.ControllerConnector.ControllerReceiver;

import net.sourcewalker.olv.service.LVController;
import net.sourcewalker.olv.service.LiveViewService;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class LiveViewPreferences extends PreferenceActivity implements ControllerReceiver<LVController>{

    private static final String TAG = "LiveViewPreferences";
	private BluetoothAdapter btAdapter;
    private ListPreference devicePreference;
    ControllerConnector<LVController, LiveViewService> connector = 
    		new ControllerConnector<LVController, LiveViewService>(this, this);
	private LVController controller = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        addPreferencesFromResource(R.xml.preferences);

        devicePreference = (ListPreference) findPreference(getString(R.string.prefs_deviceaddress_key));
		Intent i = new Intent(this, LiveViewService.class);
        startService(i);
    }
    
    @Override
    protected void onStart() {
    	Log.i(TAG, "Connecting controller");
        connector.connectController(LiveViewService.class);    	
    	super.onStart();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        fillDevices();
    }

    /**
     * Get the list of paired devices from the system and fill the preference
     * list.
     */
    private void fillDevices() {
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        List<String> names = new ArrayList<String>();
        List<String> addresses = new ArrayList<String>();
        for (BluetoothDevice dev : devices) {
            names.add(dev.getName());
            addresses.add(dev.getAddress());
        }
        devicePreference.setEntries(names.toArray(new String[0]));
        devicePreference.setEntryValues(addresses.toArray(new String[0]));
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_viewlog:
            startActivity(new Intent(this, LogViewActivity.class));
            break;
        case R.id.menu_ping:
        	if (!controller.ping()) {
				Toast.makeText(getApplicationContext(), "Ping failed", Toast.LENGTH_LONG).show();
			}
            break;
        }
        return true;
    }

	@Override
	public void onController(LVController controller) {
		Log.i(TAG, "We have controller!");
		this.controller  = controller;
	}
}
