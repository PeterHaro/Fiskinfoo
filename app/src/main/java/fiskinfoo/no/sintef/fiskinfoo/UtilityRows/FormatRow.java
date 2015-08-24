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
import android.view.View;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class FormatRow extends BaseTableRow {
    private TextView mTextView;

    public FormatRow(Context context, String buttonText) {
        super(context, R.layout.utility_row_download_format_row);

        mTextView = (TextView) getView().findViewById(R.id.download_format_row_text_view);
        mTextView.setText(buttonText);
    }

    public void setText(String buttonText) {
        mTextView.setText(buttonText);
    }

    public String getText() {
        return mTextView.getText().toString();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mTextView.setOnClickListener(onClickListener);
    }

    public void setBackgroundColor(int color) {
        getView().setBackgroundColor(color);
    }

    public void setTextViewBackgroundColor(int color) {
        mTextView.setBackgroundColor(color);
    }
}
