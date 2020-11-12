package eu.nsrsdk.javaexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Properties;

import eu.nsrsdk.v3java.NSR;
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
            }
        } catch (Exception e) {
        }
    }


    public void registerUser() {

        Toast.makeText(getApplicationContext(), "register user...", Toast.LENGTH_LONG).show();

        try {
            Log.d(TAG, "registerUser");
            NSRUser user = new NSRUser();
            user.setEmail(config.getProperty("user.email"));
            user.setCode(config.getProperty("user.code"));
            user.setFirstname(config.getProperty("user.firstname"));
            user.setLastname(config.getProperty("user.lastname"));

            JSONObject locals = new JSONObject();
            locals.put("email",config.getProperty("user.email"));
            locals.put("code",config.getProperty("user.code"));
            locals.put("firstName",config.getProperty("user.firstname"));
            locals.put("lastName",config.getProperty("user.lastname"));
            user.setLocals(locals);

            NSR.getInstance(this).registerUser(user);
        } catch (Exception e) {
        }
    }

    public void forgetUser() {
        Log.d(TAG, "forgetUser");
        NSR.getInstance(this).forgetUser();
    }

    public void sendEvent() {

        Toast.makeText(getApplicationContext(), "sending event on demand...", Toast.LENGTH_LONG).show();

        try {
            Log.d(TAG, "sendEvent: ondemand");
            String eventName = "ondemand";
            NSR.getInstance(this).sendEvent(eventName,new JSONObject());
        } catch (Exception e) {
            Log.e(TAG, "sendEvent", e);
        }
    }

    public void sendEventPush() {

        Toast.makeText(getApplicationContext(), "sending event push notification...", Toast.LENGTH_LONG).show();

        try {
            Log.d(TAG, "sendEvent: testpush");
            String eventName = "testpush";
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

        Toast.makeText(getApplicationContext(), "Setup...", Toast.LENGTH_LONG).show();

        NSRSettings settings = new NSRSettings();
        settings.setDisableLog(false);
        settings.setDevMode(true);
        settings.setBaseUrl(config.getProperty("base_url"));
        settings.setCode(config.getProperty("code"));
        settings.setSecretKey(config.getProperty("secret_key"));
        settings.setPushIcon(R.drawable.king);
        settings.setWorkflowDelegate(new WFDelegate(),getApplicationContext());
        NSR.getInstance(this).setup(settings,new JSONObject());
        NSR.getInstance(this).askPermissions(this);
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