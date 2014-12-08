package mycompany.thistest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.sql.Types;
import java.util.ArrayList;

/**
 * Created by trsq9010 on 04/12/2014.
 */
public class TypesChoice extends DialogFragment{
    ArrayList<String> mSelectedItems;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mSelectedItems = new ArrayList();  // Where we track the selected items
        final String[] types = {"restaurant", "bar","cafe", "bakery", "store", "zoo", "aquarium"};
        builder.setTitle("Types")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(types, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked)
                                {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(types[which]);
                                }
                                else if (mSelectedItems.contains(which))
                                {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        mListener.onDialogPositiveClick(TypesChoice.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(TypesChoice.this);

                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }


    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public boolean onDialogPositiveClick(TypesChoice dialog);
        public boolean onDialogNegativeClick(TypesChoice dialog);
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



}
