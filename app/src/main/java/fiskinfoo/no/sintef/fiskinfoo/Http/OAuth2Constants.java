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

public class OAuth2Constants {
    /// Parameters to send to the Authorization Endpoint
    public static final String KEY_RESPONSE_TYPE = "response_type";
    public static final String KEY_REDIRECT_URI = "redirect_uri";
    public static final String KEY_CLIENT_ID = "client_id";
    public static final String KEY_SCOPE = "scope";
    public static final String KEY_STATE = "state";

    /// Additional parameters to send to the Token Endpoint
    public static final String KEY_GRANT_TYPE = "grant_type";
    public static final String KEY_CODE = "code";

    /// Parameters received in an OK response from the Token Endpoint
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_TOKEN_TYPE = "token_type";
    public static final String KEY_EXPIRES_IN = "expires_in";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";

    /// Parameters in an ERROR response
    public static final String KEY_ERROR = "error";
    public static final String KEY_ERROR_DESCRIPTION = "error_description";
    public static final String KEY_ERROR_URI = "error_uri";
    public static final String VALUE_ERROR_ACCESS_DENIED = "access_denied";
}
