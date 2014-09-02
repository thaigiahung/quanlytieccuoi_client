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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import net.sourceforge.zbar.Symbol;

public class MainActivity extends Activity {

    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;
    private static Activity mAct;
    public JSONArray jArr;
    public static String domain = "http://192.168.0.11/quanlytieccuoi_server/";

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
        
        String url = domain + "data.php";
        postData(url);
        
        GridView gv = (GridView)findViewById(R.id.gridView1);
        gv.setAdapter(new myadapter(this));
        gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), position+"", Toast.LENGTH_SHORT).show();
			}
		});
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
    		
    		jArr = new JSONArray(result);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	Log.e("Loi", "Loi", e);
        }
    } 
    
    public void postData(String url, String code) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("code", code));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity responseEntity = response.getEntity();
    		String result = EntityUtils.toString(responseEntity);
    		
    		Log.i("a", "url: " + url + " code: " + code);
    		Log.i("b", result);
            
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
                	postData(domain + "scan.php", data.getStringExtra(ZBarConstants.SCAN_RESULT));
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
    
    
//    GridView
    public static class GridView_Item
    {
    	public TextView txtTable, txtNumOfGuest;
    }

    public class myadapter extends BaseAdapter
    {
    	Context context;
    	public myadapter(Context c)
    	{
    		context=c;
    	}
    	
		public int getCount() {
			// TODO Auto-generated method stub
			return jArr.length();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return jArr.getJSONObject(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return position;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			GridView_Item gv_item;
			LayoutInflater layoutinflater= ((Activity)context).getLayoutInflater();
			
			if(arg1==null)  
			{  
				gv_item = new GridView_Item();  
				arg1 = layoutinflater.inflate(R.layout.gridview_item, null);  
				gv_item.txtTable = (TextView) arg1.findViewById(R.id.txtTable);  
				gv_item.txtNumOfGuest = (TextView) arg1.findViewById(R.id.txtNumOfGuest);  
				arg1.setTag(gv_item);  
			}  
			else
				gv_item = (GridView_Item)arg1.getTag();
			
			JSONObject jObj;
			try {
				jObj = jArr.getJSONObject(arg0);
				gv_item.txtTable.setText("Bàn " + jObj.optString("id"));
				gv_item.txtNumOfGuest.setText("Số khách hiện tại: " + jObj.optString("current_num_of_guest"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return arg1;
		}
    	
    }

}
