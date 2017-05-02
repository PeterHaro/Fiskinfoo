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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
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

public class UtilityOnClickListeners implements OnclickListenerInterface {
    @Override
    public OnClickListener getDismissDialogListener(final Dialog dialog) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
    }

    @Override
    public OnClickListener getShowToastListener(final Context context, final String message) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public OnClickListener getSubscriptionDownloadButtonOnClickListener(final Activity activity, final PropertyDescription subscription, final User user, final String tag) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                OnclickListenerInterface onClickListenerInterface = new UtilityOnClickListeners();
                final FiskInfoUtility fiskInfoUtility = new FiskInfoUtility();
                final Dialog dialog;

                int iconId = fiskInfoUtility.subscriptionApiNameToIconId(subscription.ApiName);

                if(iconId != -1) {
                    dialog = new UtilityDialogs().getDialogWithTitleIcon(v.getContext(), R.layout.dialog_download_map_layer, subscription.Name, iconId);
                } else {
                    dialog = new UtilityDialogs().getDialog(v.getContext(), R.layout.dialog_download_map_layer, subscription.Name);
                }

                final Button downloadButton = (Button) dialog.findViewById(R.id.select_download_format_download_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.select_download_format_cancel_button);
                final LinearLayout rowsContainer = (LinearLayout) dialog.findViewById(R.id.download_map_formats_container);

                downloadButton.setOnClickListener(getShowToastListener(v.getContext(), v.getContext().getString(R.string.error_no_format_selected)));

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

                cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

                dialog.show();
            }
        };

    }

    @Override
    public OnClickListener getInformationDialogOnClickListener(final String title, final String message, final int iconId) {
        OnClickListener onClickListener;

        if(message.contains("href") || message.contains("www.")) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UtilityDialogs().getHyperlinkAlertDialog(v.getContext(), title, message, iconId).show();
                }
            };
        } else {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UtilityDialogs().getAlertDialog(v.getContext(), title, message, iconId).show();
                }
            };
        }

        return onClickListener;
    }

    @Override
    public OnClickListener getInformationDialogOnClickListener(final int titleId, final int messageId, final int iconId) {
        OnClickListener onClickListener;

        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = v.getResources().getString(messageId);
                if(message.contains("href") || message.contains("www.")) {
                    new UtilityDialogs().getHyperlinkAlertDialog(v.getContext(), titleId, messageId, iconId).show();
                } else {
                    new UtilityDialogs().getAlertDialog(v.getContext(), titleId, messageId, iconId).show();
                }
            }
        };

        return onClickListener;
    }

    @Override
    public OnClickListener getSubscriptionErrorNotificationOnClickListener(final PropertyDescription subscription) {
        OnClickListener onClickListener;

        if(subscription.ErrorText.contains("href") || subscription.ErrorText.contains("www.")) {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UtilityDialogs().getHyperlinkAlertDialog(v.getContext(), ApiErrorType.getType(subscription.ErrorType).toString(), subscription.ErrorText, android.R.drawable.ic_dialog_alert).show();
                }
            };
        } else {
            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UtilityDialogs().getAlertDialog(v.getContext(), ApiErrorType.getType(subscription.ErrorType).toString(), subscription.ErrorText, android.R.drawable.ic_dialog_alert).show();
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
                UtilityRowsInterface utilityRowsInterface = new UtilityRows();
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
                    final RadioButtonRow row = utilityRowsInterface.getRadioButtonRow(v.getContext(), format);
                    if(isSubscribed && activeSubscription.FileFormatType.equals(format)) {
                        row.setSelected(true);
                    }

                    formatsContainer.addView(row.getView());
                }

                for(String interval : subscription.SubscriptionInterval) {
                    final RadioButtonRow row = utilityRowsInterface.getRadioButtonRow(v.getContext(), SubscriptionInterval.getType(interval).toString());

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

    @Override
    public OnClickListener getOfflineModeInformationIconOnClickListener(final User user) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new UtilityDialogs().getDialog(v.getContext(), R.layout.dialog_offline_mode_info, R.string.offline_mode);
                TextView textView = (TextView) dialog.findViewById(R.id.offline_mode_info_dialog_text_view);
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.offline_mode_info_dialog_linear_layout);
                Button okButton = (Button) dialog.findViewById(R.id.offline_mode_info_dialog_dismiss_button);

                textView.setText(R.string.offline_mode_info);

                for (final SubscriptionEntry entry : user.getSubscriptionCacheEntries()) {
                    final InfoSwitchRow row = new InfoSwitchRow(v.getContext(), entry.mName, entry.mLastUpdated.replace("T", "\n"));

                    row.setChecked(entry.mOfflineActive);
                    row.setOnCheckedChangedListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SubscriptionEntry updateEntry = user.getSubscriptionCacheEntry(entry.mSubscribable.ApiName);
                            updateEntry.mOfflineActive = isChecked;
                            user.setSubscriptionCacheEntry(entry.mSubscribable.ApiName, updateEntry);
                            user.writeToSharedPref(buttonView.getContext());
                        }
                    });

                    row.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            row.setChecked(!row.isChecked());
                        }
                    });

                    linearLayout.addView(row.getView());
                }

                okButton.setOnClickListener(new UtilityOnClickListeners().getDismissDialogListener(dialog));

                dialog.show();
            }
        };
    }

    @Override
    public OnClickListener getUserSettingsDialogOnClickListener(final User user) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                final DialogInterface dialogInterface = new UtilityDialogs();
                final Dialog dialog = dialogInterface.getDialog(v.getContext(), R.layout.dialog_user_settings, R.string.user_settings);
                final UserSettings settings = user.getSettings() != null ? user.getSettings() : new UserSettings();

                final Button saveSettingsButton = (Button) dialog.findViewById(R.id.user_settings_save_settings_button);
                final Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel_button);
                final LinearLayout fieldContainer = (LinearLayout) dialog.findViewById(R.id.dialog_user_settings_main_container);

                final EditTextRow contactPersonNameRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.contact_person_name), v.getContext().getString(R.string.contact_person_name));
                final EditTextRow contactPersonPhoneRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.contact_person_phone), v.getContext().getString(R.string.contact_person_phone));
                final EditTextRow contactPersonEmailRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.contact_person_email), v.getContext().getString(R.string.contact_person_email));

                final SpinnerRow toolRow = new SpinnerRow(v.getContext(), v.getContext().getString(R.string.tool_type), ToolType.getValues());
                final EditTextRow vesselNameRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.vessel_name), v.getContext().getString(R.string.vessel_name));
                final EditTextRow vesselPhoneNumberRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.vessel_phone_number), v.getContext().getString(R.string.vessel_phone_number));
                final EditTextRow vesselIrcsNumberRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.ircs_number), v.getContext().getString(R.string.ircs_number));
                final EditTextRow vesselMmsiNumberRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.mmsi_number), v.getContext().getString(R.string.mmsi_number));
                final EditTextRow vesselImoNumberRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.imo_number), v.getContext().getString(R.string.imo_number));
                final EditTextRow vesselRegistrationNumberRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.registration_number), v.getContext().getString(R.string.registration_number));

                contactPersonNameRow.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                contactPersonPhoneRow.setInputType(InputType.TYPE_CLASS_PHONE);
                contactPersonEmailRow.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                contactPersonNameRow.setHelpText(v.getContext().getString(R.string.contact_person_name_help_description));
                contactPersonPhoneRow.setHelpText(v.getContext().getString(R.string.contact_person_phone_help_description));
                contactPersonEmailRow.setHelpText(v.getContext().getString(R.string.contact_person_email_help_description));

                vesselNameRow.setInputType(InputType.TYPE_CLASS_TEXT);
                vesselPhoneNumberRow.setInputType(InputType.TYPE_CLASS_PHONE);

                vesselIrcsNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
                vesselIrcsNumberRow.setInputFilters(new InputFilter[] { new InputFilter.LengthFilter(v.getContext().getResources().getInteger(R.integer.input_length_ircs)), new InputFilter.AllCaps()});
                vesselIrcsNumberRow.setHelpText(v.getContext().getString(R.string.ircs_help_description));

                vesselMmsiNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
                vesselMmsiNumberRow.setInputFilters(new InputFilter[] { new InputFilter.LengthFilter(v.getContext().getResources().getInteger(R.integer.input_length_mmsi))});
                vesselMmsiNumberRow.setHelpText(v.getContext().getString(R.string.mmsi_help_description));

                vesselImoNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
                vesselImoNumberRow.setInputFilters(new InputFilter[] { new InputFilter.LengthFilter(v.getContext().getResources().getInteger(R.integer.input_length_imo))});
                vesselImoNumberRow.setHelpText(v.getContext().getString(R.string.imo_help_description));

                vesselRegistrationNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
                vesselRegistrationNumberRow.setInputFilters(new InputFilter[] { new InputFilter.LengthFilter(v.getContext().getResources().getInteger(R.integer.input_length_registration_number)), new InputFilter.AllCaps()});
                vesselRegistrationNumberRow.setHelpText(v.getContext().getString(R.string.registration_number_help_description));

                fieldContainer.addView(contactPersonNameRow.getView());
                fieldContainer.addView(contactPersonPhoneRow.getView());
                fieldContainer.addView(contactPersonEmailRow.getView());
                fieldContainer.addView(vesselNameRow.getView());
                fieldContainer.addView(toolRow.getView());
                fieldContainer.addView(vesselPhoneNumberRow.getView());
                fieldContainer.addView(vesselIrcsNumberRow.getView());
                fieldContainer.addView(vesselMmsiNumberRow.getView());
                fieldContainer.addView(vesselImoNumberRow.getView());
                fieldContainer.addView(vesselRegistrationNumberRow.getView());

                if (settings != null) {
                    ArrayAdapter<String> currentAdapter = toolRow.getAdapter();
                    toolRow.setSelectedSpinnerItem(currentAdapter.getPosition(settings.getToolType() != null ? settings.getToolType().toString() : Tool.BUNNTRÅL.toString()));
                    contactPersonNameRow.setText(settings.getContactPersonName());
                    contactPersonPhoneRow.setText(settings.getContactPersonPhone());
                    contactPersonEmailRow.setText(settings.getContactPersonEmail());
                    vesselNameRow.setText(settings.getVesselName());
                    vesselPhoneNumberRow.setText(settings.getVesselPhone());
                    vesselIrcsNumberRow.setText(settings.getIrcs());
                    vesselMmsiNumberRow.setText(settings.getMmsi());
                    vesselImoNumberRow.setText(settings.getImo());
                    vesselRegistrationNumberRow.setText(settings.getRegistrationNumber());
                }

                saveSettingsButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        FiskInfoUtility utility = new FiskInfoUtility();

                        boolean validated = false;

                        validated = utility.validateName(contactPersonNameRow.getFieldText().trim()) || contactPersonNameRow.getFieldText().trim().equals("");
                        contactPersonNameRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_name));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent().getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent().getParent()).scrollTo(0, contactPersonNameRow.getView().getBottom());
                                    contactPersonNameRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = utility.validatePhoneNumber(contactPersonPhoneRow.getFieldText().trim()) || contactPersonPhoneRow.getFieldText().trim().equals("");
                        contactPersonPhoneRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_phone_number));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent().getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent().getParent()).scrollTo(0, contactPersonPhoneRow.getView().getBottom());
                                    contactPersonPhoneRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = utility.isEmailValid(contactPersonEmailRow.getFieldText().trim()) || contactPersonEmailRow.getFieldText().trim().equals("");
                        contactPersonEmailRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_email));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent().getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent().getParent()).scrollTo(0, contactPersonEmailRow.getView().getBottom());
                                    contactPersonEmailRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = utility.validateIRCS(vesselIrcsNumberRow.getFieldText().trim()) || vesselIrcsNumberRow.getFieldText().trim().equals("");
                        vesselIrcsNumberRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_ircs));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent().getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent().getParent()).scrollTo(0, vesselIrcsNumberRow.getView().getBottom());
                                    vesselIrcsNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = utility.validateMMSI(vesselMmsiNumberRow.getFieldText().trim()) || vesselMmsiNumberRow.getFieldText().trim().equals("");
                        vesselMmsiNumberRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_mmsi));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent().getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent().getParent()).scrollTo(0, vesselMmsiNumberRow.getView().getBottom());
                                    vesselMmsiNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = utility.validateIMO(vesselImoNumberRow.getFieldText().trim()) || vesselImoNumberRow.getFieldText().trim().equals("");
                        vesselImoNumberRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_imo));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent().getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent().getParent()).scrollTo(0, vesselImoNumberRow.getView().getBottom());
                                    vesselImoNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        settings.setToolType(ToolType.createFromValue(toolRow.getCurrentSpinnerItem()));
                        settings.setVesselName(vesselNameRow.getFieldText().trim());
                        settings.setVesselPhone(vesselPhoneNumberRow.getFieldText().trim());
                        settings.setIrcs(vesselIrcsNumberRow.getFieldText().trim());
                        settings.setMmsi(vesselMmsiNumberRow.getFieldText().trim());
                        settings.setImo(vesselImoNumberRow.getFieldText().trim());
                        settings.setRegistrationNumber(vesselRegistrationNumberRow.getFieldText().trim());
                        settings.setContactPersonEmail(contactPersonEmailRow.getFieldText().toLowerCase().trim());
                        settings.setContactPersonName(contactPersonNameRow.getFieldText().trim());
                        settings.setContactPersonPhone(contactPersonPhoneRow.getFieldText().trim());

                        user.setSettings(settings);
                        user.writeToSharedPref(v.getContext()); //Need wait ? Let's find out

                        dialog.dismiss();
                    }
                });

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

    public OnClickListener getToolEntryEditDialogOnClickListener(final Activity activity, final FragmentManager fragmentManager, final GpsLocationTracker locationTracker, final ToolEntry toolEntry, final User user) {
        return new OnClickListener() {
            @Override
            public void onClick(final View editButton) {
                final DialogInterface dialogInterface = new UtilityDialogs();
                final Dialog dialog = dialogInterface.getDialog(editButton.getContext(), R.layout.dialog_register_new_tool, R.string.edit_tool);
                ((Button)dialog.findViewById(R.id.dialog_register_tool_create_tool_button)).setText(editButton.getContext().getString(R.string.save));

                final Button updateButton = (Button) dialog.findViewById(R.id.dialog_register_tool_create_tool_button);
                final Button cancelButton = (Button) dialog.findViewById(R.id.dialog_register_tool_cancel_button);
                final LinearLayout fieldContainer = (LinearLayout) dialog.findViewById(R.id.dialog_register_tool_main_container);
                final DatePickerRow setupDateRow = new DatePickerRow(editButton.getContext(), editButton.getContext().getString(R.string.tool_set_date_colon), fragmentManager);
                final TimePickerRow setupTimeRow = new TimePickerRow(editButton.getContext(), editButton.getContext().getString(R.string.tool_set_time_colon), fragmentManager, false);
                final CoordinatesRow coordinatesRow = new CoordinatesRow(activity, locationTracker);
                final SpinnerRow toolRow = new SpinnerRow(editButton.getContext(), editButton.getContext().getString(R.string.tool_type), ToolType.getValues());
                final CheckBoxRow toolRemovedRow = new CheckBoxRow(editButton.getContext(), editButton.getContext().getString(R.string.tool_removed_row_text), true);
                final EditTextRow commentRow = new EditTextRow(editButton.getContext(), editButton.getContext().getString(R.string.comment_field_header), editButton.getContext().getString(R.string.comment_field_hint));

                final EditTextRow contactPersonNameRow = new EditTextRow(editButton.getContext(), editButton.getContext().getString(R.string.contact_person_name), editButton.getContext().getString(R.string.contact_person_name));
                final EditTextRow contactPersonPhoneRow = new EditTextRow(editButton.getContext(), editButton.getContext().getString(R.string.contact_person_phone), editButton.getContext().getString(R.string.contact_person_phone));
                final EditTextRow contactPersonEmailRow = new EditTextRow(editButton.getContext(), editButton.getContext().getString(R.string.contact_person_email), editButton.getContext().getString(R.string.contact_person_email));
                final EditTextRow vesselNameRow = new EditTextRow(editButton.getContext(), editButton.getContext().getString(R.string.vessel_name), editButton.getContext().getString(R.string.vessel_name));
                final EditTextRow vesselPhoneNumberRow = new EditTextRow(editButton.getContext(), editButton.getContext().getString(R.string.vessel_phone_number), editButton.getContext().getString(R.string.vessel_phone_number));
                final EditTextRow vesselIrcsNumberRow = new EditTextRow(editButton.getContext(), editButton.getContext().getString(R.string.ircs_number), editButton.getContext().getString(R.string.ircs_number));
                final EditTextRow vesselMmsiNumberRow = new EditTextRow(editButton.getContext(), editButton.getContext().getString(R.string.mmsi_number), editButton.getContext().getString(R.string.mmsi_number));
                final EditTextRow vesselImoNumberRow = new EditTextRow(editButton.getContext(), editButton.getContext().getString(R.string.imo_number), editButton.getContext().getString(R.string.imo_number));
                final EditTextRow vesselRegistrationNumberRow = new EditTextRow(editButton.getContext(), editButton.getContext().getString(R.string.registration_number), editButton.getContext().getString(R.string.registration_number));
                final ErrorRow errorRow = new ErrorRow(editButton.getContext(), editButton.getContext().getString(R.string.error_minimum_identification_factors_not_met), false);

                final SimpleDateFormat sdfMilliSeconds = new SimpleDateFormat(editButton.getContext().getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss_sss), Locale.getDefault());
                final SimpleDateFormat sdf = new SimpleDateFormat(editButton.getContext().getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss), Locale.getDefault());

                View.OnClickListener deleteButtonRowOnClickListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String confirmationText;

                        switch (toolEntry.getToolStatus()) {
                            case STATUS_RECEIVED:
                            case STATUS_SENT_UNCONFIRMED:
                                confirmationText = v.getContext().getString(R.string.confirm_registered_tool_deletion_text);
                                break;
                            case STATUS_UNSENT:
                            case STATUS_UNREPORTED:
                                confirmationText = v.getContext().getString(R.string.confirm_tool_deletion_text);
                                break;
                            case STATUS_REMOVED_UNCONFIRMED:
                                confirmationText = v.getContext().getString(R.string.confirm_registered_tool_with_local_changes_deletion_text);
                                break;
                            default:
                                confirmationText = v.getContext().getString(R.string.confirm_tool_deletion_text_general);
                                break;
                        }

                        final Dialog deleteToolDialog = dialogInterface.getConfirmationDialog(v.getContext(), v.getContext().getString(R.string.delete_tool), confirmationText, v.getContext().getString(R.string.delete));
                        Button confirmToolDeletionButton = (Button) deleteToolDialog.findViewById(R.id.dialog_bottom_confirm_bottom);

                        confirmToolDeletionButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View parentView = (View)((editButton.getParent()).getParent());
                                ((LinearLayout)parentView).removeView(((View)(editButton.getParent())));
                                Toast.makeText(v.getContext(), v.getContext().getString(R.string.tool_deleted), Toast.LENGTH_LONG).show();

                                user.getToolLog().removeTool(toolEntry.getSetupDate(), toolEntry.getToolLogId());
                                user.writeToSharedPref(v.getContext());

                                deleteToolDialog.dismiss();
                                dialog.dismiss();
                            }
                        });

                        deleteToolDialog.show();
                    }
                };

                View.OnClickListener archiveToolOnClickListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String confirmationText;

                        switch (toolEntry.getToolStatus()) {
                            case STATUS_RECEIVED:
                                confirmationText = v.getContext().getString(R.string.confirm_registered_tool_archiving);
                                break;
                            case STATUS_UNSENT:
                                if(!toolEntry.getId().isEmpty()) {
                                    confirmationText = v.getContext().getString(R.string.confirm_tool_archiving_text);
                                } else {
                                    confirmationText = v.getContext().getString(R.string.confirm_registered_tool_with_local_changes_archiving_text);
                                }
                                break;
                            default:
                                confirmationText = v.getContext().getString(R.string.confirm_tool_archiving_text_general);
                                break;
                        }

                        final Dialog archiveDialog = dialogInterface.getConfirmationDialog(v.getContext(), v.getContext().getString(R.string.archive_tool), confirmationText, v.getContext().getString(R.string.archive));
                        Button confirmToolArchiveButton = (Button) archiveDialog.findViewById(R.id.dialog_bottom_confirm_bottom);

                        confirmToolArchiveButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View parentView = (View)((editButton.getParent()).getParent()).getParent();
                                ((LinearLayout)parentView).removeView(((View)(editButton.getParent()).getParent()));
                                Toast.makeText(v.getContext(), v.getContext().getString(R.string.tool_archived), Toast.LENGTH_LONG).show();

                                toolEntry.setToolStatus(ToolEntryStatus.STATUS_REMOVED);
                                user.writeToSharedPref(v.getContext());

                                archiveDialog.dismiss();
                                dialog.dismiss();
                            }
                        });

                        archiveDialog.show();
                    }
                };

                ActionRow archiveRow = new ActionRow(editButton.getContext(), editButton.getContext().getString(R.string.archive_tool), R.drawable.ic_archive_black_24dp, archiveToolOnClickListener);
                ActionRow deleteRow = new ActionRow(editButton.getContext(), editButton.getContext().getString(R.string.delete_tool), R.drawable.ic_delete_black_24dp, deleteButtonRowOnClickListener);

                commentRow.setInputType(InputType.TYPE_CLASS_TEXT);
                commentRow.setHelpText(editButton.getContext().getString(R.string.comment_help_description));
                vesselNameRow.setInputType(InputType.TYPE_CLASS_TEXT);
                contactPersonPhoneRow.setHelpText(editButton.getContext().getString(R.string.vessel_name_help_description));
                vesselPhoneNumberRow.setInputType(InputType.TYPE_CLASS_PHONE);
                vesselIrcsNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
                vesselIrcsNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(editButton.getContext().getResources().getInteger(R.integer.input_length_ircs)), new InputFilter.AllCaps()});
                vesselIrcsNumberRow.setHelpText(editButton.getContext().getString(R.string.ircs_help_description));
                vesselMmsiNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
                vesselMmsiNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(editButton.getContext().getResources().getInteger(R.integer.input_length_mmsi))});
                vesselMmsiNumberRow.setHelpText(editButton.getContext().getString(R.string.mmsi_help_description));
                vesselImoNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
                vesselImoNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(editButton.getContext().getResources().getInteger(R.integer.input_length_imo))});
                vesselImoNumberRow.setHelpText(editButton.getContext().getString(R.string.imo_help_description));
                vesselRegistrationNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
                vesselRegistrationNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(editButton.getContext().getResources().getInteger(R.integer.input_length_registration_number)), new InputFilter.AllCaps()});
                vesselRegistrationNumberRow.setHelpText(editButton.getContext().getString(R.string.registration_number_help_description));
                contactPersonNameRow.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                contactPersonPhoneRow.setInputType(InputType.TYPE_CLASS_PHONE);
                contactPersonEmailRow.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                contactPersonNameRow.setHelpText(editButton.getContext().getString(R.string.contact_person_name_help_description));
                contactPersonPhoneRow.setHelpText(editButton.getContext().getString(R.string.contact_person_phone_help_description));
                contactPersonEmailRow.setHelpText(editButton.getContext().getString(R.string.contact_person_email_help_description));
                coordinatesRow.setCoordinates(activity, toolEntry.getCoordinates());

                setupDateRow.setEnabled(false);

                /* Should these fields be editable after tools are reported? */
