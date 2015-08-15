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

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SettingsButtonRow;

public class recycleViewSettingsButtonRowAdapter extends RecyclerView.Adapter<recycleViewSettingsButtonRowAdapter.ViewHolder> {
    private String[] mDataset;
    private ArrayList<SettingsButtonRow> mSettingsButtons;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        SettingsButtonRow mRow;

        public ViewHolder(SettingsButtonRow row) {
            super(null/*row.itemView*/);
            mRow = row;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public recycleViewSettingsButtonRowAdapter(String[] myDataset, ArrayList<SettingsButtonRow> settingsButtons) {
        mDataset = myDataset;
        mSettingsButtons = settingsButtons;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public recycleViewSettingsButtonRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.my_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        SettingsButtonRow v = new UtilityRows().getSettingsButtonRow(parent.getContext(), "placeholder");

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mRow.setButtonText(mSettingsButtons.get(position).getButtonText());
//        holder.mRow.setButtonOnClickListener(mSettingsButtons.get(position).get());

//        holder.mTextView.setText(mDataset[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.length;
    }
}