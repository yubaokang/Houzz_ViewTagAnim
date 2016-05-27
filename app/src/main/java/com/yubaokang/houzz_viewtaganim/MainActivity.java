package com.yubaokang.houzz_viewtaganim;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * https://gitlab.com/yubaokang/Houzz_ViewTagAnim
 * 动态添加view，根据重力传感器旋转，摆动,类似于Houzz app的飘带动画
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private RelativeLayout container;
    private List<ImageView> imageViewList;

    long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = (RelativeLayout) findViewById(R.id.container);
        imageViewList = new ArrayList<>();
        initSensor();
        lastTime = System.currentTimeMillis();
    }


    @Override
    protected void onResume() {
        super.onResume();
        addView(container, getDatas());
    }

    /**
     * 初始化数据-模拟
     *
     * @return
     */
    public List<MarkDataList> getDatas() {
        List<MarkDataList> datas = new ArrayList<>();
        datas.add(new MarkDataList(1, 100, 100));
        datas.add(new MarkDataList(2, 200, 200));
        datas.add(new MarkDataList(3, 300, 300));
        return datas;
    }

    private SensorManager sensorMgr;
    private Sensor localSensor;

    //初始化传感器
    public void initSensor() {
        sensorMgr = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        localSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMgr.registerListener(this, localSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorMgr.unregisterListener(this, localSensor);//接触绑定传感器监听
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long thisTime = System.currentTimeMillis();
        if (thisTime - lastTime > 200) {
            lastTime = thisTime;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            anim(x, y);
        }
    }

    public void addView(RelativeLayout container, List<MarkDataList> markDataLists) {
        int tagWidth = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_material_tag).getWidth();
        int containerWidth = 1080;//父布局宽度
        int containerHeight = 1920;//父布局高度
        float scaleX = (containerWidth / 330.0F);//MarkDataList中的x坐标是根据330F个像素进行标记的
        float scaleY = (containerHeight / 585.0F);//MarkDataList中的y坐标是根据585个像素进行标记的
        for (final MarkDataList anchorData : markDataLists) {
            ImageView localImageView = new ImageView(this);
            localImageView.setBackgroundResource(R.mipmap.ic_material_tag);
            localImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "点击了飘带" + anchorData.getId(), Toast.LENGTH_SHORT).show();
                }
            });
            imageViewList.add(localImageView);
            RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            localLayoutParams.setMargins((int) (anchorData.getRelativeX() * scaleX) - tagWidth / 2, (int) (anchorData.getRelativeY() * scaleY), 0, 0);
            container.addView(localImageView, localLayoutParams);
        }
    }

    private float xOld;
    private float yOld;

    //左右各180度
    public void anim(float x, float y) {
        for (ImageView imageView : imageViewList) {
            imageView.startAnimation(getAnim(xOld, yOld, x, y));
        }
        xOld = x;
        yOld = y;
    }

    private RotateAnimation getAnim(float xOld, float yOld, float x, float y) {
        RotateAnimation localRotateAnimation = new RotateAnimation(getRotate(xOld, yOld), getRotate(x, y), Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.2F);
        localRotateAnimation.setDuration(1000);
        localRotateAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        localRotateAnimation.setInterpolator(new OvershootInterpolator());
        return localRotateAnimation;
    }

    /**
     * 根据重力传感器的旋转方向x,y，计算得到旋转角度，包含正负，正负代表旋转的方向
     *
     * @param x
     * @param y
     * @return
     */
    public float getRotate(float x, float y) {
        float rotate = 0;
        if (x <= 0) {
            if (y > 0) {
                rotate = 9 * x;
            } else {
                rotate = -9 * (9 - y);
            }
        } else {
            if (y >= 0) {
                rotate = 9 * x;
            } else {
                rotate = 9 * (9 - y);
            }
        }
        return rotate;
    }
}
