package com.greylabs.greylabs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.greylabs.greylabs.model.LocationObject;

import java.util.List;
import com.greylabs.greylabs.R;

/**
 * Created by gaura on 09-06-2018.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder>  {

    private Context context;
    private List<LocationObject> locationObjectList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView address;


        public MyViewHolder(View view) {
            super(view);
            address = view.findViewById(R.id.address);

        }
    }

    public LocationAdapter(Context context, List<LocationObject> locationObjectList) {
        this.context = context;
        this.locationObjectList = locationObjectList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        LocationObject locationObject = locationObjectList.get(position);
        holder.address.setText(locationObject.getAddress());


    }

    @Override
    public int getItemCount() {
        return locationObjectList.size();
    }

}
