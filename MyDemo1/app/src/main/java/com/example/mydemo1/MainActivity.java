package com.example.mydemo1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.nfc.Tag;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.nex3z.fingerpaintview.FingerPaintView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="Dolphin";

    private static final String[] label = new String[]{"0","1","2","3","4","5","6","7","8","9"};
    private ArrayAdapter<String> adapter1,adapter2;
    private String currentLabel1,currentLabel2;

    private Classifier mClassifier;
    public static final int DIM_IMG_SIZE_HEIGHT = 28;
    public static final int DIM_IMG_SIZE_WIDTH = 28;

    @BindView(R.id.tv_time)TextView mTvTime;
    @BindView(R.id.tv_probability)TextView mTvProbability;
    @BindView(R.id.tv_sameOrnot)TextView mSame;
    @BindView(R.id.iv_1) ImageView imageView1;
    @BindView(R.id.iv_2) ImageView imageView2;
    @BindView(R.id.iv_new)ImageView imageViewNew;
    @BindView(R.id.spiner_1) Spinner spinner1;
    @BindView(R.id.spiner_2) Spinner spinner2;
    Bitmap bitmap_1,bitmap_2;
    Bitmap newBitmap_1,newBitmap_2;
    Bitmap showBitmap_1,showBitmap_2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initSpinner();
        init();
    }

    @OnClick(R.id.btn_detect)
    void onDetectClick(){
        if(mClassifier == null)
        {
            Log.e(TAG,"Fail in create Classfier!");
            return;
        }
        else if(imageView1 == null)
        {
            Toast.makeText(this, R.string.no_pict, Toast.LENGTH_SHORT).show();
            Log.e(TAG,"Image is null!");
            return;
        }
        Result result = mClassifier.classify(bitmap_1,bitmap_2);
        showResult(result);
    }

    private void init()
    {
        try{
            Log.i(TAG,"Before Classifier to init!");
            mClassifier = new Classifier(this);
//            bitmap_1 = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_1);
//            bitmap_2 = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_4);
//            newBitmap_1 = scaleBitmap(bitmap_1,DIM_IMG_SIZE_WIDTH/(float)bitmap_1.getWidth(),DIM_IMG_SIZE_HEIGHT/(float)bitmap_1.getHeight());
//            newBitmap_2 = scaleBitmap(bitmap_2,DIM_IMG_SIZE_WIDTH/(float)bitmap_2.getWidth(),DIM_IMG_SIZE_HEIGHT/(float)bitmap_2.getHeight());
//            showBitmap_1 = scaleBitmap(bitmap_1,(float)2.0,(float)2.0);
//            showBitmap_2 = scaleBitmap(bitmap_2,(float)2.0,(float)2.0);
//            imageView1.setImageBitmap(showBitmap_1);
//            imageView2.setImageBitmap(showBitmap_2);
        }catch (IOException e){
            Toast.makeText(this, R.string.failed_to_create_classifier, Toast.LENGTH_LONG).show();
            Log.e(TAG,"init():Failed to create tflite model",e);
        }
    }
    private void initPicture(String label,int flag)
    {
        Bitmap bitmap;
        switch (label){
            case "0":bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_0);break;
            case "1":bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_1);break;
            case "2":bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_2);break;
            case "3":bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_3);break;
            case "4":bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_4);break;
            case "5":bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_5);break;
            case "6":bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_6);break;
            case "7":bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_7);break;
            case "8":bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_8);break;
            case "9":bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_9);break;
            default:bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test_image_0);
        }
        if(flag==1){
            bitmap_1=bitmap;
            showBitmap_1 = scaleBitmap(bitmap_1,(float)2.0,(float)2.0);
            imageView1.setImageBitmap(showBitmap_1);
        }
        else {
            bitmap_2=bitmap;
            showBitmap_2 = scaleBitmap(bitmap_2,(float)2.0,(float)2.0);
            imageView2.setImageBitmap(showBitmap_2);
        }



    }

    private void initSpinner()
    {
        adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,label);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,label);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                currentLabel1 = label[arg2];
                Log.i(TAG,"Current Selected1:"+currentLabel1);
                arg0.setVisibility(View.VISIBLE);
                initPicture(currentLabel1,1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                currentLabel1=label[0];
                Log.i(TAG,"Current Selected1:"+currentLabel1);
            }

        });

        spinner2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                currentLabel2 = label[arg2];
                Log.i(TAG,"Current Selected2:"+currentLabel2);
                arg0.setVisibility(View.VISIBLE);
                initPicture(currentLabel2,2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                currentLabel2=label[0];
                Log.i(TAG,"Current Selected2:"+currentLabel2);
            }

        });

    }






    private Bitmap scaleBitmap(Bitmap bitmap,float width, float height)
    {
        Matrix matrix=new Matrix();
        matrix.postScale(width,height);
        Bitmap newBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return newBitmap;
    }

    private void showResult(Result result)
    {
        mTvTime.setText(String.format("%1$d ms", result.getTimeCost()));
        mTvProbability.setText(String.valueOf(result.getProbability()));
        mSame.setText(String.valueOf(result.getmSame()));
    }
}
