package com.chuck.artranslate.adapter;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chuck.artranslate.R;
import com.chuck.artranslate.dbresources.DBresources;
import com.chuck.artranslate.utils.App;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public abstract class DBResourcesAdapter extends DataAdapter<DBresources, DBResourcesAdapter.DBResourcesViewHolder> {

    private Geocoder geocoder = new Geocoder(App.getInstance().getApplicationContext());

    @Override
    protected void displayContent(DBResourcesViewHolder holder, DBresources data, int position) {
        holder.left.setText(data.getDetection());
        holder.right.setText(data.getTranslation());
        holder.toView.removeAllViews();
        holder.fromView.setText(data.getDetection());

        String address = "";

        // this check in case location is not turned on....

        if ((Double.valueOf(data.getLatitude()) != null) && (Double.valueOf(data.getLatitude()) != null)){
            try {

                List<Address> addressList = geocoder.getFromLocation(data.getLatitude(), data.getLongitude(), 1);

                address = addressList.get(0).getAddressLine(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{

            address = "Unknown Location";

        }

        holder.localView.setText(address);

    }

    @NonNull
    @Override
    public DBResourcesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DBResourcesViewHolder(inflate(parent)) {
            @Override
            public void onClick(View view) {
                onItemClick(view, getItem(getAdapterPosition()), getAdapterPosition());
            }
        };
    }

    @Override
    protected void onFilter(String filter) {
        for(int i = 0; i < getItemCopyCount(); i++) {
            if (getItemCopy(i).getTranslation().toLowerCase().contains(filter)
                    || getItemCopy(i).getDetection().toLowerCase().contains(filter)) {

                addForFilter(getItemCopy(i));
            }
        }
    }

//    public abstract void onItemLongClick(View view, DBresources res, int pos);

    public abstract class DBResourcesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View ExpView, CollapseView;
        public TextView left, right, translated;

        public TextView fromView, localView;
        public FlexboxLayout toView;
        public ImageButton del;

        public DBResourcesViewHolder(View itemView) {

            super(itemView);

            left = itemView.findViewById(R.id.left_side);
            right = itemView.findViewById(R.id.right_side);
            translated = itemView.findViewById(R.id.translated);

            del = itemView.findViewById(R.id.delete);
            del.setOnClickListener(this);

            ExpView = itemView.findViewById(R.id.Exp_view);
            CollapseView = itemView.findViewById(R.id.Shr_View);

            fromView = itemView.findViewById(R.id.Exp_from);
            toView = itemView.findViewById(R.id.Exp_to);
            localView = itemView.findViewById(R.id.Exp_locale);

            itemView.setOnClickListener(this);

        }

    }
}
