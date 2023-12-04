package hs_kempten.ibrush.database;

import android.content.ContentValues;
import android.util.Log;

import hs_kempten.ibrush.models.UserModel;

/**
 * Created by Antoine Schmidt
 */

public class CleanTable {
    public static final String TAG = "Clean Database";

    // CONSTANTS

    private static final String TABLENAME = "CLEANS";
    private static final String ID = "ID";
    private static final String USER = "USER";
    private static final String START = "START";
    private static final String FINISH = "FINISH";
    public static final String CREATE_TABLE
            = "CREATE TABLE IF NOT EXISTS "
            + TABLENAME
            + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + USER + " INTEGER NOT NULL, "
            + START + " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
            + FINISH + " DATETIME DEFAULT NULL, "
            + "FOREIGN KEY (USER) REFERENCES USERS(ID));";

    //Holds Current Cleaning Process
    private static long cleanProcess = -1;

    private CleanTable() {
        //Private Konstruktor
    }

    /**
     * Start a new Cleaning Process for given User
     *
     * @param me the user
     * @return success
     */
    public final static boolean startClean(UserModel me) {
        Log.d(TAG, "Start Clean Called");

        // create values to insert
        ContentValues values = new ContentValues();
        values.put(USER, me.getId());

        //insert
        cleanProcess = DatabaseHelper.getInstance().getWritableDatabase().insert(TABLENAME, null, values);

        Log.d(TAG, "Starting Clean " + cleanProcess);

        //return success
        return (cleanProcess > -1);
    }

    /**
     * Finishes Cleaning Process
     *
     * @return success
     */
    public final static boolean finishClean() {
        Log.d(TAG, "Finish Clean Called");

        boolean result = false;
        if (cleanProcess > -1) {
            Log.d(TAG, "Finishing Clean");

            // create values for update
            ContentValues values = new ContentValues();
            values.put(FINISH, "datetime()");

            //update
            result = (DatabaseHelper.getInstance().getWritableDatabase().update(TABLENAME, values, ID + " = ?", new String[]{String.valueOf(cleanProcess)}) == 1);

            //reset id
            cleanProcess = -1;
        }
        return result;
    }
}
