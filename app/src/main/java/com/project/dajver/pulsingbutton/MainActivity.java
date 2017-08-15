package com.project.dajver.pulsingbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.project.dajver.pulsingbutton.view.PulsingButtonView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PulsingButtonView.OnPulseButtonClickListener {

    @BindView(R.id.pulsing_button)
    PulsingButtonView pulsingButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        pulsingButtonView.setOnPulseButtonClick(this);
    }

    @Override
    public void onPulseButtonClick() {
        Toast.makeText(getApplicationContext(), getString(R.string.button_clicked), Toast.LENGTH_LONG).show();
    }
}
