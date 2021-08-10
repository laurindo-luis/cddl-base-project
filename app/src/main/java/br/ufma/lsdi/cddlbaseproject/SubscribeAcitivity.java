package br.ufma.lsdi.cddlbaseproject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;

public class SubscribeAcitivity extends AppCompatActivity {

    private EditText serviceName;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        this.serviceName = findViewById(R.id.serviceName);
        this.message = findViewById(R.id.message);
    }

    public void subscribeMessage(View view) {
        String serviceName = this.serviceName.getText().toString();

        Subscriber subscriber = SubscriberFactory.createSubscriber();
        subscriber.addConnection(CDDL.getInstance().getConnection());
        subscriber.subscribeServiceByName(serviceName);
        subscriber.setSubscriberListener(message -> {
            String payload = new String(message.getPayload());
            this.message.setText(payload);
        });
    }
}
