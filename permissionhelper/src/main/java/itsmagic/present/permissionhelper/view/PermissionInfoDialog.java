package itsmagic.present.permissionhelper.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.List;

import itsmagic.present.permissionhelper.util.PermissionHelper;

/**
 * Created by Alvin Rusli on 11/15/2017.
 */
public class PermissionInfoDialog {

    /** The context */
    private Context mContext;

    /** The {@link PermissionHelper} object */
    private PermissionHelper mPermissionHelper;

    /** The requested permission */
    private List<String> mPermissions;

    /** The permission information message */
    private List<String> mInfoMessages;

    /** Default constructor */
    public PermissionInfoDialog(Context context, PermissionHelper permissionHelper, List<String> permissions, List<String> infoMessages) {
        mContext = context;
        mPermissionHelper = permissionHelper;
        mPermissions = permissions;
        mInfoMessages = infoMessages;
    }

    /** Show the dialog */
    public void show() {
        final StringBuilder infoMessage = new StringBuilder();
        int size = mInfoMessages.size();
        for (int i = 0; i < size; i++) {
            infoMessage.append(mInfoMessages.get(i));
            if (i < (size - 1)) infoMessage.append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(infoMessage.toString());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPermissionHelper.requestPermissionWithoutInformation();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
