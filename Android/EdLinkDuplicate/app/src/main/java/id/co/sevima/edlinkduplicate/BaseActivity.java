package id.co.sevima.edlinkduplicate;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import org.json.JSONObject;

/* renamed from: com.dn.silentalarmadmin.BaseActivity */
public class BaseActivity extends AppCompatActivity {
    FileOutputStream fos;
    InputStream inputStream;
    private PermissionListener listener = null;
    boolean newLocationReceived = false;
    int notificationID = 0;
    OutputStream outputStream;

    /* renamed from: com.dn.silentalarmadmin.BaseActivity$FirebaseListener */
    public interface FirebaseListener {
        void onComplete(String str);
    }

    /* renamed from: com.dn.silentalarmadmin.BaseActivity$Listener */
    public interface Listener {
        void onResponse(String str);
    }

    /* renamed from: com.dn.silentalarmadmin.BaseActivity$LocationUpdateListener */
    public interface LocationUpdateListener {
        void onLocationUpdate(double d, double d2);
    }

    /* renamed from: com.dn.silentalarmadmin.BaseActivity$PermissionListener */
    public interface PermissionListener {
        void onPermissionDenied();

        void onPermissionGranted();
    }

    public void updateFCMID() {
    }

    public String getAppName() {
        return getResources().getString(R.string.app_name);
    }

    public void show(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    public void showSuccess(String str) {
        Util.showSuccess(this, str);
    }

    public void showError(String str) {
        Util.showError(this, str);
    }

    public void show(int i) {
        Toast.makeText(this, i, Toast.LENGTH_LONG).show();
    }

    public void showLog(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("sms:"));
        intent.putExtra("sms_body", str);
        startActivity(intent);
    }

    public void run(Runnable runnable) {
        new Thread(runnable).start();
    }

