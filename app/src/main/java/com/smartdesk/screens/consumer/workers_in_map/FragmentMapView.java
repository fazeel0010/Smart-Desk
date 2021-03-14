package com.smartdesk.screens.consumer.workers_in_map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.smartdesk.R;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.utility.library.CustomEditext;
import com.smartdesk.constants.Constants;
import com.smartdesk.model.fcm.Data;
import com.smartdesk.model.notification.NotificationDTO;
import com.smartdesk.model.signup.SignupMechanicDTO;
import com.smartdesk.screens.consumer.chat.ScreenChatDetail;
import com.smartdesk.screens.consumer.workers_in_map.shopInfo.ScreenShopInfo;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.memory.MemoryCache;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.Query;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.constants.Constants.DISTANCE;
import static com.smartdesk.constants.Constants.USER_DOCUMENT_ID;
import static com.smartdesk.constants.Constants.USER_MOBILE;
import static com.smartdesk.constants.Constants.USER_NAME;
import static com.smartdesk.constants.Constants.USER_PROFILE;
import static com.smartdesk.constants.Constants.activeStatus;
import static com.smartdesk.constants.Constants.cameraZoomInMap;
import static com.smartdesk.constants.Constants.const_WorkerRequestDTO;
import static com.smartdesk.constants.Constants.const_lat;
import static com.smartdesk.constants.Constants.const_lng;
import static com.smartdesk.constants.Constants.consumerRole;
import static com.smartdesk.constants.Constants.workerRole;
import static com.smartdesk.constants.Constants.shopCategoryItems;
import static com.smartdesk.constants.Constants.workerRequest;
import static com.smartdesk.screens.consumer.workers_in_map.FragmentListView.static_fragmentListView;
import static com.smartdesk.utility.UtilityFunctions.alertNoteWithOkButton;
import static com.smartdesk.utility.UtilityFunctions.calculateRating;
import static com.smartdesk.utility.UtilityFunctions.distanceBetweenTwoLocations;
import static com.smartdesk.utility.UtilityFunctions.getBitmapFromVector;
import static com.smartdesk.utility.UtilityFunctions.getDistanceInFormat;
import static com.smartdesk.utility.UtilityFunctions.getPhoneNumberInFormat;
import static com.smartdesk.utility.UtilityFunctions.getStringFromEditTextWithLengthLimit;
import static com.smartdesk.utility.UtilityFunctions.hideSoftKeyboard;
import static com.smartdesk.utility.UtilityFunctions.openCallingScreen;
import static com.smartdesk.utility.UtilityFunctions.picassoGetCircleImage;
import static com.smartdesk.utility.UtilityFunctions.redSnackBar;
import static com.smartdesk.utility.UtilityFunctions.saveNotficationCollection;
import static com.smartdesk.utility.UtilityFunctions.sendFCMMessage;
import static com.smartdesk.utility.UtilityFunctions.sendIntentNormal;
import static com.smartdesk.utility.UtilityFunctions.setupUI;

public class FragmentMapView extends Fragment {

    private View view;
    private Activity context;
    private GoogleMap mMap;

    public static String customService = "";

    List<SignupMechanicDTO> approvedMechanicDTOList = new ArrayList<>();

    public FragmentMapView() {
    }

    public FragmentMapView(Activity context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map_view, container, false);

