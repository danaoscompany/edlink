package id.co.sevima.edlinkduplicate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.loader.content.CursorLoader;

import com.scottyab.aescrypt.AESCrypt;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.json.JSONObject;

/* renamed from: com.dn.silentalarmadmin.Util */
public class Util {
    private static Thread currentThread;

    /* renamed from: com.dn.silentalarmadmin.Util$Listener */
    public interface Listener {
        void onResponse(String str);
    }

    public static void show(final Context context, final String str) {
        runLater(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void showSuccess(final Context context, final String message) {
        runLater(new Runnable() {

            @Override
            public void run() {
                View view = LayoutInflater.from(context).inflate(R.layout.toast_success, null);
                TextView messageView = view.findViewById(R.id.message);
                messageView.setText(message);
                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.show();
            }
        });
    }

    public static void showError(final Context context, final String message) {
        runLater(new Runnable() {

            @Override
            public void run() {
                View view = LayoutInflater.from(context).inflate(R.layout.toast_error, null);
                TextView messageView = view.findViewById(R.id.message);
                messageView.setText(message);
                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.show();
            }
        });
    }

    public static String getString(JSONObject jSONObject, String str, String str2) {
        try {
            String string = jSONObject.getString(str);
            return (string == null || string.equals("null") || string.equals("NULL")) ? str2 : string;
        } catch (Exception e) {
            e.printStackTrace();
            return str2;
        }
    }

    public static int getInt(JSONObject jSONObject, String str, int i) {
        try {
            String string = jSONObject.getString(str);
            if (string == null || string.equals("null") || string.equals("NULL")) {
                return i;
            }
            return Integer.parseInt(string);
        } catch (Exception e) {
            e.printStackTrace();
            return i;
        }
    }

    public static double getDouble(JSONObject jSONObject, String str, double d) {
        try {
            String string = jSONObject.getString(str);
            if (string == null || string.equals("null") || string.equals("NULL")) {
                return d;
            }
            return Double.parseDouble(string);
        } catch (Exception e) {
            e.printStackTrace();
            return d;
        }
    }

    public static String read(Context context, String str, String str2) {
        return context.getSharedPreferences("data", 0).getString(str, str2);
    }

    public static String readEncrypted(Context context, String str, String str2) {
        String string = context.getSharedPreferences("data", 0).getString(encrypt(str), "");
        if (string != null && !string.trim().equals("")) {
            try {
                return decrypt(string);
            } catch (Exception e) {
            }
        }
        return str2;
    }

    public static int read(Context context, String str, int i) {
        return context.getSharedPreferences("data", 0).getInt(str, i);
    }

    public static int readEncrypted(Context context, String str, int i) {
        String string = context.getSharedPreferences("data", 0).getString(encrypt(str), "");
        if (string != null && !string.trim().equals("")) {
            try {
                return Integer.parseInt(decrypt(string));
            } catch (Exception e) {
            }
        }
        return i;
    }

    public static void write(Context context, String str, int i) {
        SharedPreferences.Editor edit = context.getSharedPreferences("data", 0).edit();
        edit.putInt(str, i);
        edit.commit();
    }

    public static void writeEncrypted(Context context, String str, String str2) {
        SharedPreferences.Editor edit = context.getSharedPreferences("data", 0).edit();
        try {
            edit.putString(encrypt(str), encrypt(str2));
        } catch (Exception e) {
        }
        edit.commit();
    }

    public static String encrypt(String str) {
        try {
            return AESCrypt.encrypt(Build.MODEL, str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decrypt(String str) {
        try {
            return AESCrypt.decrypt(Build.MODEL, str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void write(Context context, String str, String str2) {
        SharedPreferences.Editor edit = context.getSharedPreferences("data", 0).edit();
        edit.putString(str, str2);
        edit.commit();
    }

    public static void writeEncrypted(Context context, String str, int i) {
        SharedPreferences.Editor edit = context.getSharedPreferences("data", 0).edit();
        String encrypt = encrypt(str);
        edit.putString(encrypt, encrypt("" + i));
        edit.commit();
    }

    public static Thread run(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    public static void runLater(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
        okHttpClient.interceptors().add(new Interceptor() {
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request request = chain.request();
                Response proceed = chain.proceed(request);
                int i = 0;
                while (!proceed.isSuccessful() && i < 3) {
                    i++;
                    proceed = chain.proceed(request);
                }
                return proceed;
            }
        });
        try {
            TrustManager[] trustManagerArr = {new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            SSLContext instance = SSLContext.getInstance("SSL");
            instance.init((KeyManager[]) null, trustManagerArr, new SecureRandom());
            okHttpClient.setSslSocketFactory(instance.getSocketFactory());
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String str, SSLSession sSLSession) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
        return okHttpClient;
    }

    public static void get(Context context, final Listener listener, final String str) {
        run(new Runnable() {
            public void run() {
                try {
                    final String string = Util.getOkHttpClient().newCall(new Request.Builder().url(str).build()).execute().body().string();
                    Util.runLater(new Runnable() {
                        public void run() {
                            if (listener != null) {
                                listener.onResponse(string);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Thread post(final Context context, final Listener listener, final String str, final String... strArr) {
        Thread run = run(new Runnable() {
            public void run() {
                try {
                    OkHttpClient okHttpClient = Util.getOkHttpClient();
                    MultipartBuilder type = new MultipartBuilder().type(MultipartBuilder.FORM);
                    int i = 0;
                    while (true) {
                        if (i >= strArr.length) {
                            final String response = okHttpClient.newCall(new Request.Builder().url(str).post(type.build()).build()).execute().body().string();
                            Util.runLater(new Runnable() {
                                public void run() {
                                    if (listener != null) {
                                        listener.onResponse(response);
                                    }
                                }
                            });
                            return;
                        } else if (strArr[i].equals("file")) {
                            String[] strArr2 = strArr;
                            type.addFormDataPart("file", strArr2[i + 1], RequestBody.create(MediaType.parse(strArr2[i + 2]), new File(strArr[i + 3])));
                            i += 4;
                        } else {
                            String[] strArr3 = strArr;
                            type.addFormDataPart(strArr3[i], strArr3[i + 1]);
                            i += 2;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Util.runLater(new Runnable() {
                        public void run() {
                            Util.show(context, e.getMessage());
                        }
                    });
                }
            }
        });
        currentThread = run;
        return run;
    }

    public static void postInsert(Context context, Listener listener, String str, String str2, String... strArr) {
        final String[] strArr2 = strArr;
        final String str3 = str2;
        final String str4 = str;
        final Listener listener2 = listener;
        final Context context2 = context;
        run(new Runnable() {
            public void run() {
                try {
                    OkHttpClient okHttpClient = Util.getOkHttpClient();
                    JSONObject jSONObject = new JSONObject();
                    int i = 0;
                    while (true) {
                        String[] strArr = strArr2;
                        if (i < strArr.length) {
                            jSONObject.put(strArr[i], strArr[i + 1]);
                            i += 2;
                        } else {
                            final String string = okHttpClient.newCall(new Request.Builder().url(str4).post(new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("table_name", str3).addFormDataPart("data", jSONObject.toString()).build()).build()).execute().body().string();
                            Util.runLater(new Runnable() {
                                public void run() {
                                    if (listener2 != null) {
                                        listener2.onResponse(string);
                                    }
                                }
                            });
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Util.runLater(new Runnable() {
                        public void run() {
                            Util.show(context2, e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public static InputStream trustedCertificatesInputStream(Context context) throws IOException {
        return context.getAssets().open("siapsiaga.cer");
    }

    public static X509TrustManager trustManagerForCertificates(InputStream inputStream) throws GeneralSecurityException {
        Collection<? extends Certificate> generateCertificates = CertificateFactory.getInstance("X.509").generateCertificates(inputStream);
        if (!generateCertificates.isEmpty()) {
            char[] charArray = "password".toCharArray();
            KeyStore newEmptyKeyStore = newEmptyKeyStore(charArray);
            int i = 0;
            for (Certificate certificateEntry : generateCertificates) {
                newEmptyKeyStore.setCertificateEntry(Integer.toString(i), certificateEntry);
                i++;
            }
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).init(newEmptyKeyStore, charArray);
            TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            instance.init(newEmptyKeyStore);
            TrustManager[] trustManagers = instance.getTrustManagers();
            if (trustManagers.length == 1 && (trustManagers[0] instanceof X509TrustManager)) {
                return (X509TrustManager) trustManagers[0];
            }
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        throw new IllegalArgumentException("expected non-empty set of trusted certificates");
    }

    public static KeyStore newEmptyKeyStore(char[] cArr) throws GeneralSecurityException {
        try {
            KeyStore instance = KeyStore.getInstance(KeyStore.getDefaultType());
            instance.load((InputStream) null, cArr);
            return instance;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static ProgressDialog createDialog(Context context, String str) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(str);
        return progressDialog;
    }

    public static ProgressDialog createDialog(Context context, int i) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(i));
        return progressDialog;
    }

    public static void dismissDialog(final ProgressDialog progressDialog) {
        runLater(new Runnable() {
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public static String formatPhoneNumber(String str) {
        return str.replaceAll("[^0-9]+", "");
    }

    public static String formatPrice(String str) {
        String replaceAll = str.replaceAll("[^0-9]+", "");
        return "Rp " + new DecimalFormat("#,###").format((long) Integer.parseInt(replaceAll)) + ",00";
    }

    public static String removeNonDigitValues(String str) {
        return str.replaceAll("[^0-9]+", "");
    }

    public static boolean isPackageInstalled(PackageManager packageManager, String str) {
        try {
            packageManager.getPackageInfo(str, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static int getRadioButtonIndex(RadioGroup radioGroup) {
        return radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()));
    }

    public static File createImageFile(Context context) {
        try {
            String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            return File.createTempFile("JPEG_" + format + "_", ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getWidth() / 2), (float) (bitmap.getWidth() / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    public static Bitmap textToBitmap(String str, float f, int i) {
        Paint paint = new Paint(1);
        paint.setTextSize(f);
        paint.setColor(i);
        paint.setTextAlign(Paint.Align.LEFT);
        float f2 = -paint.ascent();
        Bitmap createBitmap = Bitmap.createBitmap((int) (paint.measureText(str) + 0.0f), (int) (paint.descent() + f2 + 0.0f), Bitmap.Config.ARGB_8888);
        new Canvas(createBitmap).drawText(str, 0.0f, f2, paint);
        return createBitmap;
    }

    public static void showLog(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("sms:"));
        intent.putExtra("sms_body", str);
        context.startActivity(intent);
    }

    public static void log(String str) {
        Log.e(Build.MODEL, str);
    }

    public static String getExtension(String str) {
        return str.substring(str.lastIndexOf("."), str.length());
    }

    public static String getMediaType(Context context, Uri uri) {
        return context.getContentResolver().getType(uri);
    }

    public static String getRealPath(Context context, Uri fileUri) {
        String realPath;
        if (Build.VERSION.SDK_INT < 11) {
            realPath = getRealPathFromURI_BelowAPI11(context, fileUri);
        }
        else if (Build.VERSION.SDK_INT < 19) {
            realPath = getRealPathFromURI_API11to18(context, fileUri);
        }
        else {
            realPath = getRealPathFromURI_API19(context, fileUri);
        }
        return realPath;
    }

    public static String getRealPath2(Context ctx, Uri uri) {
        String path = null;
        String[] projections = { MediaStore.MediaColumns.DATA };
        Cursor c = ctx.getContentResolver().query(uri, projections, null, null, null);
        if (c.moveToFirst()) {
            path = c.getString(c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
        }
        c.close();
        return path;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;
        CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = 0;
        String result = "";
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return result;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getFileNameFromURI(Context ctx, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
