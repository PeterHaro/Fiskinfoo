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

public enum ProjectionType {
    EPSG_3857, EPSG_4326, EPSG_23030, EPSG_900913;

    @Override
    public String toString() {
        String retVal;
        switch(this) {
            case EPSG_3857:
                retVal = "EPSG:3857";
                break;
            case EPSG_4326:
                retVal = "EPSG:4326";
                break;
            case EPSG_23030:
                retVal = "EPSG:23030";
                break;
            case EPSG_900913:
                retVal = "EPSG:900913";
                break;
            default:
                throw new UnsupportedOperationException("Message type does not exist in the system");
        }
        return retVal;
    }
}
