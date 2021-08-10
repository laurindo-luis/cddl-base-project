package br.ufma.lsdi.cddlbaseproject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;

public class PublishAcitivity extends AppCompatActivity {

    private EditText serviceName;
    private EditText payload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        this.serviceName = findViewById(R.id.serviceName);
        this.payload = findViewById(R.id.payload);
    }

    public void publishMessage(View view) {
        String serviceName = this.serviceName.getText().toString();
        String payload = this.payload.getText().toString();

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
