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

package fiskinfoo.no.sintef.fiskinfoo.Interface;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

public interface DialogInterface {
    /**
     * Return a dialog with the given layout and title. The dialog defaults to not being cancelled by outside touch.
     * @param context
     * @param layoutId
     * @param titleId
     * @return
     */
    Dialog getDialog(Context context, int layoutId, int titleId);

    /**
     * Return a dialog with the given layout and title. The dialog defaults to not being cancelled by outside touch.
     * @param context
     * @param layoutId
     * @param title
     * @return
     */
    Dialog getDialog(Context context, int layoutId, String title);

    /**
     * Return a dialog with the given layout, title, and icon. The dialog defaults to not being cancelled by outside touch.
     * @param context
     * @param layoutId
     * @param titleId
     * @param iconId
     * @return
     */
    Dialog getDialogWithTitleIcon(Context context, int layoutId, int titleId, int iconId);

    /**
     * Return a dialog with the given layout, title, and icon. The dialog defaults to not being cancelled by outside touch.
     * @param context
     * @param layoutId
     * @param title
     * @param iconId
     * @return
     */
    Dialog getDialogWithTitleIcon(Context context, int layoutId, String title, int iconId);

    /**
     * Return a dialog with the given title and info text. The dialog defaults to not being cancelled by outside touch.
     * @param context
     * @param title
     * @param info
     * @return
     */
    AlertDialog getInfoAlertDialog(Context context, String title, String info);

    /**
     * Return a dialog with the given title, info text, and icon. The dialog defaults to not being cancelled by outside touch.
     * @param context
     * @param title
     * @param info
     * @param iconId
     * @return
     */
    AlertDialog getInfoAlertDialog(Context context, String title, String info, int iconId);

    /**
     * Return a dialog with the given title and info text. The dialog defaults to not being cancelled by outside touch.
     * @param context
     * @param titleId
     * @param infoId
     * @return
     */
    AlertDialog getInfoAlertDialog(Context context, int titleId, int infoId);

    /**
     * Return a dialog with the given title, info text, and icon. The dialog defaults to not being cancelled by outside touch.
     * @param context
     * @param titleId
     * @param infoId
     * @param iconId
     * @return
     */
    AlertDialog getInfoAlertDialog(Context context, int titleId, int infoId, int iconId);
}
