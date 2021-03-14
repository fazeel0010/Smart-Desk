package com.smartdesk.screens.consumer.sign_up;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.library.CustomEditext;
import com.google.android.material.snackbar.Snackbar;
import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.List;

import static com.smartdesk.screens.consumer.sign_up.ScreenConsumerSignup.isOkayStep1;

public class FragmentConsumerStep1 extends Fragment implements View.OnClickListener {

    private Context context;
    private View view;

    private CustomEditext et_mechanicName, et_password, et_confirmPassword;
    public MaskEditText met_phoneNumber;
    private Spinner genderSpinner, citySpinner;
    private TextView genderSpinnerError, citySpinnerError;

    private TextView termsTextview;
    private CheckBox termscheckbox;

    private static boolean isfirstRequestFocus = false;

    private ScrollView sv;
    private boolean isGenderSelected = false;
    private boolean isCitySelected = false;

    public FragmentConsumerStep1() {
    }

    public FragmentConsumerStep1(Context mContext) {
        context = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_consumer_step1, container, false);
        initIDS();
        makeAttributedText();
        setSpinnerForGender();
        setSpinnerForCity();
        getSelectedSpinnerItem();
        UtilityFunctions.setupUI(view.findViewById(R.id.parent), (Activity) context);
        initKeyBoardListener();
        return view;
    }

    private void initKeyBoardListener() {
        try {
            final int MIN_KEYBOARD_HEIGHT_PX = 150;
            final View decorView = ((ScreenConsumerSignup) context).getWindow().getDecorView();
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                private final Rect windowVisibleDisplayFrame = new Rect();
                private int lastVisibleDecorViewHeight;

                @Override
                public void onGlobalLayout() {
                    try {
                        decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                        final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                        if (lastVisibleDecorViewHeight != 0) {
                            if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                                ((ScreenConsumerSignup) context).nextBtn.setVisibility(View.GONE);
                                ((ScreenConsumerSignup) context).linearHide.setVisibility(View.GONE);
                            } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                                ((ScreenConsumerSignup) context).nextBtn.setVisibility(View.VISIBLE);
                                ((ScreenConsumerSignup) context).linearHide.setVisibility(View.VISIBLE);
                            }
                        }
                        lastVisibleDecorViewHeight = visibleDecorViewHeight;
                    } catch (Exception ex) {

                    }
                }
            });
        } catch (Exception ex) {

        }
    }

    public void makeAttributedText() {
        String textAttributed = "Are you Agree with our Terms & Conditions";
        int startIndex = textAttributed.indexOf("Terms & Conditions");
        int endIndex = "Terms & Conditions".length();

        SpannableString ss = new SpannableString(textAttributed);
        ClickableSpan privacyPolicy = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
//                Uri uri = Uri.parse("https://socolcorp.com/#/policy"); // missing 'http://' will cause crashed
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
                UtilityFunctions.orangeSnackBar((Activity) context, "Don't have terms & conditions", Snackbar.LENGTH_SHORT);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setTypeface(Typeface.DEFAULT_BOLD);
                ds.setColor(ContextCompat.getColor(context, R.color.red));
            }
        };
        ss.setSpan(privacyPolicy, startIndex, startIndex + endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsTextview.setText(ss);
        termsTextview.setMovementMethod(LinkMovementMethod.getInstance());
        termsTextview.setHighlightColor(Color.TRANSPARENT);
    }

    private void initIDS() {
        //Edit Text
        sv = view.findViewById(R.id.sv);
        et_mechanicName = view.findViewById(R.id.WorkerName);
        et_password = view.findViewById(R.id.password);
        et_confirmPassword = view.findViewById(R.id.confirmPassword);
        //Mask Text
        met_phoneNumber = view.findViewById(R.id.phoneNumber);
        //Spinners
        genderSpinner = view.findViewById(R.id.gender);
        citySpinner = view.findViewById(R.id.city);
        genderSpinnerError = view.findViewById(R.id.genderError);
        citySpinnerError = view.findViewById(R.id.cityError);

        termscheckbox = view.findViewById(R.id.termscheckbox);
        termsTextview = view.findViewById(R.id.attr_text);
        termscheckbox.setOnClickListener(this);
    }

    public void validationsAndGetValues() {
        isOkayStep1 = true;
        isfirstRequestFocus = false;
        Constants.const_ConsumerSignupDTO.setWorkerName(getDataFromEditext(et_mechanicName, "Invalid Name", 3));
        if (!UtilityFunctions.isValidName(Constants.const_ConsumerSignupDTO.getWorkerName())) {
            isOkayStep1 = false;
            et_mechanicName.setError("Invalid Name");
            if (!isfirstRequestFocus) {
                UtilityFunctions.editeTextFocusReset(sv, et_mechanicName);
                isfirstRequestFocus = true;
            }
        }

        Constants.const_ConsumerSignupDTO.setWorkerPhone(getDataFromMaskText(met_phoneNumber, "Invalid Number", 12));

        try {
            Constants.const_ConsumerSignupDTO.setWorkerPhone(Constants.const_ConsumerSignupDTO.getWorkerPhone().replaceAll("-", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!Constants.const_ConsumerSignupDTO.getWorkerPhone().startsWith("03")) {
            met_phoneNumber.setError("Number should start from 03");
            isOkayStep1 = false;
        }

        Constants.const_ConsumerSignupDTO.setWorkerPassword(getDataFromEditext(et_password, "Invalid Password", 6));
        if (Constants.const_ConsumerSignupDTO.getWorkerPassword().length() < 6) {
            et_password.setError("Password min length should be 6");
            isOkayStep1 = false;
            if (!isfirstRequestFocus) {
                UtilityFunctions.editeTextFocusReset(sv, et_password);
                isfirstRequestFocus = true;
            }
        }

        String cPass = getDataFromEditext(et_confirmPassword, "Invalid Confirm Password", 6);
        if (cPass.length() < 6) {
            et_confirmPassword.setError("Password min length should be 6");
            isOkayStep1 = false;
            if (!isfirstRequestFocus) {
                UtilityFunctions.editeTextFocusReset(sv, et_confirmPassword);
                isfirstRequestFocus = true;
            }
        }

        if (!isGenderSelected) {
            isOkayStep1 = false;
            genderSpinnerError.setVisibility(View.VISIBLE);
        }

        if (!isCitySelected) {
            isOkayStep1 = false;
            citySpinnerError.setVisibility(View.VISIBLE);
        }

        if (isOkayStep1) {
            if (!Constants.const_ConsumerSignupDTO.getWorkerPassword().equals(cPass)) {
                isOkayStep1 = false;
                et_password.setError("Password not match");
                et_confirmPassword.setError("Password not match");
                if (!isfirstRequestFocus) {
                    UtilityFunctions.editeTextFocusReset(sv, et_password);
                    isfirstRequestFocus = true;
                }
            }
        } else {
            UtilityFunctions.orangeSnackBar((Activity) context, "Please Enter Details properly", Snackbar.LENGTH_SHORT);
        }
    }

    private String getDataFromEditext(EditText editText, String errorMSG, int minimumLength) {
        String text = "";
        try {
            text = UtilityFunctions.getStringFromEditTextWithLengthLimit(editText, minimumLength);
        } catch (NullPointerException ex) {
            editText.setError(errorMSG);
            isOkayStep1 = false;
            if (!isfirstRequestFocus) {
                UtilityFunctions.editeTextFocusReset(sv, editText);
                isfirstRequestFocus = true;
            }
        }
        return text;
    }

    private String getDataFromMaskText(MaskEditText editText, String errorMSG, int minimumLength) {
        String text = "";
        try {
            text = UtilityFunctions.getStringFromMaskWithLengthLimit(editText, minimumLength);
        } catch (NullPointerException ex) {
            editText.setError(errorMSG);
            isOkayStep1 = false;
            if (!isfirstRequestFocus) {
                UtilityFunctions.editeTextFocusReset(sv, editText);
                isfirstRequestFocus = true;
            }
        }
        return text;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.termscheckbox) {
            if (termscheckbox.isChecked()) {
                ((ScreenConsumerSignup) context).nextBtn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.SmartDesk_Blue));
                ((ScreenConsumerSignup) context).nextBtn.setEnabled(true);
            } else {
                ((ScreenConsumerSignup) context).nextBtn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.SmartDesk_Blue_Oppaque));
                ((ScreenConsumerSignup) context).nextBtn.setEnabled(false);
                isOkayStep1 = false;
            }
        }
    }

    public void setSpinnerForGender() {
        try {
            List<String> spinnerArray = new ArrayList<>();
            spinnerArray.add(Constants.genderSelection);
            for (String item : Constants.genderItems)
                spinnerArray.add(item);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, spinnerArray);
            genderSpinner.setAdapter(adapter);
            genderSpinner.post(() -> {
                int height = genderSpinner.getHeight();
                genderSpinner.setDropDownVerticalOffset(height);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setSpinnerForCity() {
        try {
            List<String> spinnerArray = new ArrayList<>();
            spinnerArray.add(Constants.citySelection);
            for (String item : Constants.cityItems)
                spinnerArray.add(item);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, spinnerArray);
            citySpinner.setAdapter(adapter);
            citySpinner.post(() -> {
                int height = citySpinner.getHeight();
                citySpinner.setDropDownVerticalOffset(height);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getSelectedSpinnerItem() {
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equalsIgnoreCase(Constants.genderSelection)) {
                    isGenderSelected = false;
                } else {
                    genderSpinnerError.setVisibility(View.GONE);
                    Constants.const_ConsumerSignupDTO.setWorkerGender(parent.getItemAtPosition(position).toString());
                    isGenderSelected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equalsIgnoreCase(Constants.citySelection)) {
                    isCitySelected = false;
                } else {
                    if (parent.getItemAtPosition(position).toString().equalsIgnoreCase("Karachi")) {
                        citySpinnerError.setVisibility(View.GONE);
                        Constants.const_ConsumerSignupDTO.setCity(parent.getItemAtPosition(position).toString());
                        isCitySelected = true;
                    } else {
                        isCitySelected = false;
                        citySpinner.setSelection(0);
                        UtilityFunctions.orangeSnackBar((Activity) context, "Only available for Karachi Users!", Snackbar.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
