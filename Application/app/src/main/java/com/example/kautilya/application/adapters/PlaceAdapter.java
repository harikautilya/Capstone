package com.example.kautilya.application.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kautilya.application.FireBaseUtils;
import com.example.kautilya.application.R;
import com.example.kautilya.application.databinding.ItemPlaceBinding;
import com.example.kautilya.application.objects.Place;
import com.example.kautilya.application.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {

    private final Context context;
    private List<Place> places;

    public PlaceAdapter(Context context, List<Place> places) {
        this.places = places;
        this.context = context;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull PlaceHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull PlaceHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.removeListener();
    }

    public void updateData(List<Place> places) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MainActivity.MyDiffCallback(places, this.places));
        diffResult.dispatchUpdatesTo(this);
        this.places.clear();
        this.places.addAll(places);
    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PlaceHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_place, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceHolder placeHolder, int i) {
        placeHolder.bind(places.get(i));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class PlaceHolder extends RecyclerView.ViewHolder {
        private Place place;

        ItemPlaceBinding itemPlaceBinding;
        DatabaseReference bookReference;
        ValueEventListener bookValueEventListener;
        boolean booked;


        public PlaceHolder(@NonNull View itemView) {
            super(itemView);
            itemPlaceBinding = DataBindingUtil.bind(itemView);
        }

        void bind(final Place place) {
            this.place = place;
            itemPlaceBinding.setPlace(place);
            if (!place.getImage_url().equals(""))
                Picasso.get()
                        .load(place.getImage_url())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(itemPlaceBinding.image);

            bookValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists())
                        return;
                    booked = (dataSnapshot.getValue(Boolean.class));

                    if (booked) {
                        itemPlaceBinding.book.setText(context.getString(R.string.cancel));
                    } else {
                        itemPlaceBinding.book.setText(context.getString(R.string.book));
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.toException().printStackTrace();
                }
            };

            addListener();

            itemPlaceBinding.book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setTitle("Confirm");
                    if (booked) {
                        builder.setMessage("Are you sure you wanna cancel this event?");
                    } else {
                        builder.setMessage("Are you sure you wanna book this event?");
                    }
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            FireBaseUtils.setBookingReference(FirebaseAuth.getInstance().getUid(), place.getId(), !booked)
                                    .addOnCompleteListener(((AppCompatActivity) context), new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();

                                            } else {
                                                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    })
                                    .addOnFailureListener(((AppCompatActivity) context), new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                }
            });


        }

        public void removeListener() {
            place = null;
            if (bookReference != null) {
                bookReference.removeEventListener(bookValueEventListener);

            }

        }

        public void addListener() {
            if (place != null) {
                bookReference = FireBaseUtils.getBookingReference(FirebaseAuth.getInstance().getCurrentUser().getUid(), place.getId());
                bookReference.addValueEventListener(bookValueEventListener);
            }

        }
    }
}