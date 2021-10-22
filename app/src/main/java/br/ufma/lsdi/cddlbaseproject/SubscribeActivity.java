package br.ufma.lsdi.cddlbaseproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;

public class SubscribeActivity extends AppCompatActivity {

    private Button buttonSubscribe;
    private EditText editTextServiceName;
    private TextView textViewMessage;
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        this.eventBus = EventBus.builder().build();
        eventBus.register(this);

        this.buttonSubscribe = findViewById(R.id.buttonSubscribe);
        this.editTextServiceName = findViewById(R.id.editTextServiceName);
        this.textViewMessage = findViewById(R.id.textViewShowMessage);

        this.buttonSubscribe.setOnClickListener(view -> subscribeServiceName());
    }

    public void subscribeServiceName() {
        String serviceName = this.editTextServiceName.getText().toString();

        if(serviceName.trim().equals("")) {
            Toast.makeText(getBaseContext(), "Campo serviceName não pode ser vazio", Toast.LENGTH_LONG).show();
            return;
        }

        Subscriber subscriber = SubscriberFactory.createSubscriber();
        subscriber.addConnection(CDDL.getInstance().getConnection());
        subscriber.subscribeServiceByName(serviceName);
        Toast.makeText(getBaseContext(), "Subscribe realizado com sucesso!", Toast.LENGTH_LONG).show();
        subscriber.setSubscriberListener(message -> eventBus.post(message));

    }

    public void setMessage(Message message) {
        String payload = new String(message.getPayload());
        this.textViewMessage.setText(this.textViewMessage.getText().toString()+"\n"+payload);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(Message message) {
        setMessage(message);
    }
}