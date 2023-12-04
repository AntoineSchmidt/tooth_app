package hs_kempten.ibrush.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;

import hs_kempten.ibrush.models.BrushModel;
import hs_kempten.ibrush.models.UserModel;

/**
 * Created by Antoine Schmidt
 */

public class UserTable {

    // CONSTANTS

    private static final String TABLENAME = "USERS";
    private static final String ID = "ID";
    private static final String NAME = "NAME";
    private static final String ROLE = "ROLE";
    private static final String BRUSHLENGTH = "BRUSHLENGTH";
    public static final String CREATE_TABLE
            = "CREATE TABLE IF NOT EXISTS "
            + TABLENAME
            + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME + " VARCHAR(100) UNIQUE NOT NULL,"
            + ROLE + " INTEGER NOT NULL,"
            + BRUSHLENGTH + " INTEGER NOT NULL DEFAULT " + BrushModel.brushLengthDefault
            + ");";

    private UserTable() {
        //Private Konstruktor
    }

    /**
     * Queries the database for all existing users.
     *
     * @return a list with all users
     */
    public final static ArrayList<UserModel> getUsers() {
        // query
        Cursor cursor = DatabaseHelper.getInstance().getReadableDatabase().rawQuery("SELECT * FROM " + TABLENAME, null);

        ArrayList<UserModel> users = new ArrayList<UserModel>();

        // loop the cursor and add users one by one to the list
        while (cursor.moveToNext()) {
            int userid = cursor.getInt(0);
            String name = cursor.getString(1);
            int roleId = cursor.getInt(2);
            float brushLength = cursor.getFloat(3);
            UserModel.Role role = UserModel.Role.getForId(roleId);

            users.add(new UserModel(userid, name, role, brushLength));
        }

        // reverse the list to have the last added user on top
        Collections.reverse(users);
        return users;
    }

    /**
     * Inserts a user with the given name and role.
     *
     * @param name        the user's name
     * @param role        the user's role
     * @param brushlength the users brushlength
     * @return true if insertion succeeded, false otherwise
     */
    public final static boolean insertUser(String name, UserModel.Role role, float brushlength) {
        // create values to insert
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(ROLE, role.getId());
        values.put(BRUSHLENGTH, brushlength);

        // insert!
        long row = DatabaseHelper.getInstance().getWritableDatabase().insert(TABLENAME, null, values);

        // if row == -1, something went wrong and return false
        return !(row == -1);
    }

    /**
     * Deletes a given user.
     *
     * @param user the user to delete
     * @return true if deleting succeeded, false otherwise
     */
    public final static boolean deleteUser(UserModel user) {
        int value = DatabaseHelper.getInstance().getWritableDatabase().delete(TABLENAME, ID + "=" + user.getId(), null);
        return (value == 1);
    }
}
