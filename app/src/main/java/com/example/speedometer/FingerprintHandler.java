package com.example.speedometer;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

import android.os.Handler;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    // You should use the CancellationSignal method whenever your app can no longer process user input, for example when your app goes
    // into the background. If you don’t use this method, then other apps will be unable to access the touch sensor, including the lockscreen!//
    private CancellationSignal cancellationSignal;
    private Context context;

    MainActivity mainActivity2;

    public FingerprintHandler(Context mContext) {
        context = mContext; ;
    }

    //Implement the startAuth method, which is responsible for starting the fingerprint authentication process//

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//

    public void onAuthenticationError(int errMsgId, CharSequence errString) {

        //I’m going to display the results of fingerprint authentication as a series of toasts.
        //Here, I’m creating the message that’ll be displayed if an error occurs//

        Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
    }

    @Override

    //onAuthenticationFailed is called when the fingerprint doesn’t match with any of the fingerprints registered on the device//

    public void onAuthenticationFailed() {
        Toast.makeText(context, "ENVOI ...", Toast.LENGTH_LONG).show();
        Toast.makeText(context, "EMPREINTE ENVOYEE AVEC SUCCES", Toast.LENGTH_LONG).show();
//        clignotantGauche.getBackground().mutate().setAlpha((int) (ajout[0] / 4));
        //Toast.makeText(context, " ", Toast.LENGTH_LONG).show();
//       int vitesse = 50;
////        ConstraintLayout layout1 = (ConstraintLayout) ((Activity) context).findViewById(R.id.contraint_layout);
////        layout1.getBackground().mutate().setColorFilter(Color.argb(255, 255, 255, 255));
////        try {
////            Thread.sleep(vitesse);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        layout1.setBackgroundColor(0xFF000000);
////        try {
////            Thread.sleep(vitesse);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        layout1.setBackgroundColor(0xFFFF0000);
////        try {
////            Thread.sleep(vitesse);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        layout1.setBackgroundColor(0xFF000000);
    }

    @Override

    //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
    //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
    }@Override

    //onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
     //   context.startActivity(new Intent(context, MainActivity.class));

        LinearLayout layout1 = (LinearLayout) ((Activity) context).findViewById(R.id.layout1);
        layout1.setVisibility(View.VISIBLE);

        ImageView IV_ON = (ImageView) ((Activity) context).findViewById(R.id.IV_ON);
       IV_ON.performClick();


        // Lancement de l'animation + démarrage moto

        //mainActivity2.animationDemarrage();
//        ((YourApp)getApplication()).getHandler();
//
//        handlerGived.sendEmptyMessage(0);


//        Handler handlerCommunication = (Handler)
//
//        handlerCommunication.obtainMessage(STATUS, -1, -1, "CONNEXION EN COURS").sendToTarget();

//        MainActivity mainActivity2 =  new MainActivity();
//        mainActivity2.animationDemarrage();

          /* TextView textView = (TextView) ((Activity) context).findViewById(R.id.txt_descr);
           textView.setText("FingerPrint Verification Successful");*/
        //Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();

    }

}