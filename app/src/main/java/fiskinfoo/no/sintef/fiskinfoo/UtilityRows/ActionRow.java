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

package fiskinfoo.no.sintef.fiskinfoo.UtilityRows;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class ActionRow extends BaseTableRow {
    TextView header;
    Button actionButton;

    public ActionRow(Context context, String headerText, int actionButtonIconId, View.OnClickListener onClickListener) {
        super(context, R.layout.utility_row_action_row);

        header = (TextView) getView().findViewById(R.id.action_row_header);
        actionButton = (Button) getView().findViewById(R.id.action_row_button);

        header.setText(headerText);
        actionButton.setBackground(ContextCompat.getDrawable(context, actionButtonIconId));
        actionButton.setOnClickListener(onClickListener);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_active_tint_color));
        }
    }

    public String getHeaderText() {
        return header.getText().toString();
    }

    public void setHeaderText(String headerText) {
        header.setText(headerText);
    }

    public void setIcon(int iconId) {
        actionButton.setBackground(ContextCompat.getDrawable(getView().getContext(), iconId));
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        actionButton.setOnClickListener(onClickListener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        actionButton.setEnabled(enabled);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(enabled) {
                actionButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_active_tint_color));
            } else {
                actionButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_disabled_tint_color));
            }
        }
    }
}
