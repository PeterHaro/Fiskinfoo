package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.IBarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Interface.DialogInterface;
import fiskinfoo.no.sintef.fiskinfoo.Interface.OnclickListenerInterface;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxFormatRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.FormatRow;
import retrofit.client.Response;

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

    @Override
    public View.OnClickListener getSubscriptionDownloadButtonOnClickListener(final PropertyDescription subscription, final User user, final String tag) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogInterface dialogInterface = new UtilityDialogs();
                OnclickListenerInterface onClickListenerInterface = new UtilityOnClickListeners();
                final FiskInfoUtility fiskInfoUtility = new FiskInfoUtility();

                final Dialog dialog = dialogInterface.getDialog(v.getContext(), R.layout.dialog_select_format, R.string.download_map_layer_dialog_title);

                final Button downloadButton = (Button) dialog.findViewById(R.id.select_download_format_download_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.select_download_format_cancel_button);
                final LinearLayout rowsContainer = (LinearLayout) dialog.findViewById(R.id.select_download_format_formats_container);

                downloadButton.setOnClickListener(getShowToastListener(v.getContext(), v.getContext().getString(R.string.choose_a_download_format)));

                for (String format : subscription.Formats) {
                    final FormatRow formatRow = new FormatRow(v.getContext(), format);

                    formatRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < rowsContainer.getChildCount(); i++) {

                                rowsContainer.getChildAt(i).findViewById(R.id.format_row_text_view).setBackgroundColor(v.getContext().getResources().getColor(R.color.text_white));
                            }

                            formatRow.setTextViewBackgroundColor(v.getContext().getResources().getColor(R.color.helpful_grey));

                            downloadButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BarentswatchApi barentswatchApi = new BarentswatchApi();
                                    barentswatchApi.setAccesToken(user.getToken());
                                    IBarentswatchApi api = barentswatchApi.getApi();

                                    Response response;
                                    String downloadFormat = formatRow.getText();

                                    try {
                                        response = api.geoDataDownload(subscription.ApiName, downloadFormat);
                                        if (response == null) {
                                            Log.d(tag, "RESPONSE == NULL");
                                        }
                                        byte[] fileData = FiskInfoUtility.toByteArray(response.getBody().in());
                                        if (fiskInfoUtility.isExternalStorageWritable()) {
                                            fiskInfoUtility.writeMapLayerToExternalStorage(v.getContext(), fileData, subscription.Name, downloadFormat, user.getFilePathForExternalStorage());
                                        } else {
                                            Toast.makeText(v.getContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                                        }
                                    } catch (Exception e) {
                                        Log.d(tag, "Could not download with ApiName: " + subscription.ApiName + "  and format: " + downloadFormat);
                                    }

                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    rowsContainer.addView(formatRow.getView());
                }

                cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

                dialog.show();
            }
        };

    }

    @Override
    public View.OnClickListener getErrorNotificationOnClickListener(final PropertyDescription subscription) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(ApiErrorType.getType(subscription.ErrorType).toString())
                        .setMessage(subscription.ErrorText)
                        .setPositiveButton(v.getContext().getString(R.string.ok), null)
                        .show();
            }
        };
    }

    @Override
    public View.OnClickListener getSubscriptionSwitchOnClickListener(final PropertyDescription subscription, final Subscription activeSubscription, final User user) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogInterface dialogInterface = new UtilityDialogs();
                OnclickListenerInterface onClickListenerInterface = new UtilityOnClickListeners();
                UtilityRowsInterface utilityRowsInterface = new UtilityRows();

                final Dialog dialog = dialogInterface.getDialog(v.getContext(), R.layout.dialog_select_format, R.string.update_subscriptions);

                final Button subscribeButton = (Button) dialog.findViewById(R.id.select_download_format_download_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.select_download_format_cancel_button);
                final LinearLayout rowsContainer = (LinearLayout) dialog.findViewById(R.id.select_download_format_formats_container);

                final List<String> activeSubscriptionFormats = new ArrayList<>();

                if(activeSubscription != null) {
                    activeSubscriptionFormats.add(activeSubscription.FileFormatType);
                }

                subscribeButton.setText(R.string.update);
                subscribeButton.setOnClickListener(onClickListenerInterface.getShowToastListener(v.getContext(), v.getContext().getString(R.string.choose_a_download_format)));

                for (String format : subscription.Formats) {
                    final CheckBoxFormatRow formatRow = utilityRowsInterface.getCheckBoxFormatRow(v.getContext(), format);
                    if(activeSubscriptionFormats.contains(format)) {
                        formatRow.setChecked(true);
                    }

                    rowsContainer.addView(formatRow.getView());
                }

                subscribeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View subscribeButton) {
                        boolean isSubscribed = false;

                        BarentswatchApi barentswatchApi = new BarentswatchApi();
                        barentswatchApi.setAccesToken(user.getToken());
                        final IBarentswatchApi api = barentswatchApi.getApi();

                        for(int i = 0; i < rowsContainer.getChildCount(); i++) {
                            String currentFormat = ((TextView) rowsContainer.getChildAt(i).findViewById(R.id.format_row_text_view)).getText().toString();


                            if(((CheckBox) rowsContainer.getChildAt(i).findViewById(R.id.format_row_check_box)).isChecked()) {
                                isSubscribed = true;

                                if(activeSubscriptionFormats.contains(currentFormat)) {
                                    continue;
                                } else {
                                    // TODO: API call to add subscription
                                    Response response;
                                }
                            } else {
                                if(activeSubscription != null) {
                                    // TODO: API call to remove subscription
                                    Response response;
                                }
                            }
                        }

                        ((Switch) v).setChecked(isSubscribed);
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View cancelButton) {
                        ((Switch) v).setChecked(activeSubscriptionFormats.size() > 0);

                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        };
    }
}
