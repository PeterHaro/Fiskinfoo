package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import fiskinfoo.no.sintef.fiskinfoo.Interface.OnclickListenerInterface;

public class UtilityOnClickListeners implements OnclickListenerInterface {
    @Override
    public View.OnClickListener getDismissDialogListener(final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
    }

    @Override
    public View.OnClickListener getShowToastListener(final Context context, final String toastString) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public View.OnClickListener getShowToastListener(final Context context, final int stringId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, context.getString(stringId), Toast.LENGTH_LONG).show();
            }
        };
    }
}
