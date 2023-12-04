package hs_kempten.ibrush.events;

import org.opencv.core.Mat;

import hs_kempten.ibrush.enums.ColorEnum;
import hs_kempten.ibrush.worker.ColorDetectionWorker;

/**
 * Created by Antoine Schmidt
 */

/**
 * Interface to get notified whenever color has been detected.
 */
public interface ColorDetectionListener {

    /**
     * Called as soon as color has been detected.
     *
     * @param color  the color that has been detected
     * @param points all points detected with searched color
     */
    void onColorDetection(ColorDetectionWorker me, ColorEnum color, Mat points);
}
