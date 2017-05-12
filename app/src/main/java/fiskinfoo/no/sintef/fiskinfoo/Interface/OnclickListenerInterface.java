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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import java.util.ArrayList;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;

public interface OnclickListenerInterface {
    View.OnClickListener getDismissDialogListener(Dialog dialog);
    View.OnClickListener getShowToastListener(Context context, String message);
    View.OnClickListener getSubscriptionDownloadButtonOnClickListener(final Activity activity, final PropertyDescription subscription, final User user, final String tag);
    View.OnClickListener getSubscriptionErrorNotificationOnClickListener(final PropertyDescription subscription);
    View.OnClickListener getInformationDialogOnClickListener(String title, String message, int iconId);
    View.OnClickListener getInformationDialogOnClickListener(int titleId, int messageId, int iconId);
    View.OnClickListener getSubscriptionCheckBoxOnClickListener(final PropertyDescription subscription, final Subscription activeSubscription, final User user);
    View.OnClickListener getOfflineModeInformationIconOnClickListener(User user);
    View.OnClickListener getUserSettingsDialogOnClickListener(final User user);
    View.OnClickListener getHelpDialogOnClickListener();
}