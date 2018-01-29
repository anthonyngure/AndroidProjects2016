package ke.co.elmaxdevelopers.cheaters.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ke.co.elmaxdevelopers.cheaters.model.Message;


/**
 * Created by Tosh on 1/26/2016.
 */
public class DataController {

    private static final String TAG = "Toshde";
    private static DataController mInstance;
    private Context mContext;
    private DbHelper dbHelper;

    private DataController(Context context){
        this.mContext = context;
        dbHelper = new DbHelper(mContext);
    }

    public static synchronized DataController getInstance(Context context){
        if (mInstance == null){
            mInstance = new DataController(context.getApplicationContext());
        }
        return mInstance;
    }

    public void saveToPendingMessages(Message message){
        if (!messageExists(message)){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.clear();
            contentValues.put("body", message.getBody());
            contentValues.put("address", message.getAddress());
            contentValues.put("timestamp", message.getTimestamp());
            long insert  = db.insert(dbHelper.TABLE_PENDING_MESSAGES, null, contentValues);
            Log.d(TAG, insert+"");
        }
    }

    private boolean messageExists(Message message){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor mCursor = db.query(
                dbHelper.TABLE_PENDING_MESSAGES,
                new String[] {"body"},
                "body = ? ",
                new String[] {message.getBody()},
                null,
                null,
                null
        );
        if (mCursor != null && mCursor.getCount() > 0){
            closeCursor(mCursor);
            closeDb(db);
            return true;
        } else {
            closeCursor(mCursor);
            closeDb(db);
            return false;
        }
    }

    private void closeCursor(Cursor cursor){
        if (cursor != null && !cursor.isClosed()){
            cursor.close();
        }
    }

    private void closeDb(SQLiteDatabase db){
        if (db != null && db.isOpen()){
            db.close();
        }
    }

    public void clearPending() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                dbHelper.TABLE_PENDING_MESSAGES,
                null,
                null
        );
        closeDb(db);
    }
}
