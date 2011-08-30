package net.sourcewalker.olv.messages.calls;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import net.sourcewalker.olv.messages.MessageConstants;
import net.sourcewalker.olv.messages.SimpleLiveViewCall;

public class GetAlertResponse extends SimpleLiveViewCall {

	public GetAlertResponse(int total, int unread, int index, 
			String tsText, String headerText, String textChunk, 
			Bitmap bitmap) {
		super(MessageConstants.MSG_GETALERT_RESP);
		boolean viaImage = needBitmap(tsText) || needBitmap(headerText) || needBitmap(textChunk);
		byte[] _tsText = stringToByteArray(tsText, viaImage);
		byte[] _header = stringToByteArray(headerText, viaImage);
		byte[] _chunk = stringToByteArray(textChunk, viaImage);
		byte[] _bitmap = bitmapToByteArray(bitmap);
		buffer = ByteBuffer.allocate(20+_tsText.length+_header.length+
				_chunk.length+_bitmap.length);
		buffer.put((byte) 0);
		buffer.putShort((short) total);
		buffer.putShort((short) unread);
		buffer.putShort((short) index);
		buffer.put((byte) 0);
		buffer.put((byte) (viaImage? 1: 0));
		putString(buffer, _tsText);
		putString(buffer, _header);
		putString(buffer, _chunk);
		buffer.put((byte) 0);
		buffer.putInt(_bitmap.length);
		buffer.put(_bitmap);
	}

}
