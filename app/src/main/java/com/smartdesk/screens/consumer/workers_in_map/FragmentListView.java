package com.smartdesk.screens.consumer.workers_in_map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.model.fcm.Data;
import com.smartdesk.model.notification.NotificationDTO;
import com.smartdesk.model.signup.SignupMechanicDTO;
import com.smartdesk.screens.consumer.chat.ScreenChatDetail;
import com.smartdesk.screens.consumer.workers_in_map.shopInfo.ScreenShopInfo;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.memory.MemoryCache;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.utility.UtilityFunctions.alertNoteWithOkButton;
import static com.smartdesk.utility.UtilityFunctions.calculateRating;
import static com.smartdesk.utility.UtilityFunctions.distanceBetweenTwoLocations;
import static com.smartdesk.utility.UtilityFunctions.getDistanceInFormat;
import static com.smartdesk.utility.UtilityFunctions.getPhoneNumberInFormat;
import static com.smartdesk.utility.UtilityFunctions.openCallingScreen;
import static com.smartdesk.utility.UtilityFunctions.picassoGetCircleImage;
import static com.smartdesk.utility.UtilityFunctions.saveNotficationCollection;
import static com.smartdesk.utility.UtilityFunctions.sendFCMMessage;
import static com.smartdesk.utility.UtilityFunctions.sendIntentNormal;

public class FragmentListView extends Fragment {

    private View view;
    private Activity context;

    //RecyclerView Variables
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    Adapter adapter;
    List<SignupMechanicDTO> listViewItemsRv = new ArrayList<>();

    public static FragmentListView static_fragmentListView;

    public FragmentListView() {
    }

