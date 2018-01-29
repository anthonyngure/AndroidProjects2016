package ke.co.elmaxdevelopers.cheaters.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public static final String TABLE_PENDING_MESSAGES = "pending_messages";
    public static final String TABLE_PENDING_CALLS = "pending_calls";
    public static final String TABLE_CONFIG = "config";
    private static final String DB_NAME = "cheaterS";
    private static final int DB_VERSION = 2;

    public final String sql_create_pending_messages = "CREATE TABLE " + TABLE_PENDING_MESSAGES
            + "(_id INTEGER PRIMARY KEY, address TEXT, body TEXT, timestamp LONG)";

    public final String sql_create_pending_calls = "CREATE TABLE " + TABLE_PENDING_CALLS
            + "(_id INTEGER PRIMARY KEY, address TEXT, body TEXT, timestamp LONG)";

    public final String sql_create_config = "CREATE TABLE " + TABLE_CONFIG + " stop INTEGER)";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_create_pending_messages);
        db.execSQL(sql_create_config);
        db.execSQL(sql_create_pending_calls);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PENDING_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PENDING_CALLS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONFIG);
        onCreate(db);
    }
}
