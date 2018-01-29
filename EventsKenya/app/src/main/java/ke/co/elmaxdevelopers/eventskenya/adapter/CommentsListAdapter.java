package ke.co.elmaxdevelopers.eventskenya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.model.Comment;

public class CommentsListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<Comment> commentsArrayList;
	private LayoutInflater inflater;

	public CommentsListAdapter(Context cxt) {
		this.context = cxt;
		this.commentsArrayList = new ArrayList<>();
	}

	@Override
	public int getCount() {
		if (commentsArrayList != null) {
			return commentsArrayList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Comment getItem(int position) {
        if (commentsArrayList != null) {
            return commentsArrayList.get(position);
        } else {
            return null;
        }
	}

    public void addAll(ArrayList<Comment> comments){
        commentsArrayList.addAll(comments);
        notifyDataSetChanged();
    }

	@Override
	public long getItemId(int position) {
		return position;
	}

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Comment comment = getItem(position);
        if (inflater == null){
            inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_comment, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvContent.setText(comment.getContent());
        holder.tvDetails.setText(comment.getUsername() + " " + comment.getCreatedAt());

        return convertView;
    }

    public void add(Comment comment) {
        commentsArrayList.add(comment);
        notifyDataSetChanged();
    }

    public void add(ArrayList<Comment> comments) {
        commentsArrayList.addAll(comments);
        notifyDataSetChanged();
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.tvContent = (TextView) v.findViewById(R.id.list_item_comment_content);
        holder.tvDetails = (TextView) v.findViewById(R.id.list_item_comment_details);
        holder.avater = (ImageView) v.findViewById(R.id.list_item_comment_avater);
        return holder;
    }


    private static class ViewHolder {
        public TextView tvContent;
        public TextView tvDetails;
        public ImageView avater;
    }

}
