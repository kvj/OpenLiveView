package net.sourcewalker.olv.messages.calls;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;

import net.sourcewalker.olv.messages.MessageConstants;
import net.sourcewalker.olv.messages.SimpleLiveViewCall;

public class SetStatusBar extends SimpleLiveViewCall {

	public SetStatusBar(int menuItem, int unreadCount, Bitmap bitmap) {
		super(MessageConstants.MSG_SETSTATUSBAR);
		byte[] _bitmap = bitmapToByteArray(bitmap);
		buffer = ByteBuffer.allocate(15+_bitmap.length);
		buffer.put((byte) 0);
		buffer.putShort((short) 0);
		buffer.putShort((short) unreadCount);
		buffer.putShort((short) 0);
		buffer.put((byte) (menuItem+3));
		buffer.put((byte) 0);
		buffer.put(_bitmap);
	}
}
