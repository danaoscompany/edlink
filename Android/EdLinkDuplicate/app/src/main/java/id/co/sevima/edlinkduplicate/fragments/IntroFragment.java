package id.co.sevima.edlinkduplicate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import id.co.sevima.edlinkduplicate.IntroActivity;
import id.co.sevima.edlinkduplicate.R;

public class IntroFragment extends Fragment {
    View view;
    IntroActivity activity;
    ImageView imgView;
    TextView titleView, descriptionView;
    int imgResource;
    int title, description;

    public IntroFragment(int imgResource, int title, int description) {
        this.imgResource = imgResource;
        this.title = title;
        this.description = description;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_intro, container, false);
        imgView = view.findViewById(R.id.img);
        titleView = view.findViewById(R.id.title);
        descriptionView = view.findViewById(R.id.description);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (IntroActivity)getActivity();
        imgView.setImageResource(imgResource);
        titleView.setText(title);
        descriptionView.setText(description);
    }
}
