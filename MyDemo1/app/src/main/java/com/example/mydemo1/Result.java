package com.example.mydemo1;

import android.util.Log;


public class Result {
    private final long mTime;
    private final float mProbability;
    private final String mSame;
    private final double threshhold = 0.5;
    private static final String TAG = "Dolphin";


    public Result(float[] result,long timeCost) {
//        mNumber = argmax(result);
//        mProbability = result[mNumber];
        mTime = timeCost;
        mProbability = result[0];
        mSame = setSameOrNot(mProbability);
    }

    public long getTimeCost() {
        return mTime;
    }

    public float getProbability() {
        return mProbability;
    }

    public String getmSame(){return mSame;}


    private static int argmax(float[] probs) {
        int maxIdx = -1;
        float maxProb = 0.0f;
        for (int i = 0; i < probs.length; i++) {
            Log.i(TAG,"prob["+i+"]="+probs[i]);
            if (probs[i] > maxProb) {
                maxProb = probs[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    private String setSameOrNot(float probs){
        return probs>threshhold?"No":"Yes";
    }
}
