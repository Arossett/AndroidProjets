package mycompany.thistest.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by trsq9010 on 04/12/2014.
 */
public class TypesChoice extends DialogFragment{
    ArrayList<String> mSelectedItems;
    AlertDialog alertDialog;
    //to identify what kind of choices is made (transport or places for example)
    int type_id;
    AlertDialog.Builder builder;
    boolean isClosable;

    public void enablePositiveButton(){
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);

    }

    public void disablePositiveButton(){
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        mSelectedItems = new ArrayList();  // Where we track the selected items
        type_id = getArguments().getInt("search_id", 0);
        final String[] types = getArguments().getStringArray("types");
        isClosable = getArguments().getBoolean("closable", true);
        boolean isMultipleChoice = getArguments().getBoolean("isMultipleChoice", false);
        builder.setTitle("Types");
        if(isMultipleChoice) {
            // Specify the list array, the items to be selected by default (null for none),
            // and the listener through which to receive callbacks when items are selected
            builder.setMultiChoiceItems(types, null,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which,
                                            boolean isChecked) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                mSelectedItems.add(types[which]);
                            } else if (mSelectedItems.contains(types[which])) {
                                // Else, if the item is already in the array, remove it
                                mSelectedItems.remove(types[which]);
                            }
                            if (mSelectedItems.size() == 1) {
                                enablePositiveButton();
                            } else if (mSelectedItems.size() < 1)
                                disablePositiveButton();
                        }
                    })

            // Set the action buttons
            ;
        }else{
            mSelectedItems.add(types[0]);
            builder.setSingleChoiceItems(types, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enablePositiveButton();
                            mSelectedItems.clear();
                            mSelectedItems.add(types[which]);
                        }
                    });
        }

        if(isClosable) {
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dismiss();
                }
            });
        }

        // Create the AlertDialog object and return it
        alertDialog = builder.create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogPositiveClick(TypesChoice.this);
            }
        });

        alertDialog.show();

        //for not having empty search
        if(isMultipleChoice)
            disablePositiveButton();

        return alertDialog;
    }

    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public boolean onDialogPositiveClick(TypesChoice dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public ArrayList getmSelectedItems(){
        return mSelectedItems;
    }

    public int getTypeId(){
        return type_id;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    //to end activity if back pressed at starting
    @Override
    public void onCancel(DialogInterface dialog) {
        if(!isClosable)
            getActivity().finish();
        super.onCancel(dialog);
    }
}
