package com.example.sereincandle.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_TOKEN = "JWT_TOKEN";
    private static final String KEY_ROLE = "USER_ROLE";
    private static final String KEY_USER_NAME = "USER_NAME";
    private static final String KEY_USER_EMAIL = "USER_EMAIL";

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPref.getString(KEY_TOKEN, null);
    }

    public void saveRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public String getRole() {
        return sharedPref.getString(KEY_ROLE, null);
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(getRole());
    }

    public void saveUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }

    public String getUserName() {
        return sharedPref.getString(KEY_USER_NAME, null);
    }

    public void saveUserEmail(String userEmail) {
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.apply();
    }

    public String getUserEmail() {
        return sharedPref.getString(KEY_USER_EMAIL, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }
}
