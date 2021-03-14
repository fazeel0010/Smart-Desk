package com.smartdesk.screens.worker._home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.model.hire_worker.WorkerRequestDTO;
import com.smartdesk.screens.consumer.chat.ScreenChatList;
import com.smartdesk.screens.worker.payment_history.ScreenPaymentHistory;
import com.smartdesk.screens.worker.track_consumer.ScreenTrackConsumerHired;
import com.smartdesk.screens.user_management.help.ScreenHelp;
import com.smartdesk.screens.user_management.login.ScreenLogin;
import com.smartdesk.screens.user_management.notification.ScreenNotification;
import com.smartdesk.screens.user_management.setting.ScreenMechanicSetting;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.location.FusedLocation;
import com.smartdesk.model.fcm.Data;
import com.smartdesk.model.notification.NotificationDTO;
import com.smartdesk.screens.worker.user_requests.ScreenUserRequest;
import com.smartdesk.utility.memory.MemoryCache;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.utility.UtilityFunctions.noRoundImageCorner;
import static com.smartdesk.utility.UtilityFunctions.picassoGetCircleImage;
import static java.util.ResourceBundle.clearCache;

public class ScreenWorkerHome extends AppCompatActivity {

    Activity context;
    DrawerLayout drawer;

    TextView name, mobile;
    CircleImageView profileImage;
    ShimmerFrameLayout profileShimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_mechanic_home);
        context = this;
        initLoadingBarItems();
        actionbar();
        initIDS();
        setRecyclerView();
        UtilityFunctions.setupUI(findViewById(R.id.parent), this);
        new FusedLocation(context, true, null).startLocationUpdates(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Switch switch1 = (findViewById(R.id.onlineOffline));
        if (Constants.USER_MECHANIC_ONLINE) {
            switch1.setChecked(true);
            ((ImageView) findViewById(R.id.onlineImg)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_online));
        } else {
            switch1.setChecked(false);
            ((ImageView) findViewById(R.id.onlineImg)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_offline));
        }
        name.setText(Constants.USER_NAME);
        mobile.setText(Constants.USER_MOBILE.substring(0, 4) + "-" + Constants.USER_MOBILE.substring(4));
        UtilityFunctions.picassoGetCircleImage(context, Constants.USER_PROFILE, profileImage, profileShimmerFrameLayout, R.drawable.side_profile_icon);
        getNotificationCount();

        showDataOnList(false);
    }

    private void getNotificationCount() {
        Constants.notificationCount = 0;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.notificationCollection).whereEqualTo("documentID", Constants.USER_DOCUMENT_ID)
                    .whereEqualTo("read", false).get()
                    .addOnSuccessListener(task -> {
                        for (QueryDocumentSnapshot document : task) {
                            Constants.notificationCount++;
                        }
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            ((TextView) findViewById(R.id.countNotify)).setText("" + Constants.notificationCount);
                        }, 0);
                    }).addOnFailureListener(e -> {
            });
        }, 0);
    }

    private void initIDS() {
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.phoneNumber);
        profileImage = findViewById(R.id.profilePic);
        profileShimmerFrameLayout = findViewById(R.id.profile_shimmer);

        name.setText(Constants.USER_NAME);
        mobile.setText(Constants.USER_MOBILE.substring(0, 4) + "-" + Constants.USER_MOBILE.substring(4));
        UtilityFunctions.picassoGetCircleImage(context, Constants.USER_PROFILE, profileImage, profileShimmerFrameLayout, R.drawable.side_profile_icon);

        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.SmartDesk_Editext_red), ContextCompat.getColor(context, R.color.SmartDesk_Blue));
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> showDataOnList(true), 0));
    }

    private void actionbar() {
        new Handler().postDelayed(() -> {
            Toolbar toolbar = findViewById(R.id.actionbarInclude).findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            ((TextView) findViewById(R.id.actionbarInclude).findViewById(R.id.actionTitleBar)).setText("User Requests");
            drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(context, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.setHomeAsUpIndicator(R.drawable.icon_menu);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }, 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        new Handler().postDelayed(() -> drawer.openDrawer(GravityCompat.START), 0);
        return true;
    }

    @Override
    public void onBackPressed() {
        closeDrawer();
    }

    public void closeDrawer() {
        new Handler().postDelayed(() -> {
            try {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0);
    }

    //GET DATA

    //RecyclerView Variables
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ScreenWorkerHome.Adapter adapter;
    List<WorkerRequestDTO> workerRequestDTOS = new ArrayList<>();


    void onItemsLoadComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setRecyclerView() {
        new Handler().postDelayed(() -> {
            adapter = new Adapter(workerRequestDTOS);
            recyclerView = UtilityFunctions.setRecyclerView(findViewById(R.id.recycler_view), context);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }, 0);
    }

    public void showDataOnList(Boolean isSwipe) {
        new Handler().postDelayed(() -> {
            if (!isSwipe)
                startAnim();
            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.requestCollection).document(Constants.USER_DOCUMENT_ID).collection(FirebaseConstants.requestCollection)
                    .whereEqualTo("workerStatus", Constants.workerRequest)
                    .get().addOnSuccessListener(task -> {
                onItemsLoadComplete();
                workerRequestDTOS.clear();
                if (!isSwipe)
                    stopAnim();
                if (task.isEmpty()) {
                    findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    findViewById(R.id.listEmptyText).setVisibility(View.GONE);
                    List<WorkerRequestDTO> signupMechanicDTOSList = task.toObjects(WorkerRequestDTO.class);
                    if (signupMechanicDTOSList.size() > 0) {
                        for (int i = 0; i < task.size(); i++)
                            signupMechanicDTOSList.get(i).setDeleteMainRequestDocID(task.getDocuments().get(i).getId());
                        findViewById(R.id.listEmptyText).setVisibility(View.GONE);
                        workerRequestDTOS.addAll(signupMechanicDTOSList);
                        adapter.notifyDataSetChanged();
                    } else {
                        findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                }
                adapter.notifyDataSetChanged();
            }).addOnFailureListener(e -> {
                onItemsLoadComplete();
                workerRequestDTOS.clear();
                if (!isSwipe)
                    stopAnim();
                adapter.notifyDataSetChanged();
            });
        }, 0);
    }


    public class Adapter extends RecyclerView.Adapter<ScreenWorkerHome.Adapter.ViewHolder> {

        List<WorkerRequestDTO> workerRequestDTOList;

        public Adapter(List<WorkerRequestDTO> workerRequestDTOList) {
            this.workerRequestDTOList = workerRequestDTOList;
        }

        @NonNull
        @Override
        public ScreenWorkerHome.Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.rv_item_user_requests, parent, false);
            return new ScreenWorkerHome.Adapter.ViewHolder(view);
        }

        @Override
        public void onViewDetachedFromWindow(ScreenWorkerHome.Adapter.ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }

        @Override
        public void onBindViewHolder(@NonNull ScreenWorkerHome.Adapter.ViewHolder holder, final int position) {
            final Context innerContext = holder.itemView.getContext();

            holder.company.setText(workerRequestDTOList.get(position).getCompany());
            holder.model.setText(workerRequestDTOList.get(position).getModel());

            holder.name.setText(workerRequestDTOList.get(position).getName());
            holder.phoneNumber.setText(UtilityFunctions.getPhoneNumberInFormat(workerRequestDTOList.get(position).getNumber()));
            UtilityFunctions.picassoGetCircleImage(context, workerRequestDTOList.get(position).getProfileImage(), holder.profilePic, holder.profile_shimmer, R.drawable.side_profile_icon);

            holder.profilePic.setOnClickListener(v -> {
                showImageOn(workerRequestDTOList.get(position).getProfileImage(), "Profile");
            });

            holder.accept.setOnClickListener(v -> {
                startAnim();
                FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.requestCollection).document(Constants.USER_DOCUMENT_ID).collection(FirebaseConstants.requestCollection)
                        .document(workerRequestDTOList.get(position).getConsumerDocumentID())
                        .update("workerStatus", Constants.workerConfirm,
                                "hired", true)
                        .addOnSuccessListener(aVoid -> {
                            stopAnim();

                            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).document(Constants.USER_DOCUMENT_ID).update("hired", true);
                            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).document(workerRequestDTOList.get(position).getConsumerDocumentID())
                                    .update("hired", true, "workerDocumentID", Constants.USER_DOCUMENT_ID).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenTrackConsumerHired.class), true, Constants.changeIntentDelay);
                                }
                            });

                            UtilityFunctions.sendFCMMessage(context, new Data(workerRequestDTOList.get(position).getConsumerDocumentID(), new Timestamp(new Date().getTime()).getTime(), "Accepted", "Request accepted", " your request has been accepted by " + Constants.USER_NAME));
                            UtilityFunctions.saveNotficationCollection(new NotificationDTO(Constants.consumerRole, workerRequestDTOList.get(position).getConsumerDocumentID(), new Timestamp(new Date().getTime()), "Request accepted", " your request has been accepted by " + Constants.USER_NAME, false));
                        }).addOnFailureListener(e -> {
                    stopAnim();
                });
            });

            holder.reject.setOnClickListener(v -> {

                startAnim();
                FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.requestCollection).document(Constants.USER_DOCUMENT_ID).collection(FirebaseConstants.requestCollection)
                        .document(workerRequestDTOList.get(position).getConsumerDocumentID()).delete()
                        .addOnSuccessListener(aVoid -> {
                            stopAnim();

                            UtilityFunctions.sendFCMMessage(context, new Data(workerRequestDTOList.get(position).getConsumerDocumentID(), new Timestamp(new Date().getTime()).getTime(), "Rejected", "Request rejected", " your request has been rejected by " + Constants.USER_NAME));
                            UtilityFunctions.saveNotficationCollection(new NotificationDTO(Constants.consumerRole, workerRequestDTOList.get(position).getConsumerDocumentID(), new Timestamp(new Date().getTime()), "Request rejected", " your request has been rejected by " + Constants.USER_NAME, false));

                            showDataOnList(false);
                        }).addOnFailureListener(e -> {
                    stopAnim();
                });
            });
        }

        public int getItemCount() {
            return workerRequestDTOList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, phoneNumber, model, company;
            CircleImageView profilePic;
            ShimmerFrameLayout profile_shimmer;
            Button accept, reject;

            public ViewHolder(@NonNull View view) {
                super(view);
                accept = view.findViewById(R.id.accept);
                reject = view.findViewById(R.id.reject);

                company = view.findViewById(R.id.company);
                model = view.findViewById(R.id.model);
                name = view.findViewById(R.id.name);
                phoneNumber = view.findViewById(R.id.phoneNumber);
                profile_shimmer = view.findViewById(R.id.profile_shimmer);
                profilePic = view.findViewById(R.id.profilePic);
            }
        }
    }

    public void showImageOn(String alertDialogImageURL, String name) {
        try {
            clearCache();
            final AlertDialog confirmationAlert = new AlertDialog.Builder(context).create();
            final View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_big_image, null);
            ImageView img = dialogView.findViewById(R.id.imagePreview);
            TextView nameText = dialogView.findViewById(R.id.name);
            nameText.setText(name);
            ShimmerFrameLayout shimmer = dialogView.findViewById(R.id.shimmer);
            UtilityFunctions.noRoundImageCorner(this, img, alertDialogImageURL, shimmer);
            dialogView.findViewById(R.id.retake).setVisibility(View.GONE);
            dialogView.findViewById(R.id.hideDialog).setOnClickListener(v -> {
                confirmationAlert.dismiss();
                confirmationAlert.cancel();
            });

            confirmationAlert.setView(dialogView);
            confirmationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmationAlert.show();
        } catch (Exception ex) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    //======================================== Show Loading bar ==============================================
    private LinearLayout load;
    CoordinatorLayout bg_main;
    private ObjectAnimator anim;
    private ImageView progressBar;
    private Boolean isLoad;

    private void initLoadingBarItems() {
        load = findViewById(R.id.loading_view);
        bg_main = findViewById(R.id.bg_main);
        progressBar = findViewById(R.id.loading_image);
    }

    public void startAnim() {
        isLoad = true;
        load.setVisibility(View.VISIBLE);
        bg_main.setAlpha((float) 0.2);
        anim = UtilityFunctions.loadingAnim(this, progressBar);
        load.setOnTouchListener((v, event) -> isLoad);
    }

    public void stopAnim() {
        anim.end();
        load.setVisibility(View.GONE);
        bg_main.setAlpha((float) 1);
        isLoad = false;
    }

    public void notifications(View view) {
        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenNotification.class), false, 0);
    }

    public void settings(View view) {
        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenMechanicSetting.class), false, 0);
    }

    public void logout(View view) {
        closeDrawer();
        UtilityFunctions.removeLoginInfoInSharedPreference(this);
        UtilityFunctions.logoutSnackBar(this, "Logout Successfully!", Snackbar.LENGTH_SHORT);
        UtilityFunctions.sendIntentClearPreviousActivity(context, new Intent(context, ScreenLogin.class), Constants.changeIntentDelay);
    }

    public void help(View view) {
        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenHelp.class), false, 0);
    }

    public void chat(View view) {
        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenChatList.class), false, 0);
    }

    public void paymentHistory(View view) {
        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenPaymentHistory.class), false, 0);
    }

    public void userRequest(View view) {
        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenUserRequest.class), false, 0);
    }

    public void onlineOffiline(View view) {
        Switch switch1 = (findViewById(R.id.onlineOffline));
        if (switch1.isChecked()) {
            ((ImageView) findViewById(R.id.onlineImg)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_online));
            putOnlineStatus(true);
        } else {
            putOnlineStatus(false);
            ((ImageView) findViewById(R.id.onlineImg)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_offline));
        }
    }

    public void putOnlineStatus(Boolean value) {
        FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).document(Constants.USER_DOCUMENT_ID)
                .update("online", value)
                .addOnSuccessListener(aVoid -> {
                    Constants.USER_MECHANIC_ONLINE = value;
                });
    }
    //======================================== Show Loading bar ==============================================
}