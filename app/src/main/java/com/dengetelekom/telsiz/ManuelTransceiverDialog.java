package com.dengetelekom.telsiz;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class ManuelTransceiverDialog extends Dialog implements
        android.view.View.OnClickListener {
    private StringBuilder sb=new StringBuilder();
    private ManuelBandrolDialogResponse dialogResponse;
    private EditText barcodeStart, barcodeMiddle, barcodeEnd;
    private Activity activity;
    public ManuelTransceiverDialog(Activity activity, ManuelBandrolDialogResponse dialogResponse) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.dialogResponse = dialogResponse;
        this.activity=activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_manuel_query);
        barcodeStart= (EditText) findViewById(R.id.edit_barcode_start);
        barcodeStart.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(3)});
        barcodeStart.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(sb.length()==0&barcodeStart.length()==3)
                {
                    sb.append(s);
                    barcodeStart.clearFocus();
                    barcodeMiddle.requestFocus();
                    barcodeMiddle.setCursorVisible(true);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(sb.length()==1) sb.deleteCharAt(0);
            }
            public void afterTextChanged(Editable s) {
                if(sb.length()==0) barcodeStart.requestFocus();
            }
        });


        barcodeMiddle = (EditText) findViewById(R.id.edit_barcode_middle);
        barcodeMiddle.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(barcodeMiddle.length()==7)
                {
                    sb.append(s);
                    barcodeMiddle.clearFocus();
                    barcodeEnd.requestFocus();
                    barcodeEnd.setCursorVisible(true);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void afterTextChanged(Editable s) {

            }
        });
        barcodeMiddle.setFilters(new InputFilter[] {  new InputFilter.LengthFilter(7)});
        barcodeEnd = (EditText) findViewById(R.id.edit_barcode_end);
        barcodeEnd.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(barcodeEnd.length()==3)
                {
                    sb.append(s);
                    barcodeEnd.clearFocus();
                    barcodeEnd.setCursorVisible(false);
                    hideKeyboard();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void afterTextChanged(Editable s) {

            }
        });
        barcodeEnd.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(3)});
        ((FrameLayout) findViewById(R.id.btn_cancel_root)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_cancel)).setOnClickListener(this);

        ((FrameLayout) findViewById(R.id.btn_search_root)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_search)).setOnClickListener(this);

        barcodeStart.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setGravity(Gravity.TOP);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_root:
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_search_root:
            case R.id.btn_search:
                dialogResponse.onSearch(sb.toString());
                break;
            default:
                break;
        }
        dismiss();
    }

    interface ManuelBandrolDialogResponse {
        void onSearch(String result);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}