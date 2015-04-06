package com.lazerpower.facecheck.http;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.json.JSONException;
import org.json.JSONTokener;

import com.lazerpower.facecheck.Log;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

public class JSONResponseHandler {

    public static class Response {

        public final Object parsedJSON;
        public final int statusCode;
        public final com.squareup.okhttp.Response mOkHttpResponse;

        public Response(Object parsedJSON, int statusCode, com.squareup.okhttp.Response okHttpResponse) {
            this.parsedJSON = parsedJSON;
            this.statusCode = statusCode;
            this.mOkHttpResponse = okHttpResponse;
        }

    }

    private static final String JSON_MIME_TYPE = "application/json";

    /**
     * Handle the JSON response.
     *
     * @throws IOException
     *             on any network or I/O errors.
     * @throws ClientProtocolException
     *             If JSON cannot be parsed for any other reason.
     */
    public final Response handleResponse(com.squareup.okhttp.Response response) throws IOException {
        String responseString = getResponseString(response);
        try {
            ensureJSONContentType(response);
        } catch (ClientProtocolException e) {
            Log.e("Got unexpected response:\n" + responseString);
            throw e;
        }
        Object parsedJSON = parseJSON(responseString);
        int statusCode = response.code();
        return new Response(parsedJSON, statusCode, response);
    }

    /**
     * Check if the response has an application/json content type and throw if
     * it does not.
     *
     * @param response
     *            The response to check.
     * @throws HttpResponseException
     *             If response does not contain a JSON content type.
     */
    private void ensureJSONContentType(com.squareup.okhttp.Response response) throws ClientProtocolException {
        ResponseBody entity = response.body();
        if (entity == null) {
            throw new ClientProtocolException("Expected an entity in the response");
        }

        MediaType contentType = entity.contentType();
        if (contentType == null) {
            throw new ClientProtocolException("Expected a content type in the response entity");
        }

        List<String> contentTypeValues = response.headers().values("Content-Type");
        if (contentTypeValues == null || contentTypeValues.size() == 0) {
            throw new ClientProtocolException("Expected a non-empty content type value");
        }

        for (String element : contentTypeValues) {
            if (element.contains(JSON_MIME_TYPE)) {
                return;
            }
        }
        throw new ClientProtocolException("Expected content type to be " + JSON_MIME_TYPE);
    }

    /**
     * Get the response entity content as a string.
     *
     * @param response
     *            The HTTP response to parse.
     * @return The response entity content as a string.
     * @throws ClientProtocolException
     *             If the response entity has no content.
     * @throws IOException
     *             On any network I/O errors.
     */
    private String getResponseString(com.squareup.okhttp.Response response) throws ClientProtocolException, IOException {
        ResponseBody entity = response.body();
        String result;
        try {
            result = entity.string();
            if (result == null || result.length() == 0) {
                throw new ClientProtocolException("Expected non-empty entity content");
            }
        } catch (ParseException e) {
            throw new ClientProtocolException("Malformed entity content");
        }
        return result;
    }

    /**
     * Parse the JSON content from the given string.
     *
     * @param jsonString
     *            The string to parse.
     * @return The parsed JSON.
     * @throws ClientProtocolException
     *             If the string cannot be parsed for any reason.
     */
    private Object parseJSON(String jsonString) throws ClientProtocolException {
        JSONTokener tokener = new JSONTokener(jsonString);
        try {
            return tokener.nextValue();
        } catch (JSONException e) {
            throw new ClientProtocolException("Could not parse response JSON", e);
        }
    }

}
