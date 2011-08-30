package net.sourcewalker.olv.messages.calls;

import java.nio.ByteBuffer;

import net.sourcewalker.olv.messages.MessageConstants;
import net.sourcewalker.olv.messages.SimpleLiveViewCall;

public class SetMenuSettings extends SimpleLiveViewCall {

	public SetMenuSettings(int vibrationTime, int startMenu) {
		super(MessageConstants.MSG_SETMENUSETTINGS);
		buffer = ByteBuffer.allocate(3);
		buffer.put((byte) vibrationTime);
		buffer.put((byte) 12);
		buffer.put((byte) startMenu);
	}
}
