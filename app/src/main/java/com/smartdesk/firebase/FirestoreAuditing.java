package com.smartdesk.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreAuditing {

    public static void firestoreGenericModel(String collectionName,String msg,String action, String mobile,String userRole) {
        FirebaseFirestore.getInstance().collection(collectionName )
                .add(new AuditLogModel(msg,action,mobile,userRole, new MobileDetails()));
        System.gc();
    }
}
