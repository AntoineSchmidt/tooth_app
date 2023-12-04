package hs_kempten.ibrush.worker;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import hs_kempten.ibrush.enums.ColorEnum;
import hs_kempten.ibrush.events.ColorDetectionListener;

/**
 * Created by Antoine Schmidt
 */
public class ColorDetectionWorker implements Runnable {
    public static final String TAG = "COLOR DETECTION";

    private final ColorEnum colorToDetect;
    private final ColorDetectionListener ear;
    private final int convertMode;
    private Mat inputRGBA;

    /**
     * Konstruktor
     *
     * @param ear
     */
    public ColorDetectionWorker(final ColorDetectionListener ear, final ColorEnum colorToDetect, final int convertMode) {
        this.ear = ear;
        this.colorToDetect = colorToDetect;
        this.convertMode = convertMode;
    }

    /**
     * Sets Image Data
     *
     * @param inputRGBA
     */
    public void setData(final Mat inputRGBA) {
        //No .clone() for speed, inputRGBA read only!
        this.inputRGBA = inputRGBA;
    }

    public Mat process() {
        Log.d(TAG, "Started");

        // convert to hsv-values
        Mat hsvMat = new Mat();
        Imgproc.cvtColor(inputRGBA, hsvMat, convertMode, 3);

        // setup a new mat to store the pixels that match the given colorinterval
        Mat detectedColorMat = new Mat();

        //Search every Range in case of Color Red there are two
        for (int i = 0; i < colorToDetect.lowerLimit.length; i++) {
            //New Temp Mat for Single Result
            Mat tmpColorMat = new Mat();

            //Check hsvMat for the given color and save it in detectedColorMat
            Core.inRange(hsvMat, colorToDetect.lowerLimit[i], colorToDetect.upperLimit[i], tmpColorMat);

            if (i == 0) {
                //Just set Mat
                detectedColorMat = tmpColorMat;
            } else {
                //Join Temporary Mat
                Core.add(detectedColorMat, tmpColorMat, detectedColorMat);
            }
        }

        //Remove Noise (Single spreaded Pixels)
        Imgproc.erode(detectedColorMat, detectedColorMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));

        return detectedColorMat;
    }


    @Override
    public void run() {
        answer(this, process());
    }

    /**
     * Returns Answer over Message Loop
     *
     * @param colorPoints
     */
    private void answer(final ColorDetectionWorker me, final Mat colorPoints) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ear.onColorDetection(me, colorToDetect, colorPoints);
            }
        });
    }
}
