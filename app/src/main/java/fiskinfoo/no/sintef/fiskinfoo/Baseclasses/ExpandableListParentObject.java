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

    @SuppressWarnings("unused")
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