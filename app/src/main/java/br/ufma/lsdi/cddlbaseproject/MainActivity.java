package br.ufma.lsdi.cddlbaseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.listeners.IConnectionListener;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;

public class MainActivity extends AppCompatActivity {

    private String host;
    private CDDL cddl;
    private ConnectionImpl connection;

    private Subscriber subscribe;

    private String sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPermission();
        initCDDL();

    }

    private IConnectionListener connectionListener = new IConnectionListener() {
        @Override
        public void onConnectionEstablished() {
            Log.d(null, "Conexão estabelecida.");
            subscribeMessage();

            publishMessage();
        }

        @Override
        public void onConnectionEstablishmentFailed() {
            Log.d(null, "Falha na conexão.");
        }

        @Override
        public void onConnectionLost() {
            Log.d(null, "Conexão perdida.");
        }

        @Override
        public void onDisconnectedNormally() {
            Log.d(null, "Uma disconexão normal ocorreu.");
        }
    };

    private void initCDDL() {
        host = "192.168.18.12";
        //host = CDDL.startMicroBroker();
        connection = ConnectionFactory.createConnection();
        connection.setClientId("luis");
        connection.setHost(host);
        connection.addConnectionListener(connectionListener);
        connection.connect();
        cddl = CDDL.getInstance();
        cddl.setConnection(connection);
        cddl.setContext(this);
        cddl.startService();
        cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_ID);
    }

    private void subscribeMessage() {
        subscribe = SubscriberFactory.createSubscriber();
        subscribe.addConnection(cddl.getConnection());
        subscribe.subscribeServiceByName("imposto");
        subscribe.setSubscriberListener(new ISubscriberListener() {
            @Override
            public void onMessageArrived(Message message) {
                Log.d("cddl", "chegou mensagem");
            }
        });
    }

    private void publishMessage() {
        Publisher publisher = PublisherFactory.createPublisher();
        publisher.addConnection(cddl.getConnection());

        Message message = new Message();
        message.setServiceName("imposto");
        message.setPayload("Teste".getBytes());
        publisher.publish(message);
    }

    @Override
    protected void onDestroy() {
        cddl.stopAllCommunicationTechnologies();;
        cddl.stopService();
        connection.disconnect();
        CDDL.stopMicroBroker();

        super.onDestroy();
    }

    private void setPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}