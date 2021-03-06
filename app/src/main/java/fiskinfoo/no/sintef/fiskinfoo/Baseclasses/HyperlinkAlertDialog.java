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

package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class HyperlinkAlertDialog {

    public static AlertDialog create(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, null);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView messageTextView = (TextView) inflater.inflate(R.layout.hyperlink_alert_dialog_text_view, null);

        messageTextView.setPadding(30, 20, 30, 20);

        if(!message.contains("href")) {
            final SpannableString spannableString = new SpannableString(message);
            Linkify.addLinks(spannableString, Linkify.WEB_URLS);
            messageTextView.setText(spannableString);
            messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
            messageTextView.setText(Html.fromHtml(message));
        }

        builder.setView(messageTextView);

        return builder.create();
    }

    public static AlertDialog create(Context context, String title, String message, int iconId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if(iconId != -1) {
            builder.setIcon(iconId);
        }

        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, null);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        TextView messageView = (TextView) inflater.inflate(R.layout.hyperlink_alert_dialog_text_view, null);
        final SpannableString spannableString = new SpannableString(message);
        Linkify.addLinks(spannableString, Linkify.WEB_URLS);
        messageView.setText(spannableString);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(messageView);

        return builder.create();
    }

    public static AlertDialog create(Context context, int titleId, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(titleId);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, null);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        TextView messageView = (TextView) inflater.inflate(R.layout.hyperlink_alert_dialog_text_view, null);
        final SpannableString spannableString = new SpannableString(context.getString(messageId));
        Linkify.addLinks(spannableString, Linkify.WEB_URLS);
        messageView.setText(spannableString);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(messageView);

        return builder.create();
    }

    public static AlertDialog create(Context context, int titleId, int messageId, int iconId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if(iconId != -1) {
            builder.setIcon(iconId);
        }

        builder.setTitle(titleId);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, null);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        TextView messageView = (TextView) inflater.inflate(R.layout.hyperlink_alert_dialog_text_view, null);
        final SpannableString spannableString = new SpannableString(context.getString(messageId));
        Linkify.addLinks(spannableString, Linkify.WEB_URLS);
        messageView.setText(spannableString);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(messageView);

        return builder.create();
    }
}
