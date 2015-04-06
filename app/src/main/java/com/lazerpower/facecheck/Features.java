package com.lazerpower.facecheck;

import android.net.Uri;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Niraj on 4/5/2015.
 */

public class Features {

    private JSONObject mFeatures;

    public Features(InputStream featuresStream) throws IOException, JSONException {
        String featuresJSON = IOUtils.toString(featuresStream);
        JSONTokener tokener = new JSONTokener(featuresJSON);
        mFeatures = (JSONObject) tokener.nextValue();
    }

    public boolean isSSLEnabled() {
        return mFeatures.optBoolean("server_ssl_enabled");
    }

    public String getServerName() {
        return mFeatures.optString("server_name");
    }

    public String getServerRegion() {
        return mFeatures.optString("server_region");
    }

    public String getApiKey() {
        return mFeatures.optString("api_key");
    }

    public Uri.Builder getServerBaseUri() {
        return new Uri.Builder()
                .scheme(isSSLEnabled() ? "https" : "http")
                .encodedAuthority(getServerRegion() + "." + getServerName());
    }
}
