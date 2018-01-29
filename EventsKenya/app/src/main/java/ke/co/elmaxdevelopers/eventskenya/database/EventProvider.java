package ke.co.elmaxdevelopers.eventskenya.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class EventProvider extends ContentProvider {
	//private static final String TAG = EventProvider.class.getSimpleName();
	private DbHelper dbHelper;
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(EventContract.AUTHORITY, EventContract.TABLE_EVENTS, EventContract.DIR);
		sURIMatcher.addURI(EventContract.AUTHORITY, EventContract.TABLE_COMMENTS, EventContract.DIR);
		sURIMatcher.addURI(EventContract.AUTHORITY, EventContract.TABLE_QUEUE, EventContract.DIR);
		sURIMatcher.addURI(EventContract.AUTHORITY, EventContract.TABLE_PERSISTENCE, EventContract.DIR);
		sURIMatcher.addURI(EventContract.AUTHORITY, EventContract.TABLE_SERVICES, EventContract.DIR);

		sURIMatcher.addURI(EventContract.AUTHORITY, EventContract.TABLE_EVENTS + "/#", EventContract.ITEM);
		sURIMatcher.addURI(EventContract.AUTHORITY, EventContract.TABLE_COMMENTS + "/#", EventContract.ITEM);
		sURIMatcher.addURI(EventContract.AUTHORITY, EventContract.TABLE_QUEUE + "/#", EventContract.ITEM);
		sURIMatcher.addURI(EventContract.AUTHORITY, EventContract.TABLE_PERSISTENCE + "/#", EventContract.ITEM);
		sURIMatcher.addURI(EventContract.AUTHORITY, EventContract.TABLE_SERVICES + "/#", EventContract.ITEM);
	}
	
	@Override
	public boolean onCreate() {
		dbHelper = new DbHelper(getContext());
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case EventContract.DIR:
			switch (resolveTable(uri)){
                case EventContract.TABLE_EVENTS:
                    return EventContract.EVENT_TYPE_DIR;
                case EventContract.TABLE_COMMENTS:
                    return EventContract.COMMENT_TYPE_DIR;
				case EventContract.TABLE_QUEUE:
					return EventContract.QUEUE_TYPE_DIR;
                case EventContract.TABLE_PERSISTENCE:
                    return EventContract.PERSISTENCE_TYPE_DIR;
				case EventContract.TABLE_SERVICES:
					return EventContract.SERVICE_TYPE_DIR;
                default:
                    throw new IllegalArgumentException("Illegal Uri: "+uri);
            }
		case EventContract.ITEM:
            switch (resolveTable(uri)){
                case EventContract.TABLE_EVENTS:
                    return EventContract.EVENT_TYPE_ITEM;
                case EventContract.TABLE_COMMENTS:
                    return EventContract.COMMENT_TYPE_ITEM;
				case EventContract.TABLE_QUEUE:
					return EventContract.QUEUE_TYPE_ITEM;
                case EventContract.TABLE_PERSISTENCE:
                    return EventContract.PERSISTENCE_TYPE_ITEM;
				case EventContract.TABLE_SERVICES:
					return EventContract.SERVICE_TYPE_ITEM;
                default:
                    throw new IllegalArgumentException("Illegal Uri: "+uri);
            }
		default:
			throw new IllegalArgumentException("Illegal Uri: "+uri);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri ret = null;
		if (sURIMatcher.match(uri) != EventContract.DIR) {
			throw new IllegalArgumentException("Illegal uri: "+uri);
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId = db.insertWithOnConflict(resolveTable(uri), null, values, SQLiteDatabase.CONFLICT_IGNORE);
		if (rowId == -1) {
			/**
			long id  = values.getAsLong(EventContract.TableEvents.ID);
			ret = ContentUris.withAppendedId(uri, id);
            Log.d("Toshde", "Inserted "+ret);
            getContext().getContentResolver().notifyChange(uri, null);
             */
            Log.d("Toshde", "Failed to insert "+uri.toString());
		} else {
            Log.d("Toshde", "Inserted successfully "+uri.toString());
        }
		return ret;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String where;
		switch (sURIMatcher.match(uri)) {
		case EventContract.DIR:
			where = selection;
			break;
		case EventContract.ITEM:
			long id = ContentUris.parseId(uri);
			where = EventContract.TableEvents.ID
					+"="
					+id
					+(TextUtils.isEmpty(selection) ? "" : " and ( "+selection+ " )");
			break;
		default:
			throw new IllegalArgumentException("Illegal uri: "+uri);
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int ret = db.update(resolveTable(uri), values, where, selectionArgs);
		if (ret > 0 ) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return ret;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String where;
		switch (sURIMatcher.match(uri)) {
		case EventContract.DIR:
			where = (selection == null) ? "1" : selection;
			break;
		case EventContract.ITEM:
			long id = ContentUris.parseId(uri);
			where = EventContract.TableEvents.ID
					+"="
					+id
					+(TextUtils.isEmpty(selection) ? "" : "and ("+selection+" )");
		default:
			throw new IllegalArgumentException("Illegal uri: "+uri);
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int ret = db.delete(resolveTable(uri), where, selectionArgs);
		if (ret > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return ret;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder qb  = new SQLiteQueryBuilder();
		qb.setTables(resolveTable(uri));
		switch (sURIMatcher.match(uri)) {
		case EventContract.DIR:
			break;
		case EventContract.ITEM:
			qb.appendWhere(EventContract.TableEvents.ID+"="+uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Illegal uri: "+uri);
		}
		//String orderBy = (TextUtils.isEmpty(sortOrder) ? EventContract.DEFAULT_SORT : sortOrder);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, null);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

    private String resolveTable(Uri uri){
        return uri.toString().substring(EventContract.BASE_URI.toString().length());
    }
	
}
