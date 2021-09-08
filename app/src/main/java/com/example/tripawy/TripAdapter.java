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
        holder.getTxtDate().setText(convertDate(data.getDate()));
        holder.getTxtTime().setText(convertTime(data.getTime()));
        holder.getTxtFrom().setText(data.getFrom());
        holder.getTxtTo().setText(data.getTo());
        holder.getTxtState().setText(data.getTripState());


        holder.getNote().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notes = "";
                for (int i = 0; i < data.getNotes().size(); i++) {
                    notes += (i + 1) + ") " + data.getNotes().get(i) + "\n";
                }
                final AlertDialog alertDialogNotes = new AlertDialog.Builder(context)
                        .setCancelable(true)
                        .setTitle("Notes")
                        .setMessage(notes)
                        .create();
                alertDialogNotes.setCanceledOnTouchOutside(true);
                alertDialogNotes.show();
            }
        });

        if(data.getTripState().equals(TripState.UPCOMING.name())){
            holder.getStart().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!Settings.canDrawOverlays(context)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + context.getPackageName()));
                            v.getContext().startActivity(intent);
                        } else HelperMethods.startScheduling(context,data);

                    } else {
                        HelperMethods.startScheduling(context,data);
                    }
                }
            });

        }else{
            holder.getStart().setClickable(false);
            holder.getStart().setTextColor(context.getResources().getColor(R.color.inactive));
        }

        holder.getBtnMore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), holder.getBtnMore());
                popup.inflate(R.menu.card_more_menu);

                if(data.getTripState().equals(TripState.UPCOMING.name())) {
                    popup.getMenu().findItem(R.id.cancel).setVisible(true);
                    popup.getMenu().findItem(R.id.add_notes).setVisible(true);
                    popup.getMenu().findItem(R.id.edit).setVisible(true);
                    popup.getMenu().findItem(R.id.recall).setVisible(false);

                }else{
                    if (data.getTripType().equals(TripType.ROUND.name()) && data.getTripState().equals(TripState.DONE.name()) ){
                        popup.getMenu().findItem(R.id.recall).setVisible(true);
                    }else{
                        popup.getMenu().findItem(R.id.recall).setVisible(false);
                    }
                    popup.getMenu().findItem(R.id.cancel).setVisible(false);
                    popup.getMenu().findItem(R.id.add_notes).setVisible(false);
                    popup.getMenu().findItem(R.id.edit).setVisible(false);

                }

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.recall:
                                    data.setTripState(TripState.UPCOMING.name());
                                    changeDirection(data);
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        RoomDB.getTrips(context.getApplicationContext()).update(data);
                                    });
                                    return true;
                                case R.id.add_notes:
                                    Intent intentNotes = new Intent(v.getContext(), AddNoteActivity.class);
                                    intentNotes.putExtra("Trip", data);
                                    context.startActivity(intentNotes);
                                    return true;
                                case R.id.edit:
                                    Intent intentEdit = new Intent(v.getContext(), EditTripActivity.class);
                                    intentEdit.putExtra("trip", data);
                                    context.startActivity(intentEdit);
                                    return true;
                                case R.id.delete:
                                    AlertDialog("Do you want to remove this Trip?", 3, v, data);
                                    return true;
                                case R.id.cancel:
                                    AlertDialog("Do you want to cancel this Trip?", 4, v, data);
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

    private void changeDirection(Trip data) {
        String temp = "";
        temp = data.getFrom();
        data.setFrom(data.getTo());
        data.setTo(temp);
    }


    private void AlertDialog(String message, int index, View v, Trip data) {
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
                                data.setTripState(TripState.CANCELED.name());
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    RoomDB.getTrips(context.getApplicationContext()).update(data);
                                });
                                break;
                            default:
                                return;
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
        private TextView txtState;
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

        public TextView getTxtState() {
            if (txtState == null) {
                txtState = itemView.findViewById(R.id.txtState);
            }
            return txtState;
        }

        public Button getNote() {
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
