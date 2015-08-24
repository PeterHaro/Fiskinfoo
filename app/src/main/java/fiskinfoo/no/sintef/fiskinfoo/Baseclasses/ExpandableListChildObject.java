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
import android.view.ViewParent;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.Implementation.ExpandableListChildType;
import fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList.ParentViewHolder;

public class ExpandableListChildObject {
    private TextView mTitleTextView;
    private String mTitleText;
    private ExpandableListChildType mObjectType;

    public ExpandableListChildObject(ExpandableListChildType type) {
        mObjectType = type;
    }

    public ExpandableListChildType getObjectType() {
        return mObjectType;
    }

    public String getTitleText() {
        return mTitleText;
    }

    public void setTitleText(String titleText) {
        mTitleText = titleText;
    }

    public void setTag(String tag) {
        if(mTitleTextView != null) {
            mTitleTextView.setTag(tag);
            System.out.println("Tag set");
        } else {
            System.out.println("Cannot set tag, TextView is null");
        }
    }

    public void setTitleTextView(TextView titleTextView) {
        this.mTitleTextView = titleTextView;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        if (mTitleTextView != null && onClickListener != null) {
            mTitleTextView.setOnClickListener(onClickListener);
            System.out.println("onClickListener set");
        } else {
            System.out.println("Cannot set onClickListener, TextView is null");
        }
    }

    public ViewParent getParentViewHolder() {
        return mTitleTextView == null ? null : mTitleTextView.getParent();
    }
}
