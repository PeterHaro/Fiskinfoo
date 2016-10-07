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

public enum GeometryType {
    LINESTRING, POINT;

    public String toString() {
        String retval = null;

        switch(this) {
            case LINESTRING:
                retval = "LineString";
                break;
            case POINT:
                retval = "Point";
                break;
            default:
                throw new UnsupportedOperationException("Geometry type does not exist in the system");
        }

        return retval;
    }

    public static GeometryType createFromValue(String value) {
        if (value.equalsIgnoreCase("LineString")) {
            return GeometryType.LINESTRING;
        } else if (value.equalsIgnoreCase("Point")) {
            return GeometryType.POINT;
        }
        return null;
    }
}
