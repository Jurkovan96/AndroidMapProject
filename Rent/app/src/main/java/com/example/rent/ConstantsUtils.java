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
}
