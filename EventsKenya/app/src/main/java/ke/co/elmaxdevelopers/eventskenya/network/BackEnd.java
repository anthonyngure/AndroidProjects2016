package ke.co.elmaxdevelopers.eventskenya.network;

/**
 * Created by Tosh on 12/31/2015.
 */
public class BackEnd {

    public static final String PARAM_DATE = "date";
    public static final String PARAM_LAST_ID = "last_id";
    public static final String PARAM_COUNTY = "county";
    public static final String PARAM_CATEGORY = "category";
    public static final String EVENTS_END_POINT = "events";
    public static final String ADD_COMMENT = "comments/add/";
    public static final String GET_DETAILS = "events/details/";
    public static final String USERS_ADD = "users/add";
    public static final String UPDATE_USERNAME = "users/updateusername";
    public static final String SERVICES_END_POINT = "services";
    public static final String EVENTS_ADD_GOING = "events/add/going/";
    public static final String EVENTS_REMOVE_GOING = "events/remove/going/";
    public static final String EVENTS_ADD_INTERESTED = "events/add/interested/";
    public static final String EVENTS_REMOVE_INTERESTED = "events/remove/interested/";

    private static final String BASE_URL = "http://192.168.47.1/eventskenya.aimdevelopers.com/api/v1/";
    //private static final String BASE_URL = "http://elmaxdevelopers.co.ke/api/v1/";
    public static final String PARAM_COMMENT_CONTENT = "content";
    public static final String PARAM_USERNAME = "username";
    public static final String SYNC_COMMENTS = "comments/sync";
    public static final String REFRESH_COMMENTS = "comments/refresh/";
    public static final String ADD_EVENT = "events/add";
    public static final String ADD_IMAGE = "events/images";
    public static final String PARAM_NEW_USERNAME = "new_username";
    public static final String CHECK_OBSOLETE = "checkobsolete";
    public static final String ADD_SERVICE = "services/add";
    public static final String PARAM_SERVICE_TYPE = "service_type";
    public static final String API_KEY = "api_key";

    public static String absoluteUrl(String relativeUrl) {
        return BASE_URL+relativeUrl.trim().replace(" ","%20");
    }

}
