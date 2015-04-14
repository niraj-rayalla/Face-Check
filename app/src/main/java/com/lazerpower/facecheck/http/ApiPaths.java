package com.lazerpower.facecheck.http;

import com.lazerpower.facecheck.App;

/**
 * Created by Niraj on 4/5/2015.
 */
public class ApiPaths {

    public static String getApiChallengePath() {
        return String.format("api/lol/%s/v4.1/game/ids",
                App.getInstance().getFeatures().getServerRegion());
    }

    public static String getMatchPath(String matchId) {
        return String.format("api/lol/%s/v2.2/match/%s",
                App.getInstance().getFeatures().getServerRegion(),
                matchId);
    }

    public static String getStaticDataPath(String staticApiVersion, String staticDataType) {
        return String.format("api/lol/static-data/%s/%s/%s",
                App.getInstance().getFeatures().getServerRegion(),
                staticApiVersion,
                staticDataType);
    }
}
