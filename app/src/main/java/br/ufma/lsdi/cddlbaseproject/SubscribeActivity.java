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

import static java.util.Objects.nonNull;

public class SubscribeActivity extends AppCompatActivity {

    private Button buttonSubscribe;
    private EditText editTextServiceName;
    private EditText editTextAccuracyValue;
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
        this.editTextAccuracyValue = findViewById(R.id.editTextAccuracyValue);

        this.buttonSubscribe.setOnClickListener(view -> subscribeServiceName());
    }

    public void subscribeServiceName() {
        String serviceName = this.editTextServiceName.getText().toString();
        String textAccuracyValue = this.editTextAccuracyValue.getText().toString();

        if(serviceName.trim().equals("")) {
            Toast.makeText(getBaseContext(), "Campo serviceName não pode ser vazio", Toast.LENGTH_LONG).show();
            return;
        }

        if(textAccuracyValue.trim().equals("")) {
            Toast.makeText(getBaseContext(), "Campo acccuracy value não pode ser vazio", Toast.LENGTH_LONG).show();
            return;
        }
        Double accuracyValue = Double.parseDouble(textAccuracyValue);

        /* É necessário fazer o unsubscriber para alterar de forma dinâmica o valor da acurácia */
        if(nonNull(subscriber))
            subscriber.unsubscribeServiceByName(serviceName);

        subscriber = SubscriberFactory.createSubscriber();
        subscriber.addConnection(CDDL.getInstance().getConnection());
        subscriber.subscribeServiceByName(serviceName);
        Toast.makeText(getBaseContext(), "Subscribe realizado com sucesso!", Toast.LENGTH_LONG).show();
        subscriber.setSubscriberListener(message -> {
            /* Se a acurácia da informação de contexto for menor ou igual a 20, eu a publico no eventBus para ser
               apresentada na interface */
            if(message.getAccuracy() <= accuracyValue)
                eventBus.post(message);
        });

    }

    public void setMessage(Message message) {
        String payload;
        if(message.getServiceName().equals("Location")) {
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