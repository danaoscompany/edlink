package id.co.sevima.edlinkduplicate.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import id.co.sevima.edlinkduplicate.Constants;
import id.co.sevima.edlinkduplicate.HomeActivity;
import id.co.sevima.edlinkduplicate.MainActivity;
import id.co.sevima.edlinkduplicate.R;

public class UserFragment extends Fragment {
    View view;
    HomeActivity activity;
    LinearLayout logout;
    TextView versionView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        logout = view.findViewById(R.id.logout);
        versionView = view.findViewById(R.id.version);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (HomeActivity)getActivity();
        try {
            versionView.setText(activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setMessage(R.string.confirmation_2)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.writeEncrypted("email", "");
                                activity.writeEncrypted("password", "");
                                Constants.USER_ID = 0;
                                startActivity(new Intent(activity, MainActivity.class));
                                activity.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .create()
                        .show();
            }
        });
    }
}
