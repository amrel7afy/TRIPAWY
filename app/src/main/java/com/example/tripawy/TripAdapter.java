package com.example.tripawy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
//




import androidx.annotation.NonNull;

import com.example.tripawy.helper.HelperMethods;
//

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.Viewholder> {

    private final Context context;
    private final LiveData<List<Trip>> tripArrayList;
    private Trip data;

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
        data = tripArrayList.getValue().get(position);
        holder.getTxtName().setText(data.getName());
        holder.getTxtDate().setText(convertDate(data.getDate()));
        holder.getTxtTime().setText(convertTime(data.getTime()));
        holder.getTxtFrom().setText(data.getFrom());
        holder.getTxtTo().setText(data.getTo());

        holder.getNotes().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddNoteActivity.class);
                context.startActivity(intent);
            }
        });


        holder.getStart().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(v.getContext())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + v.getContext().getPackageName()));
                     v.getContext().startActivity(intent);
                    } else HelperMethods.startScheduling(v.getContext());

                } else {
                    HelperMethods.startScheduling(v.getContext());
                }
            }
        });


        holder.getBtnMore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), holder.getBtnMore());
                popup.inflate(R.menu.card_more_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.add_notes:

                                return true;
                            case R.id.edit:
                                Intent intent = new Intent(v.getContext(),EditTripActivity.class);
                                intent.putExtra("trip", data);
                                context.startActivity(intent);
                                return true;
                            case R.id.delete:
                                AlertDialog("Do you want to remove this Trip?", 3,v);
                                return true;
                            case R.id.cancel:
                                AlertDialog("Do you want to cancel this Trip?", 4,v);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });


    }

    private void AlertDialog(String message, int index,View v) {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(v.getContext())
                .setCancelable(false)
                .setTitle("Confirmation")
                .setMessage(message)
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create();
        alertDialogDelete.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button yesButton = (alertDialogDelete).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button noButton = (alertDialogDelete).getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (index) {
                            case 3:
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    RoomDB.getTrips(context.getApplicationContext()).delete(data);
                                });
                                break;
                            case 4:
                                data.setTripState("CANCLED");
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    RoomDB.getTrips(context.getApplicationContext()).update(data);
                                });
                                break;
                        }

                        alertDialogDelete.dismiss();

                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogDelete.dismiss();
                    }
                });
            }
        });

        alertDialogDelete.show();

    }

    //Convert Date From Long To String
    private String convertDate(Long dateLong) {
        Date date = new Date(dateLong);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String dateText = sdf.format(date);
        return dateText;
    }

    //Convert Time From Long To String
    private String convertTime(Long timeLong) {
        Date date = new Date(timeLong);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String timeText = sdf.format(date);
        return timeText;
    }

    @Override
    public int getItemCount() {
        return tripArrayList.getValue().size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView txtDate;
        private TextView txtTime;
        private TextView txtName;
        private TextView txtFrom;
        private TextView txtTo;
        private Button note;
        private ImageButton btnMore;
        Button btn_start;



        public Viewholder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
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

        public Button getNotes() {
            if (note == null) {
                note = itemView.findViewById(R.id.btnNotes);
            }
            return note;
        }
        public Button getStart() {
            if (btn_start == null) {
                btn_start = itemView.findViewById(R.id.btnStart);
            }
            return btn_start;
        }

        public ImageButton getBtnMore() {
            if (btnMore == null) {
                btnMore = itemView.findViewById(R.id.btnMore);
            }
            return btnMore;
        }


    }
    //get the selected item from data base
//    public Trip getTripAt(int pos){
//        return tripArrayList.getValue().get(pos);
//    }
}
