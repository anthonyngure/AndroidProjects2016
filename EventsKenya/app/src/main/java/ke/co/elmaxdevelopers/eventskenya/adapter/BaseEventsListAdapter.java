package ke.co.elmaxdevelopers.eventskenya.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

import ke.co.elmaxdevelopers.eventskenya.activity.CommentsActivity;
import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public abstract class BaseEventsListAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private ArrayList<Event> items = new ArrayList<Event>();
    private BroadcastReceiver broadcastReceiver;

    public BaseEventsListAdapter() {
        setHasStableIds(true);
    }

    public void registerReceiver(Context context){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(DataController.ACTION_EVENT_DATA_CHANGED)){
                    ArrayList<Event> events =  intent.getParcelableArrayListExtra(CommentsActivity.EXTRA_EVENT);
                    Event event = events.get(0);
                    for (Event e : getItems()){
                        if (e.getId() == event.getId()){
                            e.copyChangedEvent(event);
                            DataController.getInstance(context).saveEvent(e);
                        }
                    }
                    //Helper.getInstance(context).toast("Event "+event.getId()+" has changed recieved by "+context.toString());
                    //Helper.toast(context, "Notification received");
                    notifyDataSetChanged();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(DataController.ACTION_EVENT_DATA_CHANGED);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);
    }

    public void unregisterBroadcast(Context context){
        LocalBroadcastManager.getInstance(context.getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    public void add(Event object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, Event object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Event> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public ArrayList<Event> getItems(){
        return items;
    }

    /*public void addAll(Event... items) {
        addAll(Arrays.asList(items));
    }*/

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(String object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public Event getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
