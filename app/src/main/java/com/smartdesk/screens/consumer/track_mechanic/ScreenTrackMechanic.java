package com.smartdesk.screens.consumer.track_mechanic;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.model.hire_worker.WorkerRequestDTO;
import com.smartdesk.model.signup.SignupMechanicDTO;
import com.smartdesk.screens.consumer._home.ScreenConsumerHome;
import com.smartdesk.screens.consumer.chat.ScreenChat;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.library.WorkaroundMapFragment;
import com.smartdesk.utility.location.FusedLocation;
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
import com.google.firebase.firestore.DocumentReference;
import com.hsalf.smileyrating.SmileyRating;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.utility.UtilityFunctions.picassoGetCircleImage;
import static com.smartdesk.utility.UtilityFunctions.sendIntentNormal;

public class ScreenTrackMechanic extends AppCompatActivity {

    private Activity context;
    SignupMechanicDTO myData;
    WorkerRequestDTO workerRequestDTO;

    public SignupMechanicDTO mechanicDetailsScreenDTO;

    private GoogleMap mMap;
    TextView name, phoneNumber, location, address, model, company;
    CircleImageView profilePic;
    ShimmerFrameLayout profile_shimmer;

    Button statusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_track_mechanic);
        context = this;
        actionBar("Track Mechanic");
        initLoadingBarItems();
        new FusedLocation(context, true, null).startLocationUpdates(false);
        getMechanic();
    }

    private void getMechanic() {
        startAnim();
        FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).document(Constants.USER_DOCUMENT_ID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        myData = documentSnapshot.toObject(SignupMechanicDTO.class);
                        if (myData.getHired() && myData.getWorkerDocumentID() != null && !myData.getWorkerDocumentID().equals("")) {
                            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).document(myData.getWorkerDocumentID()).get()
                                    .addOnSuccessListener(mechanicSnapshot -> {
                                        stopAnim();

                                        mechanicDetailsScreenDTO = mechanicSnapshot.toObject(SignupMechanicDTO.class);
                                        realTimeListner();
                                        locationRealTime();
                                    }).addOnFailureListener(e -> {
                                stopAnim();
                            });
                        } else {
                            stopAnim();
                        }
                    } catch (Exception ex) {
                        stopAnim();
                    }
                }).addOnFailureListener(e -> {
            stopAnim();
        });
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

    public void call(View view) {
        UtilityFunctions.openCallingScreen(context, mechanicDetailsScreenDTO.getWorkerPhone());
    }

    public void chat(View view) {
        try {
            ScreenChat.Chat_DOCUMENTID_OTHER = myData.getWorkerDocumentID();
            ScreenChat.CHAT_MOBILE = mechanicDetailsScreenDTO.getWorkerPhone();
            ScreenChat.CHAT_PROFILE = mechanicDetailsScreenDTO.getProfilePicture();
            ScreenChat.CHAT_NAME = mechanicDetailsScreenDTO.getWorkerName();
            sendIntentNormal(context, new Intent(context, ScreenChat.class), false, 0);
        } catch (Exception ex) {

        }
    }

    private void setData(WorkerRequestDTO workerRequestDTO) {
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
                });
            }, 0);
        }

        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNumber);
        profile_shimmer = findViewById(R.id.profile_shimmer);
        model = findViewById(R.id.model);
        company = findViewById(R.id.company);
        location = findViewById(R.id.address);
        profilePic = findViewById(R.id.profilePic);
        statusButton = findViewById(R.id.accept);


        UtilityFunctions.picassoGetCircleImage(context, mechanicDetailsScreenDTO.getProfilePicture(), profilePic, profile_shimmer, R.drawable.side_profile_icon);


        name.setText(mechanicDetailsScreenDTO.getWorkerName());
        company.setText(workerRequestDTO.getCompany());
        model.setText(workerRequestDTO.getModel());
        phoneNumber.setText(UtilityFunctions.getPhoneNumberInFormat(mechanicDetailsScreenDTO.getWorkerPhone()));
        location.setText(UtilityFunctions.getAddressLatLng(context, mechanicDetailsScreenDTO.getWorkerLat(), mechanicDetailsScreenDTO.getWorkerLng()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    public void realTimeListner() {
        try {
            final DocumentReference docRef = FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.requestCollection).document(myData.getWorkerDocumentID())
                    .collection(FirebaseConstants.requestCollection).document(Constants.USER_DOCUMENT_ID);
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                try {
                    if (snapshot != null && snapshot.exists()) {
                        workerRequestDTO = snapshot.toObject(WorkerRequestDTO.class);
                        setData(workerRequestDTO);
                        (findViewById(R.id.notedText)).setVisibility(View.GONE);
                        (findViewById(R.id.hideContactFeatures)).setVisibility(View.VISIBLE);

                        localWorkerStatus = workerRequestDTO.getWorkerStatus();

                        if (workerRequestDTO.getWorkerStatus().equals(Constants.workerRequest)) {

                            setColorOfStatusPoint(findViewById(R.id.workerRequestPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerConfirmPoint), R.color.SmartDesk_Orange);

                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerConfirm)) {

                            setColorOfStatusPoint(findViewById(R.id.workerRequestPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerConfirmPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerRechedLocationPoint), R.color.SmartDesk_Orange);

                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerReachedLocation)) {

                            setColorOfStatusPoint(findViewById(R.id.workerRequestPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerConfirmPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerRechedLocationPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerInProgressPoint), R.color.SmartDesk_Orange);

                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerInProgress)) {

                            setColorOfStatusPoint(findViewById(R.id.workerRequestPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerConfirmPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerRechedLocationPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerInProgressPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workDonePoint), R.color.SmartDesk_Orange);

                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerDone)) {
                            (findViewById(R.id.workDone)).setVisibility(View.VISIBLE);
                            setColorOfStatusPoint(findViewById(R.id.workerRequestPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerConfirmPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerRechedLocationPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerInProgressPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workDonePoint), R.color.green);
                            setMechanicStatus(((Button) findViewById(R.id.workDone)).getText().toString(), R.color.whatsapp_green_dark);
                        } else {
                            (findViewById(R.id.workDone)).setVisibility(View.VISIBLE);
                            setColorOfStatusPoint(findViewById(R.id.workerRequestPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerConfirmPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerRechedLocationPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workerInProgressPoint), R.color.green);
                            setColorOfStatusPoint(findViewById(R.id.workDonePoint), R.color.green);
                        }

                        if (workerRequestDTO.getWorkerStatus().equals(Constants.workerPayment)) {
                            ((Button) findViewById(R.id.workDone)).setText("Waiting for the Mechanic to send amount");
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerSendAmount)) {
                            ((Button) findViewById(R.id.workDone)).setText("Worker Sent his amount");
                            amount = workerRequestDTO.getAmount();
                            setMechanicStatus("Worker charge " + amount + " rupees for his work.\nIs this amount is okay for you?", R.color.whatsapp_green_dark);
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerAmountFine)) {
                            ((Button) findViewById(R.id.workDone)).setText("Waiting for the Worker");
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerAmountNotFine)) {
                            ((Button) findViewById(R.id.workDone)).setText("Waiting for the Worker to send amount again");
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerFinish)) {
                            ((Button) findViewById(R.id.workDone)).setText("Work Completed");
                            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).document(Constants.USER_DOCUMENT_ID)
                                    .update("hired", false
                                            , "workerDocumentID", "");

                            ratingAlert(context, Gravity.CENTER, R.color.whatsapp_green_dark, R.color.white);

                        }
                    } else {
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (
                Exception ex) {
            ex.printStackTrace();
        }
        //======================================== Show Loading bar ==============================================
    }

    Integer ratingPercentage;

    public void ratingAlert(Activity activity, int gravity, int bgColor, int fontColor) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                final AlertDialog confirmationAlert = new AlertDialog.Builder(activity).create();
                final View dialogView = activity.getLayoutInflater().inflate(R.layout.alert_rating_dialog, null);
                ((TextView) dialogView.findViewById(R.id.noteText)).setGravity(gravity);

                ratingPercentage = 0;
                RatingBar ratingBar = dialogView.findViewById(R.id.rating);
                SmileyRating smileyRating = dialogView.findViewById(R.id.smile_rating);
                smileyRating.setSmileySelectedListener(type -> {
                    ratingPercentage = type.getRating();
                    (dialogView.findViewById(R.id.errorRating)).setVisibility(View.GONE);
                    ratingBar.setRating(ratingPercentage);
                });

                ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
                    ratingPercentage = Math.round(rating);
                    (dialogView.findViewById(R.id.errorRating)).setVisibility(View.GONE);
                    smileyRating.setRating(ratingPercentage, true);
                    if (ratingPercentage.equals(1)) {
                        smileyRating.setRating(SmileyRating.Type.TERRIBLE, true);
                    } else if (ratingPercentage.equals(2)) {
                        smileyRating.setRating(SmileyRating.Type.BAD, true);
                    } else if (ratingPercentage.equals(3)) {
                        smileyRating.setRating(SmileyRating.Type.OKAY, true);
                    } else if (ratingPercentage.equals(4)) {
                        smileyRating.setRating(SmileyRating.Type.GOOD, true);
                    } else if (ratingPercentage.equals(5)) {
                        smileyRating.setRating(SmileyRating.Type.GREAT, true);
                    }
                });

                ((ImageView) dialogView.findViewById(R.id.iconWarning)).setImageTintList(ContextCompat.getColorStateList(activity, fontColor));
                ((TextView) dialogView.findViewById(R.id.noteTitle)).setTextColor(ContextCompat.getColor(activity, fontColor));
                ((Button) dialogView.findViewById(R.id.yesbtn)).setTextColor(ContextCompat.getColor(activity, fontColor));
                dialogView.findViewById(R.id.yesbtn).setBackgroundTintList(ContextCompat.getColorStateList(activity, bgColor));
                dialogView.findViewById(R.id.titleColor).setBackgroundTintList(ContextCompat.getColorStateList(activity, bgColor));

                dialogView.findViewById(R.id.yesbtn).setOnClickListener(v -> {
                    if (ratingPercentage.equals(0)) {
                        (dialogView.findViewById(R.id.errorRating)).setVisibility(View.VISIBLE);
                    } else {
                        (dialogView.findViewById(R.id.errorRating)).setVisibility(View.GONE);
                        confirmationAlert.dismiss();
                        FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).document(myData.getWorkerDocumentID())
                                .update("ratingTotal", mechanicDetailsScreenDTO.getRatingTotal() + ratingPercentage
                                        , "ratingUserCount", mechanicDetailsScreenDTO.getRatingUserCount() + 1);
                        UtilityFunctions.alertNoteWithOkButton(context, "Work Completed", "Worker received the payment.", Gravity.CENTER, R.color.whatsapp_green_dark, R.color.white, false, true, new Intent(context, ScreenConsumerHome.class));
                    }
                });
                confirmationAlert.setView(dialogView);
                confirmationAlert.setCancelable(false);
                confirmationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmationAlert.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0);
    }

    public void setColorOfStatusPoint(View view, int color) {
        view.setBackgroundTintList(ContextCompat.getColorStateList(context, color));
    }

    public void moveToMechanicLocation(View view) {
        LatLng latLng = new LatLng(mechanicDetailsScreenDTO.getWorkerLat(), mechanicDetailsScreenDTO.getWorkerLng());
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                latLng, Constants.cameraZoomInMap);
        mMap.animateCamera(location);
    }

    String workDone = "Is Your Work Done?";

    String amount;

    String localWorkerStatus;

    public void workDone(View view) {
        if (((Button) findViewById(R.id.workDone)).getText().toString().equals(Constants.workerSendAmount)) {
            setMechanicStatus("Worker charge " + amount + " rupees for his work.\nIs this amount is okay for you?", R.color.whatsapp_green_dark);
        } else if (((Button) findViewById(R.id.workDone)).getText().toString().toLowerCase().contains("wait")) {
        } else {
            setMechanicStatus(((Button) findViewById(R.id.workDone)).getText().toString(), R.color.whatsapp_green_dark);
        }
    }

    public void statusUpdate(String status) {
        try {
            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.requestCollection).document(myData.getWorkerDocumentID()).collection(FirebaseConstants.requestCollection).document(Constants.USER_DOCUMENT_ID)
                    .update("workerStatus", status);
        } catch (Exception ex) {
        }
    }

    public void setMechanicStatus(String text, int color) {
        try {
            AlertDialog confirmationAlert = new AlertDialog.Builder(context).create();
            final View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_return, null);
            ((TextView) dialogView.findViewById(R.id.noteTitle)).setText("Update Progress Status");
            dialogView.findViewById(R.id.llTITLECOLOR).setBackgroundTintList(ContextCompat.getColorStateList(context, color));
            dialogView.findViewById(R.id.yesbtn).setBackgroundTintList(ContextCompat.getColorStateList(context, color));
            ((TextView) dialogView.findViewById(R.id.noteText)).setText(text);

            dialogView.findViewById(R.id.yesbtn).setOnClickListener(v -> {
                confirmationAlert.dismiss();
                if (text.equals(workDone)) {
                    statusUpdate(Constants.workerPayment);
                }

                if (localWorkerStatus.equals(Constants.workerSendAmount)) {
                    statusUpdate(Constants.workerAmountFine);
                }
            });

            dialogView.findViewById(R.id.nobtn).setOnClickListener(v -> {
                confirmationAlert.dismiss();

                if (localWorkerStatus.equals(Constants.workerSendAmount)) {
                    statusUpdate(Constants.workerAmountNotFine);
                }
            });

            confirmationAlert.setView(dialogView);
            confirmationAlert.setCancelable(false);
            confirmationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmationAlert.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //LOCATION
    public void locationRealTime() {
        try {
            final DocumentReference docRef = FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).document(myData.getWorkerDocumentID());
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                try {
                    if (snapshot != null && snapshot.exists()) {
                        SignupMechanicDTO localDataLocation = snapshot.toObject(SignupMechanicDTO.class);
                        BitmapDescriptor icon = UtilityFunctions.getBitmapFromVector(context, R.drawable.icon_mechanic,
                                ContextCompat.getColor(context, R.color.SmartDesk_Editext_red));

                        try {
                            mMap.clear();
                            LatLng latLngMechanic = new LatLng(localDataLocation.getWorkerLat(), localDataLocation.getWorkerLng());
                            mMap.addMarker(new MarkerOptions().icon(icon).position(latLngMechanic).title(localDataLocation.getWorkerName()));
                            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLngMechanic, Constants.cameraZoomInMap);
                            mMap.animateCamera(location);
                        } catch (Exception ex) {

                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (
                Exception ex) {
            ex.printStackTrace();
        }
        //======================================== Show Loading bar ==============================================
    }
}