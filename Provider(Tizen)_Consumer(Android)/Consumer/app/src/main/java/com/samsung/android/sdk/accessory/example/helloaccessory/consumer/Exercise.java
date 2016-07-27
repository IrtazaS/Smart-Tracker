package com.samsung.android.sdk.accessory.example.helloaccessory.consumer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Exercise extends AppCompatActivity {

    TextView _headline, _description;
    VideoView _videoView;
    Button _start;
    public static boolean b_deviceconnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        Bundle bundle = getIntent().getExtras();
        final FitnessExercise exercise = ManagementSystem.getInstance().getFitnessExercise((String) bundle.get("exercise"));
        final int training = (int) bundle.get("training");
        if (exercise != null) {
            _headline = (TextView) findViewById(R.id.textView2);
            _headline.setText(exercise.getName());

            _description = (TextView) findViewById(R.id.textView4);
            _description.setText(Html.fromHtml("<b>Ausgangsposition:</b><br>" + exercise.getStartingPosition() + "<br><b>Richtige Ausführung:</b><br>" + exercise.getProperExecution()));
            _description.setMovementMethod(new ScrollingMovementMethod());

            _videoView  = (VideoView)findViewById(R.id.videoView);
            _videoView.setVideoURI(exercise.getUri());
            _videoView.setMediaController(new MediaController(Exercise.this));
            _videoView.seekTo(100);
            _videoView.start();

            _start = (Button) findViewById(R.id.button);
            _start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(b_deviceconnected) {
                        Intent myIntent = new Intent(Exercise.this, ExerciseExecution.class);
                        myIntent.putExtra("exercise", exercise.getName());
                        myIntent.putExtra("training", training);
                        //myIntent.putExtra("key",value);
                        Exercise.this.startActivity(myIntent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No Device Connected", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}