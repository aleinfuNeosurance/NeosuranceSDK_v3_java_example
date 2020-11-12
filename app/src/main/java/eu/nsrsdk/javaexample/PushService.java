package eu.nsrsdk.javaexample;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class PushService extends JobIntentService {

	@Override
	protected void onHandleWork(@NonNull Intent intent) {
		onHandleIntent(intent);
	}

	//@Override
	public void onHandleIntent(Intent intent) {
		String code = intent.getExtras().getString("code");
		Long expirationTime = intent.getExtras().getLong("expirationTime");
		Log.d(">>> code ", code + " expirationTime " + expirationTime);
	}

	static void enqueueWork(Context context, Intent intent){
		enqueueWork(context, PushService.class, 1, intent);
	}

}