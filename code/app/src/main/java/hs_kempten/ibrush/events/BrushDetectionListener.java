package hs_kempten.ibrush.events;

/**
 * Created by Antoine Schmidt
 */

import hs_kempten.ibrush.models.BrushModel;

/**
 * Interface to listen to the BrushDetectionWorker
 */
public interface BrushDetectionListener {

    /**
     * Gets called when BrushModel found
     *
     * @param detected
     */
    void onBrushDetection(BrushModel detected);
}
