package Services;
import io.netty.buffer.ByteBuf;
public class DecoderService {

//    перевод байтов из потока в String

//    public static String byteToString (ByteBuf buf, byte size) {
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < size; i++) {
//            char a = (char) buf.readByte();
//            stringBuilder.append(a);
//        }
//        return stringBuilder.toString();
//    }
//
//    public static String byteToString (Object msg) {
//        ByteBuf buf = (ByteBuf) msg;
//        byte deleteFirstByte = buf.readByte();
//        StringBuilder stringBuilder = new StringBuilder();
//        while (buf.isReadable()) {
//            stringBuilder.append((char) buf.readByte());
//        }
//        return stringBuilder.toString();
//    }
    public static String byteToString (Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        byte size = buf.readByte();
        System.out.println(size);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            char a = (char) buf.readByte();
            stringBuilder.append(a);
        }
        return stringBuilder.toString();
    }
}
