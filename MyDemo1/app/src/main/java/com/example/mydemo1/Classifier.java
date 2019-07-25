package com.example.mydemo1;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.*;

public class Classifier {
    private static final String TAG = "Dolphin";
    private static final String MODEL_PATH = "siamese.tflite";

    private static final int DIM_BATCH_SIZE = 1;
    public static final int DIM_IMG_SIZE_HEIGHT = 28;
    public static final int DIM_IMG_SIZE_WIDTH = 28;
    private static final int DIM_PIXEL_SIZE = 1;
    private static final int CATEGORY_COUNT = 10;

    private final Interpreter mInterpreter;
    private ByteBuffer mImgData1,mImgData2;
    private final int[] mImagePixels = new int[DIM_IMG_SIZE_HEIGHT * DIM_IMG_SIZE_WIDTH];
    //private final float[][] mResult = new float[1][CATEGORY_COUNT];
    private float mResult[][] = new float[1][1];

    public Classifier(Activity activity) throws IOException
    {
        mInterpreter = new Interpreter(loadModelFile(activity));
        mImgData1 = ByteBuffer.allocateDirect(4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_HEIGHT * DIM_IMG_SIZE_WIDTH * DIM_PIXEL_SIZE);
        mImgData1.order(ByteOrder.nativeOrder());
        mImgData2 = ByteBuffer.allocateDirect(4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_HEIGHT * DIM_IMG_SIZE_WIDTH * DIM_PIXEL_SIZE);
        mImgData2.order(ByteOrder.nativeOrder());
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public Result classify(Bitmap bitmap1,Bitmap bitmap2)
    {
        //convertBitmapToByteBuffer(bitmap1);
        float [][]input0_0 = convertBitmapToFLoat(bitmap1);
        float [][]input0_1 = convertBitmapToFLoat(bitmap2);
        mImgData1=convertBitmapToByteBuffer(bitmap1);
        mImgData2=convertBitmapToByteBuffer(bitmap2);
        Object input1 = mImgData1;
        Object input2 = mImgData2;
        Object[] inputs = {input1,input2};
        Object[] inputs0 = {input0_0,input0_1};
        float [][] parsedOutput0 = new float[1][1];
        Map<Integer,Object> outputs =new HashMap<>();
        outputs.put(0,parsedOutput0);
        //mInterpreter.run(mImgData,mResult);
        long startTime = SystemClock.uptimeMillis();
        mInterpreter.runForMultipleInputsOutputs(inputs,outputs);
        long endTime = SystemClock.uptimeMillis();
        long timeCost = endTime-startTime;
        mResult = (float[][])outputs.get(0);
        return new Result(mResult[0],timeCost);
    }

    private float[][] convertBitmapToFLoat(Bitmap bitmap)
    {
        float[][] output = new float[DIM_IMG_SIZE_HEIGHT][DIM_IMG_SIZE_WIDTH];
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Log.i(TAG,"bitmap width:"+width);
        Log.i(TAG,"bitmap height:"+height);
        float scaleWidth = ((float)DIM_IMG_SIZE_WIDTH)/bitmap.getWidth();
        float scaleHeight = ((float)DIM_IMG_SIZE_HEIGHT)/bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap newbitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        Log.i(TAG,"width"+newbitmap.getWidth()+"height"+newbitmap.getHeight());
        newbitmap.getPixels(mImagePixels,0,newbitmap.getWidth(),0,0,newbitmap.getWidth(),newbitmap.getHeight());

        int pixel = 0;
        int k=0;
        for(int i=0;i<DIM_IMG_SIZE_WIDTH;i++)
            for(int j=0;j<DIM_IMG_SIZE_HEIGHT;j++)
            {
                final int val = mImagePixels[pixel++];
                output[j][i]=(convertToGreyScale(val));
            }
        return output;
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap)
    {
        ByteBuffer mImgData;
        mImgData = ByteBuffer.allocateDirect(4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_HEIGHT * DIM_IMG_SIZE_WIDTH * DIM_PIXEL_SIZE);
        mImgData.order(ByteOrder.nativeOrder());
        mImgData.rewind();
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Log.i(TAG,"bitmap width:"+width);
        Log.i(TAG,"bitmap height:"+height);
        float scaleWidth = ((float)DIM_IMG_SIZE_WIDTH)/bitmap.getWidth();
        float scaleHeight = ((float)DIM_IMG_SIZE_HEIGHT)/bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap newbitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        Log.i(TAG,"width"+newbitmap.getWidth()+"height"+newbitmap.getHeight());
        newbitmap.getPixels(mImagePixels,0,newbitmap.getWidth(),0,0,newbitmap.getWidth(),newbitmap.getHeight());

        int pixel = 0;
        for(int i=0;i<DIM_IMG_SIZE_WIDTH;i++)
            for(int j=0;j<DIM_IMG_SIZE_HEIGHT;j++)
            {
                final int val = mImagePixels[pixel++];
                mImgData.putFloat(convertToGreyScale(val));
            }
        return mImgData;
    }

    private float convertToGreyScale(int color) {
        return (((color >> 16) & 0xFF) + ((color >> 8) & 0xFF) + (color & 0xFF)) / 3.0f / 255.0f;
    }
}
