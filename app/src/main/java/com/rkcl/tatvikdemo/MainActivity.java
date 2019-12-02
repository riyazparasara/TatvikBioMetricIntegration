package com.rkcl.tatvikdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tatvik.fp.CaptureResult;
import org.tatvik.fp.DeviceInfo;
import org.tatvik.fp.TMF20API;
import org.tatvik.fp.TMF20ErrorCodes;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Observer {

    private Button btn_refresh, btn_deviceInfo, btn_matchfinger;
    private TextView status;
    private ImageView fingerImage1, fingerImage2;

    private CaptureResult captResult1, captResult2;
    private TMF20API tmf20lib;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        addEventListeners();
    }


    private void initViews() {
        mContext = this;
        tmf20lib = new TMF20API(mContext);
        btn_refresh = findViewById(R.id.btn_refresh);
        btn_deviceInfo = findViewById(R.id.btn_devInfo);
        btn_matchfinger = findViewById(R.id.btn_matchfinger);
        status = findViewById(R.id.status);
        fingerImage1 = findViewById(R.id.image1);
        fingerImage2 = findViewById(R.id.image2);
    }

    private void addEventListeners() {
        fingerImage1.setOnClickListener(this);
        fingerImage2.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_deviceInfo.setOnClickListener(this);
        btn_matchfinger.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                captureFingerFirst();
                break;
            case R.id.image2:
                captureFingerSecond();
                break;
            case R.id.btn_refresh:
                resetAllData();
                break;
            case R.id.btn_devInfo:
                fetchBioMetricDeviceInformation();
                break;
            case R.id.btn_matchfinger:
                matchFingerPrint();
                break;
            default:
                break;
        }
    }


    private void resetAllData() {
        fingerImage1.setImageResource(R.drawable.blank);
        fingerImage2.setImageResource(R.drawable.blank);
        captResult1 = null;
        captResult2 = null;
        status.setText("");

    }

    private void captureFingerFirst() {
        try {
            fingerImage1.setImageResource(R.drawable.intermediate);
            captResult1 = tmf20lib.captureFingerprint(10000);
            if (null != captResult1 && TMF20ErrorCodes.SUCCESS == captResult1.getStatusCode()) {
                fingerImage1.setImageResource(R.drawable.right);
                status.setText("Finger First Capture Successfully Completed");
            } else {
                fingerImage1.setImageResource(R.drawable.wrong);
                status.setText("Finger First Capture Failed Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void captureFingerSecond() {
        try {
            fingerImage2.setImageResource(R.drawable.intermediate);
            captResult2 = tmf20lib.captureFingerprint(10000);
            if (null != captResult2 && TMF20ErrorCodes.SUCCESS == captResult2.getStatusCode()) {
                fingerImage2.setImageResource(R.drawable.right);
                status.setText("Finger Second Capture Successfully Completed");
            } else {
                fingerImage2.setImageResource(R.drawable.wrong);
                status.setText("Finger Second Capture Failed Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchBioMetricDeviceInformation() {
        DeviceInfo deviceInfo = tmf20lib.getDeviceInfo();

        if (TMF20ErrorCodes.SUCCESS == deviceInfo.getErrorCode()) {
            String msg = "";
            msg = "Serial Number :" + deviceInfo.getSerialNumber() + "\n";
            msg += "Make : " + deviceInfo.getMake() + "\n";
            msg += "Model : " + deviceInfo.getModel();
            status.setText(msg);
        } else {
            String msg = "Err Code : " + deviceInfo.getErrorCode() + "\n";
            msg += deviceInfo.getErrorString();
            status.setText(msg);
        }
    }

    private void matchFingerPrint() {
        boolean isMatched = false;
        if (null != captResult1 && null != captResult2) {
            if (null != captResult1.getFmrBytes() && null != captResult2.getFmrBytes()) {
                try {
                    isMatched = tmf20lib.matchIsoTemplates(captResult1.getFmrBytes(), captResult2.getFmrBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isMatched) {
                    status.setText("Matched");
                } else {
                    status.setText("Not Matched");
                }
            } else {
                status.setText("Please capture fingerprint");
            }
        } else {
            status.setText("Please capture fingerprint");
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
