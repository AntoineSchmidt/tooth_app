package hs_kempten.ibrush.events;

import hs_kempten.ibrush.models.FaceModel;

/**
 * Created by Antoine on 16.10.2016.
 */

/**
 * Interface to get notified about facedetection.
 */
public interface FaceDetectionListener {
    /**
     * Called as soon as a FaceObject was found or null when not
     *
     * @param face the detected face
     */
    void onFaceDetection(FaceModel face);
}
