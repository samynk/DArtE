package dae.io;

import com.jme3.math.Vector3f;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Class that provides utility functions for binary readers.
 *
 * @author Koen Samyn
 */
public class BinaryUtils {

    private static ByteBuffer bb = ByteBuffer.allocate(8);
    private static byte[] tempBuffer = new byte[8];
    private static byte[] padder = {0, 0, 0, 0, 0, 0, 0, 0};

    static {
        bb.order(ByteOrder.LITTLE_ENDIAN);
    }

    public static short ReadShort(InputStream is) throws IOException {
        bb.position(0);
        is.read(tempBuffer, 0, 2);
        bb.put(tempBuffer, 0, 2);
        return bb.getShort(0);
    }

    public static int ReadUnsignedShort(InputStream is) throws IOException {
        bb.position(0);
        is.read(tempBuffer, 0, 2);

        bb.put(tempBuffer, 0, 2);
        bb.put(padder, 0, 2);

        return bb.getInt(0);
    }

    public static int ReadInt(InputStream is) throws IOException {
        bb.position(0);
        is.read(tempBuffer, 0, 4);
        bb.put(tempBuffer, 0, 4);

        return bb.getInt(0);

    }

    public static long ReadUnsignedInt(InputStream is) throws IOException {
        bb.position(0);
        is.read(tempBuffer, 0, 4);

        bb.put(tempBuffer, 0, 4);
        bb.put(padder, 0, 4);

        return bb.getLong(0);
    }

    public static float ReadFloat(InputStream is) throws IOException {
        bb.position(0);
        is.read(tempBuffer, 0, 4);
        bb.put(tempBuffer, 0, 4);
        return bb.getFloat(0);
    }
    
    public static Vector3f readOVFloat3(InputStream is) throws IOException{
        float x = ReadFloat(is);
        float y = ReadFloat(is);
        float z = ReadFloat(is);
        return new Vector3f(x,y,z);
    }

    public static double ReadDouble(InputStream is) throws IOException {
        bb.position(0);
        is.read(tempBuffer, 0, 8);
        bb.put(tempBuffer, 0, 8);
        return bb.getDouble(0);
    }

    public static int ReadByte(InputStream is) throws IOException {
        return is.read();
    }
    static char[] textbuffer = new char[1000];

    public static String ReadOVMAsciiString(InputStream is) throws IOException {
        int index = 0;
        int length = is.read();
        for (index = 0; index < length; ++index) {
            textbuffer[index] = (char) is.read();
        }
        return new String(textbuffer, 0, index);
    }
    
    public static String ReadOVMAsciiString(InputStream is, int length) throws IOException {
        byte chars[] = new byte[length];
        is.read(chars);
        return new String(chars);
    }
    
    public static String ReadLongString(InputStream is, int length) throws IOException {
        byte chars[] = new byte[length];
        is.read(chars);
        return new String(chars,StandardCharsets.UTF_8);
    }

    public static String ReadAsciiString(InputStream is) throws IOException {
        int current;
        int index = 0;
        while ((current = is.read()) != 0) {
            textbuffer[index++] = (char) current;
        }
        return new String(textbuffer, 0, index);
    }

    public static void Skip(InputStream is, int nrOfBytes) throws IOException {
        is.skip(nrOfBytes);
    }

    public static void Skip(InputStream is, long nrOfBytes) throws IOException {
        is.skip(nrOfBytes);
    }

    public static void writeOVMAsciiString(OutputStream os, String toWrite) throws IOException {
        int length = toWrite.length() < 256 ? toWrite.length() : 255;
        os.write(length);
        for (int i = 0; i < length; ++i) {
            int code = toWrite.charAt(i);
            os.write(code);
        }
    }
    
    public static int utf8StringLength(String toConvert){
        return toConvert.getBytes(StandardCharsets.UTF_8).length;
    }
    
    public static void writeOVMLongString(OutputStream os, String toWrite) throws IOException{
        byte[] bytes = toWrite.getBytes(StandardCharsets.UTF_8);
        writeUnsignedInt(os, bytes.length);
        os.write(bytes);
    }

    public static void writeUnsignedInt(OutputStream os, long intToWrite) throws IOException {
        // 
        os.write((int) (intToWrite & 0xff));
        os.write((int) ((intToWrite >> 8) & 0xff));
        os.write((int) ((intToWrite >> 16) & 0xff));
        os.write((int) ((intToWrite >> 24) & 0xff));
    }

    public static void writeOVFloat(OutputStream os, float value) throws IOException {
        int intToWrite = Float.floatToIntBits(value);
        os.write((int) (intToWrite & 0xff));
        os.write((int) ((intToWrite >> 8) & 0xff));
        os.write((int) ((intToWrite >> 16) & 0xff));
        os.write((int) ((intToWrite >> 24) & 0xff));
    }

    public static void writeOVShort(OutputStream os, int toWrite) throws IOException{
        os.write(toWrite);
        os.write((toWrite>> 8) );
    }

    public static void writeOVInt(OutputStream os, long toWrite) throws IOException{
        os.write((int)toWrite);
        os.write((int)(toWrite >> 8));
        os.write((int)(toWrite >> 16));
        os.write((int)(toWrite >> 24));
    }
    
    public static void writeOVFloat3(OutputStream os, Vector3f toWrite) throws IOException{
        writeOVFloat(os,toWrite.x);
        writeOVFloat(os,toWrite.y);
        writeOVFloat(os,toWrite.z);
    }

    public static void writeOVFloat3(OutputStream os, float x, float y, float z) throws IOException{
        writeOVFloat(os,x);
        writeOVFloat(os,y);
        writeOVFloat(os,z);
    }

    
}
