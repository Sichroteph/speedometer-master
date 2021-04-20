package com.example.speedometer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.text.SimpleDateFormat;

import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class MainActivity extends AppCompatActivity implements LocationListener,  SensorEventListener{
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView textView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    FingerprintHandler helper;

    LinearLayout layout1;
    LinearLayout layout2;
    LinearLayout layout3;
    LinearLayout layout0;

    LinearLayout layout_charge;

    // Page 1
    TextView textViewSpeed;
    TextView textViewKms;
    TextView textViewHeure;
    TextView textViewDistanceTotale;
    TextView textViewEurfy;


    ImageView clignotantGauche;
    ImageView clignotantDroit;
    ImageView pleinPhares;

    ImageView IV_ON;
    ImageView IV_PORTAIL;
    ImageView IV_CHARGE;
    ImageView IV_WARNING;

    // Page 2
    Button BTN_RETOUR;

    ImageView IV_PG3;
    Button BTN_ACCUEIL;
    Button BTN_1H;
    Button BTN_2H;
    Button BTN_3H;
    Button BTN_4H;
    Button BTN_5H;
    Button BTN_STOP_CHARGE;

    double oldLatitude = 0;
    double oldLongitude = 0;
    boolean bModeTest = false;

    private Button BTN_RL_LL_CHAR_0;
    private Button BTN_RL_LL_CHAR_1;

    private Button BTN_RL_HL_KLAX_0;
    private Button BTN_RL_HL_KLAX_1;

    private Button BTN_RL_LL_PF_0;
    private Button BTN_RL_LL_PF_1;

    private Button BTN_RL_LL_CLD_0;
    private Button BTN_RL_LL_CLD_1;

    private Button BTN_RL_LL_CLG_0;
    private Button BTN_RL_LL_CLG_1;

    private Button BTN_RL_HL_CLEF_0;
    private Button BTN_RL_HL_CLEF_1;

    private Button BTN_RL_HL_12V_0;
    private Button BTN_RL_HL_12V_1;

    private Button BTN_RL_HL_PRT1_0;
    private Button BTN_RL_HL_PRT1_1;

    private Button BTN_RL_LL_PRT2_0;
    private Button BTN_RL_LL_PRT2_1;

    private Button BTN_RL_LL_BTN_0;
    private Button BTN_RL_LL_BTN_1;

    int distanceTotale = 0;
    int currentSpeed = 0;
    boolean stopThreadReceiveData = true;

    boolean bThreadDroitOn = false;
    boolean bThreadGaucheOn = false;
    boolean bThreadPFOn = false;
    boolean bWarningsOn = false;
    boolean bChargeEnCours = false;

    long DerniereExtinction = 0;

    private SensorManager sensorManager;
    private Sensor accelerometer;

     float deltaXMax = 0;
     float deltaYMax = 0;
     float deltaZMax = 0;

     float deltaX = 0;
     float deltaY = 0;
     float deltaZ = 0;

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;

    public Vibrator v;
    String readMessage;

    private BluetoothAdapter my_bt_adapter;
    private MyBluetoothClass mybluetooth;
    private BluetoothSocket my_bt_soket = null;
    private OutputStream my_bt_out_stream = null;
    private InputStream my_bt_inp_stream = null;
    private String dev_address;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Handler handlerCommunication;
    private Handler handlerCommunicationHeure;
    private Handler handlerCommunicationDistanceTotale;
    private Handler handlerPhares;
    private Handler handlerEnvoiCommande;
   public Handler handlerDemarrageMoto;
    SharedPreferences sp;

    ClignotantGauche runClignotantGauche;
    ClignotantDroit runClignotantDroit;
    Warnings runWarnings;
    PleinPhares runPleinPhares;
    TimerCharge runTimerCharge;
    Defcon1 runDefcon1;
    MediaPlayer mp;
    boolean EstDemarree = false;
    boolean bDefcon1ON = false;
    //une seconde
    long dureeCharge = 1000 * 1;
    private final static int STATUS = 1;

    Boolean b12VON = false;
    int voltage_precedent = 0;
    int TripKm = 0;

    public void animationDemarrage() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {


            public void run() {
                EstDemarree = true;
               if (bDefcon1ON==true){
                   runDefcon1.doStop();
                   bDefcon1ON = false;
               }
                // CLEF ON
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "n").sendToTarget();
                // 12V ON
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "p").sendToTarget();
                b12VON = true;
                final int[] ajout = {11};

                for (int i = 0; i < 9; i++) {
                    try {
                        if (i == 5){
                            //CLEF OFF après 500ms
                            handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "m").sendToTarget();
                        }

                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    final int finalI = i;
                    handler.post(new Runnable() {
                        public void run() {
                            textViewSpeed.setText(Integer.toString(ajout[0]));

                            clignotantGauche.getBackground().mutate().setAlpha((int) (ajout[0] / 4));
                            clignotantDroit.getBackground().mutate().setAlpha((int) (ajout[0] / 4));
                            pleinPhares.getBackground().mutate().setAlpha((int) (ajout[0] / 4));
                            ajout[0] += 11;
                        }
                    });

                }

                final int[] ajout2 = {99};

                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            textViewSpeed.setText(Integer.toString(ajout2[0]));
                            clignotantGauche.getBackground().mutate().setAlpha((int) (ajout2[0] / 4));
                            clignotantDroit.getBackground().mutate().setAlpha((int) (ajout2[0] / 4));
                            pleinPhares.getBackground().mutate().setAlpha((int) (ajout2[0] / 4));
                            ajout2[0] -= 11;
                        }
                    });
                }

            }
        };
        new Thread(runnable).start();
    }

    public class Defcon1 implements Runnable {

        private boolean doStop = false;
        final Handler handler = new Handler();

        public synchronized void doStop() {
            this.doStop = true;
        }

        private synchronized boolean keepRunning() {
            return this.doStop == false;
        }

        @Override
        public void run() {

            int i = 0;
            while (keepRunning() && (doStop == false) && (i<15)) {

                // Allumage des lumières
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "l").sendToTarget();
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "j").sendToTarget();
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "d").sendToTarget();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                // Extinction des lumières
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "k").sendToTarget();
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "i").sendToTarget();
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "c").sendToTarget();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                }
            bDefcon1ON = false;
            if (!EstDemarree){
                handlerPhares.obtainMessage(STATUS, -1, -1, "EcranNoir").sendToTarget();
            }

            }

    }


    public class Warnings implements Runnable {

        private boolean doStop = false;
        final Handler handler = new Handler();

        public synchronized void doStop() {
            this.doStop = true;
        }

        private synchronized boolean keepRunning() {
            return this.doStop == false;
        }

        @Override
        public void run() {

            while (keepRunning()) {

                // Allumage des lumières
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "l").sendToTarget();
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "j").sendToTarget();


                // keep doing what this thread should do.
                final int[] ajout = {111};
                for (int i = 0; i < 9; i++) {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    final int finalI = i;
                    handler.post(new Runnable() {
                        public void run() {
                            clignotantGauche.getBackground().mutate().setAlpha((int) (ajout[0] / 4));
                            clignotantDroit.getBackground().mutate().setAlpha((int) (ajout[0] / 4));
                            ajout[0] += 111;
                        }
                    });

                }
                final int[] ajout2 = {999};

                // Extinction des lumières
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "k").sendToTarget();
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "i").sendToTarget();

                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    handler.post(new Runnable() {
                        public void run() {
                            clignotantGauche.getBackground().mutate().setAlpha((int) (ajout2[0] / 4));
                            clignotantDroit.getBackground().mutate().setAlpha((int) (ajout2[0] / 4));
                            ajout2[0] -= 111;
                        }
                    });

                }


            }

        }
    }
    public class ClignotantGauche implements Runnable {

        private boolean doStop = false;
        final Handler handler = new Handler();

        public synchronized void doStop() {
            this.doStop = true;
        }

        private synchronized boolean keepRunning() {
            return this.doStop == false;
        }

        @Override
        public void run() {

            while (keepRunning()) {
                mp.start();
                // Allumage des lumières
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "l").sendToTarget();


                // keep doing what this thread should do.
                final int[] ajout = {111};
                for (int i = 0; i < 9; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    final int finalI = i;
                    handler.post(new Runnable() {
                        public void run() {
                            clignotantGauche.getBackground().mutate().setAlpha((int) (ajout[0] / 4));
                            ajout[0] += 111;
                        }
                    });

                }
                final int[] ajout2 = {999};

                // Extinction des lumières
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "k").sendToTarget();

                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    handler.post(new Runnable() {
                        public void run() {
                            clignotantGauche.getBackground().mutate().setAlpha((int) (ajout2[0] / 4));
                            ajout2[0] -= 111;
                        }
                    });

                }


            }
            //mp.stop();
        }
    }

    public class ClignotantDroit implements Runnable {

        private boolean doStop = false;
        final Handler handler = new Handler();

        public synchronized void doStop() {
            this.doStop = true;
        }

        private synchronized boolean keepRunning() {
            return this.doStop == false;
        }

        @Override
        public void run() {

            while (keepRunning()) {
                mp.start();

                // keep doing what this thread should do.

                // Allumage des lumières
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "j").sendToTarget();

                final int[] ajout = {111};

                for (int i = 0; i < 9; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    final int finalI = i;
                    handler.post(new Runnable() {
                        public void run() {
                            clignotantDroit.getBackground().mutate().setAlpha((int) (ajout[0] / 4));
                            ajout[0] += 111;
                        }
                    });

                }
                final int[] ajout2 = {999};

                // Extinction des lumières
                handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "i").sendToTarget();

                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    handler.post(new Runnable() {
                        public void run() {
                            clignotantDroit.getBackground().mutate().setAlpha((int) (ajout2[0] / 4));
                            ajout2[0] -= 111;
                        }
                    });

                }


            }
            //  mp.stop();
        }
    }

    public class TimerCharge implements Runnable {
        private boolean doStop = false;
        private int dureeEnCours = 0;
        final Handler handler = new Handler();
        public synchronized void doStop() {
            this.doStop = true;
        }
        private synchronized boolean keepRunning() {
            return this.doStop == false;
        }
        @Override

        public void run() {
            // Allumage du chargeur
            //handlerCommunication.obtainMessage(STATUS, -1, -1, "Début charge").sendToTarget();
            handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "f").sendToTarget();
            while (keepRunning() && (dureeCharge > dureeEnCours)) {
                try {
                    Thread.sleep(1000);
                    dureeEnCours += 1000;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // extinction du chargeur
            // handlerCommunication.obtainMessage(STATUS, -1, -1, "Arrêt charge").sendToTarget();
            handlerEnvoiCommande.obtainMessage(STATUS, -1, -1, "e").sendToTarget();
        }
    }

    public class PleinPhares implements Runnable {

        private boolean doStop = false;
        final Handler handler = new Handler();

        public synchronized void doStop() {
            this.doStop = true;
        }

        private synchronized boolean keepRunning() {
            return this.doStop == false;
        }

        @Override
        public void run() {
            while (keepRunning()) {
                // keep doing what this thread should do.
                final int[] ajout = {111};

                for (int i = 0; i < 9; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    final int finalI = i;
                    handler.post(new Runnable() {
                        public void run() {
                            pleinPhares.getBackground().mutate().setAlpha((int) (ajout[0] / 4));
                            ajout[0] += 111;
                        }
                    });

                }
                final int[] ajout2 = {999};

                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    handler.post(new Runnable() {
                        public void run() {
                            pleinPhares.getBackground().mutate().setAlpha((int) (ajout2[0] / 4));
                            ajout2[0] -= 111;
                        }
                    });

                }


            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
          //  vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        //v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);


        sp = getSharedPreferences("Mon_Stockage", Activity.MODE_PRIVATE);
        distanceTotale = sp.getInt("distanceTotale", 0);

        keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager =
                (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        try {
            generateKey();
        } catch (FingerprintException e) {
            e.printStackTrace();
        }
        if(!bModeTest){
            if (initCipher()) {
                //If the cipher is initialized successfully, then create a CryptoObject instance//
                cryptoObject = new FingerprintManager.CryptoObject(cipher);

                helper = new FingerprintHandler(this);
                helper.startAuth(fingerprintManager, cryptoObject);
            }

        }

        // PAge 1
        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout0 = (LinearLayout) findViewById(R.id.layout0);

        layout_charge = (LinearLayout) findViewById(R.id.layout_charge);

        textViewSpeed = findViewById(R.id.textViewSpeed);
        textViewHeure = findViewById(R.id.tvHeure);
        textViewHeure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // On relance la clef
                envoieCommande("n");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                envoieCommande("m");
            }
        });

        textViewKms = findViewById(R.id.textView2);
        textViewKms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reinitialisation mémoire lissée
                voltage_precedent = 0;
                envoieCommande("f");

            }
        });

        textViewEurfy = findViewById(R.id.TV_EUFY);

        textViewDistanceTotale = findViewById(R.id.TV_Distance_Totale);
        textViewDistanceTotale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              TripKm = 0;
            }
        });


        IV_ON = (ImageView) findViewById(R.id.IV_ON);
        IV_ON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Allumage de la moto
                // relais 12V
                ouvrePage1();
                animationDemarrage();
            }
        });

        IV_WARNING = (ImageView) findViewById(R.id.IV_WARNING);
        IV_WARNING.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bWarningsOn == false){
                    runWarnings = new Warnings();
                    Thread threadWarnings = new Thread(runWarnings);
                    threadWarnings.start();
                    bWarningsOn = true;
                }
                else {
                    runWarnings.doStop();

                    bWarningsOn = false;
                }

            }
        });




        IV_PORTAIL = (ImageView) findViewById(R.id.IV_PORTAIL);
        IV_PORTAIL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // Ouverture portail
                envoieCommande("r");
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    public void run() {
                        textViewEurfy.setVisibility(View.VISIBLE);
                    }
                });

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                envoieCommande("q");
                handler.post(new Runnable() {
                    public void run() {
                        textViewEurfy.setVisibility(View.GONE);
                    }
                });


            }
        });

        IV_CHARGE = (ImageView) findViewById(R.id.IV_CHARGE);
        IV_CHARGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_charge.getVisibility() == View.GONE) {

                    layout_charge.setVisibility(View.VISIBLE);
                }
                    else{
                    layout_charge.setVisibility(View.GONE);
                }


            }
        });


        clignotantGauche = findViewById(R.id.clignotantGauche);
        clignotantDroit = findViewById(R.id.clignotantDroit);
        pleinPhares = findViewById(R.id.Pleinphares);

        // PAge 2
        layout2 = (LinearLayout) findViewById(R.id.layout2);

        // Page 3
        layout3 = (LinearLayout) findViewById(R.id.layout3);

        BTN_RETOUR = (Button) findViewById(R.id.BTN_RETOUR);
        BTN_RETOUR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvrePage2();

            }
        });


        IV_PG3 = (ImageView) findViewById(R.id.IV_PG3);
        IV_PG3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ouvrePage3();
            }
        });

        BTN_STOP_CHARGE = (Button) findViewById(R.id.BTN_STOP_CHARGE);
        BTN_STOP_CHARGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_charge.setVisibility(View.GONE);
                handlerPhares.obtainMessage(STATUS, -1, -1, "TimerChargeStop").sendToTarget();
                new TimerCharge();
            }
        });


        BTN_1H = (Button) findViewById(R.id.BTN_1H);
        BTN_1H.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_charge.setVisibility(View.GONE);
                dureeCharge = 1000 * 60 * 60 * 1;
               
                handlerPhares.obtainMessage(STATUS, -1, -1, "TimerCharge").sendToTarget();
                new TimerCharge();
            }
        });

        BTN_2H = (Button) findViewById(R.id.BTN_2H);
        BTN_2H.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_charge.setVisibility(View.GONE);
                dureeCharge = 1000 * 60 * 60 * 2;
                handlerPhares.obtainMessage(STATUS, -1, -1, "TimerCharge").sendToTarget();
                new TimerCharge();
            }
        });

        BTN_3H = (Button) findViewById(R.id.BTN_3H);
        BTN_3H.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_charge.setVisibility(View.GONE);
                dureeCharge = 1000 * 60 * 60 * 3;
                handlerPhares.obtainMessage(STATUS, -1, -1, "TimerCharge").sendToTarget();
                new TimerCharge();
            }
        });

        BTN_4H = (Button) findViewById(R.id.BTN_4H);
        BTN_4H.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_charge.setVisibility(View.GONE);
                dureeCharge = 1000 * 60 * 60 * 4;
                handlerPhares.obtainMessage(STATUS, -1, -1, "TimerCharge").sendToTarget();
                new TimerCharge();
            }
        });


        BTN_5H = (Button) findViewById(R.id.BTN_5H);
        BTN_5H.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_charge.setVisibility(View.GONE);
                dureeCharge = 1000 * 60 * 60 * 5;
                handlerPhares.obtainMessage(STATUS, -1, -1, "TimerCharge").sendToTarget();
                new TimerCharge();
            }
        });


        BTN_RL_LL_CHAR_0 = (Button) findViewById(R.id.BTN_RL_LL_CHAR_0);
        BTN_RL_LL_CHAR_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("e");

            }
        });
        BTN_RL_LL_CHAR_1 = (Button) findViewById(R.id.BTN_RL_LL_CHAR_1);
        BTN_RL_LL_CHAR_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("f");

            }
        });

        BTN_RL_HL_KLAX_0 = (Button) findViewById(R.id.BTN_RL_HL_KLAX_0);
        BTN_RL_HL_KLAX_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("c");

            }
        });
        BTN_RL_HL_KLAX_1 = (Button) findViewById(R.id.BTN_RL_HL_KLAX_1);
        BTN_RL_HL_KLAX_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("d");

            }
        });

        BTN_RL_LL_PF_0 = (Button) findViewById(R.id.BTN_RL_LL_PF_0);
        BTN_RL_LL_PF_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("g");

            }
        });
        BTN_RL_LL_PF_1 = (Button) findViewById(R.id.BTN_RL_LL_PF_1);
        BTN_RL_LL_PF_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("h");

            }
        });

        BTN_RL_LL_CLD_0 = (Button) findViewById(R.id.BTN_RL_LL_CLD_0);
        BTN_RL_LL_CLD_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("i");

            }
        });
        BTN_RL_LL_CLD_1 = (Button) findViewById(R.id.BTN_RL_LL_CLD_1);
        BTN_RL_LL_CLD_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("j");

            }
        });

        BTN_RL_LL_CLG_0 = (Button) findViewById(R.id.BTN_RL_LL_CLG_0);
        BTN_RL_LL_CLG_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("k");

            }
        });
        BTN_RL_LL_CLG_1 = (Button) findViewById(R.id.BTN_RL_LL_CLG_1);
        BTN_RL_LL_CLG_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("l");

            }
        });

        BTN_RL_HL_CLEF_0 = (Button) findViewById(R.id.BTN_RL_HL_CLEF_0);
        BTN_RL_HL_CLEF_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("m");

            }
        });
        BTN_RL_HL_CLEF_1 = (Button) findViewById(R.id.BTN_RL_HL_CLEF_1);
        BTN_RL_HL_CLEF_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("n");

            }
        });

        BTN_RL_HL_12V_0 = (Button) findViewById(R.id.BTN_RL_HL_12V_0);
        BTN_RL_HL_12V_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b12VON = false;
                envoieCommande("o");

            }
        });
        BTN_RL_HL_12V_1 = (Button) findViewById(R.id.BTN_RL_HL_12V_1);
        BTN_RL_HL_12V_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b12VON = true;
                envoieCommande("p");

            }
        });

        BTN_RL_HL_PRT1_0 = (Button) findViewById(R.id.BTN_RL_HL_PRT1_0);
        BTN_RL_HL_PRT1_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("a");

            }
        });
        BTN_RL_HL_PRT1_1 = (Button) findViewById(R.id.BTN_RL_HL_PRT1_1);
        BTN_RL_HL_PRT1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("b");

            }
        });

        BTN_RL_LL_PRT2_0 = (Button) findViewById(R.id.BTN_RL_LL_PRT2_0);
        BTN_RL_LL_PRT2_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("s");

            }
        });
        BTN_RL_LL_PRT2_1 = (Button) findViewById(R.id.BTN_RL_LL_PRT2_1);
        BTN_RL_LL_PRT2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("t");

            }
        });

        BTN_RL_LL_BTN_0 = (Button) findViewById(R.id.BTN_RL_LL_BTN_0);
        BTN_RL_LL_BTN_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("q");

            }
        });
        BTN_RL_LL_BTN_1 = (Button) findViewById(R.id.BTN_RL_LL_BTN_1);
        BTN_RL_LL_BTN_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieCommande("r");

            }
        });

        //=== Définir le handler  ==================================================================
        handlerDemarrageMoto = new Handler() {
            public void handleMessage(Message msg) {
             // animationDemarrage();
            }
        };

        handlerCommunication = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STATUS:
                        textViewKms.setText((String) (msg.obj));
                        break;
                }
            }
        };
        handlerCommunicationDistanceTotale = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STATUS:
                        textViewDistanceTotale.setText((String) (msg.obj));
                        break;
                }
            }
        };
        handlerCommunicationHeure = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STATUS:
                        textViewHeure.setText((String) (msg.obj));
                        break;
                }
            }
        };

        handlerPhares = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STATUS:
                        switch ((String) msg.obj) {
                            case "ClignotantGauche":
                                runClignotantGauche = new ClignotantGauche();
                                Thread threadClignotantGauche = new Thread(runClignotantGauche);
                                threadClignotantGauche.start();
                                break;
                            case "ClignotantGaucheStop":
                                runClignotantGauche.doStop();
                                break;

                            case "ClignotantDroit":
                                runClignotantDroit = new ClignotantDroit();
                                Thread threadClignotantDroit = new Thread(runClignotantDroit);
                                threadClignotantDroit.start();
                                break;
                            case "ClignotantDroitStop":
                                runClignotantDroit.doStop();
                                break;

                            case "PleinPhares":
                                runPleinPhares = new PleinPhares();
                                Thread threadPleinPhares = new Thread(runPleinPhares);
                                threadPleinPhares.start();
                                break;
                            case "PleinPharesStop":
                                runPleinPhares.doStop();
                                break;
                            case "Page0":
                            ouvrePage1();
                                break;
                            case "Page1":
                                ouvrePage2();
                                break;

                            case "EcranNoir":
                                ecranNoir();
                                break;

                            case "Defcon1":
                                if (bDefcon1ON != true){
                                    ecranDefcon();
                                    runDefcon1 = new Defcon1();
                                    Thread threadDefcon1 = new Thread(runDefcon1);
                                    threadDefcon1.start();
                                    bDefcon1ON = true;
                                }
                                break;

                            case "TimerCharge":
                                //* handlerCommunication.obtainMessage(STATUS, -1, -1, "bla").sendToTarget();
                                if (bChargeEnCours != true){
                                    runTimerCharge = new TimerCharge();
                                    Thread threadTimerCharge = new Thread(runTimerCharge);
                                    threadTimerCharge.start();
                                    bChargeEnCours = true;
                                }
                                break;
                            case "TimerChargeStop":
                                //* handlerCommunication.obtainMessage(STATUS, -1, -1, "bla").sendToTarget();
                            if(bChargeEnCours == true){
                                runTimerCharge.doStop();
                                bChargeEnCours= false;
                            }
                                      break;
                        }


                        break;
                }
            }
        };

        handlerEnvoiCommande = new Handler() {
            public void handleMessage(Message msg) {
                if (bModeTest == false){
                    envoieCommande((String) msg.obj);

                }

            }
        };


        // Son clignotant
        mp = MediaPlayer.create(this, R.raw.turningsoundmp3);
        // L'écran reste allumé
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Tests BT
        // ===========  affecter un identificateur au module bluetooth ============================

        if (bModeTest == false) {

            my_bt_adapter = BluetoothAdapter.getDefaultAdapter();
            if (my_bt_adapter == null) {
                Toast.makeText(this, "Pas d'interface Bluetooth", Toast.LENGTH_SHORT).show();

            }


            //============== démarrer le bluetooth s'il ne l'est pas =============================
            if (!my_bt_adapter.isEnabled()) {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!my_bt_adapter.isEnabled()) ; // attendre que le démarrage soit effectif

            // =============== afficher la liste des équipements associé dans la liste =================
            Set<BluetoothDevice> pairedDevices = my_bt_adapter.getBondedDevices();
            if (pairedDevices.isEmpty())
                Toast.makeText(this, "Liste Vide", Toast.LENGTH_SHORT).show();
            ArrayList pairedlist = new ArrayList();
            for (BluetoothDevice bt : pairedDevices) {
                pairedlist.add(bt.getName() + "\n" + bt.getAddress());

                dev_address = bt.getAddress();
                Toast.makeText(this, bt.getName() + "\n" + bt.getAddress(), Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(this, dev_address, Toast.LENGTH_SHORT).show();
            mybluetooth = new MyBluetoothClass();
            mybluetooth.start();
          //  handlerCommunication.obtainMessage(STATUS, -1, -1, "CONNEXION EN COURS").sendToTarget();

            //check for gps permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            } else {
                //start the program if the permission is granted
                doStuff();
            }

            this.updateSpeed(null);

            // Thread d'animation au démarrage de la moto
            beginListenForData();
        }
        EstDemarree = false;
        ecranNoir();



    }

    private void envoieCommande(String sCommande) {
        byte b1[] = stringToBytesASCII(sCommande);
        try {
            my_bt_out_stream.write(b1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // définition de la classe BluetoothClass pour (connexion , lecture , écriture, déconnexion)
    private class MyBluetoothClass extends Thread {
        public void run() {
            boolean SOCKET_OK, CONX_OK, OUTS_OK, INPS_OK;

            // créer un objet bluetooth pour notre HC05
            BluetoothDevice HC05 = my_bt_adapter.getRemoteDevice(dev_address);

            //Créer un soket (pipeline) pour communiquer avec notre HC05
            SOCKET_OK = true;
            try {

                my_bt_soket = HC05.createInsecureRfcommSocketToServiceRecord(myUUID);

            } catch (IOException e) {
                SOCKET_OK = false;
            }
            if (SOCKET_OK) {

                // connecter le soket
                CONX_OK = true;
                try {
                    my_bt_soket.connect();

                } catch (IOException e) {
                    CONX_OK = false;
                    // handlerCommunication.obtainMessage(STATUS, -1, -1, e.toString()).sendToTarget();
                }
                if (CONX_OK) {

                    OUTS_OK = true;
                    try {
                        my_bt_out_stream = my_bt_soket.getOutputStream();
                    } catch (IOException e) {
                        // handlerCommunication.obtainMessage(STATUS, -1, -1, "Echec création OUTPUT stream").sendToTarget();
                        OUTS_OK = false;
                    }
                    INPS_OK = true;
                    try {
                        my_bt_inp_stream = my_bt_soket.getInputStream();
                    } catch (IOException e) {
                        //   handlerCommunication.obtainMessage(STATUS, -1, -1, "Echec création INPUT STREAM").sendToTarget();
                        INPS_OK = false;
                    }

                    if (OUTS_OK && INPS_OK){
                        //Toast.makeText(context, "SUCCES", Toast.LENGTH_SHORT).show();
                        // handlerCommunication.obtainMessage(STATUS, -1, -1, "SUCCES").sendToTarget();
                    }
                    //else {
//                  //  handlerCommunication.obtainMessage(STATUS, -1, -1, "Echec Connexion").sendToTarget();
//
//            }
//                    else {
//              //  handlerCommunication.obtainMessage(STATUS, -1, -1, "Echec création Soket COMM").sendToTarget();
//            }
                }
            }
        }

        void writebyte(byte b) {
            try {
                my_bt_out_stream.write(b);
            } catch (IOException e) {
               // handlerCommunication.obtainMessage(STATUS, -1, -1, "Erreur dans writebyte").sendToTarget();
            }
        }

        int readbyte() {
            int b = 0;
            try {
                b = my_bt_inp_stream.read();
                return b;
            } catch (IOException e) {
               // handlerCommunication.obtainMessage(STATUS, -1, -1, "Erreur dans readbyte").sendToTarget();
                return -1;
            }
        }

        int available() {
            int b = 0;
            try {
                b = my_bt_inp_stream.available();
                return b;
            } catch (IOException e) {
             //   handlerCommunication.obtainMessage(STATUS, -1, -1, "Erreur dans available").sendToTarget();
                return -1;
            }
        }

        int readbytes(byte[] inpbuff) {
            int b = 0;
            try {
                b = my_bt_inp_stream.read(inpbuff);
                return b;
            } catch (IOException e) {
                handlerCommunication.obtainMessage(STATUS, -1, -1, "Erreur de lecture").sendToTarget();
                return 0;
            }
        }

        void disconnect() {
            try {
                my_bt_soket.close();
            } catch (IOException e) {
               // handlerCommunication.obtainMessage(STATUS, -1, -1, "Echec Déconnexion").sendToTarget();
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
//        if (location != null) {
//            CLocation myLocation = new CLocation(location, true);
            this.updateSpeed(location);
//        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @SuppressLint("MissingPermission")
    private void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
  //      Toast.makeText(this, "Waiting GPS Connection!", Toast.LENGTH_SHORT).show();
    }

    private void updateSpeed(Location location) {
        float fCurrentSpeed = 0;
        int entCurrentSpeed = 0;
        if (location != null) {
            //location.setUserMetricUnits(true);

            fCurrentSpeed = (float)(location.getSpeed()*3.6);

            // Conversion m/s en m/heure
            entCurrentSpeed = Math.round(fCurrentSpeed);
            currentSpeed = entCurrentSpeed;
            if ((oldLatitude != 0) && (oldLongitude != 0) && (entCurrentSpeed != 0))  {
                float[] results = new float[1];
                Location.distanceBetween(oldLatitude, oldLongitude, location.getLatitude(), location.getLongitude(), results);
                int distance = Math.round(results[0]);
                distanceTotale += distance;
                TripKm += distance;
                int distanceTotaleKms = Math.round(distanceTotale/1000);
                int TripKmRound = Math.round(TripKm/1000);
                handlerCommunicationDistanceTotale.obtainMessage(STATUS, -1, -1, distanceTotaleKms + " kms\n" + TripKmRound + " kms").sendToTarget();
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("distanceTotale", distanceTotale);
                editor.commit();

            }

            oldLatitude = location.getLatitude();
            oldLongitude = location.getLongitude();


            String strCurrentSpeed = String.valueOf(entCurrentSpeed);
            textViewSpeed.setText(strCurrentSpeed);
        }
    }

    int readbytes(byte[] inpbuff) {
        int b = 0;
        try {
            b = my_bt_inp_stream.read(inpbuff);
            return b;
        } catch (IOException e) {
            handlerCommunication.obtainMessage(STATUS, -1, -1, "Erreur de lecture2").sendToTarget();
            return 0;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.doStuff();
            } else {
                finish();
            }
        }
    }

    public static byte[] stringToBytesASCII(String str) {
        byte[] b = new byte[str.length()];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) str.charAt(i);
        }
        return b;
    }

    void beginListenForData() {

        final Handler handler = new Handler();
        stopThreadReceiveData = false;

        Thread thread = new Thread(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!Thread.currentThread().isInterrupted() && !stopThreadReceiveData) {

                    byte[] buffer = new byte[1];

                    readbytes(buffer);

                    String str = new String(buffer, StandardCharsets.UTF_8); // for UTF-8
                    if (!str.equals(";")) {
                        readMessage += str;
                    } else {
                        // Rafraichissement de l'heure

                        String currentDateTimeString = new SimpleDateFormat("HH:mm").format(new Date());

                        handlerCommunicationHeure.obtainMessage(STATUS, -1, -1, currentDateTimeString).sendToTarget();
                        //textViewHeure.setText("dssdf");


                        switch (readMessage) {
                            case "BTN_CLG_0":
                                if (bThreadGaucheOn == true) {
                                    handlerPhares.obtainMessage(STATUS, -1, -1, "ClignotantGaucheStop").sendToTarget();
                                    bThreadGaucheOn = false;
                                }
                                break;
                            case "BTN_CLG_1":
                                if (bThreadGaucheOn == false) {
                                    handlerPhares.obtainMessage(STATUS, -1, -1, "ClignotantGauche").sendToTarget();
                                    bThreadGaucheOn = true;
                                }
                                break;
                            case "BTN_CLD_0":
                                if (bThreadDroitOn == true) {
                                    handlerPhares.obtainMessage(STATUS, -1, -1, "ClignotantDroitStop").sendToTarget();
                                    bThreadDroitOn = false;
                                }
                                break;
                            case "BTN_CLD_1":
                                if (bThreadDroitOn == false) {
                                    handlerPhares.obtainMessage(STATUS, -1, -1, "ClignotantDroit").sendToTarget();
                                    bThreadDroitOn = true;
                                }
                                break;
                            case "BTN_PF_0":
                                if (bThreadPFOn == true) {
                                    handlerPhares.obtainMessage(STATUS, -1, -1, "PleinPharesStop").sendToTarget();
                                    bThreadPFOn = false;
                                }
                                break;
                            case "BTN_PF_1":
                                if (bThreadPFOn == false) {
                                    handlerPhares.obtainMessage(STATUS, -1, -1, "PleinPhares").sendToTarget();
                                    bThreadPFOn = true;
                                }
                                break;

                                case "BTN_PAGE_0":
                                if ((bThreadPFOn == true) && (EstDemarree == true)){
                                    handlerPhares.obtainMessage(STATUS, -1, -1, "Page0").sendToTarget();
                                    bThreadPFOn = false;
                                }
                                break;
                            case "BTN_PAGE_1":
                                if ((bThreadPFOn == false) && (EstDemarree == true)){
                                    handlerPhares.obtainMessage(STATUS, -1, -1, "Page1").sendToTarget();
                                    bThreadPFOn = true;
                                }
                                break;


                                default:
                                // Voltage

                            if (isfloat(readMessage)==true){
                                float f =  Float.valueOf(readMessage);
                                int iCalibration = 0;
                                if (b12VON == false) {
                                    iCalibration = 37;
                                }
                                double voltage = (f-794+iCalibration)/1.725;
                                int iVoltage = (int)voltage;

                                if (voltage_precedent == 0) {
                                    voltage_precedent = iVoltage;
                                }
                                else{
                                    if (voltage_precedent != iVoltage){
                                        if (voltage_precedent < iVoltage){
                                           voltage_precedent ++;
                                        }
                                        else{
                                            voltage_precedent --;
                                        }
                                    }
                                }

                                if (voltage_precedent < 0){
                                    voltage_precedent = 0;
                                }
                                if (voltage_precedent > 100){
                                    voltage_precedent = 100;
                                }
                                readMessage = voltage_precedent + "%";

                                }
                                else{
                                    readMessage ="";
                                }
                                handlerCommunication.obtainMessage(STATUS, -1, -1, readMessage).sendToTarget();
                                break;
                        }

                        readMessage = "";
                    }
                }
            }
        });

        thread.start();
    }
    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(event.values[0]);
        deltaY = Math.abs(event.values[1]);
        deltaZ = Math.abs(event.values[2]);

        displayCurrentValues();
    }


    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
