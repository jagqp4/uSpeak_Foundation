package com.jordangunter.second_test_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Jordan Gunter on 1/10/2018.
 * We create a custom dialog for user sign in by extending DialogFragment
 * and creating an AlertDialog in the onCreateDialog() callback method
 */

public class LoginDialogFragment extends DialogFragment {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it/
     */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    private String username;
    private String password;

    // Use this instance of the interface to deliver action events
    LoginDialogFragment.NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (LoginDialogFragment.NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //since we're using our own xml file for the dialogs style we need
        //a few extra steps to import our custom layout
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout


        final View view = inflater.inflate(R.layout.login_dialog_fragment, null);
        builder.setView(view);
        final TextView userText = view.findViewById(R.id.username);
        final TextView passText = view.findViewById(R.id.password);

        //setContentView(R.layout.login_dialog_fragment);

        builder.setMessage(R.string.dialog_name)
                .setPositiveButton(R.string.sign_in, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        username = userText.getText().toString();
                        password = passText.getText().toString();
                        listener.onDialogPositiveClick(LoginDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoginDialogFragment.this.getDialog().cancel();
                        listener.onDialogNegativeClick(LoginDialogFragment.this);
                    }
                });
        return builder.create();
    }

    String getUsername(){
        return username;
    }

    String getPassword(){
        return password;
    }


}
