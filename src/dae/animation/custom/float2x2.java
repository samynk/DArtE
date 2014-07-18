package dae.animation.custom;

import java.util.Random;

/**
 * @author Koen Samyn
 */
public class float2x2 {

    public float a1, a2, b1, b2;

    public float2x2() {
    }

    public float2x2(float a1, float a2, float b1, float b2) {
        setElements(a1, a2, b1, b2);
    }

    public float2x2 inverse() {
        float d = a1 * b2 - a2 * b1;
        return new float2x2(b2 / d, -a2 / d, -b1 / d, a1 / d);
    }

    public void inverse(float2x2 result) {
        float d = a1 * b2 - a2 * b1;
        result.setElements(b2 / d, -a2 / d, -b1 / d, a1 / d);
    }

    public float2 multiply(float2 op) {
        return new float2(a1 * op.x + a2 * op.y, b1 * op.x + b2 * op.y);
    }

    public void multiply(float2 result, float2 op) {
        float rx = a1 * op.x + a2 * op.y;
        float ry = b1 * op.x + b2 * op.y;
        result.x = rx;
        result.y = ry;
    }

    public float2 multiply(float r1, float r2) {
        return new float2(a1 * r1 + a2 * r2, b1 * r1 + b2 * r2);
    }

    public void multiply(float2 result, float r1, float r2) {
        float rx = a1 * r1 + a2 * r2;
        float ry = b1 * r1 + b2 * r2;
        result.x = rx;
        result.y = ry;
    }

    /**
     * solve the equation : a1*x + a2*y = c1 b1*x + b2*y = c2
     *
     * @param x the x parameter to solve
     * @param y the y parameter to solve
     */
    public float2 solve(float c1, float c2) {
        float2 result = new float2();
        if (solve(result, c1, c2)) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * solve the equation : a1*x + a2*y = c1 b1*x + b2*y = c2
     *
     * @param x the x parameter to solve
     * @param y the y parameter to solve
     */
    public boolean solve(float2 result, float c1, float c2) {
        float D = a1 * b2 - a2 * b1;
        if (Math.abs(D) > Float.MIN_NORMAL) {
            if (Math.abs(a1) > Float.MIN_NORMAL) {
                result.y = (a1 * c2 - b1 * c1) / D;
                result.x = (c1 - a2 * result.y) / a1;
            } else {
                result.y = c1 / a2;
                result.x = (c2 - b2 * result.y) / b1;
            }
            return true;
        }
        return false;
    }

    public void setRow1(float a1, float a2) {
        this.a1 = a1;
        this.a2 = a2;
    }

    public void setRow2(float b1, float b2) {
        this.b1 = b1;
        this.b2 = b2;
    }

    public void setColumn1(float a1, float b1) {
        this.a1 = a1;
        this.b1 = b1;
    }

    public void setColumn2(float a2, float b2) {
        this.a2 = a2;
        this.b2 = b2;
    }

    public void setElements(float a1, float a2, float b1, float b2) {
        this.a1 = a1;
        this.a2 = a2;
        this.b1 = b1;
        this.b2 = b2;
    }

    @Override
    public String toString() {
        return "[" + a1 + "," + a2 + ";" + b1 + "," + b2 + "]";
    }

    public static void main(String[] args) {
        float result2 = 1 / Float.MIN_NORMAL;
        System.out.println("Result is : " + Float.MIN_NORMAL);
        System.out.println("Result is : " + result2);
        float2x2 m = new float2x2(5, 4, 13, 7);
        float2 result = m.solve(12, 18);
        System.out.println(result);

        float2 check = new float2();
        m.multiply(check, result);

        float2x2 inverse = m.inverse();
        float2 solution2 = inverse.multiply(12, 18);

        float2 check2 = new float2();
        m.multiply(check2, solution2);

        System.out.println(check);
        System.out.println(check2);

        solveTest2();
    }

    public static void solveTest2() {
        float2x2 m = new float2x2(-4755.489f, 843.5676f, -756.9105f, 134.03232f);
        float2 solution2 = m.solve(-2953.724f, 1093.669f);

        float2 check2 = new float2();
        m.multiply(check2, solution2);

        System.out.println(solution2);
        System.out.println(check2);

        System.out.println("Errors : [" + (check2.x + 2953.724f) + "," + (check2.y - 1093.669f) + "]");
    }

    public static void solverTest() {
        float2x2 temp = new float2x2();
        float2x2 itemp = new float2x2();

        float2 solution = new float2();
        float2 check = new float2();
        Random r = new Random(System.currentTimeMillis());
        long start = System.currentTimeMillis();
        // test with inverse
        int matrixErrors = 0;
        int solverErrors = 0;
        for (int i = 1; i < 100; ++i) {
            float a1 = r.nextFloat() + (r.nextInt(10000) - 5000);
            float a2 = r.nextFloat() + (r.nextInt(10000) - 5000);
            float b1 = r.nextFloat() + (r.nextInt(10000) - 5000);
            float b2 = r.nextFloat() + (r.nextInt(10000) - 5000);

            temp.setElements(a1, a2, b1, b2);
            temp.inverse(itemp);

            float c1 = r.nextFloat() + (r.nextInt(10000) - 5000);
            float c2 = r.nextFloat() + (r.nextInt(10000) - 5000);

            itemp.multiply(solution, c1, c2);

            temp.multiply(check, solution);

            float error1 = Math.abs(check.x - c1);
            float error2 = Math.abs(check.y - c2);

            if (error1 > .1f || error2 > .1f) {
                System.out.println("error with :" + temp);
                System.out.println("inverse is :" + itemp);
                System.out.println("and C : [" + c1 + ";" + c2 + "]");
                System.out.println("Solution is : " + solution);
                System.out.println("Error1 is :" + error1);
                System.out.println("Error2 is :" + error2);
                matrixErrors++;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("inverse method : " + (end - start));

        System.out.println("#Beginning solve test");
        System.out.println("#####################");
        System.out.println("#####################");
        System.out.println("#####################");
        System.out.println("#####################");
        long start2 = System.currentTimeMillis();

        // test with inverse
        for (int i = 1; i < 100; ++i) {
            float a1 = r.nextFloat() + (r.nextInt(10000) - 5000);
            float a2 = r.nextFloat() + (r.nextInt(10000) - 5000);
            float b1 = r.nextFloat() + (r.nextInt(10000) - 5000);
            float b2 = r.nextFloat() + (r.nextInt(10000) - 5000);

            temp.setElements(a1, a2, b1, b2);

            float c1 = r.nextFloat() + (r.nextInt(10000) - 5000);
            float c2 = r.nextFloat() + (r.nextInt(10000) - 5000);

            temp.solve(solution, c1, c2);

            temp.multiply(check, solution);

            float error1 = Math.abs(check.x - c1);
            float error2 = Math.abs(check.y - c2);

            if (error1 > .1f || error2 > .1f) {
                System.out.println("error with :" + temp);

                System.out.println("and c1 : " + c1);
                System.out.println("and c2 : " + c2);

                System.out.println("Solution is : " + solution);

                System.out.println("Error1 is :" + error1);
                System.out.println("Error2 is :" + error2);

                solverErrors++;
            }
        }
        long end2 = System.currentTimeMillis();
        System.out.println("Solve method : " + (end2 - start2));

        System.out.println("Matrix errors : " + matrixErrors);
        System.out.println("Solver errors : " + solverErrors);

    }
}
