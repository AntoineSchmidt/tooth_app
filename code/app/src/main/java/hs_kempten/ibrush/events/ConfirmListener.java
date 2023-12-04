package hs_kempten.ibrush.events;

/**
 * Created by Antoine on 16.10.2016.
 */

/**
 * Inteface used to get informed whether the user clicked the
 * confirm- (positive) or decline- (negative) button.
 */
public interface ConfirmListener {
    /**
     * The used clicked the confirm-button (positive).
     */
    void onConfirmed();

    /**
     * The user clicked the decline-button (negative).
     */
    void onDeclined();
}
