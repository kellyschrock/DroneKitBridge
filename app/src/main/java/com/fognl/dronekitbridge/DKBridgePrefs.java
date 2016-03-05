package com.fognl.dronekitbridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by kellys on 2/27/16.
 */
public class DKBridgePrefs {
    private static DKBridgePrefs sInstance;

    public static final String PREF_SERVER_IP = "server_ip";
    public static final String PREF_SERVER_PORT = "server_port";
    public static final String PREF_LAST_FRAGMENT = "last_fragment";
        public static final String FRAG_SERVER = "server";
        public static final String FRAG_CLIENT = "client";
        public static final String FRAG_REMOTE = "remote_track";
    public static final String PREF_LAST_GROUPID = "last_groupid";
    public static final String PREF_LAST_USERID = "last_userid";

    public static void init(Context context) {
        if(sInstance == null) {
            sInstance = new DKBridgePrefs(context);
        }
    }

    public static DKBridgePrefs get() {
        if(sInstance == null) {
            throw new IllegalArgumentException("Call init() before calling get()");
        }

        return sInstance;
    }

    private final Context mContext;

    private DKBridgePrefs(Context context) {
        super();
        mContext = context;
    }

    public String getLastServerIp() { return getPrefs().getString(PREF_SERVER_IP, ""); }
    public void setLastServerIp(String ip) { getPrefs().edit().putString(PREF_SERVER_IP, ip).commit(); }

    public String getLastServerPort() { return getPrefs().getString(PREF_SERVER_PORT, "8888"); }
    public void setLastServerPort(String port) { getPrefs().edit().putString(PREF_SERVER_PORT, port).commit(); }

    public String getLastFragment() { return getPrefs().getString(PREF_LAST_FRAGMENT, FRAG_SERVER); }
    public void setLastFragment(String f) { getPrefs().edit().putString(PREF_LAST_FRAGMENT, f).commit(); }

    public String getLastGroupId() { return getPrefs().getString(PREF_LAST_GROUPID, null); }
    public void setLastGroupId(String groupId) { getPrefs().edit().putString(PREF_LAST_GROUPID, groupId).commit(); }

    public String getLastUserId() { return getPrefs().getString(PREF_LAST_USERID, null); }
    public void setLastUserId(String userId) { getPrefs().edit().putString(PREF_LAST_USERID, userId).commit(); }

    public void setLastGroupAndUserId(String groupId, String userId) {
        getPrefs()
            .edit()
                .putString(PREF_LAST_GROUPID, groupId)
                .putString(PREF_LAST_USERID, userId)
            .commit();
    }

    public SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}
