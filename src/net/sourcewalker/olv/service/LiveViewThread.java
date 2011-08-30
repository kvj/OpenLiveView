package net.sourcewalker.olv.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import net.sourcewalker.olv.LiveViewPreferences;
import net.sourcewalker.olv.R;
import net.sourcewalker.olv.data.LiveViewDbHelper;
import net.sourcewalker.olv.messages.DecodeException;
import net.sourcewalker.olv.messages.LiveViewCall;
import net.sourcewalker.olv.messages.LiveViewEvent;
import net.sourcewalker.olv.messages.MessageConstants;
import net.sourcewalker.olv.messages.UShort;
import net.sourcewalker.olv.messages.calls.CapsRequest;
import net.sourcewalker.olv.messages.calls.DeviceStatusAck;
import net.sourcewalker.olv.messages.calls.GetAlertResponse;
import net.sourcewalker.olv.messages.calls.GetTimeResponse;
import net.sourcewalker.olv.messages.calls.MenuItem;
import net.sourcewalker.olv.messages.calls.MessageAck;
import net.sourcewalker.olv.messages.calls.NavigationResponse;
import net.sourcewalker.olv.messages.calls.SetMenuSize;
import net.sourcewalker.olv.messages.calls.SetVibrate;
import net.sourcewalker.olv.messages.events.CapsResponse;
import net.sourcewalker.olv.messages.events.GetAlert;
import net.sourcewalker.olv.messages.events.Navigation;
import net.sourcewalker.olv.service.LVController.BTConnectionState;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author Robert &lt;xperimental@solidproject.de&gt;
 */
public class LiveViewThread extends Thread {

    private final String TAG = "LiveViewThread";

    private static final UUID SERIAL = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final byte[] menuImage = new byte[0];

    private final BluetoothAdapter btAdapter;

    private BluetoothServerSocket serverSocket;

    private long startUpTime;

    private BluetoothSocket clientSocket;

    private String deviceAddress;
    
    private LVController controller = null;
    

