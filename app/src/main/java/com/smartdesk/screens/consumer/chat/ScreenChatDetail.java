package com.smartdesk.screens.consumer.chat;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.model.signup.SignupMechanicDTO;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.library.WorkaroundMapFragment;
import com.smartdesk.utility.memory.MemoryCache;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.utility.UtilityFunctions.noRoundImageCorner;
import static com.smartdesk.utility.UtilityFunctions.picassoGetCircleImage;
import static java.util.ResourceBundle.clearCache;

public class ScreenChatDetail extends AppCompatActivity {

    Activity context;
    public static SignupMechanicDTO mechanicDetailsScreenDTO;

    private GoogleMap mMap;
    TextView name, phoneNumber, city, timeAgo, cnic, registrationDate, location;
    TextView shopName, shopCategory, shopWorkingCategory, gender;
    CircleImageView profilePic;
    ShimmerFrameLayout profile_shimmer;

    ImageView statusImage;
    TextView statusText;
    private TextView shopNumber;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_chat_detail);
        context = this;
        actionBar("Worker Details");
        initLoadingBarItems();
        setData();
    }

    private void setData() {
        if (mMap == null) {
            new Handler().postDelayed(() -> {
                SupportMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(googleMap -> {
                    mMap = googleMap;
                    MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.google_style);
                    mMap.setMapStyle(mapStyleOptions);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getUiSettings().setZoomControlsEnabled(true);

                    final ScrollView mScrollView = findViewById(R.id.scrollView); //parent scrollview in xml, give your scrollview id value
                    ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                            .setListener(() -> mScrollView.requestDisallowInterceptTouchEvent(true));

                    BitmapDescriptor icon = UtilityFunctions.getBitmapFromVector(context, R.drawable.icon_mechanic,
                            ContextCompat.getColor(context, R.color.SmartDesk_Editext_red));
                    LatLng latLngMechanic = new LatLng(mechanicDetailsScreenDTO.getWorkerLat(), mechanicDetailsScreenDTO.getWorkerLng());
                    mMap.addMarker(new MarkerOptions().icon(icon).position(latLngMechanic).title(mechanicDetailsScreenDTO.getWorkerName()));
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLngMechanic, Constants.cameraZoomInMap);
                    mMap.animateCamera(location);

                    icon = UtilityFunctions.getBitmapFromVector(context, R.drawable.icon_garage,
                            ContextCompat.getColor(context, R.color.SmartDesk_Editext_red));
//                    mMap.addMarker(new MarkerOptions().icon(icon).position(latLngShop).title(mechanicDetailsScreenDTO.getShopName()));
                });
            }, 0);
        }
        statusImage = findViewById(R.id.statusImage);
        statusText = findViewById(R.id.statusText);
        if (mechanicDetailsScreenDTO.getUserStatus().equalsIgnoreCase(Constants.activeStatus)) {

        } else if (mechanicDetailsScreenDTO.getUserStatus().equalsIgnoreCase(Constants.blockedStatus)) {

            findViewById(R.id.bgStatus).setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.SmartDesk_Orange));
            statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_block));
            statusText.setText("Account is blocked");
        } else {
            findViewById(R.id.bgStatus).setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.SmartDesk_Orange));
            statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_time_simple));
            statusText.setText("Account is in Review");
        }
        shopName = findViewById(R.id.shopName);
        shopNumber = findViewById(R.id.shopNumber);
        gender = findViewById(R.id.gender);
        shopCategory = findViewById(R.id.shopCategory);
        shopWorkingCategory = findViewById(R.id.workingCategory);

        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);
        profile_shimmer = findViewById(R.id.profile_shimmer);

        profilePic = findViewById(R.id.profilePic);

        cnic = findViewById(R.id.cnic);
        city = findViewById(R.id.city);
        location = findViewById(R.id.address);
        registrationDate = findViewById(R.id.registrationDate);
        timeAgo = findViewById(R.id.timeAgo);

        UtilityFunctions.picassoGetCircleImage(context, mechanicDetailsScreenDTO.getProfilePicture(), profilePic, profile_shimmer, R.drawable.side_profile_icon);

        gender.setText(mechanicDetailsScreenDTO.getWorkerGender());

        name.setText(mechanicDetailsScreenDTO.getWorkerName());
        registrationDate.setText("Reg date: " + UtilityFunctions.getDateFormat(mechanicDetailsScreenDTO.getRegistrationDate()));
        phoneNumber.setText(UtilityFunctions.getPhoneNumberInFormat(mechanicDetailsScreenDTO.getWorkerPhone()));
        location.setText(mechanicDetailsScreenDTO.getShopLocation());
        Date registrationDate = mechanicDetailsScreenDTO.getRegistrationDate();
        String time = UtilityFunctions.remaingTimeCalculation(new Timestamp(new Date().getTime()), new Timestamp(registrationDate.getTime()));
        timeAgo.setText(time);

        ((RatingBar) findViewById(R.id.rating)).setClickable(false);
        ((RatingBar) findViewById(R.id.rating)).setRating(UtilityFunctions.calculateRating(mechanicDetailsScreenDTO.getRatingUserCount(), mechanicDetailsScreenDTO.getRatingTotal()));
        ((TextView) findViewById(R.id.userReviews)).setText(mechanicDetailsScreenDTO.getRatingUserCount() + " user reviews");
    }

    public void actionBar(String actionTitle) {
        Toolbar a = findViewById(R.id.actionbarInclude).findViewById(R.id.toolbar);
        setSupportActionBar(a);
        ((TextView) findViewById(R.id.actionbarInclude).findViewById(R.id.actionTitleBar)).setText(actionTitle);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.icon_chevron_left_blue));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    public void sendBtn(View view) {
    }

    public void Call(View view) {
    }

    public void moveToMechanicLocation(View view) {
        LatLng latLng = new LatLng(mechanicDetailsScreenDTO.getWorkerLat(), mechanicDetailsScreenDTO.getWorkerLng());
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                latLng, Constants.cameraZoomInMap);
        mMap.animateCamera(location);
    }

    public void viewProfilePic(View view) {
        showImageOn(mechanicDetailsScreenDTO.getProfilePicture(), "Profile");
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
    //======================================== Show Loading bar ==============================================
}