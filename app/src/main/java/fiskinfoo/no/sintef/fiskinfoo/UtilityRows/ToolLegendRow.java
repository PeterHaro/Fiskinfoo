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
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class ToolLegendRow extends BaseTableRow {
    private ImageView mImageView;
    private TextView mTextView;

    public ToolLegendRow(Context context, int color, String legendName) {
        super(context, R.layout.utility_row_tool_legend);

        mImageView = (ImageView) getView().findViewById(R.id.tool_legend_row_image_view);
        mTextView = (TextView) getView().findViewById(R.id.tool_legend_row_text_view);

        mImageView.setBackgroundColor(color);
        mTextView.setText(legendName);
    }

    public int getColor() {
        ColorDrawable drawable = (ColorDrawable) mImageView.getBackground();

        try {
            return drawable.getColor();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void setColor(int color) {
        mImageView.setBackgroundColor(color);
    }

    public String getText() {
        return mTextView.getText().toString();
    }

    public void setText(String text) {
        mTextView.setText(text);
    }
}
