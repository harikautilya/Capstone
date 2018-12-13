package com.example.kautilya.application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kautilya.application.databinding.ActivityMainBinding;
import com.example.kautilya.application.databinding.ItemPlaceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    List<Place> placeString;
    private ValueEventListener valueEventListener;
    private DatabaseReference localreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeString = new ArrayList<>();
        getViewBinding().data.setLayoutManager(new LinearLayoutManager(this));
        getViewBinding().data.setAdapter(new PlaceAdapter());
        localreference = FireBaseUtils.getPlaceDetails();
        setupFirebaseListeners();
        updateUI();
    }

    private void setupFirebaseListeners() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Place> places = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Place place = new Gson().fromJson(Objects.requireNonNull(child.getValue()).toString(), Place.class);
                    places.add(place);
                }
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallback(places, placeString));
                diffResult.dispatchUpdatesTo(getViewBinding().data.getAdapter());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }


    @Override
    protected void onPause() {
        super.onPause();
        localreference.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        localreference.addValueEventListener(valueEventListener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    void updateUI() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

    }

    class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {


        @Override
        public void onViewAttachedToWindow(@NonNull PlaceHolder holder) {
            super.onViewAttachedToWindow(holder);
            holder.addListener();

        }

        @Override
        public void onViewDetachedFromWindow(@NonNull PlaceHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.removeListener();
        }

        @NonNull
        @Override
        public PlaceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new PlaceHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_place, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PlaceHolder placeHolder, int i) {
            placeHolder.bind(placeString.get(i));
        }

        @Override
        public int getItemCount() {
            return placeString.size();
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
                Picasso.get()
                        .load(place.getImage_url())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(itemPlaceBinding.image);

                bookValueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        booked = ((boolean) dataSnapshot.getValue());

                        if (booked) {
                            itemPlaceBinding.book.setText(getString(R.string.cancel));
                        } else {
                            itemPlaceBinding.book.setText(getString(R.string.book));
                        }
                        if (place.getAvaiableTokens() == 0) {
                            itemPlaceBinding.book.setText(getString(R.string.sold));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };


                itemPlaceBinding.book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (place.getAvaiableTokens() == 0 && !booked) {
                            Toast.makeText(MainActivity.this, "Tickets Sold", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();

                                                } else {
                                                    Toast.makeText(MainActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        })
                                        .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
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

                }

            }

            public void addListener() {
                if (place != null) {
                    bookReference = FireBaseUtils.getBookingReference(FirebaseAuth.getInstance().getCurrentUser().getUid(), place.getId());

                }

            }
        }
    }

    public class MyDiffCallback extends DiffUtil.Callback {

        List<Place> oldPlaces;
        List<Place> newPlaces;

        public MyDiffCallback(List<Place> newPlaces, List<Place> oldPlaces) {
            this.newPlaces = newPlaces;
            this.oldPlaces = oldPlaces;
        }

        @Override
        public int getOldListSize() {
            return oldPlaces.size();
        }

        @Override
        public int getNewListSize() {
            return newPlaces.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPlaces.get(oldItemPosition).getId() == newPlaces.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPlaces.get(oldItemPosition).equals(newPlaces.get(newItemPosition));
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            //you can return particular field for changed item.
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }
}
