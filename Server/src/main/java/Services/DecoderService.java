package Services;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DecoderService {

//    перевод байтов из потока в String

    public static String byteToString (ByteBuf buf, byte size) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            char a = (char) buf.readByte();
            stringBuilder.append(a);
        }
        return stringBuilder.toString();
    }

    public static String byteToString (Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        byte firstByte = buf.readByte();
        byte size = buf.readByte();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            char a = (char) buf.readByte();
            stringBuilder.append(a);
        }
        return stringBuilder.toString();
    }

    public static ByteBuf stringToByteBuf (String string) {
        byte [] arr = string.getBytes();
        return Unpooled.copiedBuffer(arr);
    }
}
