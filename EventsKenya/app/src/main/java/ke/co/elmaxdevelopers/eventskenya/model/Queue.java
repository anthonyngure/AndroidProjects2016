package ke.co.elmaxdevelopers.eventskenya.model;

/**
 * Created by Tosh on 1/12/2016.
 */
public class Queue {

    public static final int GOING_UPDATE = 1;
    public static final int INTERESTED_UPDATE = 2;
    private long date, event_id, operation;

    public Queue(long date, long event_id, int operation) {
        this.date = date;
        this.event_id = event_id;
        this.operation = operation;
    }

    public long getDate() {
        return date;
    }

    public long getEvent_id() {
        return event_id;
    }

    public long getOperation() {
        return operation;
    }
}
