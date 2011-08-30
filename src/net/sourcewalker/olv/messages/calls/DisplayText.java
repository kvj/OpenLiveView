package net.sourcewalker.olv.messages.calls;

import java.nio.ByteBuffer;

import net.sourcewalker.olv.messages.MessageConstants;
import net.sourcewalker.olv.messages.SimpleLiveViewCall;

public class DisplayText extends SimpleLiveViewCall {

	public DisplayText(String text) {
		super(MessageConstants.MSG_DISPLAYTEXT);
		byte[] _text = stringToByteArray(text);
		buffer = ByteBuffer.allocate(_text.length+2);
		putString(buffer, _text);
	}
}
