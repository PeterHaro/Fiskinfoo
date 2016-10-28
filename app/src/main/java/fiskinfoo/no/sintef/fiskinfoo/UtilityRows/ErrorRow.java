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
import android.widget.ImageView;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class ErrorRow extends BaseTableRow {
    private TextView textView;
    private ImageView imageView;

    public ErrorRow(Context context, String errorText, boolean visible) {
        super(context, R.layout.utility_row_error_information_row);

        textView = (TextView) getView().findViewById(R.id.error_row_text_view);
        imageView = (ImageView) getView().findViewById(R.id.error_row_image_view);

        textView.setText(errorText);
        getView().setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public String getErrorText() {
        return textView.getText().toString();
    }

    public void setErrorText(String errorText) {
        textView.setText(errorText);
    }

    public void setVisibility(boolean visible) {
        getView().setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void requestFocus() {
        textView.requestFocus();
    }
}
