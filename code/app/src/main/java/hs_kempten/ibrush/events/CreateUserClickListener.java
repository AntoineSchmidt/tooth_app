package hs_kempten.ibrush.events;

import hs_kempten.ibrush.dialogs.NewUserDialog;
import hs_kempten.ibrush.models.UserModel.Role;

/**
 * Created by Antoine on 16.10.2016.
 */

/**
 * Interface used to get notified whenever the create-user button has been clicked.
 */
public interface CreateUserClickListener {
    /**
     * Called when the create-user button has been clicked.
     *
     * @param dialog      the dialog
     * @param name        the entered name
     * @param role        the checked role
     * @param brushlength the entered brushlength
     */
    void onCreateUserButtonClick(NewUserDialog dialog, String name, Role role, float brushlength);
}
