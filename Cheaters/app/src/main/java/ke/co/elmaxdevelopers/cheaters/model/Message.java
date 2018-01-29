package ke.co.elmaxdevelopers.cheaters.model;

/**
 * Created by Tosh on 1/26/2016.
 */
public class Message {

    private String body, address;
    private long timestamp;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
