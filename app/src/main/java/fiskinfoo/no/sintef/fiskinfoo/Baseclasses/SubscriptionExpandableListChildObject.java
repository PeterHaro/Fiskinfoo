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

import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.ExpandableListChildType;

public class SubscriptionExpandableListChildObject extends ExpandableListChildObject {
    private TextView mLastUpdatedTextView;
    private Switch mSubscribedSwitch;
    private Button mDownloadMapLayerButton;
    private View.OnClickListener mDownloadButtonOnClickListener;
    private View.OnClickListener mSubscribeSwitchOnClickListener;
    private View.OnClickListener mErrorNotificationOnClickListener;

    private String mLastUpdated;
    private boolean isSubscribed;
    private ApiErrorType errorType;

    public SubscriptionExpandableListChildObject() {
        super(ExpandableListChildType.SUBSCRIPTION_EXPANDABLE_LIST_CHILD_OBJECT);
    }

    public String getLastUpdatedText() {
        return mLastUpdated;
    }

    public void setLastUpdatedText(String childText) {
        mLastUpdated = childText;
    }

    public void setLastUpdatedTextView(TextView textView) {
        this.mLastUpdatedTextView = textView;
    }

    public void setSubscribedSwitch(Switch subscribedSwitch) {
        mSubscribedSwitch = subscribedSwitch;
    }

    public void setSubscribedSwitchValue(boolean isSubscribed) {
        if(mSubscribedSwitch != null) {
            mSubscribedSwitch.setChecked(isSubscribed);
            System.out.println("isSubscribed set");
        } else {
            System.out.println("Cannot set isSubscribed, switch is null");
        }
    }

    public boolean getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
        setSubscribedSwitchValue(subscribed);
    }

    public void setDownloadMapLayerButton(Button button) {
        mDownloadMapLayerButton = button;
    }

    public void setDownloadMapLayerButtonOnClickListener(View.OnClickListener onClickListener) {
        if(mDownloadMapLayerButton != null && onClickListener != null) {
            mDownloadMapLayerButton.setOnClickListener(onClickListener);
            System.out.println("onClickListener set");
        } else {
            System.out.println("Cannot set onClickListener, button is null");
        }
    }

    public View.OnClickListener getDownloadButtonOnClickListener() {
        return mDownloadButtonOnClickListener;
    }

    public void setDownloadButtonOnClickListener(View.OnClickListener onClickListener) {
        mDownloadButtonOnClickListener = onClickListener;
    }

    public View.OnClickListener getSubscribeSwitchOnClickListener() {
        return mSubscribeSwitchOnClickListener;
    }

    public void setSubscribeSwitchOnClickListener(View.OnClickListener onClickListener) {
        mSubscribeSwitchOnClickListener = onClickListener;
    }

    public ApiErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ApiErrorType errorType) {
        this.errorType = errorType;
    }

    public View.OnClickListener getErrorNotificationOnClickListener() {
        return mErrorNotificationOnClickListener;
    }

    public void setErrorNotificationOnClickListener(View.OnClickListener onClickListener) {
        mErrorNotificationOnClickListener = onClickListener;
    }
}
