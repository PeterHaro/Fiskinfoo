/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.HyperlinkAlertDialog;
import fiskinfoo.no.sintef.fiskinfoo.Interface.DialogInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class UtilityDialogs implements DialogInterface{
    @Override
    public Dialog getDialog(Context context, int layoutId, int titleId) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(layoutId);
        dialog.setTitle(titleId);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return dialog;
    }

    @Override
    public Dialog getDialog(Context context, int layoutId, String title) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(layoutId);
        dialog.setTitle(title);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return dialog;
    }

    @Override
    public Dialog getDialogWithTitleIcon(Context context, int layoutId, int titleId, int iconId) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(layoutId);
        dialog.setTitle(titleId);
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ikon_kart_til_din_kartplotter);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return dialog;
    }

    @Override
    public Dialog getDialogWithTitleIcon(Context context, int layoutId, String title, int iconId) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(layoutId);
        dialog.setTitle(title);
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ikon_kart_til_din_kartplotter);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return dialog;
    }

    @Override
    public AlertDialog getAlertDialog(Context context, String title, String message, int iconId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null);
        if(iconId != -1) {
            builder.setIcon(iconId);
        }
        return builder.create();
    }

    @Override
    public AlertDialog getAlertDialog(Context context, int titleId, int messageId, int iconId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(titleId)
                .setMessage(messageId)
                .setPositiveButton(R.string.ok, null);
        if(iconId != -1) {
            builder.setIcon(iconId);
        }
        return builder.create();
    }

    @Override
    public AlertDialog getHyperlinkAlertDialog(Context context, String title, String info, int iconId) {
        return HyperlinkAlertDialog.create(context, title, info, iconId);

    }

    @Override
    public AlertDialog getHyperlinkAlertDialog(Context context, String title, String message) {
        return HyperlinkAlertDialog.create(context, title, message);

    }

    @Override
    public AlertDialog getHyperlinkAlertDialog(Context context, int titleId, int infoId) {
        return HyperlinkAlertDialog.create(context, titleId, infoId);
    }

    @Override
    public AlertDialog getHyperlinkAlertDialog(Context context, int titleId, int infoId, int iconId) {
        return HyperlinkAlertDialog.create(context, titleId, infoId, iconId);
    }

    @Override
    public Dialog getLoadingDialog(Context context, int messageId) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        TextView messageTextView = (TextView) dialog.findViewById(R.id.loading_dialog_info_text_view);
        messageTextView.setText(messageId);

        return dialog;
    }

    @Override
    public Dialog getLoadingDialog(Context context, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        TextView messageTextView = (TextView) dialog.findViewById(R.id.loading_dialog_info_text_view);
        messageTextView.setText(message);

        return dialog;
    }

    @Override
    public Dialog getConfirmationDialog(Context context, String title, String message, String confirmButtonText) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.setTitle(title);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        TextView textView = (TextView) dialog.findViewById(R.id.dialog_confirm_message_text_view);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_bottom_cancel_button);
        Button confirmButton = (Button) dialog.findViewById(R.id.dialog_bottom_confirm_bottom);

        textView.setText(message);
        confirmButton.setText(confirmButtonText);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }
}
