package com.example.tripawy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.Viewholder> {

    private Context context;
    private LiveData<List<Trip>> tripArrayList;

    public TripAdapter(Context context, LiveData<List<Trip>> tripArrayList) {
        this.context = context;
        this.tripArrayList = tripArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Trip data = tripArrayList.getValue().get(position);
        holder.getTxtName().setText(data.getName());
        holder.getTxtDate().setText(data.getDate()+"");
        holder.getTxtTime().setText(data.getTime()+"");
        holder.getTxtFrom().setText(data.getFrom());
        holder.getTxtTo().setText(data.getTo());
    }

    @Override
    public int getItemCount() {
        return tripArrayList.getValue().size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView txtDate;
        private TextView txtTime;
        private TextView txtName;
        private TextView txtFrom;
        private TextView txtTo;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
        }

        public TextView getTxtName() {
            if (txtName == null) {
                txtName = itemView.findViewById(R.id.txtName);
            }
            return txtName;
        }

        public TextView getTxtDate() {
            if (txtDate == null) {
                txtDate = itemView.findViewById(R.id.txtDate);
            }
            return txtDate;
        }

        public TextView getTxtTime() {
            if (txtTime == null) {
                txtTime = itemView.findViewById(R.id.txtTime);
            }
            return txtTime;
        }

        public TextView getTxtFrom() {
            if (txtFrom == null) {
                txtFrom = itemView.findViewById(R.id.txtFrom);
            }
            return txtFrom;
        }

        public TextView getTxtTo() {
            if (txtTo == null) {
                txtTo = itemView.findViewById(R.id.txtTo);
            }
            return txtTo;
        }


    }
}