    public void runLater(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public ProgressDialog createDialog(String str) {
        return Util.createDialog((Context) this, str);
    }

    public ProgressDialog createDialog(int i) {
        return Util.createDialog((Context) this, i);
    }

    public void log(String str) {
        Log.i(Build.MODEL, str);
    }

    public String getString(JSONObject jSONObject, String str, String str2) {
        return Util.getString(jSONObject, str, str2);
    }

    public int getInt(JSONObject jSONObject, String str, int i) {
        return Util.getInt(jSONObject, str, i);
    }

    public double getDouble(JSONObject jSONObject, String str, double d) {
        return Util.getDouble(jSONObject, str, d);
    }

    public String read(String str, String str2) {
        return Util.read((Context) this, str, str2);
    }

    public String readEncrypted(String str, String str2) {
        return Util.readEncrypted((Context) this, str, str2);
    }

    public int read(String str, int i) {
        return Util.read((Context) this, str, i);
    }

    public int readEncrypted(String str, int i) {
        return Util.readEncrypted((Context) this, str, i);
    }

    public void write(String str, int i) {
        Util.write((Context) this, str, i);
    }

    public void writeEncrypted(String str, String str2) {
        Util.writeEncrypted((Context) this, str, str2);
    }

    public void write(String str, String str2) {
        Util.write((Context) this, str, str2);
    }

    public void writeEncrypted(String str, int i) {
        Util.writeEncrypted((Context) this, str, i);
    }

    public void get(final Listener listener2, String str) {
        Util.get(this, new Util.Listener() {
            public void onResponse(String str) {
                Listener listener = listener2;
                if (listener != null) {
                    listener.onResponse(str);
                }
            }
        }, str);
    }

    public Thread post(final Listener listener2, String url, String... strArr) {
        return Util.post(this, new Util.Listener() {
            public void onResponse(String response) {
                Listener listener = listener2;
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        }, url, strArr);
    }

    public void postInsert(final Listener listener2, String str, String str2, String... strArr) {
        Util.postInsert(this, new Util.Listener() {
            public void onResponse(String str) {
                Listener listener = listener2;
                if (listener != null) {
                    listener.onResponse(str);
                }
            }
        }, str, str2, strArr);
    }

    public File getWallpaperFolder() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, "Pictures/" + getAppName());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public int getNextNotificationID() {
        int i = this.notificationID;
        if (i >= Integer.MAX_VALUE) {
            this.notificationID = 1;
        } else {
            this.notificationID = i + 1;
        }
        return this.notificationID;
    }

    public int getRadioGroupIndex(RadioGroup radioGroup) {
        return radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()));
    }

    public void read(final FirebaseListener firebaseListener, final String str) {
        run(new Runnable() {
            public void run() {
                try {
                    OkHttpClient okHttpClient = Util.getOkHttpClient();
                    Request.Builder builder = new Request.Builder();
                    final String string = okHttpClient.newCall(builder.url("https://siaga-satu.firebaseio.com/" + str + ".json").build()).execute().body().string();
                    BaseActivity.this.runLater(new Runnable() {
                        public void run() {
                            if (firebaseListener != null) {
                                firebaseListener.onComplete(string);
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }
        });
    }

    public void write(final FirebaseListener firebaseListener, final String str, final String... strArr) {
        run(new Runnable() {
            public void run() {
                try {
                    OkHttpClient okHttpClient = Util.getOkHttpClient();
                    JSONObject jSONObject = new JSONObject();
                    int i = 0;
                    while (true) {
                        if (i < strArr.length) {
                            jSONObject.put(strArr[i], strArr[i + 1]);
                            i += 2;
                        } else {
                            RequestBody create = RequestBody.create(MediaType.parse("application/json"), jSONObject.toString());
                            Request.Builder builder = new Request.Builder();
                            final String string = okHttpClient.newCall(builder.url("https://siaga-satu.firebaseio.com/" + str + ".json").put(create).build()).execute().body().string();
                            BaseActivity.this.runLater(new Runnable() {
                                public void run() {
                                    if (firebaseListener != null) {
                                        firebaseListener.onComplete(string);
                                    }
                                }
                            });
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    public void update(final FirebaseListener firebaseListener, final String str, final String... strArr) {
        run(new Runnable() {
            public void run() {
                try {
                    OkHttpClient okHttpClient = Util.getOkHttpClient();
                    JSONObject jSONObject = new JSONObject();
                    int i = 0;
                    while (true) {
                        if (i < strArr.length) {
                            jSONObject.put(strArr[i], strArr[i + 1]);
                            i += 2;
                        } else {
                            RequestBody create = RequestBody.create(MediaType.parse("application/json"), jSONObject.toString());
                            Request.Builder builder = new Request.Builder();
                            final String string = okHttpClient.newCall(builder.url("https://siaga-satu.firebaseio.com/" + str + ".json").put(create).build()).execute().body().string();
                            BaseActivity.this.runLater(new Runnable() {
                                public void run() {
                                    if (firebaseListener != null) {
                                        firebaseListener.onComplete(string);
                                    }
                                }
                            });
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    public void checkPermissions(String[] strArr, PermissionListener permissionListener) {
        this.listener = permissionListener;
        if (Build.VERSION.SDK_INT >= 23) {
            int length = strArr.length;
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = true;
                    break;
                } else if (checkSelfPermission(strArr[i]) != PackageManager.PERMISSION_GRANTED) {
                    break;
                } else {
                    i++;
                }
            }
            if (!z) {
                requestPermissions(strArr, 1);
            } else if (permissionListener != null) {
                permissionListener.onPermissionGranted();
            }
        } else if (permissionListener != null) {
            permissionListener.onPermissionGranted();
        }
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    public File createImageFile() {
        try {
            return Util.createImageFile(this);
        } catch (Exception e) {
            return null;
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        boolean z = true;
        if (i == 1) {
            int length = iArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                } else if (iArr[i2] != 0) {
                    z = false;
                    break;
                } else {
                    i2++;
                }
            }
            if (z) {
                PermissionListener permissionListener = this.listener;
                if (permissionListener != null) {
                    permissionListener.onPermissionGranted();
                    return;
                }
                return;
            }
            PermissionListener permissionListener2 = this.listener;
            if (permissionListener2 != null) {
                permissionListener2.onPermissionDenied();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation(final LocationUpdateListener locationUpdateListener) {
        Location lastKnownLocation;
        this.newLocationReceived = false;
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
           Location lastKnownLocation2 = locationManager.getLastKnownLocation("gps");
            if (lastKnownLocation2 == null) {
                lastKnownLocation = locationManager.getLastKnownLocation("network");
            } else {
                lastKnownLocation = lastKnownLocation2;
            }
            if (lastKnownLocation == null) {
                locationManager.requestLocationUpdates("gps", 0, 0.0f, new LocationListener() {
                    public void onProviderDisabled(String str) {
                    }

                    public void onProviderEnabled(String str) {
                    }

                    public void onStatusChanged(String str, int i, Bundle bundle) {
                    }

                    public void onLocationChanged(Location location) {
                        if (!BaseActivity.this.newLocationReceived) {
                            BaseActivity.this.newLocationReceived = true;
                            if (location != null && locationUpdateListener != null) {
                                locationUpdateListener.onLocationUpdate(location.getLatitude(), location.getLongitude());
                            }
                        }
                    }
                });
                locationManager.requestLocationUpdates("network", 0, 0.0f, new LocationListener() {
                    public void onProviderDisabled(String str) {
                    }

                    public void onProviderEnabled(String str) {
                    }

                    public void onStatusChanged(String str, int i, Bundle bundle) {
                    }

                    public void onLocationChanged(Location location) {
                        if (!BaseActivity.this.newLocationReceived) {
                            BaseActivity.this.newLocationReceived = true;
                            if (location != null && locationUpdateListener != null) {
                                locationUpdateListener.onLocationUpdate(location.getLatitude(), location.getLongitude());
                            }
                        }
                    }
                });
            } else if (!this.newLocationReceived) {
                this.newLocationReceived = true;
                if (lastKnownLocation != null && locationUpdateListener != null) {
                    locationUpdateListener.onLocationUpdate(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                }
            }
        } catch (Exception e) {
        }
    }

    public String getDeviceID() {
        return Settings.Secure.getString(getContentResolver(), "android_id");
    }

    public void getKeyHash(String str) {
        String str2 = "";
        try {
            for (Signature byteArray : getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES).signatures) {
                MessageDigest instance = MessageDigest.getInstance(str);
                instance.update(byteArray.toByteArray());
                byte[] digest = instance.digest();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    if (i != 0) {
                        sb.append(":");
                    }
                    String hexString = Integer.toHexString(digest[i] & 0xFF);
                    if (hexString.length() == 1) {
                        sb.append("0");
                    }
                    sb.append(hexString);
                }
                str2 = str2 + sb.toString();
            }
        } catch (Exception e) {
        }
    }
}
