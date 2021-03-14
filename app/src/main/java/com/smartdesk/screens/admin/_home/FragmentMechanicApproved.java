package com.smartdesk.screens.admin._home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.screens.admin.mechanics_status.ScreenMechanicDetail;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.model.signup.SignupMechanicDTO;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.utility.UtilityFunctions.picassoGetCircleImage;

public class FragmentMechanicApproved extends Fragment {

    private View view;
    private Activity context;
    boolean isStop;

    //RecyclerView Variables
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    Adapter adapter;
    List<SignupMechanicDTO> approvedMechanicDTOList = new ArrayList<>();

    public FragmentMechanicApproved() {
    }

    public FragmentMechanicApproved(Activity context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mechanic_approved, container, false);
        initIds();
        setRecyclerView();
        showDataOnList(false);
        return view;
    }

    private void initIds() {
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.SmartDesk_Editext_red), ContextCompat.getColor(context, R.color.SmartDesk_Blue));
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> showDataOnList(true), 0));
    }

    void onItemsLoadComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setRecyclerView() {
        new Handler().postDelayed(() -> {
            adapter = new Adapter(approvedMechanicDTOList);
            recyclerView = UtilityFunctions.setRecyclerView((RecyclerView) view.findViewById(R.id.recycler_view), context);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
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
        new Handler().postDelayed(() -> {
            if (!isSwipe)
                ((ScreenAdminHome) context).startAnim();
            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).whereEqualTo("role", Constants.workerRole).whereEqualTo("userStatus", Constants.activeStatus).get().
                    addOnSuccessListener(task -> {
                        onItemsLoadComplete();
                        approvedMechanicDTOList.clear();
                        if (!isSwipe)
                            ((ScreenAdminHome) context).stopAnim();
                        if (!task.isEmpty()) {
                            List<SignupMechanicDTO> signupMechanicDTOSList = task.toObjects(SignupMechanicDTO.class);
                            if (signupMechanicDTOSList.isEmpty()) {
                                view.findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            } else {
                                view.findViewById(R.id.listEmptyText).setVisibility(View.GONE);

                                if (signupMechanicDTOSList.size() > 0) {
                                    for (int i = 0; i < task.size(); i++)
                                        signupMechanicDTOSList.get(i).setLocalDocuementID(task.getDocuments().get(i).getId());
                                    view.findViewById(R.id.listEmptyText).setVisibility(View.GONE);
                                    approvedMechanicDTOList.clear();
                                    approvedMechanicDTOList.addAll(signupMechanicDTOSList);
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
                approvedMechanicDTOList.clear();
                onItemsLoadComplete();
                if (!isSwipe)
                    ((ScreenAdminHome) context).stopAnim();
                UtilityFunctions.redSnackBar(context, "No Internet!", Snackbar.LENGTH_SHORT);
                adapter.notifyDataSetChanged();
            });
        }, 0);
    }

    public class Adapter extends RecyclerView.Adapter<FragmentMechanicApproved.Adapter.ViewHolder> {

        List<SignupMechanicDTO> mechanicsList;

        public Adapter(List<SignupMechanicDTO> mechanicsList) {
            this.mechanicsList = mechanicsList;
        }

        @NonNull
        @Override
        public FragmentMechanicApproved.Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.rv_item_approved_mechanics, parent, false);
            view.findViewById(R.id.cardView).setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.whatsapp_green_dark));
            return new FragmentMechanicApproved.Adapter.ViewHolder(view);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }

        @Override
        public void onBindViewHolder(@NonNull FragmentMechanicApproved.Adapter.ViewHolder holder, final int position) {
            final Context innerContext = holder.itemView.getContext();
            Date registrationDate = mechanicsList.get(position).getRegistrationDate();
            String timeAgo = UtilityFunctions.remaingTimeCalculation(new Timestamp(new Date().getTime()), new Timestamp(registrationDate.getTime()));
            holder.ratingBar.setRating(UtilityFunctions.calculateRating(mechanicsList.get(position).getRatingUserCount(), mechanicsList.get(position).getRatingTotal()));
            holder.timeAgo.setText(timeAgo);
            holder.name.setText(mechanicsList.get(position).getWorkerName());
            holder.phoneNumber.setText(UtilityFunctions.getPhoneNumberInFormat(mechanicsList.get(position).getWorkerPhone()));
            picassoGetCircleImage(context,mechanicsList.get(position).getProfilePicture(), holder.profilePic, holder.profile_shimmer, R.drawable.side_profile_icon);

            holder.itemCardview.setOnClickListener(v -> {
                try {
                    ScreenMechanicDetail.mechanicDetailsScreenDTO = mechanicsList.get(position);
                    UtilityFunctions.sendIntentNormal((Activity) innerContext, new Intent(innerContext, ScreenMechanicDetail.class), false, 0);
                } catch (Exception ex) {
                }
            });
        }

        public int getItemCount() {
            return mechanicsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, phoneNumber, city, timeAgo, cnic;
            RatingBar ratingBar;
            CircleImageView profilePic;
            ShimmerFrameLayout profile_shimmer;
            LinearLayout itemCardview;

            public ViewHolder(@NonNull View view) {
                super(view);
                itemCardview = view.findViewById(R.id.cardView);
                name = view.findViewById(R.id.name);
                phoneNumber = view.findViewById(R.id.phoneNumber);
                profile_shimmer = view.findViewById(R.id.profile_shimmer);
                profilePic = view.findViewById(R.id.profilePic);
                cnic = view.findViewById(R.id.cnic);
                city = view.findViewById(R.id.city);
                ratingBar = view.findViewById(R.id.rating);
                timeAgo = view.findViewById(R.id.timeAgo);
            }
        }
    }
}
