package hs_kempten.ibrush.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import hs_kempten.ibrush.R;
import hs_kempten.ibrush.animations.ErrorAnimation;
import hs_kempten.ibrush.events.CreateUserClickListener;
import hs_kempten.ibrush.models.BrushModel;
import hs_kempten.ibrush.models.UserModel.Role;


/**
 * Created by Antoine Schmidt
 */
public class NewUserDialog extends AlertDialog implements DialogInterface.OnShowListener {

    // MEMBERS

    private CreateUserClickListener mOnCreateUserButtonClickListener;
    private EditText mUserNameEditText;
    private RadioGroup mUserRoleRadioGroup;
    private EditText mBrushLength;

    // CONSTRUCTORS

    /**
     * Creates a simple NewUserDialog.
     *
     * @param context we need this
     */
    public NewUserDialog(Context context) {
        super(context);
        setup();
    }

    // PRIVATE API

    /**
     * Inflates the content-view, sets the title and creates a positive-button.
     */
    private void setup() {
        // inflate the view
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View content = layoutInflater.inflate(R.layout.dialog_new_user, null);
        setView(content);

        // set title
        setTitle(R.string.new_user);

        // get notified when this dialog is shown
        setOnShowListener(this);

        // get the views within the dialog
        mUserNameEditText = (EditText) content.findViewById(R.id.nameEditText);
        mUserRoleRadioGroup = (RadioGroup) content.findViewById(R.id.roleRadioGroup);
        mBrushLength = (EditText) content.findViewById(R.id.editText);

        // just create a button here, but don't set a real clicklistener. The clicklistener
        // will be overriden when the dialog is shown. This is a needed workaround.
        setButton(BUTTON_POSITIVE, getContext().getResources().getString(R.string.create_user), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // empty.
            }
        });
    }

    // PUBLIC-API

    /**
     * Sets a listener to get notified whenever the create-user button has been clicked.
     *
     * @param listener the listener to notify
     */
    public void setOnCreateUserButtonClickListener(CreateUserClickListener listener) {
        mOnCreateUserButtonClickListener = listener;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        // set the correct clicklistener
        getButton(BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean filledOut = true;

                // check for proper name
                if (mUserNameEditText.getText().toString().isEmpty()) {
                    filledOut = false;
                    ErrorAnimation.animate(mUserNameEditText);
                }

                // check for selected role
                if (mUserRoleRadioGroup.getCheckedRadioButtonId() == -1) {
                    filledOut = false;
                    ErrorAnimation.animate(mUserRoleRadioGroup);
                }

                // check if everything is filled
                if (!filledOut) {
                    long pattern[] = {0, 100, 50, 100};
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(pattern, -1);
                    return;
                }

                // notify the listener
                if (mOnCreateUserButtonClickListener != null) {
                    String name = mUserNameEditText.getText().toString();
                    Role role;

                    if (mUserRoleRadioGroup.getCheckedRadioButtonId() == R.id.normalRadioButton) {
                        role = Role.Normal;
                    } else {
                        role = Role.Observer;
                    }

                    float brushLength;
                    try {
                        brushLength = Float.parseFloat(mBrushLength.getText().toString());
                    } catch (Exception e) {
                        //Bad or No Input setting Default
                        brushLength = BrushModel.brushLengthDefault;
                    }
                    mOnCreateUserButtonClickListener.onCreateUserButtonClick(NewUserDialog.this, name, role, brushLength);
                }
            }
        });
    }
}
