package com.samsung.android.sdk.accessory.example.helloaccessory.consumer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class ExerciseExecution extends AppCompatActivity {

    private Bundle _bundle;
    private Button _skip;
    private static TextView _showScore;
    private int _currentHalfRepeats = 0;
    private int _exerciseRepeats = 10;
    private double _detectionToleranceMin = 1.0;
    private double _detectionToleranceMax = 10.0;
    private double _additionTolerance = 0.0001;
    private double[] _lastVector = new double[3];
    private double[] _newVector = new double[3];
    static boolean active = false;
    static int trainingId;
    public static TextView _speedTextview;
    public static ProgressBar _progressbar;
    TextView _exerciseText;
    long timeold = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_execution);
        _bundle = getIntent().getExtras();
        trainingId = (int)_bundle.get("training");
        final FitnessExercise exercise = ManagementSystem.getInstance().getFitnessExercise((String) _bundle.get("exercise"));
        if (exercise != null) {
            VideoView mVideoView  = (VideoView)findViewById(R.id.videoView);
            mVideoView.setVideoURI(exercise.getUri());
            mVideoView.setMediaController(new MediaController(ExerciseExecution.this));
            mVideoView.seekTo(100);
        }
        _speedTextview = (TextView)findViewById(R.id.textView3);
        _exerciseText = (TextView)findViewById(R.id.textView2);
        _progressbar = (ProgressBar)findViewById(R.id.progressBar);
        _skip = (Button) findViewById(R.id.button);
        _skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exerciseFinished();
            }
        });
        _showScore = (TextView) findViewById(R.id.textView5);
        _exerciseText.setText(exercise.getName());
        _currentHalfRepeats = 0;

        _progressbar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        timeold = System.currentTimeMillis();

    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    private void exerciseFinished(){
        int id = trainingId;//(int)_bundle.get("training");
        id--;
        Intent myIntent;
        if(0 < id) {
            myIntent = new Intent(ExerciseExecution.this, Exercise.class);
            myIntent.putExtra("exercise", ManagementSystem.getInstance().getFitnessExercise(id).getName());
            myIntent.putExtra("training", id);
        }else{
            if(id < 0) {
                myIntent = new Intent(ExerciseExecution.this, FreeTraining.class);
            }else {
                myIntent = new Intent(ExerciseExecution.this, MainActivity.class);
            }
        }
        ExerciseExecution.this.startActivity(myIntent);
    }

    public void evaluate(double x, double y, double z){
        if(_detectionToleranceMin < x*x+y*y+z*z && x*x+y*y+z*z < _detectionToleranceMax){
            _newVector[0] = x;
            _newVector[1] = y;
            _newVector[2] = z;
            normalize(_newVector);

            if(_lastVector[0] < 0.1 && _lastVector[1] < 0.1 && _lastVector[2] < 0.1){
                _lastVector[0] = _newVector[0];
                _lastVector[1] = _newVector[1];
                _lastVector[2] = _newVector[2];
            }
            if(_lastVector[0] + _newVector[0] +  _lastVector[1] + _newVector[1] + _lastVector[2] + _newVector[2] < _additionTolerance){
                //_lastVector[0] = _newVector[0];
                //_lastVector[1] = _newVector[1];
                //_lastVector[2] = _newVector[2];
                counter();
            }
            _lastVector[0] = _newVector[0];
            _lastVector[1] = _newVector[1];
            _lastVector[2] = _newVector[2];
        }
    }

    private void counter(){
        _currentHalfRepeats++;
        long timenew = System.currentTimeMillis();
        timenew -= timeold;
        timeold = System.currentTimeMillis();
        if(timenew > 1000) {
            //zu langsam
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _progressbar.setProgress(2);
                    _progressbar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    _speedTextview.setText("zu langsam!");
                }
            });
        }
        else if(timenew < 500) {
            //zu schnell
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _progressbar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    _progressbar.setProgress(100);
                    _speedTextview.setText("zu schnell!");
                }
            });
        }
        else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _progressbar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    _progressbar.setProgress(50);
                    _speedTextview.setText("optimal");
                }
            });
        }
        //if(0 < (int)_bundle.get("training"))
            if(0 < trainingId)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _showScore.setText(_currentHalfRepeats/2 + " / " + _exerciseRepeats);
                }
            });
            if(_exerciseRepeats <= _currentHalfRepeats/2){
                _currentHalfRepeats = 0;
                exerciseFinished();
            }
        }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _showScore.setText(String.valueOf(_currentHalfRepeats/2));
                    }
                });
        }
    }

    /**
     * the vector has to be an int array with length 3
     * @param vector
     */
    private void normalize(double[] vector) {
        double length = Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2]);
        if (length != 0) {
            vector[0] = vector[0] / length;
            vector[1] = vector[1] / length;
            vector[2] = vector[2] / length;
        }
    }
}