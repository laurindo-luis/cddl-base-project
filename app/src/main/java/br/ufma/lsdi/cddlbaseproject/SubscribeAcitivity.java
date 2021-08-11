package br.ufma.lsdi.cddlbaseproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        eventBus = EventBus.builder().build();
        eventBus.register(this);

        this.serviceName = findViewById(R.id.serviceName);
        this.message = findViewById(R.id.message);

        CDDL.getInstance().startSensor("Location");
    }

    public void subscribeMessage(View view) {
        String serviceName = this.serviceName.getText().toString();

        Subscriber subscriber = SubscriberFactory.createSubscriber();
        subscriber.addConnection(CDDL.getInstance().getConnection());
        subscriber.subscribeServiceByName(serviceName);
        subscriber.setSubscriberListener(message -> {
            eventBus.post(message);
        });
    }

    public void setMessage(Message message) {
        this.message.setText(""+message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(Message message) {
        setMessage(message);
    }
}
