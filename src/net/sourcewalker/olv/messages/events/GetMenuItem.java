package net.sourcewalker.olv.messages.events;

import java.nio.ByteBuffer;

import net.sourcewalker.olv.messages.LiveViewEvent;
import net.sourcewalker.olv.messages.MessageConstants;

public class GetMenuItem extends LiveViewEvent {

	private int menuItem = -1;
	
	public GetMenuItem() {
		super(MessageConstants.MSG_GETMENUITEM);
	}
	
	@Override
	public void readData(ByteBuffer buffer) {
		menuItem = buffer.get();
	}

	public int getMenuItem() {
		return menuItem;
	}
}
