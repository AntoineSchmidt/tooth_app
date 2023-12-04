package hs_kempten.ibrush.events;

import android.view.View;

import hs_kempten.ibrush.models.UserModel;

/**
 * Created by Antoine on 16.10.2016.
 */

/**
 * Interface used to get notified whenever a specific view within
 * a listitem has been clicked.
 */
public interface UserClickListener {
    /**
     * The rootview of the listitem has been clicked.
     *
     * @param user the bound user
     */
    void onUserClick(UserModel user);

    /**
     * The editview of the listitem has been clicked.
     *
     * @param view the clicked view
     * @param user the bound user
     */
    void onEditViewClick(View view, UserModel user);
}
