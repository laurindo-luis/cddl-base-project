package br.ufma.lsdi.cddlbaseproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Flow;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;

public class PublishActivity extends AppCompatActivity {

    private Button buttonPublish;
    private EditText editTextServiceName;
    private EditText editTexPayload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        this.buttonPublish = findViewById(R.id.buttonPublish);
        this.editTexPayload = findViewById(R.id.editTextMessage);
        this.editTextServiceName = findViewById(R.id.editTextServiceName);

        this.buttonPublish.setOnClickListener(view -> {
            publishMessage();
        });
    }

    public void publishMessage() {
        String serviceName = this.editTextServiceName.getText().toString();
        String payload = this.editTexPayload.getText().toString();

        if(serviceName.trim().equals("")) {
            Toast.makeText(getBaseContext(), "Campo serviceName não pode ser vazio", Toast.LENGTH_LONG).show();
            return;
        }

        if(payload.trim().equals("")) {
            Toast.makeText(getBaseContext(), "Campo payload não pode ser vazio", Toast.LENGTH_LONG).show();
            return;
        }

        Publisher publisher = PublisherFactory.createPublisher();
        publisher.addConnection(CDDL.getInstance().getConnection());
        Message message = new Message();
        message.setServiceName(serviceName);
        message.setPayload(payload.getBytes());
        publisher.publish(message);
    }
}