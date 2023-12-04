package hs_kempten.ibrush.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import hs_kempten.ibrush.R;
import hs_kempten.ibrush.events.ConfirmListener;

/**
 * Created by Antoine Schmidt
 */
public class ConfirmDialog extends AlertDialog {

    // MEMBERS

    private ConfirmListener mOnConfirmListener;

    /**
     * Simple constructor.
     *
     * @param context we need this
     */
    public ConfirmDialog(Context context) {
        super(context);
        setup();
    }

    // PRIVATE-API

    /**
     * Sets the title and creates two buttons to confirm &
     * decline the action.
     */
    private void setup() {
        setTitle(getContext().getString(R.string.confirm));

        // create a button to confirm the action
        setButton(BUTTON_POSITIVE, getContext().getString(R.string.yes), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // pass the clickevent to a may existent OnConfirmListener
                if (mOnConfirmListener != null) {
                    mOnConfirmListener.onConfirmed();
                }
            }
        });

        // create a button to decline the action
        setButton(BUTTON_NEGATIVE, getContext().getString(R.string.no), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // pass the clickevent to a may existent OnConfirmListener
                if (mOnConfirmListener != null) {
                    mOnConfirmListener.onDeclined();
                }
            }
        });
    }

    // PUBLIC-API

    /**
     * Sets a listener to get notified whenever the confirm- (positive) or
     * decline- (negative) button has been clicked.
     *
     * @param listener the listener to notify
     */
    public void setOnConfirmListener(ConfirmListener listener) {
        mOnConfirmListener = listener;
    }

}