        if (mMap == null) {
            new Handler().postDelayed(() -> {
                System.gc();
                new MemoryCache().clear();
                SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(googleMap -> {
                    mMap = googleMap;

                    MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.google_style);
                    mMap.setMapStyle(mapStyleOptions);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getUiSettings().setZoomControlsEnabled(true);

                    shopSelectionDialog();
                });
            }, 0);
        }

        return view;
    }

    Boolean isOneTime = false;

    public void setCurrentLocation() {
        ((ScreenWorkersMap) context).stopOnUiThread();
        if (!isOneTime) {
            isOneTime = true;
            LatLng latLngUser = new LatLng(const_lat, const_lng);
            Marker m = mMap.addMarker(new MarkerOptions().position(latLngUser).title(USER_NAME + " Your Current Location"));
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLngUser, cameraZoomInMap);
            mMap.animateCamera(location);
            SignupMechanicDTO item = new SignupMechanicDTO();
            item.setWorkerName(USER_NAME);
            item.setWorkerPhone(USER_MOBILE);
            item.setProfilePicture(USER_PROFILE);
            item.setRole(consumerRole);
            item.setWorkerLat(const_lat);
            item.setWorkerLng(const_lng);
            m.setTag(item);
            showDataOnList(const_lat, const_lng);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void showDataOnList(Double const_lat, Double const_lng) {
        new Handler().postDelayed(() -> {
            System.gc();
            new MemoryCache().clear();
            ((ScreenWorkersMap) context).startAnim();
            Query query = FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection)
                    .whereEqualTo("role", workerRole)
                    .whereEqualTo("userStatus", activeStatus)
                    .whereEqualTo("hired", false)
                    .whereEqualTo("online", true);
            if (Constants.WORKPLACE_TYPE.equalsIgnoreCase("Both")) {

            } else {
                query.whereEqualTo("shopCategory", Constants.WORKPLACE_TYPE);
            }
            if (customService == null || customService.equals("")) {

            } else {
                query.whereEqualTo("workingCategory", customService);
            }

            query.get().
                    addOnSuccessListener(task -> {
                        ((ScreenWorkersMap) context).stopAnim();
                        List<SignupMechanicDTO> list = task.toObjects(SignupMechanicDTO.class);
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setLocalDocuementID(task.getDocumentChanges().get(i).getDocument().getId());
                            System.out.println(list.get(i).getWorkerName());
                            System.out.println(list.get(i).getLocalDocuementID());
                        }
                        static_fragmentListView.showDataOnList(list);
                        for (SignupMechanicDTO item : list) {
                            System.out.println(item.getWorkerName() + " ITEN");
                            item.setHavingShop(false);
                            double nearyby = distanceBetweenTwoLocations(const_lat, const_lng, item.getWorkerLat(), item.getWorkerLng(), "K");
                            if (nearyby <= DISTANCE) {
                                BitmapDescriptor icon = getBitmapFromVector(context, R.drawable.icon_mechanic,
                                        ContextCompat.getColor(context, R.color.SmartDesk_Editext_red));
                                LatLng latLngMechanic = new LatLng(item.getWorkerLat(), item.getWorkerLng());
                                Marker m = mMap.addMarker(new MarkerOptions().icon(icon).position(latLngMechanic).title(item.getWorkerName()));
                                item.setDistance(nearyby);
                                m.setTag(item);
                            }
                            if (nearyby <= DISTANCE) {
                                BitmapDescriptor icon = getBitmapFromVector(context, R.drawable.icon_garage,
                                        ContextCompat.getColor(context, R.color.SmartDesk_Editext_red));
//                                Marker m = mMap.addMarker(new MarkerOptions().icon(icon).position(latLngMechanic).title(item.getShopName()));
                                SignupMechanicDTO a = new SignupMechanicDTO(item);
                                a.setDistance(nearyby);
//                                m.setTag(a);
                            }
                        }

                        mMap.setOnMarkerClickListener(marker -> {
                            SignupMechanicDTO itemMarker = (SignupMechanicDTO) marker.getTag();
                            if (itemMarker != null) {
                                if (itemMarker.getRole().equals(workerRole)) {
                                    if (itemMarker.getHavingShop()) {
                                        openDetialsInBottomView(itemMarker, true);
                                    } else {
                                        openDetialsInBottomView(itemMarker);
                                    }
                                } else {
                                    openDetialsInBottomViewYourInfo(itemMarker);
                                }
                            }
                            return true;
                        });
                    }).addOnFailureListener(e -> {
                ((ScreenWorkersMap) context).stopAnim();
                System.out.println("ONSUCCESSFFFFFFFFFFFFFFFFF");
                e.printStackTrace();
                redSnackBar(context, "No Internet!", Snackbar.LENGTH_SHORT);
            });
        }, 0);
    }

    TextView name, phoneNumber, address, distanceShow;
    TextView shopName, shopNUmber, shopCategory, shopWorkingCategory;
    CircleImageView profilePic;
    ShimmerFrameLayout profile_shimmer;

    private void openDetialsInBottomView(SignupMechanicDTO item) {
        new Handler().postDelayed(() -> {
            try {
                System.gc();
                new MemoryCache().clear();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.bottomDialogTheme);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_bottom_mechanic_info, view.findViewById(R.id.bottom_view));


                ((RatingBar) dialogView.findViewById(R.id.rating)).setClickable(false);
                ((RatingBar) dialogView.findViewById(R.id.rating)).setRating(calculateRating(item.getRatingUserCount(), item.getRatingTotal()));
                ((TextView) dialogView.findViewById(R.id.userReviews)).setText(item.getRatingUserCount() + " user reviews");

                name = dialogView.findViewById(R.id.name);
                distanceShow = dialogView.findViewById(R.id.distanceShow);
                distanceShow.setText(getDistanceInFormat(item.getDistance()));
                phoneNumber = dialogView.findViewById(R.id.phoneNumber);
                profile_shimmer = dialogView.findViewById(R.id.profile_shimmer);
                profilePic = dialogView.findViewById(R.id.profilePic);

                picassoGetCircleImage(context, item.getProfilePicture(), profilePic, profile_shimmer, R.drawable.side_profile_icon);


                name.setText(item.getWorkerName());
                phoneNumber.setText(getPhoneNumberInFormat(item.getWorkerPhone()));


                dialogView.findViewById(R.id.moreInfo).setOnClickListener(v -> {
                    ScreenChatDetail.mechanicDetailsScreenDTO = item;
                    sendIntentNormal(context, new Intent(context, ScreenChatDetail.class), false, 0);
                });

                dialogView.findViewById(R.id.getWorkerNow).setOnClickListener(v1 -> {
                    const_WorkerRequestDTO.setWorkerDocumentID(item.getLocalDocuementID());
                    const_WorkerRequestDTO.setWorkerStatus(workerRequest);
                    const_WorkerRequestDTO.setRegistrationDate(new Timestamp(new Date().getTime()));
                    bottomSheetDialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            ((ScreenWorkersMap) context).startAnim();
                            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.requestCollection).document(item.getLocalDocuementID())
                                    .collection(FirebaseConstants.requestCollection).document(USER_DOCUMENT_ID).set(const_WorkerRequestDTO)
                                    .addOnSuccessListener(documentReference -> {
                                        ((ScreenWorkersMap) context).stopAnim();

                                        alertNoteWithOkButton(context, "Worker Placed", "Successfully sent request a worker. Wait for the worker to accept your request.\nIf worker accept your request you won't be able to sent request to other workers"
                                                , Gravity.CENTER, R.color.whatsapp_green_dark, R.color.white, false, false, null);

                                        sendFCMMessage(context, new Data(item.getLocalDocuementID(), new Timestamp(new Date().getTime()).getTime(), "Request", "Request received", "You received a request for worker by " + USER_NAME));
                                        saveNotficationCollection(new NotificationDTO(workerRole, item.getLocalDocuementID(), new Timestamp(new Date().getTime()), "Request received", "You received a request for worker by " + USER_NAME, false));

                                    })
                                    .addOnFailureListener(e -> {
                                        ((ScreenWorkersMap) context).stopAnim();
                                        alertNoteWithOkButton(context, "Worker Placed", "Unable to place a worker"
                                                , Gravity.CENTER, R.color.SmartDesk_Orange, R.color.black_color, false, false, null);
                                    });
                        }
                    }, 0);
                });
                dialogView.findViewById(R.id.call).setOnClickListener(v1 -> {
                    openCallingScreen(context, item.getWorkerPhone());
                });

                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
            } catch (Exception ex) {

            }
        }, 0);

    }

    private void openDetialsInBottomViewYourInfo(SignupMechanicDTO item) {
        new Handler().postDelayed(() -> {
            try {
                System.gc();
                new MemoryCache().clear();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.bottomDialogTheme);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_bottom_your_current_location, view.findViewById(R.id.bottom_view));

                name = dialogView.findViewById(R.id.name);
                phoneNumber = dialogView.findViewById(R.id.phoneNumber);
                address = dialogView.findViewById(R.id.address);
                profile_shimmer = dialogView.findViewById(R.id.profile_shimmer);
                profilePic = dialogView.findViewById(R.id.profilePic);

                picassoGetCircleImage(context, item.getProfilePicture(), profilePic, profile_shimmer, R.drawable.side_profile_icon);

                name.setText(item.getWorkerName());
                phoneNumber.setText(getPhoneNumberInFormat(item.getWorkerPhone()));
                address.setText(UtilityFunctions.getAddressLatLng(context, item.getWorkerLat(), item.getWorkerLng()));

                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
            } catch (Exception ex) {

            }
        }, 0);

    }

    private void openDetialsInBottomView(SignupMechanicDTO item, Boolean isShop) {
        new Handler().postDelayed(() -> {
            try {
                System.gc();
                new MemoryCache().clear();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.bottomDialogTheme);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_bottom_shop_info, view.findViewById(R.id.bottom_view));

                shopName = dialogView.findViewById(R.id.shopName);
                shopNUmber = dialogView.findViewById(R.id.shopNumber);
                shopCategory = dialogView.findViewById(R.id.shopCategory);
                shopWorkingCategory = dialogView.findViewById(R.id.workingCategory);
                address = dialogView.findViewById(R.id.address);

                distanceShow = dialogView.findViewById(R.id.distanceShow);
                distanceShow.setText(getDistanceInFormat(item.getDistance()));

                address.setText(item.getShopLocation());

                dialogView.findViewById(R.id.moreInfo).setOnClickListener(v -> {
                    ScreenShopInfo.shopInfoDTO = item;
                    sendIntentNormal(context, new Intent(context, ScreenShopInfo.class), false, 0);
                });
                dialogView.findViewById(R.id.call).setOnClickListener(v1 -> {
//                    openCallingScreen(context, item.getShopPhone());
                });

                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
            } catch (Exception ex) {

            }
        }, 0);
    }

    Boolean isOkay;

    public void shopSelectionDialog() {
        new Handler().postDelayed(() -> {
            try {
                System.gc();
                new MemoryCache().clear();
                final AlertDialog confirmationAlert = new AlertDialog.Builder(context).create();
                final View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_shop_selection, null);
                CustomEditext company = dialogView.findViewById(R.id.vehicleCompany);
                CustomEditext model = dialogView.findViewById(R.id.model);
                RadioGroup radioGroup = dialogView.findViewById(R.id.radiogroup);
                Constants.WORKPLACE_TYPE = shopCategoryItems[0];
                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId == R.id.localShop) {
                        Constants.WORKPLACE_TYPE = shopCategoryItems[0];
                    } else if (checkedId == R.id.registeredShop) {
                        Constants.WORKPLACE_TYPE = shopCategoryItems[1];
                    } else if (checkedId == R.id.both) {
                        Constants.WORKPLACE_TYPE = "Both";
                    }
                });
                dialogView.findViewById(R.id.yesbtn).setOnClickListener(v -> {
                    isOkay = true;
                    hideSoftKeyboard(context);
                    (dialogView.findViewById(R.id.companyError)).setVisibility(View.GONE);
                    (dialogView.findViewById(R.id.modelError)).setVisibility(View.GONE);
                    String dataCompany = getDataFromEditext(company, "", 2, dialogView.findViewById(R.id.companyError));
                    String dataModel = getDataFromEditext(model, "", 2, dialogView.findViewById(R.id.modelError));

                    if (isOkay) {
                        setupUI(view.findViewById(R.id.parent), context);
                        confirmationAlert.dismiss();
                        const_WorkerRequestDTO.setCompany(dataCompany);
                        const_WorkerRequestDTO.setModel(dataModel);
                        distanceDialog();
                    }
                });
                dialogView.findViewById(R.id.nobtn).setOnClickListener(v1 -> {
                    confirmationAlert.dismiss();
                    context.finish();
                });
                confirmationAlert.setView(dialogView);
                confirmationAlert.setCancelable(false);
                confirmationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmationAlert.show();
            } catch (Exception ex) {

            }
        }, 0);
    }

    Boolean isCustom = false;

    public void distanceDialog() {
        new Handler().postDelayed(() -> {
            try {
                System.gc();
                new MemoryCache().clear();
                final AlertDialog confirmationAlert = new AlertDialog.Builder(context).create();
                final View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_distance, null);
                CustomEditext distance = dialogView.findViewById(R.id.distance);
                RadioGroup radioGroup = dialogView.findViewById(R.id.radiogroup);
                DISTANCE = 1;
                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    isCustom = false;
                    if (checkedId == R.id.custom) {
                        isCustom = true;
                        (dialogView.findViewById(R.id.distance)).setVisibility(View.VISIBLE);
                    } else if (checkedId == R.id.under1) {
                        DISTANCE = 1;
                        (dialogView.findViewById(R.id.distance)).setVisibility(View.GONE);
                    } else if (checkedId == R.id.under2) {
                        DISTANCE = 2;
                        (dialogView.findViewById(R.id.distance)).setVisibility(View.GONE);
                    } else if (checkedId == R.id.anywhere) {
                        DISTANCE = 200;
                        (dialogView.findViewById(R.id.distance)).setVisibility(View.GONE);
                    }
                });
                dialogView.findViewById(R.id.yesbtn).setOnClickListener(v -> {
                    isOkay = true;

                    String dataDistance = "1";
                    if (isCustom) {
                        (dialogView.findViewById(R.id.distanceError)).setVisibility(View.GONE);
                        dataDistance = getDataFromEditext(distance, "", 1, dialogView.findViewById(R.id.distanceError));
                        if (dataDistance.startsWith("0")) {
                            (dialogView.findViewById(R.id.distanceError)).setVisibility(View.VISIBLE);
                            isOkay = false;
                        }
                    }
                    if (isOkay) {
                        confirmationAlert.dismiss();
                        setupUI(view.findViewById(R.id.parent), context);
                        if (isCustom)
                            DISTANCE = Integer.parseInt(dataDistance);

                        ((ScreenWorkersMap) context).getLocation(this);
                    }
                });
                dialogView.findViewById(R.id.nobtn).setOnClickListener(v1 -> {
                    confirmationAlert.dismiss();
                    context.finish();
                });
                confirmationAlert.setView(dialogView);
                confirmationAlert.setCancelable(false);
                confirmationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmationAlert.show();
            } catch (Exception ex) {

            }
        }, 0);
    }

    private String getDataFromEditext(EditText editText, String errorMSG, int minimumLength, TextView error) {
        String text = "";
        try {
            text = getStringFromEditTextWithLengthLimit(editText, minimumLength);
        } catch (NullPointerException ex) {
            error.setVisibility(View.VISIBLE);
            isOkay = false;
        }
        return text;
    }
}
