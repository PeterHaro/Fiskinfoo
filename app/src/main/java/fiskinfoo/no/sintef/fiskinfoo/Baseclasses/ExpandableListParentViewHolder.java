package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList.ParentViewHolder;

public class ExpandableListParentViewHolder extends ParentViewHolder{

    public ImageView logoContainer;
    public TextView dataText;
    public ImageView arrowExpand;

    public ExpandableListParentViewHolder(View itemView) {
        super(itemView);

        logoContainer = (ImageView) itemView.findViewById(R.id.recycler_item_number_parent);
        dataText = (TextView) itemView.findViewById(R.id.recycler_item_text_parent);
        arrowExpand = (ImageView) itemView.findViewById(R.id.recycler_item_arrow_parent);
    }
}
