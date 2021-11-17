package br.ufma.lsdi.cddlbaseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.listeners.IConnectionListener;
import br.ufma.lsdi.cddl.network.ConnectionImpl;

public class MainActivity extends AppCompatActivity {

    private Button buttonPublish;
    private Button buttonSubscribe;
    private Button buttonStartSensors;

    private String host;
    private CDDL cddl;
    private ConnectionImpl connection;
    private String CLIENT_ID = "app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPermission();
        initCDDL();

        this.buttonPublish = findViewById(R.id.buttonPublish);
        this.buttonSubscribe = findViewById(R.id.buttonSubscribe);
        this.buttonStartSensors = findViewById(R.id.buttonStartSensors);

        /*
            Ao iniciar esse projeto, você deve utilizar dois emuladores. O primeiro deles
            publica dados de contexto no broker. O segundo, se inscreve em um service name
            e recebe os dados de contexto que o outro emulador publica.
         */

        this.buttonPublish.setOnClickListener(view ->
                startActivity(new Intent(getBaseContext(), PublishActivity.class))
        );
        this.buttonSubscribe.setOnClickListener(view ->
                startActivity(new Intent(getBaseContext(), SubscribeActivity.class))
        );
        this.buttonStartSensors.setOnClickListener(view ->
                startActivity(new Intent(getBaseContext(), SensorsActivity.class))
        );

    }

    private IConnectionListener connectionListener = new IConnectionListener() {
        @Override
        public void onConnectionEstablished() {
            Log.d(null, "Conexão estabelecida.");
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
        /*
            Ip do Broker MQTT
            Para você utilizar um Broker externo ou na sua máquina, você deve
            configurar o proxy do emulador manualmente e colocar o ip referente
            ao Broker.
         */
        host = "192.168.18.12";

        //host = CDDL.startMicroBroker();
        connection = ConnectionFactory.createConnection();
        connection.setClientId(this.CLIENT_ID);
        connection.setHost(host);
        connection.addConnectionListener(connectionListener);
        connection.setEnableIntermediateBuffer(true);
        connection.connect();
        cddl = CDDL.getInstance();
        cddl.setConnection(connection);
        cddl.setContext(this);
        cddl.startService();
        cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_ID);
    }

    private void secureInitCDDL() {
        /*
            Ip do Broker MQTT
            Para você utilizar um Broker externo ou na sua máquina, você deve
            configurar o proxy do emulador manualmente e colocar o ip referente
            ao Broker.
            Este método utiliza o cddl no modo seguro.
         */
        host = "192.168.18.12";

//        host = CDDL.startSecureMicroBroker(getApplicationContext(), true );

        connection = ConnectionFactory.createConnection();
        connection.setClientId(this.CLIENT_ID);
        connection.setHost(host);
        connection.addConnectionListener(connectionListener);
        connection.setEnableIntermediateBuffer(true);
        connection.secureConnect(getApplicationContext());
        cddl = CDDL.getInstance();
        cddl.setConnection(connection);
        cddl.setContext(this);
        cddl.startService();
        cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_ID);
    }



    /*
        Método para gerar a requisição de certificado digital;
    */
    public void generateCSR(String nomeComum,
                            String unidadeOrganizacional,
                            String organizacao,
                            String cidade,
                            String estado,
                            String pais){
        SecurityService securityService = CDDL
                .getSecurityServiceInstance(getApplicationContext());

        securityService
                .generateCSR(
                        nomeComum,
                        unidadeOrganizacional,
                        organizacao,
                        cidade,
                        estado,
                        pais);

    }

    /*
       Método para importar o certificado assinado da autoridade certificadora e certificado do cliente
    */
    public void importClientAndCaCertificate(String caCertFileName, String clientCertFileName ){
        try {
            securityService.setCaCertificate(caCertFileName);
            securityService.setCertificate(clientCertFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
       Método para adicionar as regras de acesso a todos os tópicos ao microbroker para um cliente específico
    */
    public void importClientAndCaCertificate(String clientID, String clientCertFileName ){
        try {


            securityService.grantReadPermissionByCDDLTopic(clientID, SecurityService.ALL_TOPICS);
            securityService.grantWritePermissionByCDDLTopic(clientID,SecurityService.ALL_TOPICS);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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