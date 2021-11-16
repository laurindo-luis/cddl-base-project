package br.ufma.lsdi.cddlbaseproject;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;

public class SensorsActivity extends AppCompatActivity {

    private Button buttonStartSensorLocation;
    private Button buttonStopSensorLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        this.buttonStartSensorLocation = findViewById(R.id.buttonStartSensorLocation);
        this.buttonStopSensorLocation = findViewById(R.id.buttonStopSensorLocation);

        this.buttonStartSensorLocation.setOnClickListener(view -> {
            CDDL.getInstance().startSensor("Location");
            //publishMessageReliable();
        });

        this.buttonStopSensorLocation.setOnClickListener(view ->
                CDDL.getInstance().stopSensor("Location")
        );
    }

    private void publishMessageReliable() {
        Subscriber subscriber = SubscriberFactory.createSubscriber();
        subscriber.addConnection(CDDL.getInstance().getConnection());
        subscriber.subscribeServiceByName("Location");
        subscriber.setSubscriberListener(message -> {
            Message m = new Message();
            m.setServiceValue(message.getServiceValue());
            m.setAccuracy(message.getAccuracy());

            Publisher publisher = PublisherFactory.createPublisher();
            publisher.addConnection(CDDL.getInstance().getConnection());
            publisher.setFilter("SELECT * FROM Message WHERE accuracy <= 20");
            m.setServiceName("LocationReliable");
            publisher.publish(m);
        });
    }
}