    public FragmentListView(Activity context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_view, container, false);
        static_fragmentListView = this;
        setRecyclerView();
        return view;
    }

    public void setRecyclerView() {
        new Handler().postDelayed(() -> {
            System.gc();
            new MemoryCache().clear();
            listViewItemsRv.clear();
            adapter = new Adapter(listViewItemsRv);
            recyclerView = UtilityFunctions.setRecyclerView(view.findViewById(R.id.recycler_view), context);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }, 0);
    }

    public void showDataOnList(List<SignupMechanicDTO> signupMechanicDTOS) {
        new Handler().postDelayed(() -> {
            System.gc();
            new MemoryCache().clear();
            ArrayList<SignupMechanicDTO> listItems = new ArrayList<>();
            for (SignupMechanicDTO item : signupMechanicDTOS) {
                item.setHavingShop(false);
                double nearyby = distanceBetweenTwoLocations(Constants.const_lat, Constants.const_lng, item.getWorkerLat(), item.getWorkerLng(), "K");
                if (nearyby <= Constants.DISTANCE) {
                    item.setDistance(nearyby);
                    System.out.println(item.getHavingShop() + "afalseWala##############################");
                    listItems.add(item);
                }
            }
            for (SignupMechanicDTO item : signupMechanicDTOS) {
                item.setHavingShop(true);
            }
            for (int i = 0; i < listItems.size() / 2; i++) {
                listItems.get(i).setHavingShop(false);
            }
            if (listItems.size() > 0) {
                listViewItemsRv.clear();
                listViewItemsRv.addAll(listItems);
                adapter.notifyDataSetChanged();
                view.findViewById(R.id.listEmptyText).setVisibility(View.GONE);
            } else {
                listViewItemsRv.clear();
                adapter.notifyDataSetChanged();
                view.findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
        }, 0);
    }

    public class Adapter extends RecyclerView.Adapter<FragmentListView.Adapter.ViewHolder> {

        List<SignupMechanicDTO> mechanicsList;
        Boolean mechanicText = false;
        Boolean shopText = false;

        public Adapter(List<SignupMechanicDTO> mechanicsList) {
            this.mechanicsList = mechanicsList;
        }

        @NonNull
        @Override
        public FragmentListView.Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.rv_item_list_view, parent, false);
            return new FragmentListView.Adapter.ViewHolder(view);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }

        @Override
        public void onBindViewHolder(@NonNull FragmentListView.Adapter.ViewHolder holder, final int position) {
            final Context innerContext = holder.itemView.getContext();
            try {
                if (!mechanicsList.get(position).getHavingShop()) {
                    if (mechanicText) {
                        ((ViewGroup) holder.headingTitle.getParent()).removeView(holder.headingTitle);
                    } else {
                        mechanicText = true;
                    }
                    holder.name.setText(mechanicsList.get(position).getWorkerName());
                    picassoGetCircleImage(context, mechanicsList.get(position).getProfilePicture(), holder.profilePic, holder.profile_shimmer, R.drawable.side_profile_icon);
                    holder.ratingBar.setRating(calculateRating(mechanicsList.get(position).getRatingUserCount(), mechanicsList.get(position).getRatingTotal()));
                    holder.phoneNumber.setText(getPhoneNumberInFormat(mechanicsList.get(position).getWorkerPhone()));
                } else {
                    picassoGetCircleImage(context, null, holder.profilePic, holder.profile_shimmer, R.drawable.icon_garage);
                    ((ViewGroup) holder.ratingBar.getParent()).removeView(holder.ratingBar);
                    if (shopText) {
                        ((ViewGroup) holder.headingTitle.getParent()).removeView(holder.headingTitle);
                    } else {
                        shopText = true;
                        holder.headingTitle.setText("Shop List");
                    }
                }

                holder.itemCardview.setOnClickListener(v -> {
                    try {
                        if (mechanicsList.get(position).getHavingShop()) {
                            openDetialsInBottomView(mechanicsList.get(position), true);
                        } else {
                            openDetialsInBottomView(mechanicsList.get(position));
                        }
                    } catch (Exception ex) {
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public int getItemCount() {
            return mechanicsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, headingTitle, phoneNumber;
            RatingBar ratingBar;
            CircleImageView profilePic;
            ShimmerFrameLayout profile_shimmer;
            LinearLayout itemCardview;

            public ViewHolder(@NonNull View view) {
                super(view);
                itemCardview = view.findViewById(R.id.cardView);
                headingTitle = view.findViewById(R.id.headingTitle);
                ratingBar = view.findViewById(R.id.rating);
                name = view.findViewById(R.id.name);
                phoneNumber = view.findViewById(R.id.phoneNumber);
                profile_shimmer = view.findViewById(R.id.profile_shimmer);
                profilePic = view.findViewById(R.id.profilePic);
            }
        }
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
                    Constants.const_WorkerRequestDTO.setWorkerDocumentID(item.getLocalDocuementID());
                    Constants.const_WorkerRequestDTO.setWorkerStatus(Constants.workerRequest);
                    Constants.const_WorkerRequestDTO.setRegistrationDate(new Timestamp(new Date().getTime()));
                    bottomSheetDialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                    ((ScreenWorkersMap) context).startAnim();
                            FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.requestCollection).document(item.getLocalDocuementID())
                                    .collection(FirebaseConstants.requestCollection).document(Constants.USER_DOCUMENT_ID).set(Constants.const_WorkerRequestDTO)
                                    .addOnSuccessListener(documentReference -> {
                                        ((ScreenWorkersMap) context).stopAnim();
                                        alertNoteWithOkButton(context, "Worker Placed", "Successfully sent request a worker. Wait for the worker to accept your request.\nIf worker accept your request you won't be able to sent request to other workers"
                                                , Gravity.CENTER, R.color.whatsapp_green_dark, R.color.white, false, false, null);

                                        sendFCMMessage(context, new Data(item.getLocalDocuementID(), new Timestamp(new Date().getTime()).getTime(), "Request", "Request received", "You received a request for worker by " + Constants.USER_NAME));
                                        saveNotficationCollection(new NotificationDTO(Constants.workerRole, item.getLocalDocuementID(), new Timestamp(new Date().getTime()), "Request received", "You received a request for worker by " + Constants.USER_NAME, false));
                                    })
                                    .addOnFailureListener(e -> {
                                        ((ScreenWorkersMap) context).stopAnim();
                                        alertNoteWithOkButton(context, "Worker Placed", "Unable to place a worker"
                                                , Gravity.CENTER, R.color.SmartDesk_Orange, R.color.black_color, false, false, null);
                                    });
                        }
                    },0);
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
                ex.printStackTrace();
            }
        }, 0);
    }
}
