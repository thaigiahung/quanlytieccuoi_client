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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CheckActivity extends Activity{
	private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;
//	public static String domain = "http://192.168.1.56/quanlytieccuoi_server/";
    public static String domain = "http://giahung.net/sv/";
	public static String url = domain + "scan2.php";
	
	private Button btnScan2, btnEdit;
	private TextView txtId;
	private EditText editMoney;
	
	private static Activity mAct;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check);
		
		btnScan2 = (Button)findViewById(R.id.btnScan2);
        btnEdit = (Button)findViewById(R.id.btnEdit);
        txtId = (TextView)findViewById(R.id.txtId);
        editMoney = (EditText)findViewById(R.id.editTextMoney);
        
        btnScan2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchScanner(v);
			}
		});
        
        btnEdit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String id = txtId.getText().toString();
				String money = editMoney.getText().toString();
				postData(url, id, money);
				Toast.makeText(getApplicationContext(), "Xong", Toast.LENGTH_SHORT).show();
			}
		});
	}
	    
    public String postData(String url, String code, String money) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("code", code));
            nameValuePairs.add(new BasicNameValuePair("money", money));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity responseEntity = response.getEntity();
    		String result = EntityUtils.toString(responseEntity,"UTF-8");
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	Log.e("Loi", "Loi", e);
        }
		return null;
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
            	Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            	 // Vibrate for 500 milliseconds
            	 v.vibrate(500);
            	 
                if (resultCode == RESULT_OK) {
                	String id = data.getStringExtra(ZBarConstants.SCAN_RESULT);
                	id = id.substring(id.lastIndexOf("#")+1);
                	txtId.setText(id);                    
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
