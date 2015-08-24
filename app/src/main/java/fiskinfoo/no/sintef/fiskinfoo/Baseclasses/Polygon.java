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

import java.util.LinkedList;
import java.util.List;

public class Polygon implements java.io.Serializable {
    List<Point> vertices;

    public Polygon() {
        vertices = new LinkedList<Point>();
    }

    public Polygon(List<Point> Vertices) {
        vertices = Vertices;
    }

    public void setVertices(List<Point> Vertices) {
        for(Point vert : Vertices) {
            vertices.add(vert);
        }
    }

    public void addVertex(Point point) {
        vertices.add(point);
    }

    public List<Point> getVertices() {
        return vertices;
    }

    public void printPolygon() {
        for(Point point : vertices) {
            point.printPointValues();
        }
    }

    private static final long serialVersionUID = 9L;
}
