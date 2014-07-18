package dae.animation.custom;

/**
 * @author Koen Samyn
 */
public class float2 {

    public float2() {
    }

    public float2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    /**
     * The length of this float2 object = sqrt(x^2 + y^2 + z^2)
     *
     * @return the length of this float2 object.
     */
    public float getLength() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * The squared length of this float2 object = (x^2 + y^2 + z^2)
     *
     * @return the squared length of this float2 object.
     */
    public float getLengthSquared() {
        return x * x + y * y;
    }

    /**
     * Adds two float3 objects together
     *
     * @param op1 the first float2 object to add.
     * @param op2 the second float2 object to add.
     * @return a new float2 object with the result of the add operation.
     */
    public static float2 Add(float2 op1, float2 op2) {
        return new float2(op1.x + op2.x, op1.y + op2.y);
    }

    /**
     * Creates a linear combination of two operands. Returns the result of
     * a*op1+b*op2
     *
     * @param a the coefficient for the first operand.
     * @param op1 the first float2 operand.
     * @param b the coefficient for the second operand.
     * @param op2 the second float2 operand.
     */
    public static float2 LinearCombination(float a, float2 op1, float b, float2 op2) {
        float x = a * op1.x + b * op2.x;
        float y = a * op1.y + b * op2.y;
        return new float2(x, y);
    }

    /**
     * Subtracts the second float2 object from the first float3 object.
     *
     * @param op1 the first float2 object to add.
     * @param op2 the second float2 object to add.
     * @return a new float3 object with the result of the subtract operation.
     */
    public static float2 Subtract(float2 op1, float2 op2) {
        return new float2(op1.x - op2.x, op1.y - op2.y);
    }

    /**
     * Calculates the dot product of two float3 objects.
     *
     * @param op1 the first float2 object.
     * @param op2 the second float2 object.
     * @return the dot product of op1 and op2
     */
    public static float Dot(float2 op1, float2 op2) {
        return op1.x * op2.x + op1.y * op2.y;
    }

    /**
     * Creates a String representation of this object.
     *
     * @return this float3 as a String object.
     */
    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }
    public float x, y;
}
