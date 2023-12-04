package hs_kempten.ibrush.models;

import android.content.Context;
import android.support.annotation.IntRange;

import hs_kempten.ibrush.R;

/**
 * Created by Antoine Schmidt
 */
public class UserModel {

    private int mId;

    // MEMBERS
    private String mName;
    private Role mRole;
    private float mBrushLength;

    /**
     * Simple constructor
     *
     * @param id   the id of this user within the database
     * @param name the name of this user
     * @param role the role of this user
     */
    public UserModel(int id, String name, Role role, float length) {
        mId = id;
        mName = name;
        mRole = role;
        mBrushLength = length;
    }

    public int getId() {
        return mId;
    }

    // PUBLIC-API

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public float getBrushLength() {
        return mBrushLength;
    }

    public Role getRole() {
        return mRole;
    }

    public void setRole(Role role) {
        mRole = role;
    }

    /**
     * Enum used to define the roles a user may have.
     */
    public enum Role {
        Normal(1),
        Observer(2);

        int id;

        /**
         * Simple constructor. We need an id to map
         * the enum to an int, so that we can save it into
         * the database.
         *
         * @param id the id
         */
        Role(int id) {
            this.id = id;
        }

        /**
         * Gets the role for a given id.
         *
         * @param id the id to get the role for
         * @return the role with the given id
         */
        public static Role getForId(@IntRange(from = 1, to = 2) int id) {
            if (id == 1) {
                return Role.Normal;
            } else {
                return Role.Observer;
            }
        }

        /**
         * Gets the id of the role.
         *
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * Returns a string, describing the role.
         *
         * @param context we need this
         * @return the role as a string
         */
        public String asText(Context context) {
            if (id == Role.Normal.id) {
                return context.getResources().getString(R.string.normal_user);
            } else if (id == Role.Observer.id) {
                return context.getResources().getString(R.string.observer_user);
            }
            return "";
        }

    }

}
