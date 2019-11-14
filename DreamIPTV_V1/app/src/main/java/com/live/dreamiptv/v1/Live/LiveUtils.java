package com.live.dreamiptv.v1.Live;

/**
 * Created by Husnain Iqbal on 6/18/2017.
 */

public class LiveUtils {


    private static String previousUrlStreaming = "";
    private static boolean isUserPresentOnLargeScreen;

    public static String getPreviousUrlStreaming() {
        return previousUrlStreaming;
    }

    public static void setPreviousUrlStreaming(String prevUrl) {
        previousUrlStreaming = prevUrl;
    }

    public static boolean isUserPresentOnLargeScreen() {
        return isUserPresentOnLargeScreen;
    }

    public static void setUserPresentOnLargeScreen(boolean present) {
        isUserPresentOnLargeScreen = present;
    }
}
