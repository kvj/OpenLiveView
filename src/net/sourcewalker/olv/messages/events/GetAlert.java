package net.sourcewalker.olv.messages.events;

import java.nio.ByteBuffer;

import net.sourcewalker.olv.messages.LiveViewEvent;
import net.sourcewalker.olv.messages.MessageConstants;

public class GetAlert extends LiveViewEvent {
	
	public GetAlert() {
		super(MessageConstants.MSG_GETALERT);
	}
	
	int menuItem = -1;
	int alertAction = 0;
	int maxBodySize = 0;
	
	@Override
	public void readData(ByteBuffer buffer) {
		menuItem = buffer.get();
		alertAction = buffer.get();
		maxBodySize = buffer.getShort();
		buffer.get();
		buffer.get();
		buffer.get();
	}
	
	public int getAlertAction() {
		return alertAction;
	}
	
	public int getMaxBodySize() {
		return maxBodySize;
	}
	
	public int getMenuItem() {
		return menuItem;
	}
}
