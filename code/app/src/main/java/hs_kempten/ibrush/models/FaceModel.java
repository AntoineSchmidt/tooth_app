package hs_kempten.ibrush.models;

/**
 * Created by Antoine on 18.10.2016.
 */

import android.graphics.PointF;
import android.media.FaceDetector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import hs_kempten.ibrush.utils.Utils;

/**
 * Represents the RL Face Parameter
 */
public class FaceModel {

    //Biologische Durchschnitts Werte
    public static final float eyeDistance = 60;
    public static final float eyeToothDistance = 65;

    //Anroid Face Detection Results
    public final FaceDetector.Face detectedFace;
    private PointF mouthMidPoint;
    private PointF eyeMidPoint;

    //Calculated Translate Factor
    private float toImageFactor;

    public FaceModel(FaceDetector.Face detected) {
        this.detectedFace = detected;
        mouthMidPoint = null;
        eyeMidPoint = null;
        toImageFactor = 0;
    }

    /**
     * Returns FaceObject Mid Point
     *
     * @return
     */
    public PointF getEyeMidPoint() {
        if (eyeMidPoint == null) {
            eyeMidPoint = new PointF();
            detectedFace.getMidPoint(eyeMidPoint);
        }
        return eyeMidPoint;
    }

    /**
     * Returns the Translation Factor
     *
     * @return
     */
    public float getToImageFactor() {
        return toImageFactor;
    }

    /**
     * Sets the Translation Factor
     *
     * @param toImageFactor
     */
    public void setToImageFactor(float toImageFactor) {
        this.toImageFactor = toImageFactor;
    }

    /**
     * Returns the Calculated Mouth Mid Point
     *
     * @return
     */
    public PointF getMouthMidPoint() {
        return mouthMidPoint;
    }

    /**
     * Sets the Calculated Mouth Mid Point
     *
     * @param midPoint
     */
    public void setMouthMidPoint(PointF midPoint) {
        mouthMidPoint = midPoint;
    }

    /**
     * Draws Face To Mat
     *
     * @param rgba
     */
    public void draw(Mat rgba) {
        Imgproc.circle(rgba, Utils.getAsPoint(getEyeMidPoint()), 5, new Scalar(0, 255, 0, 255), 5);
        Imgproc.circle(rgba, Utils.getAsPoint(getMouthMidPoint()), 5, new Scalar(0, 255, 0, 255), 5);
    }

    /**
     * Writes Face To Mat
     *
     * @param rgba
     */
    public void write(Mat rgba) {
        Imgproc.putText(rgba, toString(), new Point(5, 20), Core.FONT_ITALIC, 0.7, new Scalar(0, 255, 0, 255), 2);
    }

    @Override
    public String toString() {
        return "FACE";
    }
}
