package fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ChildViewHolder extends RecyclerView.ViewHolder {

    private View mClickableView;
    /**
     * Default constructor.
     *
     * @param itemView
     */
    public ChildViewHolder(View itemView) {
        super(itemView);
        mClickableView = itemView;
    }
}