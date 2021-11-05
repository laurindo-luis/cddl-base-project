package br.ufma.lsdi.cddlbaseproject;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import br.ufma.lsdi.cddl.CDDL;

public class SensorsActivity extends AppCompatActivity {

    private Button buttonStartSensorLocation;
    private Button buttonStopSensorLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        this.buttonStartSensorLocation = findViewById(R.id.buttonStartSensorLocation);
        this.buttonStopSensorLocation = findViewById(R.id.buttonStopSensorLocation);

        this.buttonStartSensorLocation.setOnClickListener(view ->
                CDDL.getInstance().startSensor("Location")
        );

        this.buttonStopSensorLocation.setOnClickListener(view ->
                CDDL.getInstance().stopSensor("Location")
        );
    }
}