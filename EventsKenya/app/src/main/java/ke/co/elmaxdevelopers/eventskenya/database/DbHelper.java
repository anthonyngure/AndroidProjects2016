package ke.co.elmaxdevelopers.eventskenya.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public final String sql_create_persistence = "CREATE TABLE "+ EventContract.TABLE_PERSISTENCE
            +"("
            +EventContract.TablePersistence.DATE+" INTEGER, "
            +EventContract.TablePersistence.TOTAL_EVENTS+" INTEGER, "
            +EventContract.TablePersistence.NO_MORE_DATA+" INTEGER)";

    public final String sql_create_events = "CREATE TABLE " + EventContract.TABLE_EVENTS
            + "("
            + EventContract.TableEvents.ID + " INTEGER PRIMARY KEY, "
            + EventContract.TableEvents.EVENT_ID + " INTEGER, "
            + EventContract.TableEvents.NAME + " TEXT, "
            + EventContract.TableEvents.VENUE + " TEXT, "
            + EventContract.TableEvents.START_DATE + " INTEGER, "
            + EventContract.TableEvents.END_DATE + " INTEGER, "
            + EventContract.TableEvents.DATE + " TEXT, "
            + EventContract.TableEvents.TIME + " TEXT, "
            + EventContract.TableEvents.DESCRIPTION + " TEXT, "
            + EventContract.TableEvents.IMAGE_URL + " TEXT, "
            + EventContract.TableEvents.THUMBNAIL + " BLOB, "
            + EventContract.TableEvents.IMAGE + " BLOB, "
            + EventContract.TableEvents.NO_GOING + " INTEGER, "
            + EventContract.TableEvents.NO_INTERESTED + " INTEGER, "
            + EventContract.TableEvents.TOTAL_COMMENTS + " INTEGER, "
            + EventContract.TableEvents.TICKETS_LINK + " TEXT, "
            + EventContract.TableEvents.ADVANCE + " TEXT, "
            + EventContract.TableEvents.REGULAR + " TEXT, "
            + EventContract.TableEvents.VIP + " TEXT, "
            + EventContract.TableEvents.PARKING_INFO + " TEXT, "
            + EventContract.TableEvents.SECURITY_INFO + " TEXT, "
            + EventContract.TableEvents.ORGANIZER + " TEXT, "
            + EventContract.TableEvents.PROMOTER + " TEXT, "
            + EventContract.TableEvents.NEW_COMMENTS + " INTEGER, "
            + EventContract.TableEvents.HAS_LOADED_DETAILS + " INTEGER, "
            + EventContract.TableEvents.STATUS_INTERESTED + " INTEGER, "
            + EventContract.TableEvents.STATUS_GOING + " INTEGER)";

    public final String sql_create_comments = "CREATE TABLE " + EventContract.TABLE_COMMENTS
            + "("
            + EventContract.TableComments.ID + " INTEGER PRIMARY KEY, "
            + EventContract.TableComments.COMMENT_ID + " INTEGER, "
            + EventContract.TableComments.EVENT_ID + " INTEGER, "
            + EventContract.TableComments.EVENT_START_DATE + " INTEGER, "
            + EventContract.TableComments.USERNAME + " TEXT, "
            + EventContract.TableComments.CREATED_AT + " TEXT, "
            + EventContract.TableComments.CONTENT + " TEXT)";

    public final String sql_create_queue = "CREATE TABLE " + EventContract.TABLE_QUEUE
            + "("
            + EventContract.TableQueue.EVENT_DATE + " INTEGER, "
            + EventContract.TableQueue.EVENT_ID + " INTEGER, "
            + EventContract.TableQueue.OPERATION + " INTEGER)";

    public final String sql_create_services = "CREATE TABLE " + EventContract.TABLE_SERVICES
            + "("
            + EventContract.TableServices.ID + " INTEGER PRIMARY KEY, "
            + EventContract.TableServices.SERVICE_ID + " INTEGER, "
            + EventContract.TableServices.NAME + " TEXT, "
            + EventContract.TableServices.SERVICE_TYPE + " TEXT, "
            + EventContract.TableServices.IMAGE_ONE_URL + " TEXT, "
            + EventContract.TableServices.IMAGE_TWO_URL + " TEXT, "
            + EventContract.TableServices.EMAIL + " TEXT, "
            + EventContract.TableServices.IMAGE_ONE + " BLOB, "
            + EventContract.TableServices.IMAGE_TWO + " BLOB, "
            + EventContract.TableServices.PHONE + " TEXT, "
            + EventContract.TableServices.SAVED + " INTEGER, "
            + EventContract.TableServices.ABOUT + " TEXT)";

    public DbHelper(Context context) {
        super(context, EventContract.DB_NAME, null, EventContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sql_create_events);
        db.execSQL(sql_create_comments);
        db.execSQL(sql_create_queue);
        db.execSQL(sql_create_persistence);
        db.execSQL(sql_create_services);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+EventContract.TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS "+EventContract.TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS "+EventContract.TABLE_QUEUE);
        db.execSQL("DROP TABLE IF EXISTS "+EventContract.TABLE_PERSISTENCE);
        db.execSQL("DROP TABLE IF EXISTS "+EventContract.TABLE_SERVICES);
        onCreate(db);
    }
}