//        currentX.setText(Float.toString(deltaX));
//        currentY.setText(Float.toString(deltaY));
//        currentZ.setText(Float.toString(deltaZ));

       int AccelArrondi = (int)deltaY;
        // 10 secondes avant alarme
       if((AccelArrondi == 0)&&(EstDemarree == false)&&(System.currentTimeMillis()-DerniereExtinction > 10000) && (DerniereExtinction != 0)){
           handlerPhares.obtainMessage(STATUS, -1, -1, "Defcon1").sendToTarget();
       }
     // textViewDistanceTotale.setText(String.valueOf(AccelArrondi));


    }


    public boolean isfloat(String string) {
        try {
            Float.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void ouvrePage1() {

        layout0.setVisibility(View.GONE);
        layout1.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);

    }

    public void ouvrePage2() {
        layout0.setVisibility(View.GONE);
        layout1.setVisibility(View.GONE);

        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.VISIBLE);

    }

    public void ouvrePage3() {
        layout0.setVisibility(View.GONE);
        layout1.setVisibility(View.GONE);

        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.GONE);

    }

    public void ecranNoir(){
        layout0.setVisibility(View.GONE);
        layout1.setVisibility(View.GONE);

        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
    }
    public void ecranDefcon(){
        layout0.setVisibility(View.VISIBLE);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
    }

    public void onClick(View view) {
      if (currentSpeed == 0)
      {
          b12VON = false;
          envoieCommande("o");
          EstDemarree = false;
          DerniereExtinction = System.currentTimeMillis();
          Toast.makeText(this, "ATTENTION ALARME ENCLENCHEE DANS 10 SECONDES ...", Toast.LENGTH_LONG).show();
          ecranNoir();
          helper.startAuth(fingerprintManager, cryptoObject);

      }


    }
    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new

                    //Specify the operation(s) this key can be used for//
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    //Create a new method that we’ll use to initialize our cipher//
    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }




}

