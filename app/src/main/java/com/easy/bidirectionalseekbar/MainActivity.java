package com.easy.bidirectionalseekbar;

import android.app.Activity;
import android.os.Bundle;
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
    }
}
