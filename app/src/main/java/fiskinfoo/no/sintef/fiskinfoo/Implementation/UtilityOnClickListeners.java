package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.IBarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.SubscriptionInterval;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Interface.DialogInterface;
import fiskinfoo.no.sintef.fiskinfoo.Interface.OnclickListenerInterface;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxFormatRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.FormatRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.RadioButtonRow;
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

                final Dialog dialog = dialogInterface.getDialog(v.getContext(), R.layout.dialog_download_map_layer, R.string.download_map_layer_dialog_title);

                final Button downloadButton = (Button) dialog.findViewById(R.id.select_download_format_download_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.select_download_format_cancel_button);
                final LinearLayout rowsContainer = (LinearLayout) dialog.findViewById(R.id.download_map_formats_container);

                downloadButton.setOnClickListener(getShowToastListener(v.getContext(), v.getContext().getString(R.string.choose_download_format_and_interval)));

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
    public View.OnClickListener getSubscriptionCheckBoxOnClickListener(final PropertyDescription subscription, final Subscription activeSubscription, final User user) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogInterface dialogInterface = new UtilityDialogs();
                OnclickListenerInterface onClickListenerInterface = new UtilityOnClickListeners();
                UtilityRowsInterface utilityRowsInterface = new UtilityRows();

                final Dialog dialog = dialogInterface.getDialog(v.getContext(), R.layout.dialog_manage_subscription, R.string.update_subscriptions);

                final Switch subscribedSwitch = (Switch) dialog.findViewById(R.id.manage_subscription_switch);
                final LinearLayout formatsContainer = (LinearLayout) dialog.findViewById(R.id.manage_subscription_formats_container);
                final LinearLayout intervalsContainer = (LinearLayout) dialog.findViewById(R.id.manage_subscription_intervals_container);
                final Button subscribeButton = (Button) dialog.findViewById(R.id.manage_subscription_update_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.manage_subscription_cancel_button);

                final List<String> activeSubscriptionFormats = new ArrayList<>();
                boolean isSubscribed = activeSubscription != null;

                dialog.setTitle(subscription.Name);

                if(isSubscribed) {
                    activeSubscriptionFormats.add(activeSubscription.FileFormatType);
                    subscribedSwitch.setVisibility(View.VISIBLE);
                    subscribedSwitch.setChecked(true);
                    subscribedSwitch.setText(v.getResources().getString(R.string.manage_subscription_subscription_active));

                    subscribedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            System.out.println("This is checked:" + isChecked);
                            String switchText = isChecked ? v.getResources().getString(R.string.manage_subscription_subscription_active) : v.getResources().getString(R.string.manage_subscription_subscription_cancellation);
                            subscribedSwitch.setText(switchText);
                        }
                    });
                }

                for (String format : subscription.Formats) {
                    final RadioButtonRow row = utilityRowsInterface.getRadioButtonRow(v.getContext(), format);
                    if(activeSubscriptionFormats.contains(format)) {
                        row.setSelected(true);
                    }

                    formatsContainer.addView(row.getView());
                }

                for(String interval : subscription.SubscriptionInterval) {
                    final RadioButtonRow row = utilityRowsInterface.getRadioButtonRow(v.getContext(), SubscriptionInterval.getType(interval).toString());

                    if(activeSubscription != null) {
                        row.setSelected(activeSubscription.SubscriptionIntervalName.equals(interval));
                    }

                    intervalsContainer.addView(row.getView());
                }

                if(intervalsContainer.getChildCount() == 1) {
                    ((RadioButton)intervalsContainer.getChildAt(0).findViewById(R.id.radio_button_row_radio_button)).setChecked(true);
                }

                subscribeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View subscribeButton) {
                        String subscriptionFormat = null;
                        String subscriptionInterval = null;
                        boolean isSubscribed = false;
                        boolean formatSelected = false;
                        boolean intervalSelected = false;

                        BarentswatchApi barentswatchApi = new BarentswatchApi();
                        barentswatchApi.setAccesToken(user.getToken());
                        final IBarentswatchApi api = barentswatchApi.getApi();

                        for(int i = 0; i < formatsContainer.getChildCount(); i++) {
                            if(((RadioButton)intervalsContainer.getChildAt(i).findViewById(R.id.radio_button_row_radio_button)).isChecked()) {
                                subscriptionFormat = ((TextView)intervalsContainer.getChildAt(i).findViewById(R.id.radio_button_row_text_view)).getText().toString();
                                formatSelected = true;
                                break;
                            }
                        }

                        if(!formatSelected) {
                            Toast.makeText(v.getContext(), v.getContext().getString(R.string.choose_subscription_format), Toast.LENGTH_LONG).show();
                            return;
                        }

                        for(int i = 0; i < intervalsContainer.getChildCount(); i++) {
                            if(((RadioButton)intervalsContainer.getChildAt(i).findViewById(R.id.radio_button_row_radio_button)).isChecked()) {
                                subscriptionInterval = ((TextView)intervalsContainer.getChildAt(i).findViewById(R.id.radio_button_row_text_view)).getText().toString();
                                intervalSelected = true;
                                break;
                            }
                        }

                        if(!intervalSelected) {
                            Toast.makeText(v.getContext(), v.getContext().getString(R.string.choose_subscription_interval), Toast.LENGTH_LONG).show();
                            return;
                        }


                        if(activeSubscription != null) {
                            if(subscribedSwitch.isChecked()) {
                                // TODO: fix api call.
                            } else {
                                // TODO: fix api call.
                            }
                        } else {
                            // TODO: fix api call
                        }

                        ((Switch) v).setChecked(isSubscribed);
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View cancelButton) {
                        ((CheckBox) v).setChecked(activeSubscriptionFormats.size() > 0);

                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        };
    }
}
