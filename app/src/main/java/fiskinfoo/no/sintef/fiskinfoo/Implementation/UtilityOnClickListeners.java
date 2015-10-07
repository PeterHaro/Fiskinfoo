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

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionEntry;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.IBarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.SubscriptionInterval;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.SubscriptionSubmitObject;
import fiskinfoo.no.sintef.fiskinfoo.Interface.OnclickListenerInterface;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.InfoSwitchRow;
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
    public View.OnClickListener getShowToastListener(final Context context, final String message) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public View.OnClickListener getSubscriptionDownloadButtonOnClickListener(final PropertyDescription subscription, final User user, final String tag) {
        return new View.OnClickListener() {

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

                    row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < ((ViewGroup)v.getParent()).getChildCount(); i++) {
                                ((RadioButton) ((ViewGroup)v.getParent()).getChildAt(i).findViewById(R.id.radio_button_row_radio_button)).setChecked(false);
                            }
                            ((RadioButton)v.findViewById(R.id.radio_button_row_radio_button)).setChecked(true);

                            downloadButton.setOnClickListener(new View.OnClickListener() {
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
                                            fiskInfoUtility.writeMapLayerToExternalStorage(v.getContext(), fileData, subscription.Name, downloadFormat, user.getFilePathForExternalStorage(), true);
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
    public View.OnClickListener getInformationDialogOnClickListener(final String title, final String message, final int iconId) {
        View.OnClickListener onClickListener;

        if(message.contains("href") || message.contains("www.")) {
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UtilityDialogs().getHyperlinkAlertDialog(v.getContext(), title, message, iconId).show();
                }
            };
        } else {
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UtilityDialogs().getAlertDialog(v.getContext(), title, message, iconId).show();
                }
            };
        }

        return onClickListener;
    }

    @Override
    public View.OnClickListener getInformationDialogOnClickListener(final int titleId, final int messageId, final int iconId) {
        View.OnClickListener onClickListener;

            onClickListener = new View.OnClickListener() {
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
    public View.OnClickListener getSubscriptionErrorNotificationOnClickListener(final PropertyDescription subscription) {
        View.OnClickListener onClickListener;

        if(subscription.ErrorText.contains("href") || subscription.ErrorText.contains("www.")) {
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UtilityDialogs().getHyperlinkAlertDialog(v.getContext(), ApiErrorType.getType(subscription.ErrorType).toString(), subscription.ErrorText, android.R.drawable.ic_dialog_alert).show();
                }
            };
        } else {
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UtilityDialogs().getAlertDialog(v.getContext(), ApiErrorType.getType(subscription.ErrorType).toString(), subscription.ErrorText, android.R.drawable.ic_dialog_alert).show();
                }
            };
        }

        return onClickListener;
    }

    @Override
    public View.OnClickListener getSubscriptionCheckBoxOnClickListener(final PropertyDescription subscription, final Subscription activeSubscription, final User user) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                UtilityRowsInterface utilityRowsInterface = new UtilityRows();
                final FiskInfoUtility fiskInfoUtility = new FiskInfoUtility();

                final Dialog dialog;

                int iconId = fiskInfoUtility.subscriptionApiNameToIconId(subscription.ApiName);

                if(iconId != -1) {
                    dialog = new UtilityDialogs().getDialogWithTitleIcon(v.getContext(), R.layout.dialog_manage_subscription, subscription.Name, iconId);
                } else {
                    dialog = new UtilityDialogs().getDialog(v.getContext(), R.layout.dialog_download_map_layer, subscription.Name);
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

                subscribeButton.setOnClickListener(new View.OnClickListener() {
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
                                }
                            }
                        } else {
                            SubscriptionSubmitObject newSubscription = new SubscriptionSubmitObject(subscription.ApiName, subscriptionFormat, user.getUsername(), user.getUsername(), subscriptionFrequencies.get(subscriptionInterval));

                            Subscription response = api.setSubscription(newSubscription);

                            if(response != null) {
                                ((CheckBox) v).setChecked(true);
                                // TODO: add to "Mine abonnementer"
                            }

                        }

                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
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
    public View.OnClickListener getOfflineModeInformationIconOnClickListener(final User user) {
        return new View.OnClickListener() {
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
                            SubscriptionEntry newEntry = new SubscriptionEntry(entry.mName, entry.mLastUpdated, isChecked);
                            user.setSubscriptionCacheEntry(entry.mName, newEntry);
                            user.writeToSharedPref(buttonView.getContext());
                        }
                    });

                    row.setOnClickListener(new View.OnClickListener() {
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


}
