package com.samsung.android.sdk.accessory.example.helloaccessory.consumer;

import android.net.Uri;

/**
 * Created by kevin on 19.07.2016.
 */
public class FitnessExercise {

    private int _id;
    private String _name, _startingPosition, _properExecution;
    private Uri _uri;

    private FitnessExercise(){}

    public FitnessExercise(int id, String name, String startingPosition, String properExecution, Uri uri){
        this._id = id;
        this._name = name;
        this._startingPosition = startingPosition;
        this._properExecution = properExecution;
        this._uri = uri;
    }

    public int getId(){ return this._id; }

    public String getName(){
        return this._name;
    }

    public String getStartingPosition(){
        return this._startingPosition;
    }

    public String getProperExecution(){
        return this._properExecution;
    }

    public Uri getUri(){ return this._uri; }
}