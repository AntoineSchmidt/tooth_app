package hs_kempten.ibrush.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import java.util.ArrayList;

import hs_kempten.ibrush.R;
import hs_kempten.ibrush.adapters.UsersAdapter;
import hs_kempten.ibrush.database.CleanTable;
import hs_kempten.ibrush.database.UserTable;
import hs_kempten.ibrush.dialogs.ConfirmDialog;
import hs_kempten.ibrush.dialogs.NewUserDialog;
import hs_kempten.ibrush.events.ConfirmListener;
import hs_kempten.ibrush.events.CreateUserClickListener;
import hs_kempten.ibrush.events.UserClickListener;
import hs_kempten.ibrush.models.BrushModel;
import hs_kempten.ibrush.models.UserModel;

import static hs_kempten.ibrush.models.UserModel.Role;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, CreateUserClickListener, UserClickListener {
    public static final String TAG = "MAIN";
    // MEMBERS

    private CoordinatorLayout mRootView;
    private FloatingActionButton mNewUserButton;
    private ProgressBar mUsersLoadingView;
    private LinearLayout mUsersEmptyView;
    private RecyclerView mUsersRecyclerView;
    private UsersAdapter mUsersAdapter;

    // CONSTRUCTORS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the content view
        setContentView(R.layout.activity_main);

        setup();
    }

    // PRIVATE-API

    /**
     * Get all view, setup the RecyclerView and load the users.
     */
    private void setup() {

        // setup toolbar
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // get the views
        mRootView = (CoordinatorLayout) findViewById(R.id.rootView);
        mUsersRecyclerView = (RecyclerView) findViewById(R.id.userRecyclerView);
        mUsersLoadingView = (ProgressBar) findViewById(R.id.userLoadingView);
        mUsersEmptyView = (LinearLayout) findViewById(R.id.userListEmptyView);
        mNewUserButton = (FloatingActionButton) findViewById(R.id.newUserButton);

        // set a layoutmanager to our recyclerview
        mUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set onClickListener to newuser-button
        mNewUserButton.setOnClickListener(this);

        // set an adapter for the list
        mUsersAdapter = new UsersAdapter();
        mUsersAdapter.setOnUserClickListener(this);
        mUsersRecyclerView.setAdapter(mUsersAdapter);

        // load the list
        loadUsersIntoList();
    }

    /**
     * Get the users out of the database and load them asynchronously into the RecyclerView.
     */
    private void loadUsersIntoList() {

        // show that we are loading the users
        showLoadingView();

        // do it asynchronously
        new Thread(new Runnable() {
            @Override
            public void run() {
                // query the users
                final ArrayList<UserModel> users = UserTable.getUsers();

                // use a handler to do an action within the Main-thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // if we have no users so far..
                        if (users.isEmpty()) {
                            // ..show that there are no users yet
                            showEmptyView();
                            return;
                        }

                        // there are existing users!

                        // update the adapter and show the RecyclerView
                        mUsersAdapter.updateUsers(users);
                        showRecyclerView();
                    }
                });
            }
        }).start();
    }

    /**
     * Shows the emptyview only.
     */
    private void showEmptyView() {
        mUsersEmptyView.setVisibility(View.VISIBLE);
        mUsersRecyclerView.setVisibility(View.GONE);
        mUsersLoadingView.setVisibility(View.GONE);
    }

    /**
     * Shows the loadingView only.
     */
    private void showLoadingView() {
        mUsersEmptyView.setVisibility(View.GONE);
        mUsersRecyclerView.setVisibility(View.GONE);
        mUsersLoadingView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the userslist only.
     */
    private void showRecyclerView() {
        mUsersEmptyView.setVisibility(View.GONE);
        mUsersRecyclerView.setVisibility(View.VISIBLE);
        mUsersLoadingView.setVisibility(View.GONE);
    }

    /**
     * Tries to delete the given UserModel by showing a ConfirmDialog to confirm
     * the delete-action.
     *
     * @param user the user we want to delete
     */
    private void deleteUser(final UserModel user) {
        // create a new ConfirmDialog
        ConfirmDialog confirmDialog = new ConfirmDialog(this);
        confirmDialog.setMessage(getString(R.string.confirm_delete_user_message));

        // set a listener to get informed as soon as the user confirmed or declined
        // the action
        confirmDialog.setOnConfirmListener(new ConfirmListener() {
            @Override
            public void onConfirmed() {
                // finally try to delete the user
                if (UserTable.deleteUser(user)) {
                    // success deleting the user
                    // reload the list
                    showSnackbar(getString(R.string.delete_user_success));
                    loadUsersIntoList();
                } else {
                    // something went wrong, errorhandling
                    showSnackbar(getString(R.string.delete_user_error));
                }
            }

            @Override
            public void onDeclined() {
                // nothing
            }
        });

        // show the dialog
        confirmDialog.show();
    }

    /**
     * Helper-method to show a simple message within a SnackBar.
     *
     * @param message the message to show
     */
    private void showSnackbar(String message) {
        Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
    }

    // PUBLIC-API

    @Override
    public void onClick(View v) {
        // gets called whenever mNewUserButton is clicked

        // show a NewUserDialog
        NewUserDialog newUserDialog = new NewUserDialog(this);
        newUserDialog.setOnCreateUserButtonClickListener(this);
        newUserDialog.show();
    }

    public void onVideoClick(View v) {
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateUserButtonClick(NewUserDialog dialog, final String name, final Role role, final float brushlength) {
        // gets called whenever the create-user-button within a NewUserDialog is clicked

        // dismiss the dialog
        dialog.dismiss();

        // insert the user asynchronously to not block the mainthread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // insert the user
                final boolean inserted = UserTable.insertUser(name, role, brushlength);

                // user a handler to do an action within the mainthread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // if inserted successfully..
                        if (inserted) {
                            // ..reload the list
                            loadUsersIntoList();
                            showSnackbar(name + " " + getString(R.string.user_added));
                        } else {
                            // ..show an error
                            showSnackbar(name + " " + getString(R.string.user_insert_error));
                        }
                    }
                });
            }
        }).start();
    }

    // LISTITEM-ACTIONS

    @Override
    public void onUserClick(UserModel user) {
        showSnackbar(user.getName());

        //Set Brush Length
        BrushModel.brushLength = user.getBrushLength();
        Log.d(TAG, "Brushlength " + BrushModel.brushLength);

        //Start CameraActivity
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);

        //Insert new Cleaning Process
        CleanTable.startClean(user);
    }

    @Override
    public void onEditViewClick(View view, final UserModel user) {
        // create a popupmenu to show actions
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.user_listitem_editpopup, popupMenu.getMenu());
        popupMenu.show();

        // receive clickevents for each action
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editUser:
                        break;
                    case R.id.deleteUser:
                        deleteUser(user);
                        break;
                }
                return false;
            }
        });
    }

}
