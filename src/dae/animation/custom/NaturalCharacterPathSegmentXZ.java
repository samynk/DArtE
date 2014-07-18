/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.custom;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.Arrays;

/**
 *
 * @author Koen
 */
public class NaturalCharacterPathSegmentXZ implements PathSegment {

    private Vector3f p1 = new Vector3f();
    private Vector3f p2 = new Vector3f();
    private Vector3f dir1 = new Vector3f();
    private Vector3f dir2 = new Vector3f();
    private float turnRadius = 0.4f;
    private Vector3f circleCenter = new Vector3f();
    private float circleRadius;
    private Vector3f circle1Center = new Vector3f();
    private Vector3f circle2Center = new Vector3f();
    private Vector3f t1 = new Vector3f();
    private Vector3f t2 = new Vector3f();
    private float totalLength;
    private float startAngle1;
    private float endAngle1;
    private boolean cw1;
    private float turnAngle1;
    private float startAngle2;
    private float endAngle2;
    private boolean cw2;
    private float turnAngle2;
    private float length1;
    private float length2;
    private float length3;
    private Vector3f lineDir;

    public NaturalCharacterPathSegmentXZ() {
        p1 = new Vector3f(200, 200, 0);
        p2 = new Vector3f(330, 400, 0);

        dir1 = new Vector3f(2, 1, 0);
        dir1.normalizeLocal();
        dir2 = new Vector3f(1, 0.1f, 0);
        dir2.normalizeLocal();
        update();
    }

    /**
     * Constructs a NaturalCharacterPathSegment.
     *
     * @param p1 the first point of the path segment.
     * @param dir1 the direction to walk in.
     * @param p2 the second point of the path segment.
     * @param dir2 the end direction.
     */
    public NaturalCharacterPathSegmentXZ(Vector3f p1, Vector3f dir1, Vector3f p2, Vector3f dir2) {
        this.p1.set(p1);
        this.dir1.set(dir1);
        this.dir1.normalizeLocal();
        this.p2.set(p2);
        this.dir2.set(dir2);
        this.dir2.normalizeLocal();
        update();
    }

    /**
     * Interpolates the path segment.
     *
     * @param t the parameter t.
     * @param result the Vector3f object to store the result in.
     */
    @Override
    public void interpolate(float t, Vector3f result) {
        if (t < 0) {
            result.set(p1);
        } else if (t > 1) {
            result.set(p2);
        } else {
            float length = t * totalLength;
            if (length < length1) {
                float angle = length / turnRadius;
                if (cw1) {
                    angle = startAngle1 - angle;
                } else {
                    angle = startAngle1 + angle;
                }
                float x = turnRadius * FastMath.cos(angle);
                float z = turnRadius * FastMath.sin(angle);
                result.set(circle1Center.x + x, 0, circle1Center.z + z);
            } else if (length < length1 + length2) {
                float lineLength = length - length1;
                float u = lineLength / t1.distance(t2);
                result.set(t1.x + u * (t2.x - t1.x), 0, t1.z + u * (t2.z - t1.z));
            } else {
                float arcLength = length - (length1 + length2);

                float angle = arcLength / turnRadius;
                if (cw2) {
                    angle = endAngle2 - angle;
                } else {
                    angle = endAngle2 + angle;
                }
                float x = turnRadius * FastMath.cos(angle);
                float z = turnRadius * FastMath.sin(angle);
                result.set(circle2Center.x + x, 0, circle2Center.z + z);
            }
        }
    }

    /**
     * Returns the t value for the segment.
     *
     * @param t the t parameter
     * @param desiredLength the desired length.
     * @return
     */
    @Override
    public float getSegment(float t, float desiredLength) {
        float length = t * totalLength;
        float newt = (length + desiredLength) / totalLength;
        return newt;
    }

    /**
     *
     * @param t
     * @param tangent
     */
    @Override
    public void getTangent(float t, Vector3f tangent) {
        if (t < 0) {
            tangent.set(dir1);
        } else if (t > 1) {
            tangent.set(dir2);
        } else {
            float length = t * totalLength;
            if (length < length1) {
                float angle = length / turnRadius;
                if (cw1) {
                    angle = startAngle1 - angle;
                } else {
                    angle += startAngle1;
                }
                float x, z;
                if (cw1) {
                    x = FastMath.sin(angle);
                    z = -FastMath.cos(angle);
                } else {
                    x = -FastMath.sin(angle);
                    z = FastMath.cos(angle);
                }
                tangent.set(x, 0, z);
            } else if (length < length1 + length2) {
                tangent.set(lineDir);
            } else {
                float arcLength = length - (length1 + length2);

                float angle = arcLength / turnRadius;
                if (cw2) {
                    angle = endAngle2 - angle;
                } else {
                    angle += endAngle2;
                }
                float x, z;
                if (cw2) {
                    x = FastMath.sin(angle);
                    z = -FastMath.cos(angle);
                } else {
                    x = -FastMath.sin(angle);
                    z = FastMath.cos(angle);
                }
                tangent.set(x, 0, z);
            }
        }
    }
    float2x2 workMatrix = new float2x2();

