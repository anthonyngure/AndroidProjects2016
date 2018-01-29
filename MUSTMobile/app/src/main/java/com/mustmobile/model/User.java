package com.mustmobile.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mustmobile.R;

/**
 * Created by Tosh on 10/7/2015.
 */
public class User {

    public static final String BASIC_USER = "101";
    public static final String FORUM_ADMIN = "105";
    public static final String DEFAULT_RETURN = "UNDETERMINED";

    private static SharedPreferences sp;
    private static Context context;

    private User(Context c){
       this.context = c;
    }

    public static User at(Context ctx){
        sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        return new User(ctx);
    }


    public static String getName(){
        return sp.getString(context.getString(R.string.pref_user_name),DEFAULT_RETURN);
    }

    public static String getNumber(){
        return sp.getString(context.getString(R.string.pref_user_number),DEFAULT_RETURN);
    }

    public static String getYear(){
        String userClass = getStage();
        String year = userClass.substring((userClass.length() - 3), (userClass.length() - 2));
        Log.d("Toshde", "User is in year : "+year);
        return year;
    }

    public static String getSchoolCode(){
        return sp.getString(context.getString(R.string.pref_user_school_code),DEFAULT_RETURN);
    }

    public static String getStage(){
        return sp.getString(context.getString(R.string.pref_user_stage),"Y3S1");
    }

    public static String getSchoolAsName(){
        return School.Name.SPAS;
    }

    public static String getMode(){
        return "GOVERNMENT SPONSORED";
    }

    public static String getProgramName(){
        return sp.getString(context.getString(R.string.pref_user_program),
                "Bachelor of Science In Mathematics and Computer Science.");
    }

    public static String getProgramCode(){
        return sp.getString(context.getString(R.string.pref_user_program_code), "BMC");
    }

    public static int getPreferredCoverImage() {
        return sp.getInt(context.getString(R.string.pref_cover_image), 1);
    }

    public static String getPrivilegeCode() {
        String privilegeCode = sp.getString(context.getString(R.string.pref_privilege_code), BASIC_USER);
        Log.d("Toshde", "Privilege code : "+privilegeCode);
        return privilegeCode;
    }

    public static boolean getConfirmedInformation() {
        return sp.getBoolean(context.getString(R.string.pref_confirmed_information), false);
    }

}
