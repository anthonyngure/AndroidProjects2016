package ke.co.elmaxdevelopers.eventskenya.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.activity.ImageViewActivity;
import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.model.Service;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.views.SquaredImageView;

/**
 * Created by Tosh on 1/1/2016.
 */
public class ServicesListAdapter extends RecyclerView.Adapter<ServicesListAdapter.ServiceViewHolder>{

    private ArrayList<Service> items;
    private Context context;

    public ServicesListAdapter(Context mContext){
        this.context = mContext;
        this.items = new ArrayList<>();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvDescription;
        Button messageButton, callButton, emailButton, saveButton;
        SquaredImageView imageOne, imageTwo;

        public ServiceViewHolder(View convertView) {
            super(convertView);

            messageButton = (Button) convertView.findViewById(R.id.list_item_service_message);
            callButton = (Button) convertView.findViewById(R.id.list_item_service_call);
            emailButton = (Button) convertView.findViewById(R.id.list_item_service_email);
            saveButton = (Button) convertView.findViewById(R.id.list_item_service_save_button);

            tvName = (TextView) convertView.findViewById(R.id.list_item_service_name);
            tvDescription = (TextView) convertView.findViewById(R.id.list_item_service_description);

            imageOne = (SquaredImageView) convertView.findViewById(R.id.list_item_service_one);
            imageTwo = (SquaredImageView) convertView.findViewById(R.id.list_item_service_two);

        }
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_service, viewGroup, false);
        return  new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ServiceViewHolder serviceViewHolder, int position) {

        final Service s = items.get(position);

        serviceViewHolder.tvName.setText(s.getName());
        serviceViewHolder.tvDescription.setText(s.getAbout());
        if (s.hasImageOne()){
            serviceViewHolder.imageOne.setImageBitmap(s.decodeImageOne());
        } else {
            Picasso.with(context).load(BackEnd.absoluteUrl(s.getImageOneUrl()))
                    .into(serviceViewHolder.imageOne);
        }

        serviceViewHolder.imageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), ImageViewActivity.class);
                intent.putExtra("image",s.getImageOneUrl());
                intent.putExtra("name",s.getName());
                context.startActivity(intent);
            }
        });

        if (s.hasImageTwo()){
            serviceViewHolder.imageTwo.setImageBitmap(s.decodeImageTwo());
        } else {
            Picasso.with(context).load(BackEnd.absoluteUrl(s.getImageTwoUrl()))
                    .into(serviceViewHolder.imageTwo);
        }


        serviceViewHolder.imageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), ImageViewActivity.class);
                intent.putExtra("image",s.getImageTwoUrl());
                intent.putExtra("name",s.getName());
                context.startActivity(intent);
            }
        });

        View.OnClickListener saveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.setSaved(1);
                s.addImageOne(serviceViewHolder.imageOne);
                s.addImageTwo(serviceViewHolder.imageTwo);
                DataController.getInstance(context).saveService(s);
                notifyDataSetChanged();
            }
        };

        View.OnClickListener deleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.setSaved(0);
                DataController.getInstance(context).deleteSavedService(s);
                notifyDataSetChanged();
            }
        };

        if (s.getSaved() == 1){
            /**
             * This is a saved event
             */
            serviceViewHolder.saveButton.setOnClickListener(deleteListener);
            serviceViewHolder.saveButton.setText("Delete");
        } else {
            serviceViewHolder.saveButton.setOnClickListener(saveListener);
            serviceViewHolder.saveButton.setText("Save");
        }

        serviceViewHolder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent();
                messageIntent.setAction(Intent.ACTION_SENDTO);
                messageIntent.addCategory(Intent.CATEGORY_DEFAULT);
                messageIntent.setType("vnd.android-dir/mms-sms");
                messageIntent.setData(Uri.parse("sms:" + s.getPhone()));

                if (messageIntent.resolveActivity(context.getPackageManager()) != null){
                    context.startActivity(messageIntent);
                } else {
                    Helper.toast(context, "Unable to find a messaging App.");
                }
            }
        });

        serviceViewHolder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + s.getPhone()));
                if (callIntent.resolveActivity(context.getPackageManager()) != null){
                    context.startActivity(callIntent);
                } else {
                    Helper.toast(context, "Unable to find a messaging App.");
                }
            }
        });

        serviceViewHolder.emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"+s.getEmail()));

                if (emailIntent.resolveActivity(context.getPackageManager()) != null){
                    context.startActivity(emailIntent);
                } else {
                    Helper.toast(context, "Unable to find an email App.");
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(Service object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public ArrayList<Service> getItems(){
        return items;
    }

    public void add(int index, Service object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Service> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(Service... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(String object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public Service getItem(int position) {
        return items.get(position);
    }

}
