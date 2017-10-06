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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Tool;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntryStatus;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.IBarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.SubscriptionInterval;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.SubscriptionSubmitObject;
import fiskinfoo.no.sintef.fiskinfoo.Interface.DialogInterface;
import fiskinfoo.no.sintef.fiskinfoo.Interface.OnclickListenerInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CoordinatesRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.DatePickerRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ActionRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.EditTextRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ErrorRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.InfoSwitchRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.RadioButtonRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SpinnerRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.TimePickerRow;
import retrofit.client.Response;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static fiskinfoo.no.sintef.fiskinfoo.MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class UtilityOnClickListeners implements OnclickListenerInterface {

    @Override
    public OnClickListener getSubscriptionDownloadButtonOnClickListener(final Activity activity, final PropertyDescription subscription, final User user, final String tag) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[] { WRITE_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    return;
                }

                final FiskInfoUtility fiskInfoUtility = new FiskInfoUtility();
                final Dialog dialog;

                int iconId = fiskInfoUtility.subscriptionApiNameToIconId(subscription.ApiName);

                if(iconId != -1) {
                    dialog = new UtilityDialogs().getDialogWithTitleIcon(v.getContext(), R.layout.dialog_download_map_layer, subscription.Name, iconId);
                } else {
                    dialog = new UtilityDialogs().getDialog(v.getContext(), R.layout.dialog_download_map_layer, subscription.Name);
                }

                if (ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }

                final Button downloadButton = (Button) dialog.findViewById(R.id.select_download_format_download_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.select_download_format_cancel_button);
                final LinearLayout rowsContainer = (LinearLayout) dialog.findViewById(R.id.download_map_formats_container);

                downloadButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), v.getContext().getString(R.string.error_no_format_selected), Toast.LENGTH_LONG).show();
                    }
                });

                for (String format : subscription.Formats) {
                    final RadioButtonRow row = new RadioButtonRow(v.getContext(), format);

                    row.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < ((ViewGroup)v.getParent()).getChildCount(); i++) {
                                ((RadioButton) ((ViewGroup)v.getParent()).getChildAt(i).findViewById(R.id.radio_button_row_radio_button)).setChecked(false);
                            }
                            ((RadioButton)v.findViewById(R.id.radio_button_row_radio_button)).setChecked(true);

                            downloadButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BarentswatchApi barentswatchApi = new BarentswatchApi();
                                    barentswatchApi.setAccesToken(user.getToken());
                                    IBarentswatchApi api = barentswatchApi.getApi();

                                    Response response;
                                    String downloadFormat = row.getText();

                                    try {
                                        response = api.geoDataDownload(subscription.ApiName, downloadFormat);
                                        if (response == null) {
                                            Log.d(tag, "RESPONSE == NULL");
                                            throw new NullPointerException();
                                        }

                                        byte[] fileData = FiskInfoUtility.toByteArray(response.getBody().in());
                                        if (fiskInfoUtility.isExternalStorageWritable()) {
                                            fiskInfoUtility.writeMapLayerToExternalStorage(activity, fileData, subscription.Name, downloadFormat, user.getFilePathForExternalStorage(), true);
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

                    rowsContainer.addView(row.getView());
                }

                cancelButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        };

    }

    @Override
    public OnClickListener getInformationDialogOnClickListener(final String title, final String message) {
        OnClickListener onClickListener;

        if(message.contains("href") || message.contains("www.")) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UtilityDialogs().getHyperlinkAlertDialog(v.getContext(), title, message).show();
                }
            };
        } else {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton(v.getContext().getString(R.string.ok), null);

                    builder.show();
                }
            };
        }

        return onClickListener;
    }

    @Override
    public OnClickListener getSubscriptionErrorNotificationOnClickListener(final PropertyDescription subscription) {
        OnClickListener onClickListener;

        if(subscription.ErrorText.contains("href") || subscription.ErrorText.contains("www.")) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UtilityDialogs().getHyperlinkAlertDialog(v.getContext(), ApiErrorType.getType(subscription.ErrorType).toString(), subscription.ErrorText).show();
                }
            };
        } else {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setIcon(R.drawable.ic_dialog_alert_holo_light)
                            .setTitle(ApiErrorType.getType(subscription.ErrorType).toString())
                            .setMessage(subscription.ErrorText)
                            .setPositiveButton(v.getContext().getString(R.string.ok), null)
                            .show();
                }
            };
        }

        return onClickListener;
    }

    @Override
    public OnClickListener getSubscriptionCheckBoxOnClickListener(final PropertyDescription subscription, final Subscription activeSubscription, final User user) {
        return new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final FiskInfoUtility fiskInfoUtility = new FiskInfoUtility();

                final Dialog dialog;

                int iconId = fiskInfoUtility.subscriptionApiNameToIconId(subscription.ApiName);

                if(iconId != -1) {
                    dialog = new UtilityDialogs().getDialogWithTitleIcon(v.getContext(), R.layout.dialog_manage_subscription, subscription.Name, iconId);
                } else {
                    dialog = new UtilityDialogs().getDialog(v.getContext(), R.layout.dialog_manage_subscription, subscription.Name);
                }

                final Switch subscribedSwitch = (Switch) dialog.findViewById(R.id.manage_subscription_switch);
                final LinearLayout formatsContainer = (LinearLayout) dialog.findViewById(R.id.manage_subscription_formats_container);
                final LinearLayout intervalsContainer = (LinearLayout) dialog.findViewById(R.id.manage_subscription_intervals_container);
                final EditText subscriptionEmailEditText = (EditText) dialog.findViewById(R.id.manage_subscription_email_edit_text);

                final Button subscribeButton = (Button) dialog.findViewById(R.id.manage_subscription_update_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.manage_subscription_cancel_button);

                final boolean isSubscribed = activeSubscription != null;
                final Map<String, String> subscriptionFrequencies = new HashMap<>();

                dialog.setTitle(subscription.Name);

                if(isSubscribed) {
                    subscriptionEmailEditText.setText(activeSubscription.SubscriptionEmail);

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
                } else {
                    subscriptionEmailEditText.setText(user.getUsername());
                }

                for (String format : subscription.Formats) {
                    final RadioButtonRow row = new RadioButtonRow(v.getContext(), format);
                    if(isSubscribed && activeSubscription.FileFormatType.equals(format)) {
                        row.setSelected(true);
                    }

                    formatsContainer.addView(row.getView());
                }

                for(String interval : subscription.SubscriptionInterval) {
                    final RadioButtonRow row = new RadioButtonRow(v.getContext(), SubscriptionInterval.getType(interval).toString());

                    if(activeSubscription != null) {
                        row.setSelected(activeSubscription.SubscriptionIntervalName.equals(interval));
                    }

                    subscriptionFrequencies.put(SubscriptionInterval.getType(interval).toString(), interval);
                    intervalsContainer.addView(row.getView());
                }

                if(intervalsContainer.getChildCount() == 1) {
                    ((RadioButton)intervalsContainer.getChildAt(0).findViewById(R.id.radio_button_row_radio_button)).setChecked(true);
                }

                subscribeButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View subscribeButton) {
                        String subscriptionFormat = null;
                        String subscriptionInterval = null;
                        String subscriptionEmail;

                        BarentswatchApi barentswatchApi = new BarentswatchApi();
                        barentswatchApi.setAccesToken(user.getToken());
                        final IBarentswatchApi api = barentswatchApi.getApi();

                        for(int i = 0; i < formatsContainer.getChildCount(); i++) {
                            if(((RadioButton)formatsContainer.getChildAt(i).findViewById(R.id.radio_button_row_radio_button)).isChecked()) {
                                subscriptionFormat = ((TextView)formatsContainer.getChildAt(i).findViewById(R.id.radio_button_row_text_view)).getText().toString();
                                break;
                            }
                        }

                        if(subscriptionFormat == null) {
                            Toast.makeText(v.getContext(), v.getContext().getString(R.string.choose_subscription_format), Toast.LENGTH_LONG).show();
                            return;
                        }

                        for(int i = 0; i < intervalsContainer.getChildCount(); i++) {
                            if(((RadioButton)intervalsContainer.getChildAt(i).findViewById(R.id.radio_button_row_radio_button)).isChecked()) {
                                subscriptionInterval = ((TextView)intervalsContainer.getChildAt(i).findViewById(R.id.radio_button_row_text_view)).getText().toString();
                                break;
                            }
                        }

                        if(subscriptionInterval == null) {
                            Toast.makeText(v.getContext(), v.getContext().getString(R.string.choose_subscription_interval), Toast.LENGTH_LONG).show();
                            return;
                        }

                        subscriptionEmail = subscriptionEmailEditText.getText().toString();

                        if(!(new FiskInfoUtility().isEmailValid(subscriptionEmail))) {
                            Toast.makeText(v.getContext(), v.getContext().getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(isSubscribed) {
                            if(subscribedSwitch.isChecked()) {
                                if(!(subscriptionFormat.equals(activeSubscription.FileFormatType) && activeSubscription.SubscriptionIntervalName.equals(subscriptionFrequencies.get(subscriptionInterval)) &&
                                        user.getUsername().equals(subscriptionEmail))) {
                                    SubscriptionSubmitObject updatedSubscription = new SubscriptionSubmitObject(subscription.ApiName, subscriptionFormat, user.getUsername(), user.getUsername(), subscriptionFrequencies.get(subscriptionInterval));
                                    Subscription newSubscriptionObject = api.updateSubscription(String.valueOf(activeSubscription.Id), updatedSubscription);

                                    if(newSubscriptionObject != null) {
                                        ((CheckBox) v).setChecked(true);
                                    }
                                }
                            } else {
                                Response response = api.deleteSubscription(String.valueOf(activeSubscription.Id));


                                if(response.getStatus() == 204) {
                                    ((CheckBox) v).setChecked(false);
                                    Toast.makeText(v.getContext(), R.string.subscription_update_successful, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(v.getContext(), R.string.subscription_update_failed, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            SubscriptionSubmitObject newSubscription = new SubscriptionSubmitObject(subscription.ApiName, subscriptionFormat, user.getUsername(), user.getUsername(), subscriptionFrequencies.get(subscriptionInterval));

                            Subscription response = api.setSubscription(newSubscription);

                            if(response != null) {
                                ((CheckBox) v).setChecked(true);
                                // TODO: add to "Mine abonnementer"
                                Toast.makeText(v.getContext(), R.string.subscription_update_successful, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(v.getContext(), R.string.subscription_update_failed, Toast.LENGTH_LONG).show();
                            }
                        }

                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View cancelButton) {
                        ((CheckBox) v).setChecked(isSubscribed);

                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        };
    }
}