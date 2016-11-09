# BidirectionalSeekBar
### 一 效果图
![](http://upload-images.jianshu.io/upload_images/3344501-26ca5fce7048d85b.gif?imageMogr2/auto-orient/strip)
### 二 使用方法
> 由于新版jcenter强制添加组织还要收费貌似，所以改用jitpack了

![最新版本号](https://jitpack.io/v/kaxi4it/BidirectionalSeekBar.svg)<br>
1. 在你root的gradle中添加引用
```java
allprojects {
    repositories {
    ...
    maven { url "https://jitpack.io" }
    }
 }
```
然后在module的gradle中添加引用
```java
dependencies {
    compile 'com.github.kaxi4it:BidirectionalSeekBar:1.0'
}
```
2. 在你的布局文件中添加xml代码如：
```java
<com.guyj.BidirectionalSeekBar
    android:id="@+id/bSeekBar1"
    android:layout_width="300dp"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:layout_margin="20dp"
    app:ball_left_solid_color="@android:color/white"
    app:ball_left_stroke_color="@android:color/black"
    app:ball_right_solid_color="@android:color/holo_green_light"
    app:ball_right_stroke_color="@android:color/holo_green_dark"
    app:ball_radius_size="8dp"
    app:ball_stroke_size="1dp"
    app:text_left_num="566"
    app:text_right_num="820"
    app:text_min_unit="1"
    app:pb_height="3dp"
    app:pb_within_color="@android:color/holo_blue_bright"
    app:pb_without_color="@android:color/holo_red_light"
    app:ball_left_drawable="@mipmap/ic_launcher"
    app:ball_right_drawable="@mipmap/ic_launcher"
    />
```
ball_left_solid_color 代表左侧圆球的填充色<br>
ball_left_stroke_color 代表左侧圆球的边框颜色<br>
ball_right_solid_color 代表右侧圆球的填充色<br>
ball_right_stroke_color 代表右侧圆球的边框颜色<br>
ball_radius_size 圆球半径<br>
ball_stroke_size 圆球边框显示宽度<br>
text_left_num 进度条最小值 单位int<br>
text_right_num 进度条最大值 单位int<br>
text_min_unit 进度条可拖动的最小单位 单位int<br>
pb_height 进度条高度<br>
pb_within_color 双向选中部分的进度条颜色<br>
pb_without_color 双向未选中部分的进度条颜色<br>
ball_left_drawable 左侧圆球可以引用图片资源<br>
ball_right_drawable 右侧圆球可以引用图片资源<br>
>目前版本使用中的关键点如下：<br>
a. 如果使用图片资源代替圆球，那么需要左右2个圆球全部被替换，左右2个图片资源建议等宽等高，否则会出现你不想看到的结果，后续更新中可能会解决非等宽等高的图片问题<br>
b. 当用图片资源替换圆球后，那么圆球半径，边框宽度，圆球颜色等属性为失效不可用状态，你写了也是白写<br>
c. 如果不需要使用图片资源的话，请千万不要填写ball_left_drawable和ball_right_drawable属性

3. Activity中的数据回调，通过该回调可以实时获得进度条所选中范围的值
```java
bidirectionalSeekBar.setOnSeekBarChangeListener(new BidirectionalSeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(int leftProgress, int rightProgress) {
        textView.setText("left=" + leftProgress + " right=" + rightProgress);
    }
});
```

### 三 最终章
希望该控件能解决您的双向滑动需求
