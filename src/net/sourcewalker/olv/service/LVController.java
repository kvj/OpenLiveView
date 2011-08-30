package net.sourcewalker.olv.service;

import net.sourcewalker.olv.LiveViewPreferences;
import net.sourcewalker.olv.messages.calls.DisplayPanel;
import net.sourcewalker.olv.messages.calls.DisplayText;
import net.sourcewalker.olv.messages.calls.SetLED;
import net.sourcewalker.olv.messages.calls.SetStatusBar;
import net.sourcewalker.olv.messages.calls.SetVibrate;
import android.util.Log;

public class LVController {
	
    private static final String TAG = "LVController";
	private LiveViewThread workerThread = null;
    private LiveViewService service = null;
    public enum BTConnectionState {NotConnected, Waiting, Connected};
    private BTConnectionState state = BTConnectionState.NotConnected;
    
    
	public LVController(LiveViewService service) {
		this.service = service;
		setState(BTConnectionState.NotConnected);
	}
    
    /**
     * Starts the worker thread if the thread is not already running. If there
     * is a thread running that has already been stopped then a new thread is
     * started.
     */
    void startThread(String address) {
        if (workerThread == null || !workerThread.isLooping()) {
        	Log.w(TAG, "Creating new thread");
            workerThread = new LiveViewThread(this, address);
            workerThread.start();
        }
    }

    /**
     * Signals the current worker thread to stop itself. If no worker thread is
     * available then nothing is done.
     */
    void stopThread() {
    	Log.w(TAG, "Stopping thread");
        if (workerThread != null && workerThread.isAlive()) {
            workerThread.stopLoop();
        }
    }
    
    public void setState(BTConnectionState state) {
		this.state = state;
		switch (state) {
		case NotConnected:
			service.hideNotification();
			break;
		case Connected:
			service.raiseNotification("LiveView connected", LiveViewPreferences.class);
			break;
		case Waiting:
			service.raiseNotification("LiveView waiting", LiveViewPreferences.class);
			break;

		default:
			break;
		}
	}
    
    public BTConnectionState getState() {
		return state;
	}
    
    public boolean ping() {
    	try {
        	if (state == BTConnectionState.Connected) {
        		
//    			workerThread.sendCall(new DisplayPanel("Hi korea!", "Проверка", null, true));
//    			workerThread.sendCall(new SetLED(255, 0, 255, 0, 2000));
    			workerThread.sendCall(new SetStatusBar(1, 2, null));
    			return true;
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
}
