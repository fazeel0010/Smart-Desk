package com.smartdesk.screens.consumer._home;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.screens.consumer.chat.ScreenChatList;
import com.smartdesk.screens.consumer.tutorials.ScreenTutorials;
import com.smartdesk.screens.consumer.workers_in_map.FragmentMapView;
import com.smartdesk.screens.consumer.workers_in_map.ScreenWorkersMap;
import com.smartdesk.screens.user_management.help.ScreenHelp;
import com.smartdesk.screens.user_management.login.ScreenLogin;
import com.smartdesk.screens.user_management.notification.ScreenNotification;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.location.FusedLocation;
import com.smartdesk.model.signup.SignupMechanicDTO;
import com.smartdesk.screens.consumer.track_mechanic.ScreenTrackMechanic;
import com.smartdesk.screens.user_management.setting.ScreenConsumerSetting;
import com.smartdesk.utility.memory.MemoryCache;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.utility.UtilityFunctions.picassoGetCircleImage;

public class ScreenConsumerHome extends AppCompatActivity {

    Activity context;
    DrawerLayout drawer;

    TextView name, mobile;
    CircleImageView profileImage;
    ShimmerFrameLayout profileShimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_consumer_home);
        context = this;
        initLoadingBarItems();
        actionbar();
        initIDS();
        UtilityFunctions.setupUI(findViewById(R.id.parent), this);
        realTimeListner();
        new FusedLocation(context, true, null).startLocationUpdates(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        name.setText(Constants.USER_NAME);
        mobile.setText(Constants.USER_MOBILE.substring(0, 4) + "-" + Constants.USER_MOBILE.substring(4));
        UtilityFunctions.picassoGetCircleImage(context, Constants.USER_PROFILE, profileImage, profileShimmerFrameLayout, R.drawable.side_profile_icon);
        getNotificationCount();
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
    }

    private void actionbar() {
        new Handler().postDelayed(() -> {
            Toolbar toolbar = findViewById(R.id.actionbarInclude).findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            ((TextView) findViewById(R.id.actionbarInclude).findViewById(R.id.actionTitleBar)).setText("Services");
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

    //======================================== Show Loading bar ==============================================
    private LinearLayout load, bg_main;
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
        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenConsumerSetting.class), false, 0);
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

    public void trackMechanic(View view) {
        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenTrackMechanic.class), false, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    public void goToMapView(View view) {
        switch (view.getId()) {
            case R.id.emergency:
                FragmentMapView.customService = "";
                UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenWorkersMap.class), false, 0);
                break;
            case R.id.car_Bike_Mechanic_Service:
                FragmentMapView.customService = Constants.workingCategoryItems[0];
                UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenWorkersMap.class), false, 0);
                break;
            case R.id.car_Mechanic_Service:
                FragmentMapView.customService = Constants.workingCategoryItems[1];
                UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenWorkersMap.class), false, 0);
                break;
            case R.id.bike_Mechanic_Service:
                FragmentMapView.customService = Constants.workingCategoryItems[2];
                UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenWorkersMap.class), false, 0);
                break;

            case R.id.car_Bike_Puncture_Service:
                FragmentMapView.customService = Constants.workingCategoryItems[3];
                UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenWorkersMap.class), false, 0);
                break;
            case R.id.car_Puncture_Service:
                FragmentMapView.customService = Constants.workingCategoryItems[4];
                UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenWorkersMap.class), false, 0);
                break;
            case R.id.bike_Puncture_Service:
                FragmentMapView.customService = Constants.workingCategoryItems[5];
                UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenWorkersMap.class), false, 0);
                break;
            case R.id.towing_Service:
                FragmentMapView.customService = Constants.workingCategoryItems[6];
                UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenWorkersMap.class), false, 0);
                break;
            case R.id.tutorials:
                FragmentMapView.customService = "";
                UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenTutorials.class), false, 0);
                break;
        }
    }
    //======================================== Show Loading bar ==============================================

    public void realTimeListner() {
        try {
            final DocumentReference docRef = FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).document(Constants.USER_DOCUMENT_ID);
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                try {
                    if (snapshot != null && snapshot.exists()) {
                        SignupMechanicDTO signupMechanicDTO = snapshot.toObject(SignupMechanicDTO.class);
                        if (signupMechanicDTO.getHired()) {
                            UtilityFunctions.alertNoteWithOkButton(context, "Worker Accepted", "Worker accepted your request"
                                    , Gravity.CENTER, R.color.whatsapp_green_dark, R.color.white, false, false, new Intent(context, ScreenTrackMechanic.class));
                        } else {
                        }
                    } else {
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}