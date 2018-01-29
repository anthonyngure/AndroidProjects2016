package ke.co.elmaxdevelopers.eventskenya.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import org.joda.time.LocalDate;

import java.util.ArrayList;

import ke.co.elmaxdevelopers.eventskenya.activity.CommentsActivity;
import ke.co.elmaxdevelopers.eventskenya.model.Comment;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.model.Queue;
import ke.co.elmaxdevelopers.eventskenya.model.Service;
import ke.co.elmaxdevelopers.eventskenya.utils.DateUtils;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;

/**
 * Created by Tosh on 1/5/2016.
 */
public class DataController {

    public static final String ACTION_EVENT_DATA_CHANGED= "ke.co.elmaxdevelopers.eventskenya.action.EVENT_COMMENTS_CHANGE";
    private static DataController mInstance;
    private static Context context;
    private static ContentResolver contentResolver;
    private static ArrayList<Event> savedEventArrayList;
    private static ArrayList<Service> savedServiceArrayList;


    private DataController(Context mContext){
        this.context = mContext;
        contentResolver = context.getContentResolver();
        savedEventArrayList = null;
        savedServiceArrayList = null;
    }

    public static DataController getInstance(Context mContext){
        if (mInstance == null){
            mInstance = new DataController(mContext.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Saves or re saves an event, the event must have all of its data changed accordingly
     * @param e
     */
    public void saveEvent(Event e){
        if (eventExists(e)){
            contentResolver.update(
                    EventContract.TABLE_EVENTS_URI,
                    createValues(e),
                    EventContract.TableEvents.EVENT_ID+" = ? ",
                    new String[]{String.valueOf(e.getId())}
            );
        } else {
            contentResolver.insert(EventContract.TABLE_EVENTS_URI, createValues(e));
        }
        if (e.getComments().size() > 0 && e.getComments() != null){
           saveAllComments(e.getComments(), e.getStartDate() );
        }
    }

    /**
     * Saves multiple events
     * @param eventArrayList
     */

    public void saveAllEvents(ArrayList<Event> eventArrayList){
        for (int i = 0; i < eventArrayList.size(); i++){
            saveEvent(eventArrayList.get(i));
        }
    }

    /**
     * @param event event to check if is in database;
     * @return true if it exists
     */
    private boolean eventExists(Event event){
        Cursor mCursor = contentResolver.query(
                EventContract.TABLE_EVENTS_URI,
                new String[]{EventContract.TableEvents.EVENT_ID},
                EventContract.TableEvents.EVENT_ID+" = ?",
                new String[] {String.valueOf(event.getId())},
                null
        );
        if (mCursor != null && mCursor.getCount() > 0) {
            closeCursor(mCursor);
            return true;
        } else {
            closeCursor(mCursor);
            return false;
        }
    }

    public void saveService(Service service){
        if (serviceExists(service)){
            contentResolver.update(
                    EventContract.TABLE_SERVICE_URI,
                    createValues(service),
                    EventContract.TableServices.SERVICE_ID + " = ? ",
                    new String[]{String.valueOf(service.getId())}
            );
        } else {
            contentResolver.insert(EventContract.TABLE_SERVICE_URI, createValues(service));
        }

        invalidate();
    }

    private boolean serviceExists(Service event){
        Cursor mCursor = contentResolver.query(
                EventContract.TABLE_SERVICE_URI,
                new String[]{EventContract.TableServices.SERVICE_ID},
                EventContract.TableServices.SERVICE_ID+" = ?",
                new String[] {String.valueOf(event.getId())},
                null
        );
        if (mCursor != null && mCursor.getCount() > 0) {
            closeCursor(mCursor);
            return true;
        } else {
            closeCursor(mCursor);
            return false;
        }
    }

    public void deleteSavedService(Service service){
        contentResolver.delete(
                EventContract.TABLE_SERVICE_URI,
                EventContract.TableServices.SERVICE_ID+" = ? ",
                new String[] {String.valueOf(service.getId())}
        );
        invalidate();
    }

    private ContentValues createValues(Service service){
        ContentValues values = new ContentValues();
        values.clear();
        values.put(EventContract.TableServices.SERVICE_ID, service.getId());
        values.put(EventContract.TableServices.NAME, service.getName());
        values.put(EventContract.TableServices.PHONE, service.getPhone());
        values.put(EventContract.TableServices.EMAIL, service.getEmail());
        values.put(EventContract.TableServices.IMAGE_ONE_URL, service.getImageOneUrl());
        values.put(EventContract.TableServices.IMAGE_TWO_URL, service.getImageTwoUrl());
        values.put(EventContract.TableServices.IMAGE_ONE, service.getImageOne());
        values.put(EventContract.TableServices.IMAGE_TWO, service.getImageTwo());
        values.put(EventContract.TableServices.ABOUT, service.getAbout());
        values.put(EventContract.TableServices.SERVICE_TYPE, service.getServiceType());
        values.put(EventContract.TableServices.SAVED, service.getSaved());
        return values;
    }

    private ContentValues createValues(Event e) {
        ContentValues values = new ContentValues();
        values.clear();
        values.put(EventContract.TableEvents.EVENT_ID, e.getId());
        values.put(EventContract.TableEvents.NAME, e.getName());
        values.put(EventContract.TableEvents.VENUE, e.getVenue());
        values.put(EventContract.TableEvents.START_DATE, e.getStartDate());
        values.put(EventContract.TableEvents.END_DATE, e.getEndDate());
        values.put(EventContract.TableEvents.DATE, e.getDate());
        values.put(EventContract.TableEvents.TIME, e.getTime());
        values.put(EventContract.TableEvents.DESCRIPTION, e.getDescription());
        values.put(EventContract.TableEvents.IMAGE_URL, e.getImageUrl());
        values.put(EventContract.TableEvents.THUMBNAIL, e.getThumbnail());
        values.put(EventContract.TableEvents.IMAGE, e.getImage());
        values.put(EventContract.TableEvents.NO_GOING, e.getNoGoing());
        values.put(EventContract.TableEvents.NEW_COMMENTS, e.getNewComments());
        values.put(EventContract.TableEvents.NO_INTERESTED, e.getNoInterested());
        values.put(EventContract.TableEvents.STATUS_GOING, e.getStatusGoing());
        values.put(EventContract.TableEvents.STATUS_INTERESTED, e.getStatusInterested());
        values.put(EventContract.TableEvents.TICKETS_LINK, e.getTicketsLink());
        values.put(EventContract.TableEvents.TOTAL_COMMENTS, e.getTotalComments());
        values.put(EventContract.TableEvents.ADVANCE, e.getAdvance());
        values.put(EventContract.TableEvents.REGULAR, e.getRegular());
        values.put(EventContract.TableEvents.VIP, e.getVip());
        values.put(EventContract.TableEvents.SECURITY_INFO, e.getSecurityInfo());
        values.put(EventContract.TableEvents.PARKING_INFO, e.getParkingInfo());
        values.put(EventContract.TableEvents.ORGANIZER, e.getOrganizer());
        values.put(EventContract.TableEvents.PROMOTER, e.getPromoter());
        values.put(EventContract.TableEvents.HAS_LOADED_DETAILS, e.getHasLoadedDetails());
        return values;
    }

    private void saveComment(Comment comment, long eventStartDate){
        if (!commentExists(comment)){
            contentResolver.insert(EventContract.TABLE_COMMENTS_URI, createContentValues(comment, eventStartDate));
        }
    }

    private ContentValues createContentValues(Comment comment, long eventStartDate){
        ContentValues mContentValues = new ContentValues();
        mContentValues.clear();
        mContentValues.put(EventContract.TableComments.COMMENT_ID, comment.getId());
        mContentValues.put(EventContract.TableComments.EVENT_ID, comment.getEventId());
        mContentValues.put(EventContract.TableComments.EVENT_START_DATE, eventStartDate);
        mContentValues.put(EventContract.TableComments.USERNAME, comment.getUsername());
        mContentValues.put(EventContract.TableComments.CREATED_AT, comment.getCreatedAt());
        mContentValues.put(EventContract.TableComments.CONTENT, comment.getContent());
        return mContentValues;
    }

    private boolean commentExists(Comment comment){
        Cursor mCursor = contentResolver.query(
                EventContract.TABLE_COMMENTS_URI,
                new String[]{EventContract.TableComments.COMMENT_ID},
                EventContract.TableComments.COMMENT_ID+" = ? ",
                new String[] {String.valueOf(comment.getId())},
                null
        );
        if (mCursor != null && mCursor.getCount() > 0){
            closeCursor(mCursor);
            return true;
        } else {
            closeCursor(mCursor);
            return false;
        }

    }

    private void closeCursor(Cursor mCursor){
        if (mCursor != null && !mCursor.isClosed()){
            mCursor.close();
        }
    }

    private void saveAllComments(ArrayList<Comment> commentArrayList, long eventStartDate){
        for(int i = 0; i < commentArrayList.size(); i++) {
            saveComment(commentArrayList.get(i), eventStartDate );
        }
    }

    public ArrayList<Event> readSavedEvents(Cursor cursor) {

        if (cursor == null){
            cursor = contentResolver.query(
                    EventContract.TABLE_EVENTS_URI,
                    null,
                    null,
                    null,
                    null
            );
        }

        ArrayList<Event> events = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Event e = new Event();
                e.setId(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.EVENT_ID)));
                e.setName(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.NAME)));
                e.setVenue(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.VENUE)));
                e.setStartDate(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.START_DATE)));
                e.setEndDate(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.END_DATE)));
                e.setDate(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.DATE)));
                e.setTime(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.TIME)));
                e.setDescription(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.DESCRIPTION)));
                e.setImageUrl(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.IMAGE_URL)));
                e.setThumbnail(cursor.getBlob(cursor.getColumnIndex(EventContract.TableEvents.THUMBNAIL)));
                e.setImage(cursor.getBlob(cursor.getColumnIndex(EventContract.TableEvents.IMAGE)));
                e.setNoGoing(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.NO_GOING)));
                e.setNewComments(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.NEW_COMMENTS)));
                e.setNoInterested(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.NO_INTERESTED)));
                e.setStatusGoing(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.STATUS_GOING)));
                e.setStatusInterested(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.STATUS_INTERESTED)));
                e.setTotalComments(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.TOTAL_COMMENTS)));
                e.setTicketsLink(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.TICKETS_LINK)));
                e.setAdvance(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.ADVANCE)));
                e.setRegular(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.REGULAR)));
                e.setVip(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.VIP)));
                e.setPromoter(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.PROMOTER)));
                e.setOrganizer(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.ORGANIZER)));
                e.setSecurityInfo(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.SECURITY_INFO)));
                e.setParkingInfo(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.PARKING_INFO)));
                e.setSecurityInfo(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.SECURITY_INFO)));
                e.setHasLoadedDetails(cursor.getInt(cursor.getColumnIndex(EventContract.TableEvents.HAS_LOADED_DETAILS)));
                e.setComments(readSavedComments(cursor.getString(cursor.getColumnIndex(EventContract.TableEvents.EVENT_ID))));
                events.add(e);
            } while (cursor.moveToNext());
        }
        closeCursor(cursor);
        return events;
    }

    private ArrayList<Comment> readSavedComments(String eventId) {
        ArrayList<Comment> commentArrayList = new ArrayList<>();
        Cursor mCursor = contentResolver.query(
                EventContract.TABLE_COMMENTS_URI,
                null,
                EventContract.TableComments.EVENT_ID + " = ?",
                new String[]{eventId},
                null
        );
        if (mCursor != null && mCursor.getCount() > 0) {
            while (mCursor.moveToNext()) {
                Comment comment = new Comment();

                String username = mCursor.getString(mCursor.getColumnIndex(EventContract.TableComments.USERNAME));
                comment.setUsername(username);
                comment.setId(mCursor.getInt(mCursor.getColumnIndex(EventContract.TableComments.COMMENT_ID)));
                comment.setEventId(mCursor.getInt(mCursor.getColumnIndex(EventContract.TableComments.EVENT_ID)));
                comment.setCreatedAt(mCursor.getString(mCursor.getColumnIndex(EventContract.TableComments.CREATED_AT)));
                comment.setContent(mCursor.getString(mCursor.getColumnIndex(EventContract.TableComments.CONTENT)));
                commentArrayList.add(comment);
            }
        }
        closeCursor(mCursor);
        return commentArrayList;
    }


    public ArrayList<Event> getSavedEvents(){
        if (savedEventArrayList == null){
            savedEventArrayList = new ArrayList<>();
            savedEventArrayList = readSavedEvents(null);
        }
        return savedEventArrayList;
    }

    private ArrayList<Service> readSavedServices(){
        ArrayList<Service>services = new ArrayList<>();
        Cursor mCursor = contentResolver.query(
                EventContract.TABLE_SERVICE_URI,
                null,
                null,
                null,
                null
        );
        if (mCursor != null && mCursor.getCount() > 0){
            while (mCursor.moveToNext()){
                Service service = new Service();
                service.setName(mCursor.getString(mCursor.getColumnIndex(EventContract.TableServices.NAME)));
                service.setId(mCursor.getInt(mCursor.getColumnIndex(EventContract.TableServices.SERVICE_ID)));
                service.setServiceType(mCursor.getString(mCursor.getColumnIndex(EventContract.TableServices.SERVICE_TYPE)));
                service.setImageOneUrl(mCursor.getString(mCursor.getColumnIndex(EventContract.TableServices.IMAGE_ONE_URL)));
                service.setImageTwoUrl(mCursor.getString(mCursor.getColumnIndex(EventContract.TableServices.IMAGE_TWO_URL)));
                service.setImageOne(mCursor.getBlob(mCursor.getColumnIndex(EventContract.TableServices.IMAGE_ONE)));
                service.setImageTwo(mCursor.getBlob(mCursor.getColumnIndex(EventContract.TableServices.IMAGE_TWO)));
                service.setAbout(mCursor.getString(mCursor.getColumnIndex(EventContract.TableServices.ABOUT)));
                service.setPhone(mCursor.getString(mCursor.getColumnIndex(EventContract.TableServices.PHONE)));
                service.setEmail(mCursor.getString(mCursor.getColumnIndex(EventContract.TableServices.EMAIL)));
                service.setSaved(mCursor.getInt(mCursor.getColumnIndex(EventContract.TableServices.SAVED)));
                services.add(service);
            }
        }
        closeCursor(mCursor);
        return services;
    }
    public ArrayList<Service> getSavedServices(){
        if (savedServiceArrayList == null){
            savedServiceArrayList = new ArrayList<>();
            savedServiceArrayList = readSavedServices();
        }
        return savedServiceArrayList;
    }

    public void addQueue(Queue queue){

        ContentValues mValues = new ContentValues();
        mValues.put(EventContract.TableQueue.EVENT_DATE, queue.getDate());
        mValues.put(EventContract.TableQueue.EVENT_ID, queue.getEvent_id());
        mValues.put(EventContract.TableQueue.OPERATION, queue.getOperation());

        if (!queueExists(queue)){
            contentResolver.insert(EventContract.TABLE_QUEUE_URI, mValues);
        }
    }

    public void removeQueue(Queue queue){
        if (queueExists(queue)){
            contentResolver.delete(
                    EventContract.TABLE_QUEUE_URI,
                    EventContract.TableQueue.EVENT_ID+" = ? AND "
                    +EventContract.TableQueue.OPERATION+" = ? ",
                    new String[] {String.valueOf(queue.getEvent_id()), String.valueOf(queue.getOperation())}
            );
        }
    }

    private boolean queueExists(Queue queue){
        Cursor mCursor = contentResolver.query(
                EventContract.TABLE_QUEUE_URI,
                null,
                EventContract.TableQueue.EVENT_ID+" = ? AND "
                +EventContract.TableQueue.OPERATION+" = ?",
                new String[]{String.valueOf(queue.getEvent_id()), String.valueOf(queue.getOperation())},
                null
        );
        if (mCursor != null && mCursor.getCount() > 0){
            closeCursor(mCursor);
            return true;
        } else {
            closeCursor(mCursor);
            return false;
        }
    }

    public void addPersistence(long date, int totalEvents, int noMoreData){

        ContentValues mValues = new ContentValues();
        mValues.put(EventContract.TablePersistence.DATE, date);
        mValues.put(EventContract.TablePersistence.TOTAL_EVENTS, totalEvents);
        mValues.put(EventContract.TablePersistence.NO_MORE_DATA, noMoreData);

        if (!getPersistenceExist(date)){
            contentResolver.insert(EventContract.TABLE_PERSISTENCE_URI, mValues);
        } else {
            contentResolver.update(
                    EventContract.TABLE_PERSISTENCE_URI,
                    mValues,
                    EventContract.TablePersistence.DATE + " = ? ",
                    new String[]{String.valueOf(date)}
            );
        }
    }

    public boolean getPersistenceExist(long date){
        Cursor mCursor = contentResolver.query(
                EventContract.TABLE_PERSISTENCE_URI,
                null,
                EventContract.TablePersistence.DATE+" = ? ",
                new String[]{String.valueOf(date)},
                null
        );
        if (mCursor != null && mCursor.getCount() > 0){
            closeCursor(mCursor);
            return true;
        } else {
            closeCursor(mCursor);
            return false;
        }
    }

    public boolean persistenceNoMoreData(long date){
        Cursor mCursor = contentResolver.query(
                EventContract.TABLE_PERSISTENCE_URI,
                null,
                EventContract.TablePersistence.DATE+" = ? AND "+
                        EventContract.TablePersistence.NO_MORE_DATA+" = ? ",
                new String[]{String.valueOf(date),"1"},
                null
        );
        if (mCursor != null && mCursor.getCount() > 0){
            closeCursor(mCursor);
            return true;
        } else {
            closeCursor(mCursor);
            return false;
        }
    }

    /**
     *This broadcast is sent from {@link CommentsActivity} and {@link}
     * When new comments are added to an event, and events have ben set to have new comments
     * The broadcast is received by the {@link}
     * which is used by the EventsChatFragment and EventListFragment, the adapters saves the event sent
     * via the broadcast intent and refreshes data to reflect the changes
     * @param event
     */
    public void sendBroadCastOnEventDataChanged(Event event){
        Intent intent = new Intent(ACTION_EVENT_DATA_CHANGED);
        ArrayList<Event> parcelableEvent = new ArrayList<>(1);
        parcelableEvent.add(event);
        intent.putParcelableArrayListExtra(CommentsActivity.EXTRA_EVENT, parcelableEvent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * This deletes queues older than the current date
     * TODO remove the cursor count and delete directly, cursor count is for testing this operation
     */
    public void cleanQueues(){
        long today = DateUtils.getIntegerDate(new LocalDate().toString());
        contentResolver.delete(
                EventContract.TABLE_QUEUE_URI,
                EventContract.TableQueue.EVENT_DATE + " < ? ",
                new String[]{String.valueOf(today)}
        );
    }

    public ArrayList<Queue> getQueues() {
        ArrayList<Queue> queues = new ArrayList<>();
        Cursor mCursor = contentResolver.query(
                EventContract.TABLE_QUEUE_URI,
                null,
                null,
                null,
                null
        );
        if (mCursor != null && mCursor.getCount() > 0){
            while (mCursor.moveToNext()){
                Queue q = new Queue(
                        mCursor.getInt(mCursor.getColumnIndex(EventContract.TableQueue.EVENT_DATE)),
                        mCursor.getInt(mCursor.getColumnIndex(EventContract.TableQueue.EVENT_ID)),
                        mCursor.getInt(mCursor.getColumnIndex(EventContract.TableQueue.OPERATION))
                );
                queues.add(q);
            }
        }
        closeCursor(mCursor);
        return queues;
    }

    /**
     * Persistence data cleaned after 10 days
     * This method is called from the {@link} onCreate
     */
    public void sweep(){

        long lastSweepDate = PrefUtils.getInstance(context).getLastSweepDate();
        long threeMonthsAgoDate = DateUtils.getIntegerDate(new LocalDate().minusMonths(1).toString());

        if (lastSweepDate <= threeMonthsAgoDate){
            doSweep();
            PrefUtils.getInstance(context)
                    .writeLastPersistenceDataCleanUp(DateUtils.getIntegerDate(new LocalDate().toString()));

            //Helper.toast(context, "Swept Successfully");
        } else {
            //Helper.toast(context, "Days past last sweep " + (today - lastSweepDate));
        }

    }

    private void doSweep(){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+EventContract.TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS "+EventContract.TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS "+EventContract.TABLE_QUEUE);
        db.execSQL("DROP TABLE IF EXISTS "+EventContract.TABLE_PERSISTENCE);
        db.execSQL("DROP TABLE IF EXISTS "+EventContract.TABLE_SERVICES);
        db.execSQL(dbHelper.sql_create_events);
        db.execSQL(dbHelper.sql_create_comments);
        db.execSQL(dbHelper.sql_create_queue);
        db.execSQL(dbHelper.sql_create_persistence);
        db.execSQL(dbHelper.sql_create_services);
    }

    public void forceSweep(){
        doSweep();
    }

    /**
     * Cleans events that have passed and their comments
     */
    public void cleanEvents(){
        long threeDaysAgo = DateUtils.getIntegerDate(new LocalDate().minusDays(1).toString());
        contentResolver.delete(
                EventContract.TABLE_EVENTS_URI,
                EventContract.TableEvents.START_DATE+" < ? ",
                new String[] {String.valueOf(threeDaysAgo)}
        );
        contentResolver.delete(
                EventContract.TABLE_COMMENTS_URI,
                EventContract.TableComments.EVENT_START_DATE + " < ? ",
                new String[]{String.valueOf(threeDaysAgo)}
        );
    }

    public void invalidate(){
        savedEventArrayList = null;
        savedServiceArrayList = null;
        contentResolver = null;
        mInstance = null;
        context = null;
    }
}
