package com.smartdesk.screens.desk_users_screens._home.desk_user;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.databinding.ScreenSmartDeskDetailManagerBinding;
import com.smartdesk.databinding.ScreenSmartDeskDetailUserBinding;
import com.smartdesk.model.SmartDesk.NewDesk;
import com.smartdesk.model.fcm.Data;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.library.WorkaroundMapFragment;
import com.smartdesk.utility.memory.MemoryCache;

import java.sql.Timestamp;
import java.util.Date;

public class ScreenSmartDeskDetailUser extends AppCompatActivity {

    ScreenSmartDeskDetailUserBinding binding;
    private Activity context;
    public static NewDesk deskUserDetailsScreenDTO;


    private GoogleMap mMap;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ScreenSmartDeskDetailUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        actionBar("Smart Desk Detail");
        initIds();

        if (mMap == null) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(googleMap -> {
                    mMap = googleMap;
                    MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.google_style);
                    mMap.setMapStyle(mapStyleOptions);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getUiSettings().setZoomControlsEnabled(true);

                    final ScrollView mScrollView = findViewById(R.id.scrollView); //parent scrollview in xml, give your scrollview id value
                    ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                            .setListener(() -> mScrollView.requestDisallowInterceptTouchEvent(true));

                    try {
                        BitmapDescriptor icon = UtilityFunctions.getBitmapFromVector(context, R.drawable.z_desk_loading,
                                ContextCompat.getColor(context, R.color.colorPrimary));
                        LatLng latLngMechanic = new LatLng(deskUserDetailsScreenDTO.getDeskLat(), deskUserDetailsScreenDTO.getDeskLng());
                        mMap.addMarker(new MarkerOptions().icon(icon).position(latLngMechanic).title(deskUserDetailsScreenDTO.getName()));
                        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLngMechanic, Constants.cameraZoomInMap);
                        mMap.animateCamera(location);

                        icon = UtilityFunctions.getBitmapFromVector(context, R.drawable.icon_garage,
                                ContextCompat.getColor(context, R.color.SmartDesk_Editext_red));
                    } catch (Exception eex) {
                    }

                });
            }, 0);
        }
    }

    private void initIds() {
//        if (deskUserDetailsScreenDTO.getUserStatus().equalsIgnoreCase(Constants.activeStatus)) {
//            ((LinearLayout) findViewById(R.id.llStatuslist)).setWeightSum(8);
//            findViewById(R.id.approved).setVisibility(View.GONE);
//        } else if (deskUserDetailsScreenDTO.getUserStatus().equalsIgnoreCase(Constants.blockedStatus)) {
//            ((LinearLayout) findViewById(R.id.llStatuslist)).setWeightSum(8);
//            findViewById(R.id.blocked).setVisibility(View.GONE);
//
//            findViewById(R.id.bgStatus).setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.SmartDesk_Orange));
//            binding.statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_block));
//            binding.statusText.setText("Account is blocked");
//        } else {
//            findViewById(R.id.bgStatus).setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.SmartDesk_Orange));
//            binding.statusImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_time_simple));
//            binding.statusText.setText("Account is in Review");
//        }

        binding.deskID.setText(deskUserDetailsScreenDTO.getId());
        binding.name.setText(deskUserDetailsScreenDTO.getName());
        binding.registrationDate.setText("Reg. date: " + UtilityFunctions.getDateFormat(deskUserDetailsScreenDTO.getRegistrationDate()));

        Date registrationDate = deskUserDetailsScreenDTO.getRegistrationDate();
        String time = UtilityFunctions.remaingTimeCalculation(new Timestamp(new Date().getTime()), new Timestamp(registrationDate.getTime()));
        binding.timeAgo.setText(time);
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
    private ObjectAnimator anim;
    private Boolean isLoad;

    public void startAnim() {
        isLoad = true;
        binding.loadingView.setVisibility(View.VISIBLE);
        binding.bgMain.setAlpha((float) 0.2);
        anim = UtilityFunctions.loadingAnim(this, binding.loadingImage);
        binding.loadingView.setOnTouchListener((v, event) -> isLoad);
    }

    public void stopAnim() {
        anim.end();
        binding.loadingView.setVisibility(View.GONE);
        binding.bgMain.setAlpha((float) 1);
        isLoad = false;
    }

    public void delete(View view) {
        setDeskStatus("Are you sure, you want to delete the Desk?", R.color.red);
    }

    public void setDeskStatus(String text, int color) {
        try {
            AlertDialog confirmationAlert = new AlertDialog.Builder(context).create();
            final View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_return, null);
            ((TextView) dialogView.findViewById(R.id.noteTitle)).setText("Smart Desk Status");
            dialogView.findViewById(R.id.llTITLECOLOR).setBackgroundTintList(ContextCompat.getColorStateList(context, color));
            dialogView.findViewById(R.id.yesbtn).setBackgroundTintList(ContextCompat.getColorStateList(context, color));
            ((TextView) dialogView.findViewById(R.id.noteText)).setText(text);
            dialogView.findViewById(R.id.yesbtn).setOnClickListener(v -> {
                confirmationAlert.dismiss();
                startAnim();
                if (color == R.color.red) {
                    String docId = UtilityFunctions.getDocumentID(context);

                    FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.smartDeskCollection).document(deskUserDetailsScreenDTO.getDocID())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                stopAnim();

                                UtilityFunctions.sendFCMMessage(context, new Data(docId, new Timestamp(new Date().getTime()).getTime(), "deletion", "SmartDesk deleted", deskUserDetailsScreenDTO.getName() + " your desk has been deleted"));
                                UtilityFunctions.deleteUserCompletelu(docId);

                                UtilityFunctions.greenSnackBar(context,  "Smart Desk Deleted Successfully!", Snackbar.LENGTH_LONG);
                                new Handler(Looper.getMainLooper()).postDelayed(() -> finish(), 1000);
                            }).addOnFailureListener(e -> {
                        stopAnim();
                        UtilityFunctions.redSnackBar(context, "Smart Desk Deletion Unsuccessfully!", Snackbar.LENGTH_LONG);
                    });
                }
            });

            dialogView.findViewById(R.id.nobtn).setOnClickListener(v -> confirmationAlert.dismiss());
            confirmationAlert.setView(dialogView);
            confirmationAlert.setCancelable(false);
            confirmationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmationAlert.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //======================================== Show Loading bar ==============================================

    public void moveToMechanicLocation(View view) {
        try {
            LatLng latLng = new LatLng(deskUserDetailsScreenDTO.getDeskLat(), deskUserDetailsScreenDTO.getDeskLng());
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    latLng, Constants.cameraZoomInMap);
            mMap.animateCamera(location);
        } catch (Exception ex) {
        }
    }

    public void Book(View view) {
        UtilityFunctions.orangeSnackBar(context,"Feature Not Implemented Yet",Snackbar.LENGTH_LONG);
    }
}