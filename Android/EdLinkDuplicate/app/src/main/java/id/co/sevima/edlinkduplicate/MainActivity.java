package id.co.sevima.edlinkduplicate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        String email = readEncrypted("email", "");
        String password = readEncrypted("password", "");
        if (email.trim().equals("") || password.trim().equals("")) {
            startActivity(new Intent(this, IntroActivity.class));
            finish();
        } else {
            final ProgressDialog dialog = createDialog(R.string.logging_in);
            dialog.show();
            post(new Listener() {

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        int responseCode = getInt(obj, "response_code", 0);
                        if (responseCode == 1) {
                            Constants.USER_ID = getInt(obj, "id", 0);
                            int emailVerified = getInt(obj, "email_verified", 0);
                            if (emailVerified == 0) {
                                post(new Listener() {

                                    @Override
                                    public void onResponse(String response) {
                                        dialog.dismiss();
                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            String verificationCode = getString(obj, "verification_code", "");
                                            startActivity(new Intent(MainActivity.this, VerifyActivity.class)
                                                    .putExtra("email", email)
                                                    .putExtra("verification_code", verificationCode));
                                            finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, Constants.API_URL+"/user/send_verification_code", "email", email);
                            } else {
                                dialog.dismiss();
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            }
                        } else {
                            dialog.dismiss();
                            startActivity(new Intent(MainActivity.this, IntroActivity.class));
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, Constants.API_URL+"/user/login", "email", email, "password", password);
        }
    }
}