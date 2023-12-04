package hs_kempten.ibrush.objects;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import hs_kempten.ibrush.events.FaceDetectionListener;
import hs_kempten.ibrush.models.FaceModel;
import hs_kempten.ibrush.worker.FaceDetectionWorker;

/**
 * Created by Antoine on 17.10.2016.
 */

public class FaceObject implements FaceDetectionListener {
    public static final String TAG = "FACE OBJECT";

    private FaceModel detectedFace;

    private FaceDetectionWorker detectionWorker;
    private boolean detectionThreadRunning;

    /**
     * Konstruktor
     */
    public FaceObject() {
        detectedFace = null;
        detectionWorker = new FaceDetectionWorker(this);
        detectionThreadRunning = false;
    }

    /**
     * Recalculates FaceObject Detection goes easy on Thread
     *
     * @param inputRGBA
     */
    public synchronized void update(final Mat inputRGBA) {
        startFaceDetectionWorker(inputRGBA);
    }

    /**
     * Starts Face Detection Worker
     *
     * @param inputRGBA
     */
    private synchronized void startFaceDetectionWorker(final Mat inputRGBA) {
        if (!detectionThreadRunning) {
            detectionWorker.setData(inputRGBA);
            Log.d(TAG, "Starting Thread");
            new Thread(detectionWorker).start();
            detectionThreadRunning = true;
        }
    }

    /**
     * Draws FaceObject Recognition to Screen
     *
     * @param inputRGBA
     */
    public synchronized void draw(Mat inputRGBA) {
        if (isAvailable()) {
            detectedFace.draw(inputRGBA);
        }
    }

    /**
     * Write FaceObject Recognition to Screen
     *
     * @param inputRGBA
     */
    public synchronized void write(Mat inputRGBA) {
        if (isAvailable()) {
            detectedFace.write(inputRGBA);
        } else {
            Imgproc.putText(inputRGBA, "NO FACE", new Point(5, 20), Core.FONT_ITALIC, 0.7, new Scalar(255, 0, 0, 255), 2);
        }
    }

    /**
     * Checks if a Detected FaceObject is Available
     *
     * @return
     */
    private synchronized boolean isAvailable() {
        return (detectedFace != null);
    }

    /**
     * Returns Current Detected Face
     *
     * @return
     */
    public synchronized FaceModel getFace() {
        return detectedFace;
    }


    @Override
    public synchronized void onFaceDetection(FaceModel face) {
        Log.d(TAG, "Thread Finished");
        detectedFace = face;
        detectionThreadRunning = false;
    }
}