//                vesselRegistrationNumberRow.setEnabled(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_UNREPORTED);
//                vesselImoNumberRow.setEnabled(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_UNREPORTED);
//                vesselMmsiNumberRow.setEnabled(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_UNREPORTED);
//                vesselNameRow.setEnabled(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_UNREPORTED);
//                vesselIrcsNumberRow.setEnabled(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_UNREPORTED);

                ArrayAdapter<String> currentAdapter = toolRow.getAdapter();
                toolRow.setSelectedSpinnerItem(currentAdapter.getPosition(toolEntry.getToolType().toString()));
                toolRemovedRow.setChecked(!toolEntry.getRemovedTime().isEmpty());
                commentRow.setText(toolEntry.getComment());
                contactPersonNameRow.setText(toolEntry.getContactPersonName());
                contactPersonPhoneRow.setText(!toolEntry.getContactPersonPhone().equals("") ? toolEntry.getContactPersonPhone() : toolEntry.getVesselPhone());
                contactPersonEmailRow.setText(!toolEntry.getContactPersonEmail().equals("") ? toolEntry.getContactPersonEmail() : toolEntry.getVesselEmail());
                vesselNameRow.setText(toolEntry.getVesselName());
                vesselPhoneNumberRow.setText(toolEntry.getVesselPhone());
                vesselIrcsNumberRow.setText(toolEntry.getIRCS());
                vesselMmsiNumberRow.setText(toolEntry.getMMSI());
                vesselImoNumberRow.setText(toolEntry.getIMO());
                vesselRegistrationNumberRow.setText(toolEntry.getRegNum());

                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date setupDate = null;
                String toolSetupDateTime;

                try {
                    setupDate = sdf.parse(toolEntry.getSetupDateTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                sdfMilliSeconds.setTimeZone(TimeZone.getDefault());
                toolSetupDateTime = sdfMilliSeconds.format(setupDate);
                setupDateRow.setDate(toolSetupDateTime.substring(0, 10));
                setupTimeRow.setTime(toolSetupDateTime.substring(toolEntry.getSetupDateTime().indexOf('T') + 1, toolEntry.getSetupDateTime().indexOf('T') + 6));

                fieldContainer.addView(coordinatesRow.getView());
                fieldContainer.addView(setupDateRow.getView());
                fieldContainer.addView(setupTimeRow.getView());
                fieldContainer.addView(toolRow.getView());
                fieldContainer.addView(toolRemovedRow.getView());
                fieldContainer.addView(commentRow.getView());
                fieldContainer.addView(contactPersonNameRow.getView());
                fieldContainer.addView(contactPersonPhoneRow.getView());
                fieldContainer.addView(contactPersonEmailRow.getView());
                fieldContainer.addView(vesselNameRow.getView());
                fieldContainer.addView(vesselPhoneNumberRow.getView());
                fieldContainer.addView(vesselIrcsNumberRow.getView());
                fieldContainer.addView(vesselMmsiNumberRow.getView());
                fieldContainer.addView(vesselImoNumberRow.getView());
                fieldContainer.addView(vesselRegistrationNumberRow.getView());
                fieldContainer.addView(errorRow.getView());

                if(toolEntry.getToolStatus() != ToolEntryStatus.STATUS_UNREPORTED) {
                    fieldContainer.addView(archiveRow.getView());
                }

                fieldContainer.addView(deleteRow.getView());

                updateButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View updateButton) {
                        List<Point> coordinates = coordinatesRow.getCoordinates();
                        ToolType toolType = ToolType.createFromValue(toolRow.getCurrentSpinnerItem());
                        boolean toolRemoved = toolRemovedRow.isChecked();
                        String vesselName = vesselNameRow.getFieldText();
                        String vesselPhoneNumber = vesselPhoneNumberRow.getFieldText();
                        String toolSetupDate = setupDateRow.getDate();
                        String toolSetupTime = setupTimeRow.getTime();
                        String toolSetupDateTime;
                        String commentString = commentRow.getFieldText();
                        String vesselIrcsNumber = vesselIrcsNumberRow.getFieldText();
                        String vesselMmsiNumber = vesselMmsiNumberRow.getFieldText();
                        String vesselImoNumber = vesselImoNumberRow.getFieldText();
                        String registrationNumber = vesselRegistrationNumberRow.getFieldText();
                        String contactPersonName = contactPersonNameRow.getFieldText();
                        String contactPersonPhone = contactPersonPhoneRow.getFieldText();
                        String contactPersonEmail = contactPersonEmailRow.getFieldText();
                        FiskInfoUtility utility = new FiskInfoUtility();
                        boolean validated;
                        boolean edited = false;
                        boolean ircsValidated;
                        boolean mmsiValidated;
                        boolean imoValidated;
                        boolean regNumValidated;
                        boolean minimumIdentificationFactorsMet;

                        validated = coordinates != null;
                        if(!validated) {
                            return;
                        }

                        validated = utility.validateName(contactPersonNameRow.getFieldText().trim());
                        contactPersonNameRow.setError(validated ? null : updateButton.getContext().getString(R.string.error_invalid_name));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, contactPersonNameRow.getView().getBottom());
                                    contactPersonNameRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = utility.validatePhoneNumber(contactPersonPhoneRow.getFieldText().trim());
                        contactPersonPhoneRow.setError(validated ? null : updateButton.getContext().getString(R.string.error_invalid_phone_number));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, contactPersonPhoneRow.getView().getBottom());
                                    contactPersonPhoneRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = utility.isEmailValid(contactPersonEmailRow.getFieldText().trim());
                        contactPersonEmailRow.setError(validated ? null : updateButton.getContext().getString(R.string.error_invalid_email));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, contactPersonEmailRow.getView().getBottom());
                                    contactPersonEmailRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = vesselNameRow.getFieldText().trim() != null && !vesselNameRow.getFieldText().isEmpty();
                        vesselNameRow.setError(validated ? null : updateButton.getContext().getString(R.string.error_invalid_vessel_name));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, vesselNameRow.getView().getBottom());
                                    vesselNameRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = vesselPhoneNumberRow.getFieldText().trim() != null && !vesselPhoneNumberRow.getFieldText().isEmpty();
                        vesselPhoneNumberRow.setError(validated ? null : updateButton.getContext().getString(R.string.error_invalid_phone_number));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, vesselPhoneNumberRow.getView().getBottom());
                                    vesselPhoneNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = (ircsValidated = utility.validateIRCS(vesselIrcsNumber)) || vesselIrcsNumber.isEmpty();
                        vesselIrcsNumberRow.setError(validated ? null : updateButton.getContext().getString(R.string.error_invalid_ircs));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, vesselIrcsNumberRow.getView().getBottom());
                                    vesselIrcsNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = (mmsiValidated = utility.validateMMSI(vesselMmsiNumber)) || vesselMmsiNumber.isEmpty();
                        vesselMmsiNumberRow.setError(validated ? null : updateButton.getContext().getString(R.string.error_invalid_mmsi));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, vesselMmsiNumberRow.getView().getBottom());
                                    vesselMmsiNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = (imoValidated = utility.validateIMO(vesselImoNumber)) || vesselImoNumber.isEmpty();
                        vesselImoNumberRow.setError(validated ? null : updateButton.getContext().getString(R.string.error_invalid_imo));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, vesselImoNumberRow.getView().getBottom());
                                    vesselImoNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = (regNumValidated = utility.validateRegistrationNumber(registrationNumber)) || registrationNumber.isEmpty();
                        vesselRegistrationNumberRow.setError(validated ? null : editButton.getContext().getString(R.string.error_invalid_registration_number));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent().getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent().getParent()).scrollTo(0, vesselRegistrationNumberRow.getView().getBottom());
                                    vesselRegistrationNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        sdf.setTimeZone(TimeZone.getDefault());
                        Date setupDate = null;
                        String setupDateString = toolSetupDate + "T" + toolSetupTime + ":00Z";

                        try {
                            setupDate = sdf.parse(setupDateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("UTC"));
                        setupDateString = sdfMilliSeconds.format(setupDate);
                        toolSetupDateTime = setupDateString.substring(0, setupDateString.indexOf('.')).concat("Z");

                        minimumIdentificationFactorsMet = !vesselName.isEmpty() && (ircsValidated || mmsiValidated || imoValidated || regNumValidated);

                        if((coordinates != null && coordinates.size() != toolEntry.getCoordinates().size()) ||
                                toolType != toolEntry.getToolType() ||
                                (toolRemoved) == (toolEntry.getRemovedTime().isEmpty()) ||
                                (vesselName != null && !vesselName.equals(toolEntry.getVesselName())) ||
                                (vesselPhoneNumber != null && !vesselPhoneNumber.equals(toolEntry.getVesselPhone())) ||
                                (toolSetupDateTime != null && !toolSetupDateTime.equals(toolEntry.getSetupDateTime())) ||
                                (vesselIrcsNumber != null && !vesselIrcsNumber.equals(toolEntry.getIRCS())) ||
                                (vesselMmsiNumber != null && !vesselMmsiNumber.equals(toolEntry.getMMSI())) ||
                                (vesselImoNumber != null && !vesselImoNumber.equals(toolEntry.getIMO())) ||
                                (registrationNumber != null && !registrationNumber.equals(toolEntry.getRegNum())) ||
                                (contactPersonName != null && !contactPersonName.equals(toolEntry.getContactPersonName())) ||
                                (contactPersonPhone != null && !contactPersonPhone.equals(toolEntry.getContactPersonPhone())) ||
                                (contactPersonEmail != null && !contactPersonEmail.equals(toolEntry.getContactPersonEmail())) ||
                                (commentString != null && !commentString.equals(toolEntry.getComment())))
                        {
                            edited = true;
                        } else {
                            List<Point> points = toolEntry.getCoordinates();
                            for(int i = 0; i < coordinates.size(); i++) {
                                if(coordinates.get(i).getLatitude() != points.get(i).getLatitude() ||
                                        coordinates.get(i).getLongitude() != points.get(i).getLongitude()) {
                                    edited = true;
                                    break;
                                }
                            }
                        }

                        if(edited) {
                            if(!minimumIdentificationFactorsMet) {
                                errorRow.setVisibility(!minimumIdentificationFactorsMet);
                                return;
                            }

                            Date lastChangedDate = new Date();
                            Date previousSetupDate = null;
                            SimpleDateFormat sdfSetupCompare = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

                            String lastChangedDateString = sdfMilliSeconds.format(lastChangedDate)
                                    .concat("Z");
                            sdfSetupCompare.setTimeZone(TimeZone.getTimeZone("UTC"));

                            try {
                                previousSetupDate = sdf.parse(toolEntry.getSetupDateTime().substring(0, toolEntry.getSetupDateTime().length() - 1).concat(".000"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(!sdfSetupCompare.format(previousSetupDate).equals(sdfSetupCompare.format(setupDate))) {
                                // TODO: setup date is changed, tool needs to be moved in log so the app does not crash when trying to delete tool.
//                                user.getToolLog().removeTool(toolEntry.getSetupDate(), toolEntry.getToolLogId());
//                                user.getToolLog().addTool(toolEntry, toolEntry.getSetupDateTime().substring(0, 10));
//                                user.writeToSharedPref(editButton.getContext());
                            }

                            ToolEntryStatus toolStatus = (toolRemoved ? ToolEntryStatus.STATUS_REMOVED_UNCONFIRMED : (toolEntry.getToolStatus() == ToolEntryStatus.STATUS_UNREPORTED ? ToolEntryStatus.STATUS_UNREPORTED : ToolEntryStatus.STATUS_UNSENT));

                            toolEntry.setToolStatus(toolStatus);
                            toolEntry.setCoordinates(coordinates);
                            toolEntry.setToolType(toolType);
                            toolEntry.setVesselName(vesselName);
                            toolEntry.setVesselPhone(vesselPhoneNumber);
                            toolEntry.setSetupDateTime(toolSetupDateTime);
                            toolEntry.setRemovedTime(toolRemoved ? lastChangedDateString : null);
                            toolEntry.setComment(commentString);
                            toolEntry.setIRCS(vesselIrcsNumber);
                            toolEntry.setMMSI(vesselMmsiNumber);
                            toolEntry.setIMO(vesselImoNumber);
                            toolEntry.setRegNum(registrationNumber);
                            toolEntry.setLastChangedDateTime(lastChangedDateString);
                            toolEntry.setLastChangedBySource(lastChangedDateString);
                            toolEntry.setContactPersonName(contactPersonName);
                            toolEntry.setContactPersonPhone(contactPersonPhone);
                            toolEntry.setContactPersonEmail(contactPersonEmail);

                            try {
                                ImageView notificationView = (ImageView) ((View)editButton.getParent()).findViewById(R.id.tool_log_row_reported_image_view);

                                if(notificationView != null) {
                                    notificationView.setVisibility(View.VISIBLE);
                                    notificationView.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(v.getContext(), R.string.notification_tool_unreported_changes, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                TextView dateTimeTextView = (TextView) ((View)editButton.getParent()).findViewById(R.id.tool_log_row_latest_date_text_view);
                                TextView toolTypeTextView = (TextView)((View)editButton.getParent()).findViewById(R.id.tool_log_row_tool_type_text_view);
                                TextView positionTextView = (TextView)((View)editButton.getParent()).findViewById(R.id.tool_log_row_tool_position_text_view);
                                StringBuilder sb = new StringBuilder();



                                sb.append(FiskInfoUtility.decimalToDMS((toolEntry.getCoordinates().get(0).getLatitude())));
                                sb.append(", ");
                                sb.append(FiskInfoUtility.decimalToDMS((toolEntry.getCoordinates().get(0).getLongitude())));

                                String coordinateString = sb.toString();
                                coordinateString = toolEntry.getCoordinates().size() < 2 ? coordinateString : coordinateString + "\n..";

                                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                                setupDateString = sdfMilliSeconds.format(setupDate);

                                dateTimeTextView.setText(setupDateString.substring(0, 16).replace("T", " "));
                                toolTypeTextView.setText(toolEntry.getToolType().toString());
                                positionTextView.setText(coordinateString);

                                Date toolDate;
                                Date currentDate = new Date();
                                try {
                                    toolDate = sdf.parse(toolEntry.getSetupDateTime().substring(0, toolEntry.getSetupDateTime().length() - 1).concat(".000"));

                                    long diff = currentDate.getTime() - toolDate.getTime();
                                    double days = diff / updateButton.getContext().getResources().getInteger(R.integer.milliseconds_in_a_day);

                                    if(days > 14) {
                                        dateTimeTextView.setTextColor(ContextCompat.getColor(updateButton.getContext(), (R.color.error_red)));
                                    } else {
                                        dateTimeTextView.setTextColor(toolTypeTextView.getCurrentTextColor());
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return;
                                }
                            } catch(ClassCastException e) {
                                e.printStackTrace();
                            }


                            user.writeToSharedPref(updateButton.getContext());
                        } else {
                            Toast.makeText(editButton.getContext(), R.string.no_changes_made, Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }
                });

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

    public OnClickListener getHelpDialogOnClickListener() {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                final DialogInterface dialogInterface = new UtilityDialogs();
                final Dialog dialog = dialogInterface.getDialog(v.getContext(), R.layout.dialog_settings, R.string.about);

                final Button closeDialogButton = (Button) dialog.findViewById(R.id.settings_dialog_close_button);
                final LinearLayout fieldContainer = (LinearLayout) dialog.findViewById(R.id.dialog_user_settings_main_container);


                closeDialogButton.setText(v.getContext().getString(R.string.back_to_settings));

                closeDialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {




                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        };
    }
}