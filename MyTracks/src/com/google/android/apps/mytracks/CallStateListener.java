package com.google.android.apps.mytracks;

import com.google.android.maps.mytracks.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;

public class CallStateListener extends PhoneStateListener {
	
	private Context context;	
	private boolean listening;
	
	public CallStateListener(Context context){
		super();
		this.context = context;
		this.listening = true;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);	
		tm.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);

		int derp = 0 ;
	    derp++;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
      SharedPreferences sp = context.getApplicationContext().getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
      boolean selected = sp.getBoolean(context.getString(R.string.block_call_option_key), true);
      
      if(listening && selected){
    		switch (state) {
    		case TelephonyManager.CALL_STATE_RINGING:
    			try{
    				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    				Class c = Class.forName(tm.getClass().getName()); // Define a reference c for the Class of tm
    				Method m = c.getDeclaredMethod("getITelephony"); // Fetch private method from c
    				m.setAccessible(true); // Change method visibility
    				
    //				Define a reference cITelephony for the class of the object resulting from invoking our method m
    				Class cITelephony = Class.forName(m.invoke(tm).getClass().getName());
    				Object stub = m.invoke(tm); // Save the object returned from invoking m
    				Method mEndCall = cITelephony.getDeclaredMethod("endCall"); // Fetch the method endCall() from our cITelephony class definition
    				mEndCall.invoke(stub); // Invoke endCall method
    			
    				Toast.makeText(context, "Blocked a call", Toast.LENGTH_LONG).show();
    			}catch(Exception e){}
    			break;
    		}
	    }
	}
	
	public void setListening(boolean b){
	  this.listening = b;
	}
}