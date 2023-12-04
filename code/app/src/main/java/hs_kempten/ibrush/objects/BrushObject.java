package hs_kempten.ibrush.objects;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hs_kempten.ibrush.enums.ColorEnum;
import hs_kempten.ibrush.events.BrushDetectionListener;
import hs_kempten.ibrush.events.ColorDetectionListener;
import hs_kempten.ibrush.models.BrushModel;
import hs_kempten.ibrush.worker.BrushDetectionWorker;
import hs_kempten.ibrush.worker.ColorDetectionWorker;

/**
 * Created by Antoine on 17.10.2016.
 */

public class BrushObject implements ColorDetectionListener, BrushDetectionListener {
    public static final String TAG = "BRUSH OBJECT";

    private FaceObject face;

    private BrushModel brushDetected;
    private BrushDetectionWorker brushDetectionWorker;
    private boolean brushDetectionThreadRunning;

    private Map<ColorEnum, Mat> colorsDetected;
    private Map<ColorEnum, Mat> colorsDetectedCompleteSet;
    private List<ColorDetectionWorker> colorDetectionWorkers;
    private List<ColorDetectionWorker> colorDetectionWorkerThreads;
    private boolean colorDetectionThreadsRunning;

    /**
     * Konstruktor
     *
     * @param face
     */
    public BrushObject(final FaceObject face) {
        this.face = face;
        brushDetected = null;
        brushDetectionWorker = new BrushDetectionWorker(this);
        brushDetectionThreadRunning = false;
        colorDetectionWorkers = new java.util.ArrayList<>();
        colorDetectionWorkerThreads = new java.util.ArrayList<>();
        colorDetectionThreadsRunning = false;
        colorsDetected = new HashMap<>();
        colorsDetectedCompleteSet = null;
        prepareColorDetectionWorkers();
    }

    private void prepareColorDetectionWorkers() {
        colorDetectionWorkers.add(new ColorDetectionWorker(this, BrushModel.colorBeginTop, Imgproc.COLOR_RGB2HSV));
        colorDetectionWorkers.add(new ColorDetectionWorker(this, BrushModel.colorBeginBottom, Imgproc.COLOR_RGB2HSV));
        colorDetectionWorkers.add(new ColorDetectionWorker(this, BrushModel.colorEndTop, Imgproc.COLOR_RGB2HSV));
        colorDetectionWorkers.add(new ColorDetectionWorker(this, BrushModel.colorEndBottom, Imgproc.COLOR_RGB2HSV));
    }

    /**
     * Recalculates ColorEnum Detection goes easy on Thread
     *
     * @param inputRGBA
     */
    public synchronized void update(final Mat inputRGBA) {
        startColorDetectionWorker(inputRGBA);
    }

    /**
     * Starts Color Detection Worker
     *
     * @param inputRGBA
     */
    private synchronized void startColorDetectionWorker(final Mat inputRGBA) {
        if (!colorDetectionThreadsRunning) {
            for (ColorDetectionWorker worker : colorDetectionWorkers) {
                colorDetectionWorkerThreads.add(worker);
                worker.setData(inputRGBA);
                new Thread(worker).start();
            }
            colorDetectionThreadsRunning = true;
        }
    }

    /**
     * Starts Brush Detection Worker, needs to be Public for the Invoker
     */
    public synchronized void startBrushDetectionWorker() {
        if (!brushDetectionThreadRunning) {
            brushDetectionWorker.setData(colorsDetectedCompleteSet, face.getFace());
            new Thread(brushDetectionWorker).start();
            brushDetectionThreadRunning = true;
        }
    }

    /**
     * Invoke Brush Detection Worker
     */
    private void invokeStartBrushDetectionWorker() {
        final BrushObject me = this;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                me.startBrushDetectionWorker();
            }
        });
    }

    /**
     * Checks if a Detected BrushModel is Available
     *
     * @return
     */
    private synchronized boolean isAvailable() {
        return (brushDetected != null);
    }

    /**
     * Draws BrushModel Recognition to Screen
     *
     * @param inputRGBA
     */
    public synchronized void draw(Mat inputRGBA) {
        if (isAvailable()) {
            brushDetected.draw(inputRGBA);
        }
    }

    /**
     * Write BrushModel Recognition to Screen
     *
     * @param inputRGBA
     */
    public synchronized void write(Mat inputRGBA) {
        if (isAvailable()) {
            brushDetected.write(inputRGBA);
        } else {
            Imgproc.putText(inputRGBA, "NO BRUSH", new Point(5, 40), Core.FONT_ITALIC, 0.7, new Scalar(255, 0, 0, 255), 2);
        }
    }

    /**
     * Clones HashMap .clone() is protected
     */
    private Map<ColorEnum, Mat> clone(Map<ColorEnum, Mat> me) {
        Map<ColorEnum, Mat> tmp = new HashMap<>();
        for (Map.Entry<ColorEnum, Mat> entry : colorsDetected.entrySet()) {
            tmp.put(entry.getKey(), entry.getValue());
        }
        return tmp;
    }

    @Override
    public synchronized void onColorDetection(ColorDetectionWorker me, ColorEnum color, Mat points) {
        colorsDetected.put(color, points);
        colorDetectionWorkerThreads.remove(me);
        if (colorDetectionWorkerThreads.isEmpty()) {
            colorsDetectedCompleteSet = clone(colorsDetected);
            colorDetectionThreadsRunning = false;
            Log.d(TAG, "Color Detection Finished");
            invokeStartBrushDetectionWorker();
        }
    }

    @Override
    public synchronized void onBrushDetection(BrushModel detected) {
        Log.d(TAG, "Brush Detection Finished");
        brushDetected = detected;
        brushDetectionThreadRunning = false;
    }
}
