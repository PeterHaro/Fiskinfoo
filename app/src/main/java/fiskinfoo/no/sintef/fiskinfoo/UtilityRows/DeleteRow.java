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
import android.widget.Button;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class DeleteRow extends BaseTableRow {
    TextView header;
    Button deleteButton;

    public DeleteRow(Context context, String headerText, View.OnClickListener onClickListener) {
        super(context, R.layout.utility_row_delete_row);

        header = (TextView) getView().findViewById(R.id.delete_row_header);
        deleteButton = (Button) getView().findViewById(R.id.delete_row_button);

        deleteButton.setOnClickListener(onClickListener);
    }

    public String getHeaderText() {
        return header.getText().toString();
    }

    public void setHeaderText(String headerText) {
        header.setText(headerText);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        deleteButton.setOnClickListener(onClickListener);
    }
}
