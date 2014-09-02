package com.dm.zbar.android.examples;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Detail extends Activity{

	public static String mResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		
		try {
			JSONArray jArr = new JSONArray(mResult);
			
			if(jArr.length() > 0)
			{
				String text = "";
				for(int i = 0; i < jArr.length(); i++)
				{
					text += i+1+") " + jArr.optString(i) + "\n";
				}
				TextView txtGuestList = (TextView)findViewById(R.id.textView1);		
				txtGuestList.setText(text);
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public static void newInstance(Activity act, String result) {
		mResult = result;
		
		Intent intent = new Intent(act, Detail.class);
		act.startActivity(intent);
		act.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
