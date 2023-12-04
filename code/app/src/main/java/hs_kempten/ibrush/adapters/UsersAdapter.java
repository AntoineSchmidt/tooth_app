package hs_kempten.ibrush.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hs_kempten.ibrush.R;
import hs_kempten.ibrush.events.UserClickListener;
import hs_kempten.ibrush.models.UserModel;

/**
 * Created by Antoine Schmidt
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    // MEMBERS

    private ArrayList<UserModel> mUsers;
    private UserClickListener mOnUserClickListener;

    // CONSTRUCTORS

    /**
     * Creates a new UsersAdapter.
     */
    public UsersAdapter() {
        mUsers = new ArrayList<>();
    }

    // ADAPTER

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get the LayoutInflater to create new views
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        // create a listitem-view
        View view = layoutInflater.inflate(R.layout.listitem_user, parent, false);

        // create a new ViewHolder with the currently created view
        UserViewHolder userViewHolder = new UserViewHolder(view);
        return userViewHolder;
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        // get the current user
        UserModel currentUser = mUsers.get(position);

        // update the holder with this user
        holder.setUser(currentUser);
    }

    // PUBLIC-API

    /**
     * Sets the given list of users as the datasource and updates the connected RecyclerView.
     *
     * @param users a list of users to show within the RecyclerView
     */
    public void updateUsers(List<UserModel> users) {
        // clear the current users
        mUsers.clear();

        // add all users of the given list
        mUsers.addAll(users);

        // tell the adapter that the data has changed and the view needs to be updated
        notifyDataSetChanged();
    }

    /**
     * Sets a listener to get callbacks whenever a specific view within
     * the listitem has been clicked.
     *
     * @param listener your listener
     */
    public void setOnUserClickListener(UserClickListener listener) {
        mOnUserClickListener = listener;
    }

    /**
     * ViewHolder used to show a user.
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        // MEMBERS

        private LinearLayout mRoot;
        private ImageView mUserImageView;
        private TextView mUserNameTextView;
        private TextView mUserRoleTextView;
        private ImageView mEditView;

        // CONSTRUCTOR

        /**
         * Simple constructor.
         *
         * @param view the view to hold
         */
        public UserViewHolder(View view) {
            super(view);
            mRoot = (LinearLayout) view.findViewById(R.id.userRoot);
            mUserImageView = (ImageView) view.findViewById(R.id.userIconImageView);
            mUserNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
            mUserRoleTextView = (TextView) view.findViewById(R.id.userRoleTextView);
            mEditView = (ImageView) view.findViewById(R.id.userEditView);
        }

        // PUBLIC-API

        /**
         * Updates the holden view with the values of the given user.
         *
         * @param user the user to show within this ViewHolder
         */
        public void setUser(final UserModel user) {
            // update name & role
            mUserNameTextView.setText(user.getName());
            mUserRoleTextView.setText(user.getRole().asText(itemView.getContext()));

            // set an OnCLickListener on the whole listitem
            mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // pass trough the clickevent to the OnUserClickListener
                    if (mOnUserClickListener != null) {
                        mOnUserClickListener.onUserClick(user);
                    }
                }
            });

            // set an OnClickListener on the editview
            mEditView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // pass trough the clickevent to the OnUserClickListener
                    if (mOnUserClickListener != null) {
                        mOnUserClickListener.onEditViewClick(view, user);
                    }
                }
            });
        }

    }

}
