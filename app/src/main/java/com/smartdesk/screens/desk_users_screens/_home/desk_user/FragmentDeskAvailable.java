package com.smartdesk.screens.desk_users_screens._home.desk_user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.smartdesk.R;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.model.SmartDesk.NewDesk;
import com.smartdesk.screens.admin.desk_user_status.ScreenDeskUserDetail;
import com.smartdesk.screens.desk_users_screens._home.ScreenDeskUserHome;
import com.smartdesk.utility.UtilityFunctions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.utility.UtilityFunctions.getDeskRegDate;
import static com.smartdesk.utility.UtilityFunctions.picassoGetCircleImage;

public class FragmentDeskAvailable extends Fragment {

    private View view;
    private Activity context;
    boolean isStop;

    //RecyclerView Variables
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    Adapter adapter;
    List<NewDesk> avaiablesDesks = new ArrayList<>();

    public FragmentDeskAvailable() {
    }

    public FragmentDeskAvailable(Activity context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_request, container, false);
        initIds();
        ((TextView) view.findViewById(R.id.listEmptyText)).setText("Desks are not available");
        setRecyclerView();
        showDataOnList(false);
        return view;
    }

    private void initIds() {
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.SmartDesk_Editext_red), ContextCompat.getColor(context, R.color.SmartDesk_Blue));
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> showDataOnList(true), 0));
    }

    void onItemsLoadComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setRecyclerView() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter = new Adapter(avaiablesDesks);
                recyclerView = UtilityFunctions.setRecyclerView((RecyclerView) view.findViewById(R.id.recycler_view), context);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }, 0);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isStop) {
            showDataOnList(false);
            isStop = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isStop = true;
    }

    public void showDataOnList(Boolean isSwipe) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isSwipe)
                ((ScreenDeskUserHome) context).startAnim();
            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.smartDeskCollection).get()
                    .addOnSuccessListener(task -> {
                        onItemsLoadComplete();
                        avaiablesDesks.clear();
                        if (!isSwipe)
                            ((ScreenDeskUserHome) context).stopAnim();
                        if (!task.isEmpty()) {
                            List<NewDesk> deskLLL = task.toObjects(NewDesk.class);
                            if (deskLLL.isEmpty()) {
                                view.findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            } else {
                                view.findViewById(R.id.listEmptyText).setVisibility(View.GONE);
                                if (deskLLL.size() > 0) {
                                    view.findViewById(R.id.listEmptyText).setVisibility(View.GONE);
                                    avaiablesDesks.clear();
                                    avaiablesDesks.addAll(deskLLL);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    view.findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            view.findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
//                                redSnackBar(context, "No Internet!", Snackbar.LENGTH_SHORT);
                        }
                        adapter.notifyDataSetChanged();
                    }).addOnFailureListener(e -> {
                onItemsLoadComplete();
                avaiablesDesks.clear();
                if (!isSwipe)
                    ((ScreenDeskUserHome) context).stopAnim();
                UtilityFunctions.redSnackBar(context, "No Internet!", Snackbar.LENGTH_SHORT);
                adapter.notifyDataSetChanged();
            });
        }, 0);
    }

    public class Adapter extends RecyclerView.Adapter<FragmentDeskAvailable.Adapter.ViewHolder> {

        List<NewDesk> availableDeskList;

        public Adapter(List<NewDesk> availableDeskList) {
            this.availableDeskList = availableDeskList;
        }

        @NonNull
        @Override
        public FragmentDeskAvailable.Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.rv_item_for_desk, parent, false);
            view.findViewById(R.id.cardView).setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.tumblr_logo));
            return new FragmentDeskAvailable.Adapter.ViewHolder(view);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }

        @Override
        public void onBindViewHolder(@NonNull FragmentDeskAvailable.Adapter.ViewHolder holder, final int position) {
            final Context innerContext = holder.itemView.getContext();

            Date registrationDate = availableDeskList.get(position).getRegistrationDate();
            String timeAgo = UtilityFunctions.remaingTimeCalculation(new Timestamp(new Date().getTime()), new Timestamp(registrationDate.getTime()));
            holder.timeAgo.setText(timeAgo);

            holder.regDate.setText(UtilityFunctions.getDateFormat(availableDeskList.get(position).getRegistrationDate()));
            holder.name.setText(availableDeskList.get(position).getName());
            holder.deskID.setText(UtilityFunctions.getDeskID(availableDeskList.get(position).id));

            holder.mordetails.setOnClickListener(v -> {
                try {
                    ScreenSmartDeskDetailUser.deskUserDetailsScreenDTO = availableDeskList.get(position);
                    UtilityFunctions.sendIntentNormal((Activity) innerContext, new Intent(innerContext, ScreenSmartDeskDetailUser.class), false, 0);
                } catch (Exception ex) {
                }
            });
        }

        public int getItemCount() {
            return availableDeskList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, deskID, regDate,timeAgo;
            Button mordetails;

            public ViewHolder(@NonNull View view) {
                super(view);
                mordetails = view.findViewById(R.id.moreDetailsbtn);
                timeAgo = view.findViewById(R.id.timeAgo);
                name = view.findViewById(R.id.name);
                deskID = view.findViewById(R.id.deskID);
                regDate = view.findViewById(R.id.regDate);
            }
        }
    }
}
