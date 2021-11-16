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
import br.ufma.lsdi.cddl.listeners.IMonitorListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Monitor;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;

import static java.util.Objects.nonNull;

public class SubscribeActivity extends AppCompatActivity {

    private Button buttonSubscribe;
    private EditText editTextServiceName;
    private TextView textViewMessage;
    private EventBus eventBus;

    private Subscriber subscriber;

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

        /* É necessário fazer o unsubscriber para alterar de forma dinâmica o valor da acurácia */
        if(nonNull(subscriber))
            subscriber.unsubscribeServiceByName(serviceName);

        subscriber = SubscriberFactory.createSubscriber();
        subscriber.addConnection(CDDL.getInstance().getConnection());
        subscriber.subscribeServiceByName(serviceName);
        Toast.makeText(getBaseContext(), "Subscribe realizado com sucesso!", Toast.LENGTH_LONG).show();
        Monitor monitor = subscriber.getMonitor();
        monitor.addRule("SELECT * FROM Message WHERE accuracy <= 20", iMonitorListener);
        subscriber.setSubscriberListener(message -> {
            //Recebe todas as informações de contexto aqui!
            //eventBus.post(message);
        });
    }

    private IMonitorListener iMonitorListener = new IMonitorListener() {
        @Override
        public void onEvent(Message message) {
            /* Na interface Listener do Monitor, recebe apenas as mensagens que
            atendem as regras da EPL especificada */
            eventBus.post(message);
        }
    };

    public void setMessage(Message message) {
        String payload;
        if(message.getServiceName().contains("Location")) {
            Double lat = (Double) message.getServiceValue()[0];
            Double lon = (Double) message.getServiceValue()[1];
            payload = String.format("%s, %s", lat, lon);
        } else {
            payload = new String(message.getPayload());
        }

        String messageWithQoC  = String.format("Payload: %s | accuracy: %s | Age: %s | Resolution: %s | Delay: %s | Expiration Time: %s \n",
                payload, message.getAccuracy(), message.getAge(), message.getNumericalResolution(),
                message.getDelay(), message.getExpirationTime());

        this.textViewMessage.setText(messageWithQoC+"\n"+this.textViewMessage.getText().toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(Message message) {
        setMessage(message);
    }
}