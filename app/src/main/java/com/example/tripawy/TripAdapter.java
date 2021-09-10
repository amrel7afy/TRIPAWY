package com.example.tripawy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripawy.methods.Methods;

import java.text.DateFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private final Context context;
    private final LiveData<List<Trip>> tripArrayList;

    public TripAdapter(Context context, LiveData<List<Trip>> tripArrayList) {
        this.context = context;
        this.tripArrayList = tripArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip data = Objects.requireNonNull(tripArrayList.getValue()).get(position);
        holder.getTxtName().setText(data.getName());
        holder.getTxtDate().setText(DateFormat.getDateInstance().format(data.getDate()));
        holder.getTxtTime().setText(DateFormat.getTimeInstance().format(data.getTime()));
        holder.getTxtFrom().setText(data.getFrom());
        holder.getTxtTo().setText(data.getTo());
        holder.getTxtState().setText(data.getTripState());

        //Alarm scheduling
        if (data.getTripState().equals(TripState.UPCOMING.name())) {
            Methods.startScheduling(context.getApplicationContext(), data, 0);
        } else {
            Methods.stopAlarm(context, data);
        }

        //Show Notes Button
        holder.getNote().setOnClickListener(v -> {
            StringBuilder notes = new StringBuilder();
            if (data.getNotes() != null) {
                for (int i = 0; i < data.getNotes().size(); i++) {
                    notes.append(i + 1).append(") ").append(data.getNotes().get(i)).append("\n");
                }
            } else {
                notes = new StringBuilder("No Notes To show");
            }
            final AlertDialog alertDialogNotes = new AlertDialog.Builder(context)
                    .setCancelable(true)
                    .setTitle("Notes")
                    .setMessage(notes.toString())
                    .create();
            alertDialogNotes.setCanceledOnTouchOutside(true);
            alertDialogNotes.show();
        });

        //Start Button
        if (data.getTripState().equals(TripState.UPCOMING.name())) {
            holder.getStart().setOnClickListener(v -> {
                Methods.stopAlarm(context, data);
                data.setTripState("DONE");
                Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(context.getApplicationContext()).update(data));
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + data.getTo()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);

            });

        } else {
            holder.getStart().setClickable(false);
            holder.getStart().setTextColor(context.getResources().getColor(R.color.inactive));
        }

        //More PopUp Menu
        holder.getBtnMore().setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.getBtnMore());
            popup.inflate(R.menu.card_more_menu);
            if (data.getTripState().equals(TripState.UPCOMING.name())) {
                popup.getMenu().findItem(R.id.cancel).setVisible(true);
                popup.getMenu().findItem(R.id.add_notes).setVisible(true);
                popup.getMenu().findItem(R.id.edit).setVisible(true);
                popup.getMenu().findItem(R.id.recall).setVisible(false);

            } else {
                popup.getMenu().findItem(R.id.recall).setVisible(data.getTripType().equals(TripType.ROUND.name()) && data.getTripState().equals(TripState.DONE.name()));
                popup.getMenu().findItem(R.id.cancel).setVisible(false);
                popup.getMenu().findItem(R.id.add_notes).setVisible(false);
                popup.getMenu().findItem(R.id.edit).setVisible(false);

            }

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.recall:
                        data.setTripState(TripState.UPCOMING.name());
                        changeDirection(data);
                        Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(context.getApplicationContext()).update(data));
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
                        Methods.stopAlarm(context, data);
                        return true;
                    case R.id.cancel:
                        AlertDialog("Do you want to cancel this Trip?", 4, v, data);
                        Methods.stopAlarm(context, data);

                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
        });


    }

    //Cahnge Direction on recalling
    private void changeDirection(Trip data) {
        String temp;
        temp = data.getFrom();
        data.setFrom(data.getTo());
        data.setTo(temp);
    }


    //Alert dialog on Deleting or Cancel
    private void AlertDialog(String message, int index, View v, Trip data) {
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(v.getContext())
                .setCancelable(false)
                .setTitle("Confirmation")
                .setMessage(message)
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .create();
        alertDialogDelete.setOnShowListener(dialogInterface -> {
            Button yesButton = (alertDialogDelete).getButton(AlertDialog.BUTTON_POSITIVE);
            Button noButton = (alertDialogDelete).getButton(AlertDialog.BUTTON_NEGATIVE);

            yesButton.setOnClickListener(view -> {
                switch (index) {
                    case 3:
                        Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(context.getApplicationContext()).delete(data));
                        break;
                    case 4:
                        data.setTripState(TripState.CANCELED.name());
                        Executors.newSingleThreadExecutor().execute(() -> RoomDB.getTrips(context.getApplicationContext()).update(data));
                        break;
                    default:
                        return;
                }

                alertDialogDelete.dismiss();

            });

            noButton.setOnClickListener(view -> alertDialogDelete.dismiss());
        });

        alertDialogDelete.show();

    }

    @Override
    public int getItemCount() {
        return Objects.requireNonNull(tripArrayList.getValue()).size();
    }

    //ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private TextView txtDate;
        private TextView txtTime;
        private TextView txtName;
        private TextView txtFrom;
        private TextView txtTo;
        private TextView txtState;
        private Button note;
        private ImageButton btnMore;
        private Button btn_start;


        public ViewHolder(@NonNull View itemView) {
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

}
