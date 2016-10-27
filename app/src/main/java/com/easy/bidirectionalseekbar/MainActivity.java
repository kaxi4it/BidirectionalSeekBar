package com.easy.bidirectionalseekbar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    TextView textView;
    BidirectionalSeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = (BidirectionalSeekBar) findViewById(R.id.bSeekBar);
        textView = (TextView) findViewById(R.id.text);
        seekBar.setOnSeekBarChangeListener(new BidirectionalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int leftProgress, int rightProgress) {
                textView.setText("left=" + leftProgress + " right=" + rightProgress);
            }
        });
    }
}
