package com.example.rent;

import com.example.rent.model.User;

import java.util.List;

public class ConstantsUtils {

    public static volatile User sUserGlobalModel;

    public static boolean checkForDuplicateUserName(String userName, List<String> mUserNameList) {
        boolean isDuplicated = false;
        for (String name : mUserNameList) {
            if (name.equalsIgnoreCase(userName)) {
                isDuplicated = true;
                break;
            }
        }
        return isDuplicated;
    }

    public static final String TERRAIN_ITEM = "terrainItem";

    public static final String TWO_POINTS = " : ";

    public static final String[] HOURS_LIST_START = new String[]{"08", "09", "10", "11", "12", "13", "14",
            "15", "16", "17", "18", "19", "20", "22"};

    public static final String[] MINUTES_LIST = new String[]{"00"};

    public static final String TIME_FORMAT_RESERVATION_HOUR = "HH:mm";

    public static final String SPACE = " ";

    public static final String EMPTY_SPACE = "";

    public static final String ERROR_MESSAGE = "Error message";

    public static final String TIMESTAMP_FORMAT_DATE = "dd/MM/yyyy";

    public static final String DMY_FORMAT = "%d/%d/%d";

    public static final long DAY = 1000 * 60 * 60 * 24;

    public static final String IS_NEW_USER = "isNewUser";

    public static final String SPECIAL_CHAR_EXCEPTION = "User name cannot contain the following: + $ @ %";

    public static final String INVALID_PASSWORD = "Invalid password";

    public static final String FOOTBALL = "FOOTBALL";

    public static final String BASKET = "BASKETBALL";

    public static final String TENNIS = "TENNIS";

    public static final String CUSTOM = "CUSTOM";

    public static final String LOCATION_ID = "locationId";

}
