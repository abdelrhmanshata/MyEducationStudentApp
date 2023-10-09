package com.shata.myeducationstudentapp.UI.Lecuter;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.shata.myeducationstudentapp.Model.ModelLecuter.ModelLecuter;
import com.shata.myeducationstudentapp.R;
import com.shata.myeducationstudentapp.YouTubeConfig;
import com.shata.myeducationstudentapp.databinding.ActivityLectuerBinding;


public class LectuerActivity extends YouTubeBaseActivity {

    ActivityLectuerBinding lecuterBinding;
    YouTubePlayerView mYouTubePlayerView;
    YouTubePlayer.OnInitializedListener mOnInitializedListener;

    ModelLecuter lecuter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lecuterBinding = ActivityLectuerBinding.inflate(getLayoutInflater());
        setContentView(lecuterBinding.getRoot());

        lecuter = (ModelLecuter) getIntent().getSerializableExtra("ModelLecuter");

        mYouTubePlayerView = findViewById(R.id.youTubePLayer);
        mOnInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(getIDYouTubeVideo(lecuter.getLecURL()));
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
        lecuterBinding.FABPlayVideo.setOnClickListener(v -> {
            mYouTubePlayerView.initialize(YouTubeConfig.getApiKey(), mOnInitializedListener);
            lecuterBinding.FABPlayVideo.setVisibility(View.GONE);
        });

        if (lecuter.getLecUrlPdf().isEmpty()) {
            lecuterBinding.layoutDownLoad.setVisibility(View.GONE);
        }

        lecuterBinding.lectuerNameTV.setText(lecuter.getLecName() + "");
        lecuterBinding.lectuerLinkTV.setText(lecuter.getLecURL() + "");
        lecuterBinding.lectuerDateTV.setText(lecuter.getLecDate() + "");
        lecuterBinding.lecPdfNameDownLoad.setText(lecuter.getLecName() + ".pdf");

        lecuterBinding.downLoadPdf.setOnClickListener(v -> {
            downloadPdfLectuer(lecuter);
        });
    }

    @SuppressLint("IntentReset")
    private void downloadPdfLectuer(ModelLecuter lec) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("application/pdf");
        intent.setData(Uri.parse(lec.getLecUrlPdf()));
        startActivity(intent);
    }

    public String getIDYouTubeVideo(String url) {
        if (url.contains("https://youtu.be/")) {
            url = url.substring(url.lastIndexOf("/") + 1);
            return url;
        } else if (url.contains("=")) {
            url = url.substring(url.lastIndexOf("=") + 1);
            return url;
        }
        return url;

    }
}