    /**
     * Updates the extra constructs for the path.
     */
    public void update() {
        // check if direction vectors are in line
        Vector3f direction = p2.subtract(p1).normalizeLocal();
        if (FastMath.abs(dir1.dot(dir2) - 1.0f) > 1e-2 || FastMath.abs(dir1.dot(direction) - 1.0f) > 1e-2) {

            Vector3f p1dir = new Vector3f(dir1.z, 0, -dir1.x);
            Vector3f p2dir = new Vector3f(dir2.z, 0, -dir2.x);

            p1dir.normalizeLocal();
            p2dir.normalizeLocal();

            if (p1.distance(p2) < turnRadius * 3.0f) {
                //circleCenter = p1.add(p1dir.mult(result.x));
                //circleRadius = p1.distance(circleCenter);
                turnRadius = p1.distance(p2) / 3.0f;
            }

            PathingResult currentResult = new PathingResult();
            // choose the best solution (least amount of rotation)
            // solution 1
            circle1Center = p1.add(p1dir.mult(turnRadius));
            circle2Center = p2.add(p2dir.mult(turnRadius));
            calculatePath(currentResult, p1, p2, dir1, dir2, circle1Center, circle2Center, turnRadius, t1, t2);


            circle1Center = p1.add(p1dir.mult(-turnRadius));
            circle2Center = p2.add(p2dir.mult(turnRadius));
            calculatePath(currentResult, p1, p2, dir1, dir2, circle1Center, circle2Center, turnRadius, t1, t2);

            circle1Center = p1.add(p1dir.mult(-turnRadius));
            circle2Center = p2.add(p2dir.mult(-turnRadius));
            calculatePath(currentResult, p1, p2, dir1, dir2, circle1Center, circle2Center, turnRadius, t1, t2);

            circle1Center = p1.add(p1dir.mult(+turnRadius));
            circle2Center = p2.add(p2dir.mult(-turnRadius));
            calculatePath(currentResult, p1, p2, dir1, dir2, circle1Center, circle2Center, turnRadius, t1, t2);

            this.t1.set(currentResult.t1);
            this.t2.set(currentResult.t2);
            this.circle1Center.set(currentResult.c1);
            this.circle2Center.set(currentResult.c2);

            endAngle1 = currentResult.endAngle1;
            startAngle1 = currentResult.startAngle1;

            endAngle2 = currentResult.endAngle2;
            startAngle2 = currentResult.startAngle2;

            cw1 = currentResult.cw1;
            cw2 = currentResult.cw2;

            turnAngle1 = calculateTurnAngle(startAngle1, endAngle1, cw1);
            turnAngle2 = calculateTurnAngle(endAngle2, startAngle2, cw2);

            totalLength = 0;
            length1 = Math.abs(turnAngle1 * turnRadius);
            length2 = t1.distance(t2);
            length3 = Math.abs(turnAngle2 * turnRadius);
            totalLength = length1 + length2 + length3;
            lineDir = t2.subtract(t1);
            lineDir.normalizeLocal();
            currentResult.printAngles();
        } else {
            circle1Center = this.p1;
            circle2Center = this.p2;
            turnAngle1 = 0.0f;
            turnAngle2 = 0.0f;
            t1 = this.p1;
            t2 = this.p2;
            length1 = 0;
            length2 = t1.distance(t2);
            length3 = 0;
            totalLength = length2;
            lineDir = t2.subtract(t1);
            lineDir.normalizeLocal();

        }
    }

    private float calculateTurnAngle(float startAngle, float endAngle, boolean cw) {
        if (!cw) {

            if (endAngle > startAngle) {
                return endAngle - startAngle;
            } else {
                return endAngle + FastMath.TWO_PI - startAngle;
            }
        } else {
            if (startAngle > endAngle) {
                return -(startAngle - endAngle);
            } else {
                return -(startAngle + FastMath.TWO_PI - endAngle);
            }
        }
    }

    public Vector3f getT1() {
        return t1;
    }

    public Vector3f getT2() {
        return t2;
    }

    public float getStartAngle1() {
        return startAngle1;
    }

    public float getTurnAngle1() {

        return turnAngle1;
    }

