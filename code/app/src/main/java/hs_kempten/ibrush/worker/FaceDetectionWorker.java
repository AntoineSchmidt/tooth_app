package hs_kempten.ibrush.worker;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import hs_kempten.ibrush.events.FaceDetectionListener;
import hs_kempten.ibrush.models.FaceModel;

/**
 * Created by Antoine Schmidt
 */
public class FaceDetectionWorker implements Runnable {
    public static final String TAG = "FACE DETECTION";

    private final FaceDetectionListener ear;
    private Mat inputRGBA;
    private Bitmap tmpBitmap;

    /**
     * Konstruktor
     *
     * @param ear
     */
    public FaceDetectionWorker(FaceDetectionListener ear) {
        this.ear = ear;
        inputRGBA = null;
        tmpBitmap = null;
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

    public FaceModel process() {
        Log.d(TAG, "Started");

        //No Data
        if (inputRGBA == null) {
            Log.d(TAG, "Data Null");
            return null;
        }

        //Create Bitmap Data Hold only Once for speed
        if (tmpBitmap == null) {
            //Same Sizes in on Run
            tmpBitmap = Bitmap.createBitmap(inputRGBA.width(), inputRGBA.height(), Bitmap.Config.RGB_565);
        }

        //Assign Mat-Data to Bitmap
        try {
            // convert the rgbaMAT to a bitmap
            Utils.matToBitmap(inputRGBA, tmpBitmap);
        } catch (Exception e) {
            Log.d(TAG, "Input RGBA has different Size than the Previous one (FaceDetectionWorker)");
            tmpBitmap = Bitmap.createBitmap(inputRGBA.width(), inputRGBA.height(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(inputRGBA, tmpBitmap);
        }

        // setup everything for facedetection
        FaceDetector.Face[] faces = new FaceDetector.Face[1];
        FaceDetector faceDetector = new FaceDetector(inputRGBA.width(), inputRGBA.height(), 1);

        //Run FaceDetection
        if (faceDetector.findFaces(tmpBitmap, faces) != 0) {
            final FaceModel detectedFace = new FaceModel(faces[0]);
            float factor = detectedFace.detectedFace.eyesDistance() / FaceModel.eyeDistance;
            detectedFace.setToImageFactor(factor);
            float distance = FaceModel.eyeToothDistance * factor;
            PointF mouthMid = new PointF();
            mouthMid.set(detectedFace.getEyeMidPoint());
            mouthMid.set(mouthMid.x, mouthMid.y + distance);
            detectedFace.setMouthMidPoint(mouthMid);

            return detectedFace;
        } else {
            //No Face
            return null;
        }
    }

    @Override
    public void run() {
        answer(process());
    }

    /**
     * Returns Result over Message Looper
     *
     * @param detected
     */
    private void answer(final FaceModel detected) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ear.onFaceDetection(detected);
            }
        });
    }
}
