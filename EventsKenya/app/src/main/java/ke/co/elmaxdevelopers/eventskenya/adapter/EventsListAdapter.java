package ke.co.elmaxdevelopers.eventskenya.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.activity.CommentsActivity;
import ke.co.elmaxdevelopers.eventskenya.activity.DetailsActivity;
import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.model.Comment;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.model.Queue;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.network.GoingInterestClickListener;
import ke.co.elmaxdevelopers.eventskenya.stickyheaderslibrary.library.StickyRecyclerHeadersAdapter;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;
import ke.co.elmaxdevelopers.eventskenya.views.ProgressWheel;

public class EventsListAdapter extends BaseEventsListAdapter<RecyclerView.ViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private static Context context;
    private Target target;

    public EventsListAdapter(Context mContext){
        this.context = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_event_content, parent, false);
        return new RecyclerView.ViewHolder(view){};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindEvents(new ContentViewHolder(holder.itemView, context), getItem(position), position);
    }

    @Override
    public long getHeaderId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_event_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        HeaderViewHolder headerViewHolder = new HeaderViewHolder(holder.itemView);
        headerViewHolder.tvName.setText(getItem(position).getName());
        headerViewHolder.tvVenue.setText(getItem(position).getVenue());
        holder.itemView.setBackgroundColor(Helper.getRandomColor());

    }

    private static class HeaderViewHolder {
        TextView tvName;
        TextView tvVenue;
        public HeaderViewHolder(View convertView){
            this.tvName = (TextView) convertView.findViewById(R.id.list_item_event_name);
            this.tvVenue = (TextView) convertView.findViewById(R.id.list_item_event_venue);
        }
    }

    public void bindEvents(final ContentViewHolder eventViewHolder, final Event e, final int position){

        final BaseEventsListAdapter thisAdapter = this;

        eventViewHolder.tvTime.setText(e.getTime());
        eventViewHolder.tvDate.setText(e.getDate());

        eventViewHolder.imageLoader.startSpinning();
        eventViewHolder.imageLoader.setText(context.getString(R.string.loading_poster));

        final View.OnClickListener detailListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                ArrayList<Event> parcelableEvent = new ArrayList<>(1);
                parcelableEvent.add(e);
                intent.putParcelableArrayListExtra(DetailsActivity.EXTRA_EVENT, parcelableEvent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        };

        if (e.hasImage()){
            eventViewHolder.thumbnailOverLay.setVisibility(View.GONE);
            eventViewHolder.imageLoader.setVisibility(View.GONE);
            eventViewHolder.imageView.setImageBitmap(e.decodeImage());
        } else if (e.hasThumbnail()){
            eventViewHolder.imageLoader.setVisibility(View.GONE);
            eventViewHolder.imageView.setImageBitmap(e.decodeThumbnail());
        } else {
            if (!PrefUtils.getInstance(context).autoDownloadPosters()){
                loadThumbnail(eventViewHolder, position);
            } else {
                loadFullPoster(eventViewHolder, position);
            }
        }

        final View.OnClickListener posterDownLoadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFullPoster(eventViewHolder, position);
            }

        };

        eventViewHolder.downloadPoster.setOnClickListener(posterDownLoadListener);
        eventViewHolder.imageLoader.setOnClickListener(posterDownLoadListener);
        eventViewHolder.imageView.setOnClickListener(detailListener);

        eventViewHolder.tvDescription.setText(e.getDescription());

        handleComments(eventViewHolder, position);

        eventViewHolder.tvNoGoing.setText(convertThousands(e.getNoGoing()) + " Going");

        eventViewHolder.tvNoInterested.setText(convertThousands(e.getNoInterested()) + " Interested");

        eventViewHolder.detailsButton.setOnClickListener(detailListener);

        View.OnClickListener viewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsActivity.class);
                ArrayList<Event> parcelableEvent = new ArrayList<>(1);
                parcelableEvent.add(e);
                intent.putParcelableArrayListExtra(CommentsActivity.EXTRA_EVENT, parcelableEvent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        };

        eventViewHolder.seeAllCommentsButton.setOnClickListener(viewListener);
        eventViewHolder.commentButton.setOnClickListener(viewListener);

        View.OnClickListener goListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.setStatusGoing(1);
                DataController.getInstance(context).saveEvent(e);
                APIConnector.getInstance(context)
                        .addGoing(e.getId(), eventViewHolder, position, thisAdapter, Queue.GOING_UPDATE);
            }
        };

        View.OnClickListener unGoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.setStatusGoing(0);
                DataController.getInstance(context).saveEvent(e);
                APIConnector.getInstance(context)
                        .removeGoing(e.getId(), eventViewHolder, position, thisAdapter, Queue.GOING_UPDATE);
            }
        };

        if (e.getStatusGoing() == 1){
            eventViewHolder.goingButton.setImageResource(R.drawable.ic_action_is_going);
            eventViewHolder.goingButton.setOnClickListener(unGoListener);
        } else {
            eventViewHolder.goingButton.setImageResource(R.drawable.ic_action_go);
            eventViewHolder.goingButton.setOnClickListener(goListener);
        }

        View.OnClickListener followListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.setStatusInterested(1);
                DataController.getInstance(context).saveEvent(e);
                APIConnector.getInstance(context.getApplicationContext())
                        .addInterested(e.getId(), eventViewHolder, position, thisAdapter, Queue.INTERESTED_UPDATE);
            }
        };

        View.OnClickListener unFollowListener = new View.OnClickListener()  {
            @Override
            public void onClick(View v)  {
                e.setStatusInterested(0);
                DataController.getInstance(context).saveEvent(e);
                APIConnector.getInstance(context.getApplicationContext())
                        .removeInterested(e.getId(), eventViewHolder, position, thisAdapter, Queue.INTERESTED_UPDATE);
            }
        };

        if (e.getStatusInterested() == 1){
            eventViewHolder.followButton.setImageResource(R.drawable.ic_action_is_interested);
            eventViewHolder.followButton.setOnClickListener(unFollowListener);
        } else {
            eventViewHolder.followButton.setImageResource(R.drawable.ic_action_interested);
            eventViewHolder.followButton.setOnClickListener(followListener);
        }

        eventViewHolder.overFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePopupMenu(v, position);
            }
        });
    }

    private void loadThumbnail(final ContentViewHolder eventViewHolder, final int position){
        eventViewHolder.thumbnailOverLay.setVisibility(View.VISIBLE);
        eventViewHolder.imageView.setVisibility(View.GONE);
        Picasso.with(context)
                .load(BackEnd.absoluteUrl("thumbnails/" + getItem(position).getImageUrl()))
                .noFade()
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .tag(EventsListAdapter.class.getSimpleName())
                .into(eventViewHolder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        eventViewHolder.imageLoader.stopSpinning();
                        eventViewHolder.imageLoader.setVisibility(View.GONE);
                        eventViewHolder.imageView.setVisibility(View.VISIBLE);
                        eventViewHolder.imageView.setOnClickListener(null);
                        getItem(position).addThumbnail(eventViewHolder.imageView);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void loadFullPoster(final ContentViewHolder eventViewHolder, int position){
        final Event e = getItem(position);
        eventViewHolder.imageLoader.startSpinning();
        eventViewHolder.imageLoader.setText(context.getString(R.string.loading_poster));
        eventViewHolder.imageLoader.setVisibility(View.VISIBLE);
        eventViewHolder.imageView.setVisibility(View.GONE);
        eventViewHolder.thumbnailOverLay.setVisibility(View.GONE);
        Picasso.with(context).load(
                BackEnd.absoluteUrl("images/" + e.getImageUrl()))
                .noFade()
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .tag(EventsListAdapter.class.getSimpleName())
                .into(eventViewHolder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        eventViewHolder.imageLoader.setVisibility(View.GONE);
                        eventViewHolder.imageLoader.stopSpinning();
                        eventViewHolder.imageView.setVisibility(View.VISIBLE);
                        e.addImage(eventViewHolder.imageView);
                    }

                    @Override
                    public void onError() {
                        eventViewHolder.imageView.setVisibility(View.GONE);
                        eventViewHolder.imageLoader.setVisibility(View.VISIBLE);
                        eventViewHolder.imageLoader.stopSpinning();
                        eventViewHolder.imageLoader.setText(context.getString(R.string.failed_tap_to_retry));
                    }
                });
    }

    private void handleComments(ContentViewHolder eventViewHolder, int position){
        final Event e = getItem(position);
        final ArrayList<Comment> eventComments = e.getComments();
        if (eventComments.size() >= 6 && e.getTotalComments() >= 6){
            int totalComms = eventComments.size() <= e.getTotalComments() ? e.getTotalComments() : eventComments.size();
            eventViewHolder.seeAllCommentsButton.setText("View all "+ totalComms +" comments");
            // TODO convert comments past 1000 to 1k
            eventViewHolder.seeAllCommentsButton.setVisibility(View.VISIBLE);
        } else {
            eventViewHolder.seeAllCommentsButton.setVisibility(View.GONE);
        }

        if (eventComments.size() >= 1){
            eventViewHolder.tvComment1Author.setText(eventComments.get(0).getUsername());
            eventViewHolder.tvComment1Content.setText(eventComments.get(0).getContent());
            eventViewHolder.comment1Container.setVisibility(View.VISIBLE);
        } else {
            eventViewHolder.comment1Container.setVisibility(View.GONE);
        }

        if (eventComments.size() >= 2){
            eventViewHolder.tvComment2Author.setText(eventComments.get(1).getUsername());
            eventViewHolder.tvComment2Content.setText(eventComments.get(1).getContent());
            eventViewHolder.comment2Container.setVisibility(View.VISIBLE);
        } else {
            eventViewHolder.comment2Container.setVisibility(View.GONE);
        }

        if (eventComments.size() >= 3){
            eventViewHolder.tvComment3Author.setText(eventComments.get(2).getUsername());
            eventViewHolder.tvComment3Content.setText(eventComments.get(2).getContent());
            eventViewHolder.comment3Container.setVisibility(View.VISIBLE);
        } else {
            eventViewHolder.comment3Container.setVisibility(View.GONE);
        }

        if (eventComments.size() >= 4){
            eventViewHolder.tvComment4Author.setText(eventComments.get(3).getUsername());
            eventViewHolder.tvComment4Content.setText(eventComments.get(3).getContent());
            eventViewHolder.comment4Container.setVisibility(View.VISIBLE);
        } else {
            eventViewHolder.comment4Container.setVisibility(View.GONE);
        }

        if (eventComments.size() >= 5){
            eventViewHolder.tvComment5Author.setText(eventComments.get(4).getUsername());
            eventViewHolder.tvComment5Content.setText(eventComments.get(4).getContent());
            eventViewHolder.comment5Container.setVisibility(View.VISIBLE);
        } else {
            eventViewHolder.comment5Container.setVisibility(View.GONE);
        }

    }

    private void handlePopupMenu(View v, int position){
        final Event e = getItem(position);
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup_list_item_event, popup.getMenu());

        if (e.getTicketsLink().equalsIgnoreCase("Not Provided")) {
            popup.getMenu().findItem(R.id.event_popup_action_buy_ticket).setVisible(false);
        } else {
            popup.getMenu().findItem(R.id.event_popup_action_buy_ticket).setVisible(true);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.event_popup_action_share:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, "Hi there, "
                                + e.getName() + " is an event happening on "
                                + e.getDate() + " from "
                                + e.getTime() + " at "
                                + e.getVenue() + "\n"
                                + context.getString(R.string.app_download_info) + " "
                                + context.getString(R.string.company_website));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(Intent.createChooser(intent, "Share Event via... "));
                        return true;
                    case R.id.event_popup_action_buy_ticket:
                        // TODO WORK ON TICKETS BUYING LINKS
                        Intent buyTicketIntent = new Intent(Intent.ACTION_VIEW);
                        String url = e.getTicketsLink();
                        if (!url.isEmpty()) {
                            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                url = "http://" + url;
                            }
                            buyTicketIntent.setData(Uri.parse(url));
                            buyTicketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (buyTicketIntent.resolveActivity(context.getPackageManager()) != null) {
                                context.startActivity(buyTicketIntent);
                            }
                        }
                        return true;
                    case R.id.event_popup_action_report:
                        Helper.toast(context,"Event reported!");
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    private String convertThousands(int no) {
        if (no > 1000){
            String n = String.valueOf(no);
            return n.substring(0, 1)+"."+n.substring(2,3)+"k";
        } else {
            return String.valueOf(no);
        }
    }

    private static class ContentViewHolder implements GoingInterestClickListener {
        TextView tvTime, tvDescription, tvDate;
        TextView tvNoGoing, tvNoInterested;
        Button commentButton;
        TextView seeAllCommentsButton;
        ImageButton goingButton, followButton, detailsButton, overFlow;
        ImageView imageView, downloadPoster;
        ProgressWheel imageLoader;
        ProgressBar goingFollowingUpdateProgress;
        ViewGroup comment1Container, comment2Container, comment3Container, comment4Container;
        ViewGroup comment5Container;
        TextView tvComment1Author, tvComment1Content;
        TextView tvComment2Author, tvComment2Content;
        TextView tvComment3Author, tvComment3Content;
        TextView tvComment4Author, tvComment4Content;
        TextView tvComment5Author, tvComment5Content;
        FrameLayout thumbnailOverLay;

        private Context context;

        public ContentViewHolder(View itemView, Context mContext){

            this.context = mContext;

            tvTime = (TextView) itemView.findViewById(R.id.list_item_event_time);
            tvDescription = (TextView) itemView.findViewById(R.id.list_item_event_description);
            imageView = (ImageView) itemView.findViewById(R.id.list_item_event_image);
            downloadPoster = (ImageView) itemView.findViewById(R.id.list_item_event_download_poster);
            imageLoader = (ProgressWheel) itemView.findViewById(R.id.list_item_event_image_loader);
            imageLoader.setSpinSpeed(3f);
            thumbnailOverLay = (FrameLayout) itemView.findViewById(R.id.list_item_event_thumbnail_overlay);
            tvDate = (TextView) itemView.findViewById(R.id.list_item_event_date);

            tvNoGoing = (TextView) itemView.findViewById(R.id.list_item_event_no_going);
            tvNoInterested = (TextView) itemView.findViewById(R.id.list_item_event_no_interested);

            commentButton = (Button) itemView.findViewById(R.id.list_item_event_button_write_comment);
            seeAllCommentsButton = (TextView) itemView.findViewById(R.id.list_item_event_button_see_all_comments);


            detailsButton = (ImageButton) itemView.findViewById(R.id.list_item_event_button_details);
            goingButton = (ImageButton) itemView.findViewById(R.id.list_item_event_button_going);
            goingFollowingUpdateProgress = (ProgressBar) itemView.findViewById(R.id.list_item_event_following_going_update_progress);
            followButton = (ImageButton) itemView.findViewById(R.id.list_item_event_button_follow);
            overFlow = (ImageButton) itemView.findViewById(R.id.list_item_event_overflow);

            comment1Container = (ViewGroup) itemView.findViewById(R.id.list_item_event_comment_1_container);
            tvComment1Author = (TextView) itemView.findViewById(R.id.list_item_event_comment_1_details);
            tvComment1Content = (TextView) itemView.findViewById(R.id.list_item_event_comment_1_content);

            comment2Container = (ViewGroup) itemView.findViewById(R.id.list_item_event_comment_2_container);
            tvComment2Author = (TextView) itemView.findViewById(R.id.list_item_event_comment_2_details);
            tvComment2Content = (TextView) itemView.findViewById(R.id.list_item_event_comment_2_content);

            comment3Container = (ViewGroup) itemView.findViewById(R.id.list_item_event_comment_3_container);
            tvComment3Author = (TextView) itemView.findViewById(R.id.list_item_event_comment_3_details);
            tvComment3Content = (TextView) itemView.findViewById(R.id.list_item_event_comment_3_content);

            comment4Container = (ViewGroup) itemView.findViewById(R.id.list_item_event_comment_4_container);
            tvComment4Author = (TextView) itemView.findViewById(R.id.list_item_event_comment_4_details);
            tvComment4Content = (TextView) itemView.findViewById(R.id.list_item_event_comment_4_content);

            comment5Container = (ViewGroup) itemView.findViewById(R.id.list_item_event_comment_5_container);
            tvComment5Author = (TextView) itemView.findViewById(R.id.list_item_event_comment_5_details);
            tvComment5Content = (TextView) itemView.findViewById(R.id.list_item_event_comment_5_content);
        }

        @Override
        public void onNetworkDataLoadingStarted(int position,int operation) {
            if (operation == Queue.GOING_UPDATE){
                goingButton.setVisibility(View.GONE);
                goingFollowingUpdateProgress.setVisibility(View.VISIBLE);
                followButton.setEnabled(false);
            } else{
                followButton.setVisibility(View.GONE);
                goingFollowingUpdateProgress.setVisibility(View.VISIBLE);
                goingButton.setEnabled(false);
            }
        }

        @Override
        public void onNetworkDataLoadingFailed(int statusCode, int position, BaseEventsListAdapter adapter, int operation) {
            if (operation == Queue.GOING_UPDATE){
                goingFollowingUpdateProgress.setVisibility(View.GONE);
                goingButton.setVisibility(View.VISIBLE);
                followButton.setEnabled(true);
                /**
                 * The operation is added to queue if it was not an ungoing
                 */
                if (adapter.getItem(position).getStatusGoing() != 0){
                    Queue queue = new Queue(adapter.getItem(position).getStartDate(),adapter.getItem(position).getId(), operation);
                    DataController.getInstance(context)
                            .addQueue(queue);
                }
            } else {
                goingFollowingUpdateProgress.setVisibility(View.GONE);
                followButton.setVisibility(View.VISIBLE);
                goingButton.setEnabled(true);
                /**
                 * The operation is added to queue if it was not an uninterested
                 */
                if (adapter.getItem(position).getStatusInterested() != 0){
                    Queue queue = new Queue(adapter.getItem(position).getStartDate(),adapter.getItem(position).getId(), operation);
                    DataController.getInstance(context).addQueue(queue);
                }
            }

        }

        @Override
        public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response,
                                                int position,  BaseEventsListAdapter adapter, int operation) {
            if (operation == Queue.GOING_UPDATE){
                try {
                    if (response.getString("success").equalsIgnoreCase("success")) {
                        adapter.getItem(position).setNoGoing(response.getInt("no_going"));
                        adapter.notifyItemChanged(position);
                    }else {

                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } finally {
                    goingFollowingUpdateProgress.setVisibility(View.GONE);
                    goingButton.setVisibility(View.VISIBLE);
                    followButton.setEnabled(true);
                }
            } else {
                try {
                    if (response.getString("success").equalsIgnoreCase("success")) {
                        adapter.getItem(position).setNoInterested(response.getInt("no_interested"));
                        adapter.notifyItemChanged(position);
                    } else {

                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } finally {
                    goingFollowingUpdateProgress.setVisibility(View.GONE);
                    followButton.setVisibility(View.VISIBLE);
                    goingButton.setEnabled(true);
                }
            }
        }
    }

}