    public float getStopAngle1() {
        return endAngle1;
    }

    public boolean isAngle1CW() {
        return cw1;
    }

    public float getStartAngle2() {
        return startAngle2;
    }

    public float getTurnAngle2() {
        return turnAngle2;
    }

    public float getStopAngle2() {
        return endAngle2;
    }

    public boolean isAngle2CW() {
        return cw2;
    }

    public void calculatePath(PathingResult current, Vector3f p1, Vector3f p2, Vector3f dir1, Vector3f dir2, Vector3f c1, Vector3f c2, float radius, Vector3f tp1, Vector3f tp2) {
        float[][] tangents = this.getTangents(c1.x, c1.z, radius, c2.x, c2.z, radius);

        for (int row = 0; row < tangents.length; ++row) {
//            System.out.println("Tangents :" + row);
//            System.out.println("---------------------");
            float x1 = tangents[row][0];
            float z1 = tangents[row][1];
            float x2 = tangents[row][2];
            float z2 = tangents[row][3];
//
            tp1.set(x1, 0, z1);
            tp2.set(x2, 0, z2);

            Vector3f walkDirection = tp2.subtract(tp1);
            float length = walkDirection.length();
            walkDirection.divideLocal(1 / length);

            Vector3f v1 = new Vector3f(p1.x - c1.x, 0, p1.z - c1.z);
//            v1.normalizeLocal();
            Vector3f v2 = new Vector3f(x1 - c1.x, 0, z1 - c1.z);
//            v2.normalizeLocal();

            Vector3f cross11 = v1.cross(v2);
            Vector3f cross12 = dir1.cross(walkDirection);



            if (cross11.y * cross12.y < 0) {
//                System.out.println("Angle 1 is : " + FastMath.RAD_TO_DEG * angle);
//                System.out.println("Dircheck 1 : " + dircheck1 + ", [ " + dir1.length() + "," + v2.length() + "]");
                continue;

            }



            Vector3f v3 = new Vector3f(p2.x - c2.x, 0, p2.z - c2.z);
            //v3.normalizeLocal();
            Vector3f v4 = new Vector3f(x2 - c2.x, 0, z2 - c2.z);
            //v4.normalizeLocal();

            Vector3f cross21 = v3.cross(v4);
            Vector3f cross22 = dir2.cross(walkDirection);

            if (cross21.y * cross22.y < 0) {
//                System.out.println("Angle 2 is : " + FastMath.RAD_TO_DEG * angle2);
//                System.out.println("Dircheck 2 : " + dircheck2 + ", [ " + dir2.length() + "," + v4.length() + "]");
                continue;
            }


            if (length < current.length) {
                current.startAngle1 = FastMath.atan2(v1.z, v1.x);
                if (current.startAngle1 < 0) {
                    current.startAngle1 += FastMath.TWO_PI;
                }
                current.endAngle1 = FastMath.atan2(v2.z, v2.x);
                if (current.endAngle1 < 0) {
                    current.endAngle1 += FastMath.TWO_PI;
                }
                current.startAngle2 = FastMath.atan2(v3.z, v3.x);
                if (current.startAngle2 < 0) {
                    current.startAngle2 += FastMath.TWO_PI;
                }
                current.endAngle2 = FastMath.atan2(v4.z, v4.x);
                if (current.endAngle2 < 0) {
                    current.endAngle2 += FastMath.TWO_PI;
                }

                Vector3f cross1 = v1.cross(dir1);
                current.cw1 = cross1.y > 0;
                Vector3f cross2 = v3.cross(dir2);
                current.cw2 = cross2.y > 0;

                current.length = length;
                current.t1.set(tp1);
                current.t2.set(tp2);
                current.c1.set(c1);
                current.c2.set(c2);
            }
        }
        //System.out.println("returning false ");
        //return false;
    }

    public Vector3f getCircleCenter() {
        return circleCenter;
    }

    public float getCircleRadius() {
        return circleRadius;
    }

    /**
     * Returns the value of p1
     *
     * @return the p1 point.
     */
    public Vector3f getP1() {
        return p1;
    }

    /**
     * Sets a new value for the first point.
     *
     * @param p1 the p1 to set
     */
    public void setP1(Vector3f p1) {
        this.p1 = p1;
        update();
    }

    /**
     * Returns a value for the second point.
     *
     * @return the p2
     */
    public Vector3f getP2() {
        return p2;
    }

    /**
     * Sets the value for the second point.
     *
     * @param p2 the p2 to set
     */
    public void setP2(Vector3f p2) {
        this.p2 = p2;
        update();
    }

    /**
     * Returns the value for the first direction.
     *
     * @return the dir1
     */
    public Vector3f getDir1() {
        return dir1;
    }

