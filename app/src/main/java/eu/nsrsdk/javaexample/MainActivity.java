package eu.nsrsdk.javaexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import eu.nsrsdk.v3java.NSR;
import eu.nsrsdk.v3java.NSRSecurityResponse;
import eu.nsrsdk.v3java.NSRSettings;
import eu.nsrsdk.v3java.NSRUser;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = "NSRSample";
    private WebView mainView;
    private Properties config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Log.d("NSR SDK JavaExample - MainActivity", ">>> " + NSR.TAG);

        try {
            config = new Properties();
            config.load(this.getAssets().open("config.properties"));

            WebView.setWebContentsDebuggingEnabled(true);
            mainView = new WebView(this);
            mainView.getSettings().setJavaScriptEnabled(true);
            mainView.getSettings().setDomStorageEnabled(true);
            mainView.getSettings().setAllowFileAccessFromFileURLs(true);
            mainView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            mainView.addJavascriptInterface(this, "app");
            mainView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

            setContentView(mainView);
            mainView.loadUrl("file:///android_asset/sample.html");
            setup();
            //mainView.loadUrl("https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/capture");
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

    }

    protected void eval(final String code) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                mainView.evaluateJavascript(code, null);
            }
        });
    }

    @JavascriptInterface
    public void postMessage(final String json) {
        try {
            final JSONObject body = new JSONObject(json);
            String what = body.getString("what");
            if (what != null) {
                if ("setup".equals(what)) {
                    setup();
                }
                if ("registerUser".equals(what)) {
                    registerUser();
                }
                if ("forgetUser".equals(what)) {
                    forgetUser();
                }
                if ("showApp".equals(what)) {
                    showApp();
                }
                if ("sendEvent".equals(what)) {
                    sendEvent();
                }
                if ("sendEventPush".equals(what)) {
                    sendEventPush();
                }
                if ("appLogin".equals(what)) {
                    appLogin();
                }
                if ("appPayment".equals(what)) {
                    appPayment();
                }
                if ("accurateLocation".equals(what)) {
                    NSR.getInstance(this).accurateLocation(0, 20, true);
                }
                if ("accurateLocationEnd".equals(what)) {
                    NSR.getInstance(this).accurateLocationEnd();
                }
                if ("resetCruncher".equals(what)) {
                    NSR.getInstance(this).resetCruncher();
                }
                if ("closeView".equals(what)) {
                    NSR.getInstance(this).closeView();
                }
                if ("policies".equals(what)) {
                    JSONObject criteria = new JSONObject();
                    criteria.put("available",true);
                    NSR.getInstance(this).policies(criteria, new NSRSecurityResponse() {
                        public void completionHandler(JSONObject json, String error) throws Exception {
                            if (error == null) {
                                Log.d(TAG, "policies response");
                                Log.d(TAG, json.toString());
                            } else {
                                Log.e(TAG, "policies error: " + error);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
        }
    }


    public void registerUser() {

        //Toast.makeText(getApplicationContext(), "register user...", Toast.LENGTH_LONG).show();

        try {
            Log.d(TAG, "registerUser");
            NSRUser user = new NSRUser();
            user.setEmail(config.getProperty("user.email"));
            user.setCode(config.getProperty("user.code"));
            user.setFirstname(config.getProperty("user.firstname"));
            user.setLastname(config.getProperty("user.lastname"));

            user.setAddress(config.getProperty("user.address"));
            user.setZipCode(config.getProperty("user.cap"));
            user.setCity(config.getProperty("user.city"));
            user.setStateProvince(config.getProperty("user.province"));
            user.setFiscalCode(config.getProperty("user.fiscalcode"));


            JSONObject locals = new JSONObject();
            locals.put("email",config.getProperty("user.email"));
            locals.put("code",config.getProperty("user.code"));
            locals.put("firstName",config.getProperty("user.firstname"));
            locals.put("lastName",config.getProperty("user.lastname"));
            //user.setLocals(locals);

            NSR.getInstance(this).registerUser(user);

            if(Build.VERSION.SDK_INT >= 30)
                MainActivity.askPermissionsBackground(this);

        } catch (Exception e) {
        }
    }

    public void forgetUser() {
        Log.d(TAG, "forgetUser");
        NSR.getInstance(this).forgetUser();
    }

    public void sendEvent() {

        //Toast.makeText(getApplicationContext(), "sending event on demand...", Toast.LENGTH_LONG).show();

        try {
            Log.d(TAG, "sendEvent: ondemand");
            String eventName = "ondemand";
            NSR.getInstance(this).sendEvent(eventName,new JSONObject());
        } catch (Exception e) {
            Log.e(TAG, "sendEvent", e);
        }
    }

    public void sendEventPush() {

        //Toast.makeText(getApplicationContext(), "sending event push notification...", Toast.LENGTH_LONG).show();

        try {
            Log.d(TAG, "sendEvent: inpoi"); //testpush
            String eventName = "inpoi"; //"testpush";
            NSR.getInstance(this).sendEvent(eventName,new JSONObject());
        } catch (Exception e) {
            Log.e(TAG, "sendEventPush", e);
        }
    }

    public void showApp() {
        Log.d(TAG, "showApp");
        NSR.getInstance(this).showApp();
    }

    public void setup() {
        Log.d(TAG, "setup");

        //Toast.makeText(getApplicationContext(), "Setup...", Toast.LENGTH_LONG).show();

        NSRSettings settings = new NSRSettings();
        settings.setDisableLog(false);
        settings.setDevMode(true);
        settings.setBaseUrl(config.getProperty("base_url"));
        settings.setCode(config.getProperty("code"));
        settings.setSecretKey(config.getProperty("secret_key"));
        settings.setPushIcon(R.drawable.king);
        settings.setWorkflowDelegate(new WFDelegate(),getApplicationContext());
        NSR.getInstance(this).setup(settings,new JSONObject());

        if(Build.VERSION.SDK_INT >= 30)
            MainActivity.askPermissions(this);
        else
            NSR.getInstance(this).askPermissions(this);
    }

    public static void askPermissionsBackground(Activity activity) {
        setData("permission_requested", "*", activity.getApplicationContext());
        List<String> permissionsList = new ArrayList();


        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), "android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
            permissionsList.add("android.permission.ACCESS_BACKGROUND_LOCATION");
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, (String[])permissionsList.toArray(new String[permissionsList.size()]), 8259);
        }

    }

    public static void askPermissions(Activity activity) {
        setData("permission_requested", "*", activity.getApplicationContext());
        List<String> permissionsList = new ArrayList();
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
            permissionsList.add("android.permission.ACCESS_FINE_LOCATION");
        }

        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            permissionsList.add("android.permission.ACCESS_COARSE_LOCATION");
        }

        /*
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), "android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
            permissionsList.add("android.permission.ACCESS_BACKGROUND_LOCATION");
        }
         */

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, (String[])permissionsList.toArray(new String[permissionsList.size()]), 8259);
        }

    }

    public static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences("NSRSDK", 0);
    }

    private static final byte[] K = Base64.decode("Ux44AGRuanL0y7qQDeasT3", 2);
    private static final byte[] I = Base64.decode("ycB4AGR7a0fhoFXbpoHy43", 2);

    public static String toe(String input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(1, new SecretKeySpec(Arrays.copyOf(K, 16), "AES"), new IvParameterSpec(Arrays.copyOf(I, 16)));
        return Base64.encodeToString(cipher.doFinal(input.getBytes()), 2);
    }

    public static void setData(String key, String value, Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        if (value != null) {
            try {
                editor.putString(key, toe(value));
            } catch (Exception var5) {
            }
        } else {
            editor.remove(key);
        }

        editor.commit();
    }

    public void appLogin() {
        Log.d(TAG, "appLogin");
        String url = WFDelegate.getData(this, "login_url");
        if (url != null) {
            NSR.getInstance(this).loginExecuted(url);
            WFDelegate.setData(this, "login_url", null);
        }
    }

    public void appPayment() {
        Log.d(TAG, "appPayment");
        try {
            String url = WFDelegate.getData(this, "payment_url");
            if (url != null) {
                JSONObject paymentInfo = new JSONObject();
                paymentInfo.put("transactionCode", "abcde");
                NSR.getInstance(this).paymentExecuted(paymentInfo, url);
                WFDelegate.setData(this, "payment_url", null);
            }
        } catch (Exception e) {
            Log.e(TAG, "appPayment", e);
        }
    }

    public void timeline() {
        Log.d(TAG, "timeline");
        NSR.getInstance(this).showUrl("https://s3.eu-west-2.amazonaws.com/neosurancesandbox/apps/timeline/app.html");
    }

}