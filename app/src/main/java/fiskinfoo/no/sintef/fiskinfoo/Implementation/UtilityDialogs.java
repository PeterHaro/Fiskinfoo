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
import android.view.Window;
import android.view.WindowManager;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.HyperlinkAlertDialog;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
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
    public AlertDialog getInfoAlertDialog(Context context, String title, String info, int iconId) {
        return HyperlinkAlertDialog.create(context, title, info, iconId);

    }

    @Override
    public AlertDialog getInfoAlertDialog(Context context, int titleId, int infoId, int iconId) {
        return HyperlinkAlertDialog.create(context, titleId, infoId, iconId);
    }
}
