package hs_kempten.ibrush.utils;

import android.graphics.PointF;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 * Created by Antoine on 26.10.2016.
 */

public class Utils {

    /**
     * Static Class
     */
    private Utils() {
    }

    /**
     * Returns Integer Point from Float Point
     *
     * @param me
     * @return
     */
    public static Point getAsPoint(final PointF me) {
        return new Point((int) me.x, (int) me.y);
    }

    /**
     * Returns Middle Point of Mat
     *
     * @param me
     * @return
     */
    public static PointF getMid(final Mat me) {
        Moments moments = Imgproc.moments(me, true);
        float center_x = (float) (moments.m10 / moments.m00);
        float center_y = (float) (moments.m01 / moments.m00);
        if (!Float.isNaN(center_x) && !Float.isNaN(center_y)) {
            return new PointF(center_x, center_y);
        } else {
            return null;
        }
    }

    /**
     * Returns Degree the Points build from 0 to 360 with Point a as Center
     *
     * @param a
     * @param b
     * @return
     */
    public static float getDegree(final PointF a, final PointF b) {
        float lengthX = a.x - b.x;
        float lengthY = a.y - b.y;
        if (lengthX == 0) {
            if (lengthY == 0) {
                return Float.NaN;
            }
            if (lengthY > 0) {
                return 90;
            }
            if (lengthY < 0) {
                return 270;
            }
        }
        float degrees = (float) Math.abs(Math.toDegrees(Math.atan(lengthY / lengthX)));
        if (lengthX < 0) {
            if (lengthY >= 0) return degrees;
            else return (360 - degrees);
        } else {
            if (lengthY >= 0) return (180 - degrees);
            else return (180 + degrees);
        }
    }

    /**
     * Returns Distance of two Points
     *
     * @param a
     * @param b
     * @return
     */
    public static float getDistance(final PointF a, final PointF b) {
        float lengthX = a.x - b.x;
        float lengthY = a.y - b.y;
        return (float) Math.sqrt(lengthX * lengthX + lengthY * lengthY);
    }

}
