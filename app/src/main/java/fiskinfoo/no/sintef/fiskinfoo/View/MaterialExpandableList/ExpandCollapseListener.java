package fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList;

public interface ExpandCollapseListener {
    /**
     * Method called when an item in the ExpandableRecycleView is expanded
     */
    void onRecyclerViewItemExpanded(int position);

    /**
     * Method called when an item in the ExpandableRecyclerView is collapsed
     */
    void onRecyclerViewItemCollapsed(int position);
}
