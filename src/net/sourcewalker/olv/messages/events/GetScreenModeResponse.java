package net.sourcewalker.olv.messages.events;

import java.nio.ByteBuffer;

import net.sourcewalker.olv.messages.LiveViewEvent;
import net.sourcewalker.olv.messages.MessageConstants;

public class GetScreenModeResponse extends LiveViewEvent {

	boolean auto = false;
	int mode = 0;
	
	public GetScreenModeResponse() {
		super(MessageConstants.MSG_GETSCREENMODE_RESP);
	}

	@Override
	public void readData(ByteBuffer buffer) {
		byte val = buffer.get();
		auto = (val & 1)>0;
		mode = val >> 1;
	}
	
	public int getMode() {
		return mode;
	}
	
	public boolean isAuto() {
		return auto;
	}
}
