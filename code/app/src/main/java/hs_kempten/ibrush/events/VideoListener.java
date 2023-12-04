package hs_kempten.ibrush.events;

/**
 * Created by Antoine on 31.10.2016.
 */

public interface VideoListener {

    /**
     * Gets called when Progress where made
     */
    void onProgress(int prog);

    /**
     * Gets called when VideoWorker Thread has Finished
     */
    void onProcessingFinished();
}
