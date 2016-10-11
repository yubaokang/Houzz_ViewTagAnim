# Houzz_ViewTagAnim
动态添加view，根据加速度传感器旋转，排动,类似于Houzz app的飘带动画

[blog](http://www.jianshu.com/p/e4cbfe58ade6)

先看效果图：飘带会随着手机的旋转进行飘动
![飘带.gif](http://upload-images.jianshu.io/upload_images/1874706-f416a3cef4e0d9f9.gif?imageMogr2/auto-orient/strip)
代码量比较少，所有就都写一块啦，哈哈！直接贴代码了，说明直接看注释就OK。
```java
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.yubaokang.houzz_viewtaganim.MainActivity">

    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@mipmap/bg"/>
</RelativeLayout>
```
```java
/**
 * 锚点对象
 * Created by Hank on 2016/4/22.
 */
public class MarkDataList {

    private int id;
    private int x;
    private int y;

    public MarkDataList(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
```

```java
/**
 * https://gitlab.com/yubaokang/Houzz_ViewTagAnim
 * 动态添加view，根据加速度传感器旋转，摆动,类似于Houzz app的飘带动画
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
        //这里最后一个参数（SensorManager.SENSOR_DELAY_FASTEST）可以修改成SENSOR_DELAY_NORMAL或者其他的来调整传感器灵敏度，
        // 然后去掉onSensorChanged()方法中thisTime和lastTime的时间差判断，
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
        //改变传感器灵敏度，可以把这里的时间差判断去掉
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
            localLayoutParams.setMargins((int) (anchorData.getX() * scaleX) - tagWidth / 2, (int) (anchorData.getY() * scaleY), 0, 0);
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
     * 根据加速度传感器的旋转方向x,y，计算得到旋转角度，包含正负，正负代表旋转的方向
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
```
