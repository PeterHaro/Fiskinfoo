package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.Dialog;
import android.view.View;

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
}
