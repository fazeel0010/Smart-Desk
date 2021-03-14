package com.smartdesk.constants;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseConstants {

    //Firebase fcm base url
    public static String Firebase_BASE_URL = "https://fcm.googleapis.com/";

    //Firebase Key
    public static final String FirebaseKey ="AAAAteiPjAc:APA91bH3tuKUOnc7lCvLx7Y1U32Hzt1oaJk0RceJSltaPTUhiTDzJMaivScSXDHgRgH8TA93LnLHlmnegXR_ATMeowGtBU1DZNjiBoLZtmmumtCJCXz3JADY3Ryo9DbWnnhCV8vCYKBX";
    //Firebase Services
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    //Firebase Collections
    public static String usersCollection = "Users";
    public static String notificationCollection = "Notification";
    public static String paymentCollection = "Payment";
    public static String chatListCollection = "ChatList";
    public static String chatCollection = "Chat";
    public static String requestCollection = "Request";

    public static String adminDocumentID = "3lclTYUq7LmVdaRpXqbb";

    //FCM Topic
    public static String fcmTopic = "notification";
}
