package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList.ParentObject;

public class ExpandableListParentObject implements ParentObject {
    private List<Object> mChildObjectList;

    private String mParentText;
    private int mParentNumber;
    private int resourcePathToImageResource;


    public ExpandableListParentObject() {
    }

    public String getParentText() {
        return mParentText;
    }

    public void setParentText(String parentText) {
        mParentText = parentText;
    }

    public int getParentNumber() {
        return mParentNumber;
    }

    public void setParentNumber(int parentNumber) {
        mParentNumber = parentNumber;
    }

    /**
     * Getter method for the list of children associated with this parent object
     *
     * @return list of all children associated with this specific parent object
     */
    @Override
    public List<Object> getChildObjectList() {
        return mChildObjectList;
    }

    /**
     * Setter method for the list of children associated with this parent object
     *
     * @param childObjectList the list of all children associated with this parent object
     */
    @Override
    public void setChildObjectList(List<Object> childObjectList) {
        mChildObjectList = childObjectList;
    }

    public int getResourcePathToImageResource() {
        return resourcePathToImageResource;
    }

    public void setResourcePathToImageResource(int resourcePathToImageResource) {
        this.resourcePathToImageResource = resourcePathToImageResource;
    }
}