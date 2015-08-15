package fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList;

import java.util.List;

public interface ParentObject {

    /**
     * Getter method object to the reference to this ParentObject's child list. The list should
     * contain all children to be displayed. If list is empty, no children will be added.
     *
     * @return this Parent's child object
     */
    List<Object> getChildObjectList();

    /**
     * Setter method for this parent's child object list. Multiple can be added or none
     *
     * @param childObjectList
     */
    void setChildObjectList(List<Object> childObjectList);
}