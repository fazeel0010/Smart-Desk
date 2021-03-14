package com.smartdesk.constants;

import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.smartdesk.model.hire_worker.WorkerRequestDTO;
import com.smartdesk.model.signup.SignupUserDTO;

public class Constants {

    //    public static final String topicPrefix = "/topics/";
    public static final String api_key = "AIzaSyAJwu52r7NzrZFYKsLW7zT8lVdvfWe1AF8";
    public static final String smartDeskLogo = "https://thumbs.dreamstime.com/z/gold-smart-table-lamp-system-icon-isolated-black-background-internet-things-concept-wireless-connection-vector-187995951.jpg";

    //SmartDesk email
    public static String emailID = "smartdesk21.uk@gmail.com";
    public static String password = "smartdesk123";
    public static String SmartDesk = "Smart-Desk";

    //Roles
    public static Integer adminRole = 1;
    public static Integer managerRole = 2;
    public static Integer deskUserRole = 3;

    //User Details Constants
    public static Integer USER_ROLE;
    public static String USER_NAME;
    public static String USER_MOBILE;
    public static String USER_PROFILE;
    public static String USER_DOCUMENT_ID;
    public static Boolean USER_MECHANIC_ONLINE;

    //Share Preference Constants
    public static final String SP_FILE_NAME = "info";
    public static final String SP_MOBILE = "Mobile";
    public static final String SP_PASSWORD = "Password";
    public static final String SP_DOCUMENT_ID = "Document";
    public static final String SP_ISLOGIN = "isLogin";

    //Dto COnstants
    public static SignupUserDTO const_usersSignupDTO;
    public static SignupUserDTO const_ConsumerSignupDTO;
    public static WorkerRequestDTO const_WorkerRequestDTO;
    public static Integer DISTANCE;
    public static String WORKPLACE_TYPE;

    //Notification
    public static int notificationCount = 0;

    //Spinner Constants
    public static String genderSelection = "Select Gender ⁕";
    public static String[] genderItems = {"Male", "Female"};

    public static String citySelection = "Select City ⁕";
    public static String[] cityItems = {"Karachi", "Lahore", "Islamabad"};

    public static String shopCategorySelection = "Select Workplace Category ⁕";
    public static String[] shopCategoryItems = {"Local Shop", "Registered Company"};

    public static String workingCategorySelection = "Select Service Category ⁕";
    public static String[] workingCategoryItems = {"Car/Bike Mechanic Service", "Car Mechanic Service", "Bike Mechanic Service", "Car/Bike Puncher Service", "Car Puncher Service", "Bike Puncher Service", "Towing Service"};


    //Location Constants
    public static Double const_lat = 24.8612;
    public static Double const_lng = 67.0695;
    public static EditText addressEditext;

    //Otp Timeout
    public static String countryCode = "+44";
    public static int timeoutOtp = 90; // 120 seconds means 2 minutes expiration
    public static int changeIntentDelay = 600;

    //User Status
    public static String activeStatus = "Active";
    public static String newAccountStatus = "Your account is in review process, we will notify you once done. Please wait for admin approval";
    public static String disableStatus = "Your Account has been disabled! ";
    public static String blockedStatus = "Your Account has been blocked";

    //camera zoom
    public static int cameraZoomInMap = 15;

    //Location Alert Dialog
    public static AlertDialog confirmationAlert;


    //Tracking Order
    public static String workerRequest = "Placed";
    public static String workerConfirm = "Confirm";
    public static String workerReachedLocation = "Reached";
    public static String workerInProgress = "InProgress";
    public static String workerDone = "Done";

    public static String workerPayment = "Payment";
    public static String workerSendAmount = "AmountSent";

    public static String workerAmountNotFine = "NotAmountFine";
    public static String workerAmountFine = "AmountFine";
    public static String workerFinish = "Finish";
}
