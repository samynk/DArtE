/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.io;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.io.IOException;
import java.io.Writer;
import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author Koen Samyn
 */
public class XMLUtils {

    public static void writeAttribute(Writer w, String key, String value) throws IOException {
        if ( value == null){
            return;
        }
        w.write(key);
        w.write("='");
        w.write(value);
        w.write("' ");
    }

    public static void writeAttribute(Writer w, String key, boolean value) throws IOException {
        w.write(key);
        w.write("='");
        if (value) {
            w.write("true");
        } else {
            w.write("false");
        }
        w.write("' ");
    }

    public static void writeAttribute(Writer w, String key, int value) throws IOException {
        w.write(key);
        w.write("='");
        w.write(Integer.toString(value));
        w.write("' ");
    }

    public static void writeAttribute(Writer w, String key, float value) throws IOException {
        w.write(key);
        w.write("='");
        w.write(Float.toString(value));
        w.write("' ");
    }

    public static void writeAttribute(Writer w, String key, Vector3f value) throws IOException {
        if ( value == null){
            return;
        }
        w.write(key);
        w.write("='[");
        w.write(Float.toString(value.x));
        w.write(",");
        w.write(Float.toString(value.y));
        w.write(",");
        w.write(Float.toString(value.z));
        w.write("]' ");
    }
    
    public static void writeAttribute(Writer w, String key, Quaternion value) throws IOException {
        if (value == null) {
            return;
        }
        w.write(key);
        w.write("='[");
        w.write(Float.toString(value.getX()));
        w.write(',');
        w.write(Float.toString(value.getY()));
        w.write(',');
        w.write(Float.toString(value.getZ()));
        w.write(',');
        w.write(Float.toString(value.getW()));
        w.write("]' ");
    }

    /**
     * Parses a float3 from a string defined as [x,y,z] with x, y and z floats
     *
     * @param float3 a string that defines 3 floating point variables.
     * @return a new Vector3f object.
     */
    public static Vector3f parseFloat3(String float3) {
        if (float3.length() > 0) {
            String withoutBrackets = float3.substring(1, float3.length() - 1);
            String[] cs = withoutBrackets.split(",");
            if (cs.length == 3) {
                try {
                    float x = Float.parseFloat(cs[0]);
                    float y = Float.parseFloat(cs[1]);
                    float z = Float.parseFloat(cs[2]);
                    return new Vector3f(x, y, z);
                } catch (NumberFormatException ex) {

                    return new Vector3f(0, 0, 0);
                }
            } else {
                return new Vector3f(0, 0, 0);
            }
        } else {
            return Vector3f.ZERO;
        }
    }

    /**
     * Parses a quaternion from a string defined as [x,y,z,w]
     *
     * @param quat a string that defines a quaternion.
     * @return a new Quaternion object.
     */
    public static Quaternion parseQuaternion(String quat) {
        String withoutBrackets = quat.substring(1, quat.length() - 1);
        String[] cs = withoutBrackets.split(",");
        if (cs.length == 4) {
            try {
                float x = Float.parseFloat(cs[0]);
                float y = Float.parseFloat(cs[1]);
                float z = Float.parseFloat(cs[2]);
                float w = Float.parseFloat(cs[3]);
                return new Quaternion(x, y, z, w);
            } catch (NumberFormatException ex) {

                return Quaternion.IDENTITY;
            }
        } else {
            return Quaternion.IDENTITY;
        }
    }

    /**
     * Parses a string that contains a color parameter defined as [r,g,b,a]. The
     * r,g,b and a value should be in the range [0,1].
     *
     * @param color a string that defines a color.
     * @return a new ColorRGBA color.
     */
    public static ColorRGBA parseColor(String color) {
        String withoutBrackets = color.substring(1, color.length() - 1);
        String[] cs = withoutBrackets.split(",");
        if (cs.length == 4) {
            try {
                float x = Float.parseFloat(cs[0]);
                float y = Float.parseFloat(cs[1]);
                float z = Float.parseFloat(cs[2]);
                float w = Float.parseFloat(cs[3]);
                return new ColorRGBA(x, y, z, w);
            } catch (NumberFormatException ex) {

                return ColorRGBA.White;
            }
        } else {
            return ColorRGBA.Magenta;
        }
    }
    
    /**
     * Parses the value of the given attribute as a float.
     * @param attributeName the name of the attribute.
     * @param map the map that contains all the attributes.
     * @return the float value.
     */
    public static float parseFloat(String attributeName, NamedNodeMap map) {
        org.w3c.dom.Node attr = map.getNamedItem(attributeName);
        try {
            return attr != null ? Float.parseFloat(attr.getTextContent()) : 0f;
        } catch (NumberFormatException ex) {
            return 0.0f;
        }
    }

     /**
     * Parses the value of the given attribute as an float.
     * @param attributeName the name of the attribute.
     * @param map the map that contains all the attributes.
     * @return the float value.
     */
    public static int parseInt(String attributeName, NamedNodeMap map) {
        org.w3c.dom.Node attr = map.getNamedItem(attributeName);
        try {
            return attr != null ? Integer.parseInt(attr.getTextContent()) : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * Parses the value of the given attribute as an boolean.
     * @param attributeName the name of the attribute.
     * @param map the map that contains all the attributes.
     * @return the float value.
     */
    public static boolean parseBoolean(String attributeName, NamedNodeMap map) {
        org.w3c.dom.Node attr = map.getNamedItem(attributeName);
        return attr != null ? Boolean.parseBoolean(attr.getTextContent()) : false;
    }
}