    /**
     * Sets the value for the first direction.
     *
     * @param dir1 the dir1 to set
     */
    public void setDir1(Vector3f dir1) {
        this.dir1 = dir1;
        update();
    }

    /**
     * Returns the value for the second direction.
     *
     * @return the dir2
     */
    public Vector3f getDir2() {
        return dir2;
    }

    /**
     * Sets the value for the second direction.
     *
     * @param dir2 the dir2 to set
     */
    public void setDir2(Vector3f dir2) {
        this.dir2 = dir2;
        update();
    }

    public Vector3f getCircle1Center() {
        return circle1Center;
    }

    public Vector3f getCircle2Center() {
        return circle2Center;
    }

    public float getTurnRadius() {
        return turnRadius;
    }

    public static double[][] getTangents(double x1, double y1, double r1, double x2, double y2, double r2) {
        double d_sq = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
        if (d_sq <= (r1 - r2) * (r1 - r2)) {
            return new double[0][4];
        }

        double d = Math.sqrt(d_sq);
        double vx = (x2 - x1) / d;
        double vy = (y2 - y1) / d;

        double[][] res = new double[4][4];
        int i = 0;

        // Let A, B be the centers, and C, D be points at which the tangent
        // touches first and second circle, and n be the normal vector to it.
        //
        // We have the system:
        //   n * n = 1          (n is a unit vector)          
        //   C = A + r1 * n
        //   D = B +/- r2 * n
        //   n * CD = 0         (common orthogonality)
        //
        // n * CD = n * (AB +/- r2*n - r1*n) = AB*n - (r1 -/+ r2) = 0,  <=>
        // AB * n = (r1 -/+ r2), <=>
        // v * n = (r1 -/+ r2) / d,  where v = AB/|AB| = AB/d
        // This is a linear equation in unknown vector n.

        for (int sign1 = +1; sign1 >= -1; sign1 -= 2) {
            double c = (r1 - sign1 * r2) / d;

            // Now we're just intersecting a line with a circle: v*n=c, n*n=1

            if (c * c > 1.0) {
                continue;
            }
            double h = Math.sqrt(Math.max(0.0, 1.0 - c * c));

            for (int sign2 = +1; sign2 >= -1; sign2 -= 2) {
                double nx = vx * c - sign2 * h * vy;
                double ny = vy * c + sign2 * h * vx;

                double[] a = res[i++];
                a[0] = x1 + r1 * nx;
                a[1] = y1 + r1 * ny;
                a[2] = x2 + sign1 * r2 * nx;
                a[3] = y2 + sign1 * r2 * ny;
            }
        }

        return Arrays.copyOf(res, i);
    }

    public static float[][] getTangents(float x1, float y1, float r1, float x2, float y2, float r2) {
        float d_sq = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
        if (d_sq <= (r1 - r2) * (r1 - r2)) {
            return new float[0][4];
        }

        float d = FastMath.sqrt(d_sq);
        float vx = (x2 - x1) / d;
        float vy = (y2 - y1) / d;

        float[][] res = new float[4][4];
        int i = 0;

        // Let A, B be the centers, and C, D be points at which the tangent
        // touches first and second circle, and n be the normal vector to it.
        //
        // We have the system:
        //   n * n = 1          (n is a unit vector)          
        //   C = A + r1 * n
        //   D = B +/- r2 * n
        //   n * CD = 0         (common orthogonality)
        //
        // n * CD = n * (AB +/- r2*n - r1*n) = AB*n - (r1 -/+ r2) = 0,  <=>
        // AB * n = (r1 -/+ r2), <=>
        // v * n = (r1 -/+ r2) / d,  where v = AB/|AB| = AB/d
        // This is a linear equation in unknown vector n.

        for (int sign1 = +1; sign1 >= -1; sign1 -= 2) {
            float c = (r1 - sign1 * r2) / d;

            // Now we're just intersecting a line with a circle: v*n=c, n*n=1

            if (c * c > 1.0) {
                continue;
            }
            float h = FastMath.sqrt(Math.max(0.0f, 1.0f - c * c));

            for (int sign2 = +1; sign2 >= -1; sign2 -= 2) {
                float nx = vx * c - sign2 * h * vy;
                float ny = vy * c + sign2 * h * vx;

                float[] a = res[i++];
                a[0] = x1 + r1 * nx;
                a[1] = y1 + r1 * ny;
                a[2] = x2 + sign1 * r2 * nx;
                a[3] = y2 + sign1 * r2 * ny;
            }
        }

        return Arrays.copyOf(res, i);
    }

    public float getTotalLength() {
        return this.totalLength;
    }
}