    public LiveViewThread(LVController controller, String deviceAddress) {
        super("LiveViewThread");
        this.controller = controller;
        Log.i(TAG, "Thread ready to start...:"+hashCode());
        this.deviceAddress = deviceAddress;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        try {
//            InputStream stream = parentService.getAssets().open(
//                    "menu_blank.png");
//            ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            while (stream.available() > 0) {
//                int read = stream.read(buffer);
//                arrayStream.write(buffer, 0, read);
//            }
//            stream.close();
//            menuImage = arrayStream.toByteArray();
//            Log.d(TAG, "Menu icon size: " + menuImage.length);
//        } catch (IOException e) {
//            Log.e(TAG, "Error reading menu icon: " + e.getMessage());
//            throw new RuntimeException("Error reading menu icon: "
//                    + e.getMessage(), e);
//        }
    }

    private boolean serveConnection() {
        Log.d(TAG, "Starting LiveView thread.");
        startUpTime = System.currentTimeMillis();
        try {
            Log.d(TAG, "Listening for LV...");
        	controller.setState(BTConnectionState.Waiting);
            clientSocket = serverSocket.accept();
            EventReader reader = new EventReader(clientSocket.getInputStream());
            // Single connect only
            Log.d(TAG, "LV connected.");
            sendCall(new CapsRequest());
            Log.d(TAG, "Message sent.");
        	controller.setState(BTConnectionState.Connected);
            do {
                try {
                    LiveViewEvent response = reader.readMessage();
                    sendCall(new MessageAck(response.getId()));
                    Log.i(TAG, "Got message: " + response);
                    processEvent(response);
                } catch (DecodeException e) {
                    Log.e(TAG, "Error decoding message: " + e.getMessage());
                }
            } while (true);
        } catch (IOException e) {
        	controller.setState(BTConnectionState.NotConnected);
        	e.printStackTrace();
            String msg = e.getMessage();
            if (!msg.contains("Connection timed out")) {
                Log.e(TAG, "Error communicating with LV: " + e.getMessage());
            }
        }
        try {
            if (clientSocket != null) {
    			clientSocket.close();
    			clientSocket = null;
    		}
            if (null != serverSocket) {
                serverSocket.close();
                serverSocket = null;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error closing client socket: ", e);
		}
        Log.d(TAG, "Stopped LiveView thread(with sockets):");
    	
        // Log runtime
        long runtime = (System.currentTimeMillis() - startUpTime) / 1000;
        long runHour = runtime / 3600;
        runtime -= runHour * 3600;
        long runMinute = runtime / 60;
        runtime -= runMinute * 60;
        String message = String.format(
                "Service runtime: %d hours %d minutes %d seconds", runHour,
                runMinute, runtime);
        Log.d(TAG, message);
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        serverSocket = null;
        try {
            Log.d(TAG, "Starting server...");
            serverSocket = btAdapter.listenUsingInsecureRfcommWithServiceRecord(
                    "LiveView", SERIAL);
        } catch (IOException e) {
        	controller.setState(BTConnectionState.NotConnected);
            Log.e(TAG, "Error starting BT server: " + e.getMessage());
            return;
        }
        while (true) {
        	BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
        	Log.d(TAG, "Current device state: "+device.getBondState());
        	if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
				Log.w(TAG, "Closing this thread");
				break;
			}
        	if(!serveConnection()) {
        		break;
        	}
		}
        // Stop surrounding service
        Log.w(TAG, "Thread stopped!");
    }

    /**
     * Process a message that was sent by the LiveView device.
     * 
     * @param event
     *            Event sent by device.
     * @throws IOException
     */
    private void processEvent(LiveViewEvent event) throws IOException {
        switch (event.getId()) {
        case MessageConstants.MSG_GETCAPS_RESP:
            CapsResponse caps = (CapsResponse) event;
            Log.d(TAG, "LV capabilities: " + caps.toString());
            Log.d(TAG, "LV Version: " + caps.getSoftwareVersion());
            sendCall(new SetMenuSize(3));
            sendCall(new SetVibrate(0, 50));
            break;
        case MessageConstants.MSG_GETTIME:
            Log.d(TAG, "Sending current time...");
            sendCall(new GetTimeResponse());
            break;
        case MessageConstants.MSG_DEVICESTATUS:
            Log.d(TAG, "Acknowledging status.");
            sendCall(new DeviceStatusAck());
            break;
        case MessageConstants.MSG_GETMENUITEMS:
            Log.d(TAG, "Sending menu items...");
            sendCall(new MenuItem((byte) 0, true, new UShort((short) 0),
                    "Test", menuImage));
            sendCall(new MenuItem((byte) 1, true, new UShort((short) 0),
                    "Hi korea!", menuImage));
            sendCall(new MenuItem((byte) 2, false, new UShort((short) 0),
                    "Non alert item", menuImage));
            break;
        case MessageConstants.MSG_NAVIGATION:
            Navigation nav = (Navigation) event;
            sendCall(new NavigationResponse(MessageConstants.RESULT_OK));
//        	sendCall(new SetVibrate(0, 50));
//            if (nav.getNavAction() == MessageConstants.NAVACTION_PRESS
//                    && nav.getNavType() == MessageConstants.NAVTYPE_MENUSELECT) {
//                sendCall(new NavigationResponse(MessageConstants.RESULT_OK));
//            } else if (nav.getNavAction() == MessageConstants.NAVACTION_LONGPRESS) {
//                sendCall(new NavigationResponse(MessageConstants.RESULT_OK));
//            } else {
//                Log.d(TAG, "Bringing back to menu.");
//                sendCall(new NavigationResponse(MessageConstants.RESULT_CANCEL));
//            }
            break;
        case MessageConstants.MSG_GETALERT:
        	GetAlert getAlert = (GetAlert) event;
            Log.i(TAG, "Need to send alert: "+getAlert.getMenuItem()+", "+getAlert.getMaxBodySize()+", "+getAlert.getAlertAction());
            sendCall(new GetAlertResponse(2, 2, 0, "Test english", "Проверка", "aaaaaaaaaaaaaa aaaaaaabbbb bbbbbcccc ccccdddddd dddffffff", null));
            break;
        }
    }

    /**
     * Send a message to the LiveView device if one is connected.
     * 
     * @param call
     *            {@link LiveViewCall} to send to device.
     * @throws IOException
     *             If the message could not be sent successfully.
     */
    void sendCall(LiveViewCall call) throws IOException {
        if (clientSocket == null) {
            throw new IOException("No client connected!");
        } else {
            clientSocket.getOutputStream().write(call.getEncoded());
        }
    }

    public void stopLoop() {
    	controller.setState(BTConnectionState.NotConnected);
    	Log.w(TAG, "Stopping thread...");
        if (isLooping()) {
            try {
            	if (null != serverSocket) {
                    serverSocket.close();
                    serverSocket = null;
				}
            	if (null != clientSocket) {
					clientSocket.close();
					clientSocket = null;
				}
            	if (isAlive()) {
					interrupt();
				}
            } catch (Exception e) {
                Log.e(TAG,
                        "Error while closing server socket: " + e.getMessage());
            }
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
    	Log.w(TAG, "Doing finalize because of GC");
    	super.finalize();
    	stopLoop();
    }

    public boolean isLooping() {
        return serverSocket != null;
    }

}
