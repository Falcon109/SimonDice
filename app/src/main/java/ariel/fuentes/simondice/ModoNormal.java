package ariel.fuentes.simondice;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;


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

    private CountDownTimer countDownTimer;
    private long tiempoRestante = 4000; // 4 segundos en milisegundos

    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayer2;
    int paused;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modonormal);

        FloatingActionButton volumen = findViewById(R.id.Volumen);
        Nivel = findViewById(R.id.Nivel);
        ContadorActividades = findViewById(R.id.Contador);

        NumeroNivel=1;
        Nivel.setText(getString(R.string.Nivel) + " " + String.valueOf(NumeroNivel) + " -");

        NumeroActividades = 10;
        ContadorActividades.setText(String.valueOf(NumeroActividades));

        ContadorTiempo = findViewById(R.id.tiempotext);

        Actividad =findViewById(R.id.frace);
        gestos = new GestureDetector(this, new EscuchGestos());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mediaPlayer = MediaPlayer.create(this, R.raw.emotionalorchestra);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
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
                    Actividad.setText(R.string.Derecha);
                    NumeroActividades--;
                    ContadorActividades.setText(String.valueOf(NumeroActividades));
                    // Verificar si el contador de actividades llegó a 0
                    if (NumeroActividades == 0) {
                        NumeroActividades = 10;
                        ContadorActividades.setText(String.valueOf(NumeroActividades));
                        NumeroNivel++;
                        Nivel.setText(getString(R.string.Nivel) + " " + String.valueOf(NumeroNivel) + " -");
                    }
                } else {
                    Actividad.setText(R.string.Izquierda);
                    NumeroActividades--;
                    ContadorActividades.setText(String.valueOf(NumeroActividades));
                    // Verificar si el contador de actividades llegó a 0
                    if (NumeroActividades == 0) {
                        NumeroActividades = 10;
                        ContadorActividades.setText(String.valueOf(NumeroActividades));
                        NumeroNivel++;
                        Nivel.setText(getString(R.string.Nivel) + " " + String.valueOf(NumeroNivel) + " -");
                    }
                }
            } else {
                if (e2.getY() > e1.getY()) {
                    Actividad.setText(R.string.Abajo);
                    NumeroActividades--;
                    ContadorActividades.setText(String.valueOf(NumeroActividades));
                    // Verificar si el contador de actividades llegó a 0
                    if (NumeroActividades == 0) {
                        NumeroActividades = 10;
                        ContadorActividades.setText(String.valueOf(NumeroActividades));
                        NumeroNivel++;
                        Nivel.setText(getString(R.string.Nivel) + " " + String.valueOf(NumeroNivel) + " -");
                    }
                } else {
                    Actividad.setText(R.string.Arriba);
                    NumeroActividades--;
                    ContadorActividades.setText(String.valueOf(NumeroActividades));
                    // Verificar si el contador de actividades llegó a 0
                    if (NumeroActividades == 0) {
                        NumeroActividades = 10;
                        ContadorActividades.setText(String.valueOf(NumeroActividades));
                        NumeroNivel++;
                        Nivel.setText(getString(R.string.Nivel) + " " + String.valueOf(NumeroNivel) + " -");
                    }
                }
            }

            // Reiniciar el temporizador
            startTimer();

            return true;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        double acceleration = Math.sqrt(x * x + y * y + z * z);

        if (acceleration > 60) {
            Actividad.setText(R.string.Agitado);
            vibrator.vibrate(100);
            NumeroActividades--;
            ContadorActividades.setText(String.valueOf(NumeroActividades));
            // Verificar si el contador de actividades llegó a 0
            if (NumeroActividades == 0) {
                NumeroActividades = 10;
                ContadorActividades.setText(String.valueOf(NumeroActividades));
                NumeroNivel++;
                Nivel.setText(getString(R.string.Nivel) + " " + String.valueOf(NumeroNivel) + " -");
            }

            // Reiniciar el temporizador
            startTimer();
        }
    }

    private void generateRandomActivity() {
        currentActivityIndex = random.nextInt(activities.length);
        String activityText = activities[currentActivityIndex];
        Actividad.setText(activityText);

        // Restar 1 al contador de actividades y actualizar el TextView
        NumeroActividades--;
        ContadorActividades.setText(String.valueOf(NumeroActividades));

        // Verificar si el contador de actividades llegó a 0
        if (NumeroActividades == 0) {
            NumeroActividades = 10;
            ContadorActividades.setText(String.valueOf(NumeroActividades));
            NumeroNivel++;
            Nivel.setText(getString(R.string.Nivel) + " " + String.valueOf(NumeroNivel) + " -");
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

        // Iniciar el temporizador al reanudar la actividad
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
        paused=mediaPlayer.getCurrentPosition();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        // Detener el temporizador al pausar la actividad
        stopTimer();
    }

    private void startTimer() {
        stopTimer(); // Detener el temporizador existente si hay uno en progreso

        countDownTimer = new CountDownTimer(tiempoRestante, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Actualizar el tiempo restante en segundos y mostrarlo en el TextView
                long segundos = millisUntilFinished / 1000;
                ContadorTiempo.setText(String.valueOf(segundos));
            }

            @Override
            public void onFinish() {
                // El temporizador ha finalizado, realizar alguna acción si es necesario
                // Por ejemplo, generar una nueva actividad
                generateRandomActivity();
                // Reiniciar el temporizador
                startTimer();
            }
        };
        countDownTimer.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
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
}