package ariel.fuentes.simondice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.content.pm.ActivityInfo;



public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView frace;
    private ImageView altavoz;
    boolean isMuted = false;
    private GestureDetector gestos;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Vibrator vibrator;

    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayer2;
    int paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frace=findViewById(R.id.frace);
        gestos = new GestureDetector(this, new EscuchGestos());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mediaPlayer = MediaPlayer.create(this, R.raw.emotionalorchestra);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        altavoz = findViewById(R.id.altavoz);
        altavoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMuted) {
                    // Si está silenciado, reanuda la reproducción y cambia la imagen a la de sonido activo
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    altavoz.setImageResource(R.drawable.volume_up);
                    isMuted = false;
                } else {
                    // Si no está silenciado, silencia la reproducción y cambia la imagen a la de sonido silenciado
                    mediaPlayer.setVolume(0.0f, 0.0f);
                    altavoz.setImageResource(R.drawable.volume_mute);
                    isMuted = true;
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestos.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class EscuchGestos extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            float ancho = Math.abs(e2.getX()-e1.getX());
            float alto = Math.abs(e2.getY()-e1.getY());
            if (ancho>alto){
                if (e2.getX() > e1.getX()) {
                    frace.setText(R.string.Derecha);
                } else {
                    frace.setText(R.string.Izquierda);
                }
            }
            else {
                if (e2.getY() > e1.getY()) {
                    frace.setText(R.string.Abajo);
                } else {
                    frace.setText(R.string.Arriba);
                }
            }
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.seekTo(paused);
        mediaPlayer.start();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
        paused=mediaPlayer.getCurrentPosition();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        double acceleration = Math.sqrt(x * x + y * y + z * z);

        if (acceleration > 25) {
            frace.setText(R.string.Agitado);
            vibrator.vibrate(100);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se requiere implementación aquí
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libera los recursos del MediaPlayer al finalizar la actividad
        mediaPlayer.release();
        mediaPlayer = null;
    }

    /*public void play (View view){
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this, R.raw.emotionalorchestra);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }else if (!mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(paused);
            mediaPlayer.start();
        }
    }

    public void pause (View view){
        mediaPlayer.pause();
        paused=mediaPlayer.getCurrentPosition();
    }

    public void stop (View view){
        mediaPlayer.release();
        mediaPlayer=null;
    }

    public void winner (View view2){
        if(mediaPlayer2 == null){
            mediaPlayer2 = MediaPlayer.create(this, R.raw.victory);
            mediaPlayer2.start();
        }else if (!mediaPlayer2.isPlaying()){
            mediaPlayer2.seekTo(paused);
            mediaPlayer2.start();
        }
    }

    public void gameover (View view3){
        if(mediaPlayer2 == null){
            mediaPlayer2 = MediaPlayer.create(this, R.raw.game_over);
            mediaPlayer2.start();
        }else if (!mediaPlayer2.isPlaying()){
            mediaPlayer2.seekTo(paused);
            mediaPlayer2.start();
        }
    }*/

}