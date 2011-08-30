package net.sourcewalker.olv.messages.calls;

import java.nio.ByteBuffer;

import net.sourcewalker.olv.messages.MessageConstants;
import net.sourcewalker.olv.messages.SimpleLiveViewCall;

public class SetLED extends SimpleLiveViewCall {

	public SetLED(int r, int g, int b, int delayTime, int onTime) {
		super(MessageConstants.MSG_SETLED);
		buffer = ByteBuffer.allocate(6);
		buffer.putShort((short) (((r & 0x31) << 10) | ((g & 0x31) << 5) | (b & 0x31)));
		buffer.putShort((short) delayTime);
		buffer.putShort((short) onTime);
	}

}
