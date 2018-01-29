package ke.co.elmaxdevelopers.eventskenya.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ke.co.elmaxdevelopers.eventskenya.model.Comment;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.model.Service;
import ke.co.elmaxdevelopers.eventskenya.network.Response;

/**
 * Created by Tosh on 1/13/2016.
 */
public class JsonUtils {

    private JsonUtils(){

    }

    public static Event getEventFromJsonObject(JSONObject eventObject){
        String startDate, endDate;
        Event event = new Event();
        try {
            event.setId(eventObject.getInt(Response.EventData.ID));
            event.setName(eventObject.getString(Response.EventData.NAME));

            startDate = eventObject.getString(Response.EventData.START_DATE);
            endDate = eventObject.getString(Response.EventData.END_DATE);

            event.setStartDate(DateUtils.getIntegerDate(startDate));
            event.setEndDate(DateUtils.getIntegerDate(endDate));
            event.setDate(DateUtils.sortDates(startDate, endDate));

            event.setTime(eventObject.getString(Response.EventData.TIME));
            event.setVenue(eventObject.getString(Response.EventData.VENUE));
            event.setImageUrl(eventObject.getString(Response.EventData.IMAGE));
            event.setTicketsLink(eventObject.getString(Response.EventData.TICKETS_LINK));
            event.setDescription(eventObject.getString(Response.EventData.DESCRIPTION));
            event.setNoGoing(eventObject.getInt(Response.EventData.NO_GOING));
            event.setNoInterested(eventObject.getInt(Response.EventData.NO_INTERESTED));
            event.setTotalComments(eventObject.getInt(Response.EventData.TOTAL_COMMENTS));

            JSONArray eventComments = eventObject.getJSONArray(Response.EventData.COMMENTS);
            ArrayList<Comment> comments = new ArrayList<>();
            for (int j = 0; j < eventComments.length(); j++){
                comments.add(getCommentFromJsonObject(eventComments.getJSONObject(j)));
            }
            event.setComments(comments);
            event.setStatusGoing(0);
            event.setStatusInterested(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    public static Comment getCommentFromJsonObject(JSONObject commObj){
        Comment comment = new Comment();
        try {
            comment.setUsername(commObj.getString(Response.CommentData.USERNAME));
            comment.setCreatedAt(shortenCommentCreatedAt(commObj.getString(Response.CommentData.CREATED_AT)));
            comment.setContent(commObj.getString(Response.CommentData.CONTENT));
            comment.setEventId(commObj.getInt(Response.CommentData.EVENT_ID));
            comment.setId(commObj.getInt(Response.CommentData.ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comment;
    }

    private static String shortenCommentCreatedAt(String s){
        return s.substring((s.length() - 8), s.length());
    }

    public static Service getServiceFromJsonObject(JSONObject serviceObject) {

        Service s = new Service();
        try {
            s.setName(serviceObject.getString(Response.ServiceData.NAME));
            s.setId(serviceObject.getInt(Response.ServiceData.ID));
            s.setAbout(serviceObject.getString(Response.ServiceData.ABOUT));
            s.setPhone(serviceObject.getString(Response.ServiceData.PHONE));
            s.setEmail(serviceObject.getString(Response.ServiceData.EMAIL));
            s.setImageOneUrl(serviceObject.getString(Response.ServiceData.IMAGE_ONE));
            s.setImageTwoUrl(serviceObject.getString(Response.ServiceData.IMAGE_TWO));
            s.setServiceType( serviceObject.getString(Response.ServiceData.SERVICE_TYPE));
            s.setSaved(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }
}
