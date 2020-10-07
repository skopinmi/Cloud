package Services;
import io.netty.buffer.ByteBuf;
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

    public static String byteToStringIsReadable (Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        StringBuilder stringBuilder = new StringBuilder();
        while (buf.isReadable()) {
            char a = (char) buf.readByte();
            System.out.print(a);
            stringBuilder.append(a);
        }
        return stringBuilder.toString();
    }
}
