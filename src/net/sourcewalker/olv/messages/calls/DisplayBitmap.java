package net.sourcewalker.olv.messages.calls;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import net.sourcewalker.olv.messages.MessageConstants;
import net.sourcewalker.olv.messages.SimpleLiveViewCall;

public class DisplayBitmap extends SimpleLiveViewCall {

	public DisplayBitmap(int x, int y, Bitmap bitmap) {
		super(MessageConstants.MSG_DISPLAYBITMAP);
		byte[] _bitmap = bitmapToByteArray(bitmap);
		buffer = ByteBuffer.allocate(3+_bitmap.length);
		buffer.put((byte) x);
		buffer.put((byte) y);
		buffer.put((byte) 1);
		buffer.put(_bitmap);
	}

}
