/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.io;

import com.jme3.math.Vector3f;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author samyn_000
 */
public class XMLUtils {

    public static void writeAttribute(Writer w, String key, String value) throws IOException {
        w.write(key);
        w.write("='");
        w.write(value);
        w.write("'");
    }

    public static void writeAttribute(Writer w, String key, boolean value) throws IOException {
        w.write(key);
        w.write("='");
        if (value) {
            w.write("true");
        } else {
            w.write("false");
        }
        w.write("'");
    }

    public static void writeAttribute(Writer w, String key, int value) throws IOException {
        w.write(key);
        w.write("='");
        w.write(Integer.toString(value));
        w.write("'");
    }

    public static void writeAttribute(Writer w, String key, float value) throws IOException {
        w.write(key);
        w.write("='");
        w.write(Float.toString(value));
        w.write("'");
    }

    public static void writeAttribute(Writer w, String key, Vector3f value) throws IOException {
        w.write(key);
        w.write("='[");
        w.write(Float.toString(value.x));
        w.write(",");
        w.write(Float.toString(value.y));
        w.write(",");
        w.write(Float.toString(value.z));
        w.write("]'");
    }
}
