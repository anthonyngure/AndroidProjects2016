package ke.co.elmaxdevelopers.eventskenya.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class EventContract {
	
	public static final String DB_NAME = "eventskenya";
	public static final int DB_VERSION = 52;
	
	public static final String TABLE_EVENTS = "events";
	public static final String TABLE_COMMENTS = "comments";
	public static final String TABLE_QUEUE = "queue";
	public static final String TABLE_SERVICES = "services";
	public static final String TABLE_PERSISTENCE = "persistence";

	public static final String DEFAULT_SORT = TableEvents.ID + " DESC";
	
	public static final String AUTHORITY = "ke.co.elmaxdevelopers.eventskenya.EventProvider";
	public static final Uri BASE_URI = Uri.parse("content://"+AUTHORITY+"/");
	public static final Uri TABLE_EVENTS_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_EVENTS);
	public static final Uri TABLE_COMMENTS_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_COMMENTS);
	public static final Uri TABLE_QUEUE_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_QUEUE);
	public static final Uri TABLE_PERSISTENCE_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_PERSISTENCE);
	public static final Uri TABLE_SERVICE_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_SERVICES);

	public static final int ITEM = 1;
	public static final int DIR = 2;

	public static final String EVENT_TYPE_ITEM = 
			"vnd.android.cursor.item/vnd.ke.co.elmaxdevelopers.eventskenya.provider.event";
	public static final String COMMENT_TYPE_ITEM =
			"vnd.android.cursor.item/vnd.ke.co.elmaxdevelopers.eventskenya.provider.comment";
    public static final String QUEUE_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.ke.co.elmaxdevelopers.eventskenya.provider.queue";
    public static final String PERSISTENCE_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.ke.co.elmaxdevelopers.eventskenya.provider.persistence";
    public static final String SERVICE_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.ke.co.elmaxdevelopers.eventskenya.provider.service";

	public static final String EVENT_TYPE_DIR = 
			"vnd.android.cursor.dir/vnd.ke.co.elmaxdevelopers.eventskenya.provider.event";
	public static final String COMMENT_TYPE_DIR =
			"vnd.android.cursor.dir/vnd.ke.co.elmaxdevelopers.eventskenya.provider.comment";
    public static final String QUEUE_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.ke.co.elmaxdevelopers.eventskenya.provider.queue";
    public static final String PERSISTENCE_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.ke.co.elmaxdevelopers.eventskenya.provider.queue";
    public static final String SERVICE_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.ke.co.elmaxdevelopers.eventskenya.provider.service";
	
	public class TableEvents {
		public static final String ID = BaseColumns._ID;
		public static final String EVENT_ID = "event_id";
		public static final String NAME = "name";
		public static final String VENUE = "venue";
		public static final String START_DATE = "start_date";
		public static final String END_DATE = "end_date";
		public static final String DATE = "date";
		public static final String TIME = "time";
		public static final String DESCRIPTION = "description";
		public static final String IMAGE_URL = "image_url";
		public static final String NEW_COMMENTS = "new_comments";
        public static final String STATUS_INTERESTED = "interested";
        public static final String STATUS_GOING = "going";
		public static final String NO_GOING = "no_going";
		public static final String NO_INTERESTED = "no_interested";
		public static final String TOTAL_COMMENTS = "total_comments";
		public static final String PARKING_INFO = "parking_info";
		public static final String SECURITY_INFO = "security_info";
		public static final String ORGANIZER = "organizer";
		public static final String PROMOTER = "promoter";
		public static final String ADVANCE = "advance";
		public static final String REGULAR = "regular";
		public static final String VIP = "vip";
		public static final String TICKETS_LINK = "tickets_link";
		public static final String HAS_LOADED_DETAILS = "has_loaded_details";
		public static final String THUMBNAIL = "thumbnail";
		public static final String IMAGE = "image";
	}

	public class TableComments {
		public static final String ID = BaseColumns._ID;
		public static final String EVENT_ID = "event_id";
		public static final String COMMENT_ID = "comment_id";
		public static final String USERNAME = "author";
		public static final String CREATED_AT = "created_at";
		public static final String CONTENT = "content";
		public static final String EVENT_START_DATE = "event_start_date"; //Helps in cleaning comments
	}

	public class TableQueue {
		public static final String EVENT_DATE = "date";
		public static final String EVENT_ID = "event_id";
		public static final String OPERATION = "operation";
	}

    public class TablePersistence {
        public static final String DATE = "date";
        public static final String TOTAL_EVENTS = "total_events";
        public static final String NO_MORE_DATA = "no_more_data";
    }

	public static class TableServices {
		public static final String ID = BaseColumns._ID;
		public static final String SERVICE_ID = "service_id";
		public static final String NAME = "name";
		public static final String ABOUT = "about";
		public static final String PHONE = "phone";
		public static final String IMAGE_ONE = "image_one";
		public static final String IMAGE_TWO = "image_two";
		public static final String EMAIL = "email";
		public static final String IMAGE_ONE_URL = "image_one_url";
		public static final String IMAGE_TWO_URL = "image_two_url";
		public static final String SERVICE_TYPE = "service_type";
		public static final String SAVED = "saved";
	}
}
