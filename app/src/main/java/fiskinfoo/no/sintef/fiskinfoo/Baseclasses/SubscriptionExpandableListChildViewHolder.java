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
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class SubscriptionExpandableListChildViewHolder extends ExpandableListChildViewHolder {

    public TextView dataText;
    public TextView lastUpdatedTextView;
    public CheckBox subscribedSwitch;
    public Button downloadButton;

    /**
     * Public constructor for the custom child ViewHolder
     *
     * @param itemView the child ViewHolder's view
     */
    public SubscriptionExpandableListChildViewHolder(View itemView) {
        super(itemView);

        dataText = (TextView) itemView.findViewById(R.id.recycler_child_item_title_text_view);
        lastUpdatedTextView = (TextView) itemView.findViewById(R.id.recycler_child_last_updated_text_view);
        subscribedSwitch = (CheckBox) itemView.findViewById(R.id.recycle_child_view_subscribed_check_box);
        downloadButton = (Button) itemView.findViewById(R.id.recycler_item_download_map_button);
    }
}
