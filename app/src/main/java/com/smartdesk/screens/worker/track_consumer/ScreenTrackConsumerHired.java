package com.smartdesk.screens.worker.track_consumer;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.smartdesk.R;
import com.smartdesk.screens.consumer.chat.ScreenChat;
import com.smartdesk.utility.library.CustomEditext;
import com.smartdesk.constants.Constants;
import com.smartdesk.model.hire_worker.WorkerRequestDTO;
import com.smartdesk.model.payment_history.PaymentHistoryDTO;
import com.smartdesk.model.signup.SignupMechanicDTO;
import com.smartdesk.screens.worker._home.ScreenWorkerHome;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;

import java.sql.Timestamp;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.constants.Constants.USER_DOCUMENT_ID;
import static com.smartdesk.constants.Constants.cameraZoomInMap;
import static com.smartdesk.constants.Constants.workerAmountFine;
import static com.smartdesk.constants.Constants.workerConfirm;
import static com.smartdesk.constants.Constants.workerDone;
import static com.smartdesk.constants.Constants.workerFinish;
import static com.smartdesk.constants.Constants.workerInProgress;
import static com.smartdesk.constants.Constants.workerPayment;
import static com.smartdesk.constants.Constants.workerReachedLocation;
import static com.smartdesk.constants.Constants.workerSendAmount;
import static com.smartdesk.constants.FirebaseConstants.firebaseFirestore;
import static com.smartdesk.constants.FirebaseConstants.paymentCollection;
import static com.smartdesk.constants.FirebaseConstants.requestCollection;
import static com.smartdesk.constants.FirebaseConstants.usersCollection;
import static com.smartdesk.utility.UtilityFunctions.alertNoteWithOkButton;
import static com.smartdesk.utility.UtilityFunctions.getAddressLatLng;
import static com.smartdesk.utility.UtilityFunctions.getBitmapFromVector;
import static com.smartdesk.utility.UtilityFunctions.getPhoneNumberInFormat;
import static com.smartdesk.utility.UtilityFunctions.getStringFromEditTextWithLengthLimit;
import static com.smartdesk.utility.UtilityFunctions.loadingAnim;
import static com.smartdesk.utility.UtilityFunctions.noRoundImageCorner;
import static com.smartdesk.utility.UtilityFunctions.openCallingScreen;
import static com.smartdesk.utility.UtilityFunctions.picassoGetCircleImage;
import static com.smartdesk.utility.UtilityFunctions.removeFocusFromEditexts;
import static com.smartdesk.utility.UtilityFunctions.sendIntentNormal;
import static com.smartdesk.utility.UtilityFunctions.setupUI;
import static java.util.ResourceBundle.clearCache;

public class ScreenTrackConsumerHired extends AppCompatActivity {

    Activity context;
    public SignupMechanicDTO mechanicDetailsScreenDTO;

    private GoogleMap mMap;
    TextView name, phoneNumber, location, address, model, company;
    CircleImageView profilePic;
    ShimmerFrameLayout profile_shimmer;

