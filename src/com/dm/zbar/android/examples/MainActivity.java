package com.dm.zbar.android.examples;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import net.sourceforge.zbar.Symbol;

public class MainActivity extends Activity {

    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;
    private static Activity mAct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mAct = this;
        
        Button btnScan = (Button)findViewById(R.id.btnScan);
        Button btnRefresh = (Button)findViewById(R.id.btnRefresh);
        
        btnScan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchScanner(v);
			}
		});
        
        btnRefresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				newInstance(mAct);
			}
		});
        
        String url = "http://192.168.1.53/quanlytieccuoi_server/data.php";
        postData(url);
    }
    
    //http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
    public void postData(String url) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
            // Add your data
            /*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));*/

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity responseEntity = response.getEntity();
    		String result = EntityUtils.toString(responseEntity);
    		Log.i("a", result);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	Log.e("Loi", "Loi", e);
        }
    } 
    
    public static void newInstance(Activity act) {
		Intent intent = new Intent(act, MainActivity.class);
		act.startActivity(intent);
		act.finish();
		act.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

    public void launchScanner(View v) {
        if (isCameraAvailable()) {
            Intent intent = new Intent(this, ZBarScannerActivity.class);
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, "Không có camera", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isCameraAvailable() {
    	if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) && 
				!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			return false;
		}
    	return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZBAR_SCANNER_REQUEST:
            case ZBAR_QR_SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
                } else if(resultCode == RESULT_CANCELED && data != null) {
                    String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                    if(!TextUtils.isEmpty(error)) {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
