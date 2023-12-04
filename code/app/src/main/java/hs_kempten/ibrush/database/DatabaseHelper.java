package hs_kempten.ibrush.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Antoine Schmidt
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // CONSTANTS

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    // MEMBERS

    private static DatabaseHelper mDatabaseHelper = null;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * Simple constructor.
     *
     * @param pContext we need this
     */
    private DatabaseHelper(Context pContext) {
        super(pContext, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = pContext;
    }

    // PUBLIC-API

    /**
     * Gets the one-and-only singletoninstance of this class.
     *
     * @return the DatabaseHelper-singleton
     */
    public static DatabaseHelper getInstance() {
        return mDatabaseHelper;
    }

    /**
     * Creates the Database Instance to Avoid needing Context each Time
     *
     * @param pContext
     */
    public static void createInstance(Context pContext) {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(pContext);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // create the data-tables
        database.execSQL(UserTable.CREATE_TABLE);
        database.execSQL(CleanTable.CREATE_TABLE);
        mDatabase = database;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // empty
    }
}
