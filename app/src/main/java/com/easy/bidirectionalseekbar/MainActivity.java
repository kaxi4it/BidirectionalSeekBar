package com.easy.bidirectionalseekbar;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.guyj.BidirectionalSeekBar;

public class MainActivity extends Activity {
    TextView textView1,textView2;
    BidirectionalSeekBar seekBar1,seekBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar1 = (BidirectionalSeekBar) findViewById(R.id.bSeekBar1);
        seekBar2 = (BidirectionalSeekBar) findViewById(R.id.bSeekBar2);
        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        seekBar1.setOnSeekBarChangeListener(new BidirectionalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int leftProgress, int rightProgress) {
                textView1.setText("left=" + leftProgress + " right=" + rightProgress);
            }
        });
        seekBar2.setOnSeekBarChangeListener(new BidirectionalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int leftProgress, int rightProgress) {
                textView2.setText("left=" + leftProgress + " right=" + rightProgress);
            }
        });
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View cView=View.inflate(MainActivity.this,R.layout.activity_main,null);
                BidirectionalSeekBar seekBar3=cView.findViewById(R.id.bSeekBar1);
                BidirectionalSeekBar seekBar4=cView.findViewById(R.id.bSeekBar2);
                final TextView tv3=cView.findViewById(R.id.text1);
                final TextView tv4=cView.findViewById(R.id.text2);
                PopupWindow window=new PopupWindow(cView,getWindowManager().getDefaultDisplay().getWidth()-100,
                        getWindowManager().getDefaultDisplay().getHeight()-100);
                window.setBackgroundDrawable(new BitmapDrawable());
                window.setOutsideTouchable(true);
                window.setTouchable(true);
                window.showAtLocation(textView1,Gravity.CENTER,0,0);
                seekBar3.setOnSeekBarChangeListener(new BidirectionalSeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(int leftProgress, int rightProgress) {
                        tv3.setText("left=" + leftProgress + " right=" + rightProgress);
                    }
                });
                seekBar4.setOnSeekBarChangeListener(new BidirectionalSeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(int leftProgress, int rightProgress) {
                        tv4.setText("left=" + leftProgress + " right=" + rightProgress);
                    }
                });
            }
        });

    }
}
