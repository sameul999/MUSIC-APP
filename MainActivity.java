package com.example.musicapp;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private MediaPlayer player;
    private EditText urlInput;
    private TextView status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 60, 40, 40);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("Online Music Player");
        title.setTextSize(24);
        layout.addView(title);

        urlInput = new EditText(this);
        urlInput.setHint("Direct MP3 or audio URL");
        urlInput.setSingleLine(true);
        layout.addView(urlInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        Button play = new Button(this);
        play.setText("Play");
        layout.addView(play);

        Button pause = new Button(this);
        pause.setText("Pause / Resume");
        layout.addView(pause);

        status = new TextView(this);
        status.setText("Enter a direct audio link");
        layout.addView(status);

        play.setOnClickListener(v -> playAudio());

        pause.setOnClickListener(v -> {
            if (player == null) return;
            try {
                if (player.isPlaying()) {
                    player.pause();
                    status.setText("Paused");
                } else {
                    player.start();
                    status.setText("Playing");
                }
            } catch (IllegalStateException e) {
                status.setText("Audio is not ready");
            }
        });

        setContentView(layout);
    }

    private void playAudio() {
        String url = urlInput.getText().toString().trim();
        if (!url.startsWith("https://")) {
            Toast.makeText(this, "Enter an HTTPS audio link", Toast.LENGTH_SHORT).show();
            return;
        }

        releasePlayer();
        status.setText("Loading...");

        try {
            player = new MediaPlayer();
            player.setDataSource(url);
            player.setOnPreparedListener(mp -> {
                mp.start();
                status.setText("Playing");
            });
            player.setOnCompletionListener(mp -> status.setText("Finished"));
            player.setOnErrorListener((mp, what, extra) -> {
                status.setText("Could not play this link");
                return true;
            });
            player.prepareAsync();
        } catch (Exception e) {
            releasePlayer();
            status.setText("Could not open this link");
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }
}
