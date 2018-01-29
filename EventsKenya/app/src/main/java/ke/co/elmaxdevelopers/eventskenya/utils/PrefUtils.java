package ke.co.elmaxdevelopers.eventskenya.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.loopj.android.http.RequestParams;

import org.joda.time.LocalDate;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;

/**
 * Created by Tosh on 1/6/2016.
 */
public class PrefUtils {

    private static PrefUtils mInstance;
    private static SharedPreferences sharedPreferences;
    private static Context context;

    private PrefUtils(Context mContext){
        this.context = mContext;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PrefUtils getInstance(Context context){
        if (mInstance == null){
            mInstance = new PrefUtils(context.getApplicationContext());
        }
        return mInstance;
    }

    public String getSetCounty(){
        String county = sharedPreferences.getString(context.getString(R.string.pref_county),
                context.getString(R.string.default_county));
        return county;
    }

    public String getSetCategory(){
        String category = sharedPreferences.getString(context.getString(R.string.pref_category),
                "All");
        if (category.equalsIgnoreCase("All event categories")){
            return "All";
        } else {
            return category;
        }
    }

    public String getSetUsername(){
        String user = sharedPreferences.getString(context.getString(R.string.pref_user),
                context.getString(R.string.default_user));
        return user;
    }

    public RequestParams getDefaultParams(RequestParams params ){
        if (params == null){
            params = new RequestParams();
        }
        params.put(BackEnd.PARAM_USERNAME, getSetUsername());
        params.put(BackEnd.API_KEY,"WAMBui25");
        params.put(BackEnd.PARAM_COUNTY, getSetCounty());
        params.put(BackEnd.PARAM_CATEGORY, getSetCategory());
        return params;
    }

    public void invalidate() {
        sharedPreferences = null;
        context = null;
        this.mInstance = null;
    }

    public boolean getIsFirstInstance() {
        boolean isFirstInstance = sharedPreferences.getBoolean(context.getString(R.string.pref_is_first_Instance), true);
        return isFirstInstance;
    }

    public void writeIsFirstInstance(boolean isFirstInstance){
        sharedPreferences.edit().putBoolean(context.getString(R.string.pref_is_first_Instance), isFirstInstance).apply();
    }

    public long getLastSweepDate(){
        return sharedPreferences.getLong(context.getString(R.string.pref_last_sweep),
                DateUtils.getIntegerDate(new LocalDate().toString()));
    }

    public void writeLastPersistenceDataCleanUp(long date){
        sharedPreferences.edit().putLong(context.getString(R.string.pref_last_sweep),
                date).apply();
    }

    public void writeDefaultInitialPrefs(String username){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_user), username);
        editor.putBoolean(context.getString(R.string.pref_has_finished_starter_settings), false);
        editor.putBoolean(context.getString(R.string.pref_is_first_install), false);
        editor.putLong(context.getString(R.string.pref_last_sweep), DateUtils.getIntegerDate(new LocalDate().toString()));
        editor.putLong(context.getString(R.string.pref_obsolete_last_check),DateUtils.getIntegerDate(new LocalDate().toString()));
        editor.putBoolean(context.getString(R.string.pref_has_downloaded_new_version), true);
        editor.apply();
    }

    public boolean isFirstRun(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_is_first_install), true);
    }

    public void writeSetCategory(String category) {
        sharedPreferences.edit().putString(context.getString(R.string.pref_category),
                category).apply();
    }

    public boolean allowLoadingEventsForAllTabs() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_load_events_in_all_tabs), false);
    }

    public void writeObsoleteLastCheck(long integerDate) {
        sharedPreferences.edit().putLong(context.getString(R.string.pref_obsolete_last_check),
                integerDate).apply();
    }

    public long getLastObsoleteCheck() {
        return sharedPreferences.getLong(context.getString(R.string.pref_obsolete_last_check),
                DateUtils.getIntegerDate(new LocalDate().toString()));
    }

    public boolean hasDownloadedNewVersion() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_has_downloaded_new_version),
                true);
    }

    public void writeHasNotDownloadedNewVersion() {
        sharedPreferences.edit().putBoolean(context.getString(R.string.pref_has_downloaded_new_version), false).apply();
    }

    public void writeSetCounty(String county) {
        sharedPreferences.edit().putString(context.getString(R.string.pref_county), county).apply();
    }

    public boolean autoDownloadPosters() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_autodownload_posters), true);
    }

    public void writePrefAutoDownloadPosters(boolean autoDownloadPosters) {
        sharedPreferences.edit().putBoolean(context.getString(R.string.pref_autodownload_posters),
                autoDownloadPosters).apply();
    }

    public boolean hasFinishedStarterSettings() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_has_finished_starter_settings),false);
    }

    public void writePrefLoadEventsInAllTabs(boolean loadEventsInAllTabs) {
        sharedPreferences.edit().putBoolean(
                context.getString(R.string.pref_load_events_in_all_tabs),
                loadEventsInAllTabs).apply();
    }

    public void writePrefCheckNewCommentsForSavedEvents(boolean checkNewCommentsForSavedEvents) {
        sharedPreferences.edit().putBoolean(
                context.getString(R.string.pref_check_new_comments_for_saved_events),
                checkNewCommentsForSavedEvents).apply();
    }

    public void writePrefHasFinishedStarterSettings(boolean hasFinishedStarterSettings) {
        sharedPreferences.edit().putBoolean(
                context.getString(R.string.pref_has_finished_starter_settings),
                hasFinishedStarterSettings).apply();
    }
}
