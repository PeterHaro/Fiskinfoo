package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import android.view.View;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList.ChildViewHolder;

public class ExpandableListChildViewHolder extends ChildViewHolder{

    public TextView dataText;

    /**
     * Public constructor for the custom child ViewHolder
     *
     * @param itemView the child ViewHolder's view
     */
    public ExpandableListChildViewHolder(View itemView) {
        super(itemView);

        dataText = (TextView) itemView.findViewById(R.id.recycler_item_text_child);
    }
}
