package ariel.fuentes.simondice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class ModoNormal extends AppCompatActivity implements SensorEventListener {

    private TextView Actividad;
    private TextView ContadorTiempo;
    private TextView Nivel;
    private TextView ContadorActividades;

    private int NumeroActividades;
    private int NumeroNivel;

    boolean isMuted = false;
    private GestureDetector gestos;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Vibrator vibrator;

    private String[] activities;
    private Random random;
    private int currentActivityIndex;

    private Timer timer;
    private final long TIMER_INTERVAL = 3000; // Intervalo de actualización en milisegundos

    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayer2;
    int paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modonormal);

        FloatingActionButton volumen = findViewById(R.id.Volumen);

        Actividad = findViewById(R.id.frace);
        gestos = new GestureDetector(this, new EscuchGestos());

        // Obtener referencias a las ImageView de los corazones
        heart1 = findViewById(R.id.corazon1);
        heart2 = findViewById(R.id.corazon2);
        heart3 = findViewById(R.id.corazon3);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mediaPlayer = MediaPlayer.create(this, R.raw.emotionalorchestra);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        //altavoz = findViewById(R.id.altavoz);
        volumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMuted) {
                    // Si está silenciado, reanuda la reproducción y cambia la imagen a la de sonido activo
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    volumen.setImageResource(R.drawable.volume_up);
                    isMuted = false;
                } else {
                    // Si no está silenciado, silencia la reproducción y cambia la imagen a la de sonido silenciado
                    mediaPlayer.setVolume(0.0f, 0.0f);
                    volumen.setImageResource(R.drawable.volume_mute);
                    isMuted = true;
                }
            }
        });

        // Cargar el array activities desde el archivo XML
        activities = getResources().getStringArray(R.array.actividades);
        random = new Random();
        generateRandomActivity();

        // Iniciar el temporizador para actualizar el texto periódicamente
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        generateRandomActivity();
                    }
                });
            }
        }, TIMER_INTERVAL, TIMER_INTERVAL);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestos.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class EscuchGestos extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            float ancho = Math.abs(e2.getX() - e1.getX());
            float alto = Math.abs(e2.getY() - e1.getY());
            if (ancho > alto) {
                if (e2.getX() > e1.getX()) {
                    String expectedActivity = getResources().getString(R.string.Derecha);
                    if (Actividad.getText().toString().equals(expectedActivity)) {
                        Actividad.setText(R.string.Correcto);
                    } else {
                        Actividad.setText(R.string.Incorrecto);
                        decrementLives();
                    }
                } else {
                    String expectedActivity = getResources().getString(R.string.Izquierda);
                    if (Actividad.getText().toString().equals(expectedActivity)) {
                        Actividad.setText(R.string.Correcto);
                    } else {
                        Actividad.setText(R.string.Incorrecto);
                        decrementLives();
                    }
                }
            } else {
                if (e2.getY() > e1.getY()) {
                    String expectedActivity = getResources().getString(R.string.Abajo);
                    if (Actividad.getText().toString().equals(expectedActivity)) {
                        Actividad.setText(R.string.Correcto);
                    } else {
                        Actividad.setText(R.string.Incorrecto);
                        decrementLives();
                    }
                } else {
                    String expectedActivity = getResources().getString(R.string.Arriba);
                    if (Actividad.getText().toString().equals(expectedActivity)) {
                        Actividad.setText(R.string.Correcto);
                    } else {
                        Actividad.setText(R.string.Incorrecto);
                        decrementLives();
                    }
                }
            }
            return true;
        }
    }

    private int lives = 3;
    private ImageView heart1, heart2, heart3;

    private void decrementLives() {
        lives--;
        if (lives == 2) {
            heart3.setVisibility(View.INVISIBLE);
        } else if (lives == 1) {
            heart2.setVisibility(View.INVISIBLE);
        } else if (lives == 0) {
            heart1.setVisibility(View.INVISIBLE);
            Actividad.setText(R.string.Perdiste);
        }
    }

    private void generateRandomActivity() {
        currentActivityIndex = random.nextInt(activities.length);
        String activityText = activities[currentActivityIndex];
        Actividad.setText(activityText);
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
        paused = mediaPlayer.getCurrentPosition();
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
            //frace.setText(R.string.Agitado);
            vibrator.vibrate(150);
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

        // Detener el temporizador
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}