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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class UtilityRows implements UtilityRowsInterface {
    @Override
    public View getToolLegendRow(Context context, String toolName, int toolColor) {
        TableRow tr = new TableRow(context);
        View v = LayoutInflater.from(context).inflate(R.layout.tool_legend_row, tr, false);
        TextView textView = (TextView) v.findViewById(R.id.tool_legend_row_text_view);
        ImageView imageView = (ImageView) v.findViewById(R.id.tool_legend_row_image_view);

        textView.setText(toolName);
        imageView.setBackgroundColor(toolColor);

        return v;
    }
}
