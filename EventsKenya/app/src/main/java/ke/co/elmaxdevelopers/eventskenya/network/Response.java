package ke.co.elmaxdevelopers.eventskenya.network;

/**
 * Created by Tosh on 12/14/2015.
 */
public class Response {

    public static final String SUCCESS = "success";
    public static final String DATA = "data";
    public static final String FAILURE = "failure";
    public static final String MESSAGE = "message";

    public static final class ServiceData {

        public static final String NAME = "name";
        public static final String ABOUT = "about";
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";
        public static final String IMAGE_ONE = "image_one";
        public static final String IMAGE_TWO = "image_two";
        public static final String ID = "id";
        public static final String SERVICE_TYPE = "service_type";
    }

    public static final class EventData {
        public static final String ID = "id";
        public static final String COMMENTS = "comments";
        public static final String NO_INTERESTED = "no_interested";
        public static final String NO_GOING = "no_going";
        public static final String DESCRIPTION = "description";
        public static final String TICKETS_LINK = "tickets_link";
        public static final String IMAGE = "image";
        public static final String VENUE = "venue";
        public static final String TIME = "time";
        public static final String END_DATE = "end_date";
        public static final String START_DATE = "start_date";
        public static final String NAME = "name";
        public static final String NEW_COMMENTS = "new_comments";
        public static final String ADVANCE = "advance";
        public static final String REGULAR = "regular";
        public static final String VIP = "vip";
        public static final String CATEGORY = "category";
        public static final String COUNTY = "county";
        public static final String PROMOTER = "promoter";
        public static final String ORGANIZER = "organizer";
        public static final String PARKING_INFO = "parking_info";
        public static final String SECURITY_INFO = "security_info";
        public static final String TOTAL_COMMENTS = "total_comments";
    }

    public static final class CommentData {
        public static final String EVENT_ID = "event_id";
        public static final String CONTENT = "content";
        public static final String CREATED_AT = "created_at";
        public static final String ID = "id";
        public static final String USERNAME = "username";
    }

    public static class VersionControlData {
        public static final String VERSION_CODE = "version_code";
        public static final String VERSION_NAME = "version_name";
        public static final String MESSAGE = "message";
        public static final String DOWNLOAD_LINK = "download_link";
        public static final String RELEASE_DATE = "release_date";
    }
}
