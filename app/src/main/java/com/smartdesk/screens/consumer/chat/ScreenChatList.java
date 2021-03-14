package com.smartdesk.screens.consumer.chat;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.model.chat.ChatListDTO;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.memory.MemoryCache;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.constants.FirebaseConstants.chatListCollection;
import static com.smartdesk.utility.UtilityFunctions.sendIntentNormal;

public class ScreenChatList extends AppCompatActivity {

    Activity context;

    //RecyclerView Variables
    RecyclerView recyclerView;
    ScreenChatList.Adapter adapter;
    List<ChatListDTO> chatListDTOS = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_chat_list);
        context = this;
        actionBar("Chats");
        initLoadingBarItems();
        setRecyclerView();
        showDataOnList();
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

    public void showDataOnList() {
        new Handler().postDelayed(() -> {

            startAnim();
            FirebaseConstants.firebaseFirestore.collection(chatListCollection)
                    .document(Constants.USER_DOCUMENT_ID).collection(chatListCollection)
                    .get().
                    addOnSuccessListener(task -> {
                        chatListDTOS.clear();

                        stopAnim();
                        if (task.isEmpty()) {
                            findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        } else {
                            List<ChatListDTO> chatListDTOList = task.toObjects(ChatListDTO.class);
                            Collections.sort(chatListDTOList);

                            chatListDTOS.addAll(chatListDTOList);
                            if (chatListDTOList.size() > 0) {
                                findViewById(R.id.listEmptyText).setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        adapter.notifyDataSetChanged();
                    }).addOnFailureListener(e -> {

                stopAnim();
                chatListDTOS.clear();
                adapter.notifyDataSetChanged();
            });
        }, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    public void setRecyclerView() {
        new Handler().postDelayed(() -> {
            adapter = new ScreenChatList.Adapter(chatListDTOS);
            recyclerView = UtilityFunctions.setRecyclerView(findViewById(R.id.recycler_view), context);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }, 0);
    }

    public class Adapter extends RecyclerView.Adapter<ScreenChatList.Adapter.ViewHolder> {

        List<ChatListDTO> chatListDTOS;

        public Adapter(List<ChatListDTO> chatListDTOS) {
            this.chatListDTOS = chatListDTOS;
        }

        @NonNull
        @Override
        public ScreenChatList.Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.rv_item_chat_list, parent, false);
            return new ScreenChatList.Adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ScreenChatList.Adapter.ViewHolder holder, final int position) {
            final Context innerContext = holder.itemView.getContext();

            holder.name.setText(chatListDTOS.get(position).getName());
            holder.phoneNumber.setText(chatListDTOS.get(position).getPhoneNumber());
            holder.date.setText(UtilityFunctions.getDateFormat(chatListDTOS.get(position).getDate()));

            UtilityFunctions.picassoGetCircleImage(context, chatListDTOS.get(position).getProfileUrl(), holder.profilePic, holder.profile_shimmer, R.drawable.side_profile_icon);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ScreenChat.Chat_DOCUMENTID_OTHER = chatListDTOS.get(position).getOtherDocumentID();
                        ScreenChat.CHAT_MOBILE = chatListDTOS.get(position).getPhoneNumber();
                        ScreenChat.CHAT_PROFILE = chatListDTOS.get(position).getProfileUrl();
                        ScreenChat.CHAT_NAME = chatListDTOS.get(position).getName();
                        sendIntentNormal(context, new Intent(context, ScreenChat.class), false, 0);
                    } catch (Exception ex) {

                    }
                }
            });
        }

        @Override
        public void onViewDetachedFromWindow(ScreenChatList.Adapter.ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }

        public int getItemCount() {
            return chatListDTOS.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, phoneNumber, date;
            CircleImageView profilePic;
            ShimmerFrameLayout profile_shimmer;
            LinearLayout cardView;

            public ViewHolder(@NonNull View view) {
                super(view);
                cardView = view.findViewById(R.id.cardView);
                date = view.findViewById(R.id.date);
                name = view.findViewById(R.id.name);
                phoneNumber = view.findViewById(R.id.phoneNumber);
                profilePic = view.findViewById(R.id.profilePic);
                profile_shimmer = view.findViewById(R.id.profile_shimmer);
            }
        }
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

    public void sendBtn(View view) {
    }
    //======================================== Show Loading bar ==============================================
}