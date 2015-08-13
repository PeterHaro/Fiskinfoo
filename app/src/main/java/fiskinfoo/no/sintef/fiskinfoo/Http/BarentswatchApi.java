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

package fiskinfoo.no.sintef.fiskinfoo.Http;

/**
 * OK HTTPLIB
 */
public class BarentswatchApi {
    private static final String BARENTSWATCH_API_AUTH_URL = "https://www.barentswatch.no/api/token";
    private static final String BARENTSWATCH_API_BASE_URL = "https://www.barentswatch.no/api/v1/geodata/";
    private static final String BARENTSWATCH_API_SUBSCRIBABLE_NORWEGIAN = "subscribable/";
    private static final String BARENTSWATCH_API_SUBSCRIBABLE_ENGLISH = "subscribable/?lang={en}";
    private static final String BARENTSWATCH_API_SUBSCRIPTION = "PropertyDescription/";
    private static String accessToken;
    private static boolean isAuthenticated;  //Probably remove this

    public BarentswatchApi(String accessToken) {
        this.accessToken = accessToken;
        isAuthenticated = true;
    }

    public BarentswatchApi() {
        isAuthenticated = false;
    }


}