    Button statusButton;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_track_consumer);
        context = this;
        actionBar("Consumer Details");
        initLoadingBarItems();
        new FusedLocation(context, true, null).startLocationUpdates(false);
        getData();
    }

    private void getData() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startAnim();

            firebaseFirestore.collection(requestCollection).document(USER_DOCUMENT_ID)
                    .collection(requestCollection)
                    .whereEqualTo("hired", true)
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {

                try {
                    WorkerRequestDTO workerRequestDTO = queryDocumentSnapshots.toObjects(WorkerRequestDTO.class).get(0);
                    consumeerDocumentID = workerRequestDTO.getConsumerDocumentID();
                    firebaseFirestore.collection(usersCollection).document(workerRequestDTO.getConsumerDocumentID())
                            .get().addOnSuccessListener(documentSnapshot -> {
                        stopAnim();
                        mechanicDetailsScreenDTO = documentSnapshot.toObject(SignupMechanicDTO.class);
                        setData(workerRequestDTO);
                    }).addOnFailureListener(e -> {
                        stopAnim();
                    });
                } catch (Exception ex) {
                    stopAnim();
                    ex.printStackTrace();
                }
            }).addOnFailureListener(e -> {
                stopAnim();
            });
        }, 0);
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

                    BitmapDescriptor icon = getBitmapFromVector(context, R.drawable.icon_consumer_man,
                            ContextCompat.getColor(context, R.color.SmartDesk_Editext_red));
                    LatLng latLngMechanic = new LatLng(mechanicDetailsScreenDTO.getWorkerLat(), mechanicDetailsScreenDTO.getWorkerLng());
                    mMap.addMarker(new MarkerOptions().icon(icon).position(latLngMechanic).title(mechanicDetailsScreenDTO.getWorkerName()));
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLngMechanic, cameraZoomInMap);
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


        picassoGetCircleImage(context, mechanicDetailsScreenDTO.getProfilePicture(), profilePic, profile_shimmer, R.drawable.side_profile_icon);


        name.setText(mechanicDetailsScreenDTO.getWorkerName());
        company.setText(workerRequestDTO.getCompany());
        model.setText(workerRequestDTO.getModel());
        phoneNumber.setText(getPhoneNumberInFormat(mechanicDetailsScreenDTO.getWorkerPhone()));
        location.setText(getAddressLatLng(context, mechanicDetailsScreenDTO.getWorkerLat(), mechanicDetailsScreenDTO.getWorkerLng()));
        realTimeListner();
        locationRealTime();
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
        anim = loadingAnim(this, progressBar);
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

    public void moveToMechanicLocation(View view) {
        try {
            LatLng latLng = new LatLng(mechanicDetailsScreenDTO.getWorkerLat(), mechanicDetailsScreenDTO.getWorkerLng());
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    latLng, cameraZoomInMap);
            mMap.animateCamera(location);
        }catch (Exception ex){

        }
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
            noRoundImageCorner(this, img, alertDialogImageURL, shimmer);
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

    public void call(View view) {
        try {
            openCallingScreen(context, mechanicDetailsScreenDTO.getWorkerPhone());
        }catch (Exception ex){

        }
    }

    String request = "Please Accept User Request";
    String confirm = "Have you reached to User Location?";
    String reached = "Have you completed your task in progress?";
    String inProgress = "Have you done your work successfully?";
    String workDone = "Waiting for User to confirm";
    String payment = "How much you charge for work?";


    String consumeerDocumentID = "";

    public void realTimeListner() {
        try {
            final DocumentReference docRef = firebaseFirestore.collection(requestCollection).document(USER_DOCUMENT_ID)
                    .collection(requestCollection).document(consumeerDocumentID);
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                try {
                    Button yourStatusButton = findViewById(R.id.yourMechanicStatus);
                    if (snapshot != null && snapshot.exists()) {
                        WorkerRequestDTO workerRequestDTO = snapshot.toObject(WorkerRequestDTO.class);

                        localWorkerStatus = workerRequestDTO.getWorkerStatus();
                        if (workerRequestDTO.getWorkerStatus().equals(Constants.workerRequest)) {
                            yourStatusButton.setText(request);
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerConfirm)) {
                            yourStatusButton.setText(confirm);
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerReachedLocation)) {
                            yourStatusButton.setText(reached);
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerInProgress)) {
                            yourStatusButton.setText(inProgress);
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerDone)) {
                            yourStatusButton.setText(workDone);
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerPayment)) {
                            yourStatusButton.setText(payment);
                        } else if (workerRequestDTO.getWorkerStatus().equals(workerSendAmount)) {
                            yourStatusButton.setText("Waiting for the User to Accept amount");
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerAmountNotFine)) {
                            alertNoteWithOkButton(context, "Amount", "User rejected your desired amount", Gravity.CENTER, R.color.red, R.color.white, false, false, null);
                            yourStatusButton.setText(payment);
                        } else if (workerRequestDTO.getWorkerStatus().equals(Constants.workerAmountFine)) {
                            yourStatusButton.setText("Is user payment your desired amount in cash?");
                            setMechanicStatus(((Button) findViewById(R.id.yourMechanicStatus)).getText().toString(), R.color.whatsapp_green_dark);
                        }else if(workerRequestDTO.getWorkerStatus().equals(workerFinish)){
                            yourStatusButton.setText("Work Completed");
                            PaymentHistoryDTO paymentHistoryDTO = new PaymentHistoryDTO();
                            paymentHistoryDTO.setAmount(workerRequestDTO.getAmount());
                            paymentHistoryDTO.setCompany(workerRequestDTO.getCompany());
                            paymentHistoryDTO.setModel(workerRequestDTO.getModel());
                            paymentHistoryDTO.setDateFomat(new Timestamp(new Date().getTime()));
                            paymentHistoryDTO.setDocumentID(USER_DOCUMENT_ID);

                            firebaseFirestore.collection(paymentCollection).add(paymentHistoryDTO);
                            firebaseFirestore.collection(usersCollection).document(USER_DOCUMENT_ID)
                                    .update("hired", false);
                            firebaseFirestore.collection(requestCollection).document(USER_DOCUMENT_ID)
                                    .collection(requestCollection).document(consumeerDocumentID).delete();

                            alertNoteWithOkButton(context, "Work Completed", "WellDone.You have done serving the user", Gravity.CENTER, R.color.whatsapp_green_dark, R.color.white, false, true, new Intent(context, ScreenWorkerHome.class));
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

    public void statusUpdate(String status) {
        try {
            firebaseFirestore.collection(requestCollection).document(USER_DOCUMENT_ID).collection(requestCollection).document(consumeerDocumentID)
                    .update("workerStatus", status);
        } catch (Exception ex) {
        }
    }

    public void yourStatusClick(View view) {
        if (((Button) findViewById(R.id.yourMechanicStatus)).getText().toString().equals(payment)) {
            openBottomView("Amount", R.drawable.icon_amount, InputType.TYPE_CLASS_NUMBER, "Invalid Amount", 1, "workerName", "Enter Amount ⁕");
        } else if (((Button) findViewById(R.id.yourMechanicStatus)).getText().toString().toLowerCase().contains("wait")) {
        } else
            setMechanicStatus(((Button) findViewById(R.id.yourMechanicStatus)).getText().toString(), R.color.whatsapp_green_dark);
    }

    String localWorkerStatus;

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
                try {
                    if (text.equals(request)) {
                        statusUpdate(workerConfirm);
                    } else if (text.equals(confirm)) {
                        statusUpdate(workerReachedLocation);
                    } else if (text.equals(reached)) {
                        statusUpdate(workerInProgress);
                    } else if (text.equals(inProgress)) {
                        statusUpdate(workerDone);
                    } else if (text.equals(workDone)) {
                        statusUpdate(workerPayment);
                    } else if (localWorkerStatus.equals(workerAmountFine)) {
                        statusUpdate(workerFinish);
                    }
                }catch (Exception ex){

                }
            });

            dialogView.findViewById(R.id.nobtn).setOnClickListener(v -> {
                confirmationAlert.dismiss();
            });

            confirmationAlert.setView(dialogView);
            confirmationAlert.setCancelable(false);
            confirmationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmationAlert.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    Boolean isOkay = true;

    public void openBottomView(String title, int icon, int inputType, String errorMsg, int minimumLength, String fieldName, String hint) {
        try {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomDialogTheme);
            View bottomView = LayoutInflater.from(this).inflate(R.layout.alert_bottom_amount_editext, findViewById(R.id.bottom_view));
            setupUI(bottomView.findViewById(R.id.bottom_view), context);
            ((TextView) bottomView.findViewById(R.id.changeTitle)).setText(title);
            ((ImageView) bottomView.findViewById(R.id.iconImage)).setImageDrawable(ContextCompat.getDrawable(context, icon));
            CustomEditext CustomEditext = bottomView.findViewById(R.id.editext);
            CustomEditext.setInputType(inputType);
            CustomEditext.setHint(hint);
            bottomView.findViewById(R.id.save).setOnClickListener(v -> {
                isOkay = true;
                (bottomView.findViewById(R.id.spinnerError)).setVisibility(View.GONE);
                ((TextView) bottomView.findViewById(R.id.spinnerError)).setText(errorMsg);

                removeFocusFromEditexts(bottomView.findViewById(R.id.parent), context);
                String data = getDataFromEditext(CustomEditext, errorMsg, minimumLength, bottomView.findViewById(R.id.spinnerError));

                if (isOkay) {
                    bottomSheetDialog.dismiss();
                    try {
                        firebaseFirestore.collection(requestCollection).document(USER_DOCUMENT_ID).collection(requestCollection).document(consumeerDocumentID)
                                .update("amount", data,
                                        "workerStatus", workerSendAmount);
                    } catch (Exception ex) {
                    }
                }
            });
            bottomSheetDialog.setContentView(bottomView);
            bottomSheetDialog.show();
        } catch (Exception ex) {
        }
    }

    private String getDataFromEditext(EditText editText, String errorMSG, int minimumLength, TextView viewById) {
        String text = "";
        try {
            text = getStringFromEditTextWithLengthLimit(editText, minimumLength);
        } catch (NullPointerException ex) {
            editText.setError(errorMSG);
            viewById.setVisibility(View.VISIBLE);
            ;
            isOkay = false;
        }
        return text;
    }
    //======================================== Show Loading bar ==============================================

    //LOCATION
    public void locationRealTime() {
        try {
            final DocumentReference docRef = firebaseFirestore.collection(usersCollection).document(consumeerDocumentID);
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                try {
                    if (snapshot != null && snapshot.exists()) {
                        SignupMechanicDTO localDataLocation = snapshot.toObject(SignupMechanicDTO.class);
                        BitmapDescriptor icon = getBitmapFromVector(context, R.drawable.icon_consumer_man,
                                ContextCompat.getColor(context, R.color.SmartDesk_Editext_red));

                        try {
                            mMap.clear();
                            LatLng latLngMechanic = new LatLng(localDataLocation.getWorkerLat(), localDataLocation.getWorkerLng());
                            mMap.addMarker(new MarkerOptions().icon(icon).position(latLngMechanic).title(localDataLocation.getWorkerName()));
                            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLngMechanic, cameraZoomInMap);
                            mMap.animateCamera(location);
                        }catch (Exception ex){

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

    public void chat(View view) {
        try {
            ScreenChat.Chat_DOCUMENTID_OTHER = consumeerDocumentID;
            ScreenChat.CHAT_MOBILE = mechanicDetailsScreenDTO.getWorkerPhone();
            ScreenChat.CHAT_PROFILE = mechanicDetailsScreenDTO.getProfilePicture();
            ScreenChat.CHAT_NAME = mechanicDetailsScreenDTO.getWorkerName();
            sendIntentNormal(context, new Intent(context, ScreenChat.class), false, 0);
        }catch (Exception ex){

        }
    }
}