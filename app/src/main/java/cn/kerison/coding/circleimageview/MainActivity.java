package cn.kerison.coding.circleimageview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import cn.kerison.coding.widget.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView mImageLightView;
    private CircleImageView mImageDarkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageLightView = (CircleImageView) findViewById(R.id.image_view_light);
        mImageLightView.setBorderOverlay(true);

        mImageDarkView = (CircleImageView) findViewById(R.id.image_view_dark);


        ((SeekBar)findViewById(R.id.seek_bar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                mImageLightView.setBorderWidth(progress);
                mImageDarkView.setBorderWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {

            }
        });
    }
}