package ariel.fuentes.simondice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

public class ModoNormal extends AppCompatActivity implements SensorEventListener {

    private TextView Actividad;
    private TextView ContadorTiempo;
    private TextView Nivel;
    private TextView ContadorActividades;
    private ImageView heart1, heart2, heart3;
    private int NumeroActividades;
    private int NumeroNivel;
    private boolean isMuted = false;
    private GestureDetector gestos;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Vibrator vibrator;

    private String[] activities;
    private Random random;
    private int currentActivityIndex;

    private CountDownTimer countDownTimer;
    private long tiempoRestante = 4000; // 4 segundos en milisegundos

    private MediaPlayer mediaPlayerMusicaFondo;
    private MediaPlayer mediaPlayercorrecto;
    private MediaPlayer mediaPlayerincorrecto;
    private int paused;

    private int puntaje; // Variable para almacenar el puntaje obtenido
    private int mejorPuntajeGuardado;
    private SharedPreferences sharedPreferences; // Preferencias para guardar el puntaje

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modonormal);

        FloatingActionButton volumen = findViewById(R.id.Volumen);
        Nivel = findViewById(R.id.Nivel);
        ContadorActividades = findViewById(R.id.Contador);

        NumeroNivel = 1;
        Nivel.setText(getString(R.string.Nivel) + " " + String.valueOf(NumeroNivel) + " -");

        NumeroActividades = 10;
        ContadorActividades.setText(String.valueOf(NumeroActividades));

        ContadorTiempo = findViewById(R.id.tiempotext);

        // Obtener referencias a las ImageView de los corazones
        heart1 = findViewById(R.id.corazon1);
        heart2 = findViewById(R.id.corazon2);
        heart3 = findViewById(R.id.corazon3);

        Actividad = findViewById(R.id.frace);
        gestos = new GestureDetector(this, new EscuchGestos());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mediaPlayerMusicaFondo = MediaPlayer.create(this, R.raw.lets_freaking_do_this);
        mediaPlayerincorrecto = MediaPlayer.create(this, R.raw.incorrecto);
        mediaPlayercorrecto = MediaPlayer.create(this, R.raw.correcto);

        mediaPlayerMusicaFondo.setLooping(true);
        mediaPlayerMusicaFondo.start();

        volumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMuted) {
                    // Si está silenciado, reanuda la reproducción de todos los reproductores de medios
                    setMediaVolume(1.0f);
                    volumen.setImageResource(R.drawable.volume_up);
                    isMuted = false;
                } else {
                    // Si no está silenciado, silencia la reproducción de todos los reproductores de medios
                    setMediaVolume(0.0f);
                    volumen.setImageResource(R.drawable.volume_mute);
                    isMuted = true;
                }
            }
        });

        // Cargar el array activities desde el archivo XML
        activities = getResources().getStringArray(R.array.actividades);
        random = new Random();
        generateRandomActivity();

        // Inicializar las preferencias
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mejorPuntajeGuardado = sharedPreferences.getInt("MejorPuntajeObtenido", 0);
        puntaje = sharedPreferences.getInt("ElUltimoPuntajeObtenido", 0);
    }

    // Método para ajustar el volumen de todos los reproductores de medios
    private void setMediaVolume(float volume) {
        mediaPlayerMusicaFondo.setVolume(volume, volume);
        mediaPlayercorrecto.setVolume(volume, volume);
        mediaPlayerincorrecto.setVolume(volume, volume);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestos.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class EscuchGestos extends GestureDetector.SimpleOnGestureListener {
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            float ancho = Math.abs(e2.getX() - e1.getX());
            float alto = Math.abs(e2.getY() - e1.getY());
            if (ancho > alto) {
                if (e2.getX() > e1.getX()) {
                    checkActivity(R.string.Derecha);
                } else {
                    checkActivity(R.string.Izquierda);
                }
            } else {
                if (e2.getY() > e1.getY()) {
                    checkActivity(R.string.Abajo);
                } else {
                    checkActivity(R.string.Arriba);
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
            checkActivity(R.string.Agitado);

            // Reiniciar el temporizador
            startTimer();
        }
    }

    private void checkActivity(int activityResourceId) {
        String currentActivity = activities[currentActivityIndex];
        String gestureActivity = getString(activityResourceId);

        if (currentActivity.equalsIgnoreCase(gestureActivity)) {
            // La actividad realizada coincide con la actividad actual
            // Incrementar el contador de actividades completadas y actualizar el TextView
            NumeroActividades--;
            ContadorActividades.setText(String.valueOf(NumeroActividades));
            setMediaVolume(0.5f);
            mediaPlayercorrecto.start();

            // Incrementar el puntaje
            puntaje++;

            // Verificar si el contador de actividades llegó a 0
            if (NumeroActividades == 0) {
                // Si se completaron todas las actividades, generar una nueva lista de actividades y aumentar el nivel
                NumeroActividades = 10;
                ContadorActividades.setText(String.valueOf(NumeroActividades));
                NumeroNivel++;
                Nivel.setText(getString(R.string.Nivel) + " " + String.valueOf(NumeroNivel) + " -");
            }

            // Generar una nueva actividad aleatoria
            generateRandomActivity();
        } else {
            // La actividad realizada no coincide con la actividad actual
            // Realizar alguna acción si es necesario (p. ej., mostrar un mensaje de error)
            setMediaVolume(0.5f);
            mediaPlayerincorrecto.start();
            updateLives();
        }
    }

    private void generateRandomActivity() {
        currentActivityIndex = random.nextInt(activities.length);
        String activityText = activities[currentActivityIndex];
        Actividad.setText(activityText);
    }

    private void updateLives() {
        if (heart1.getVisibility() == View.VISIBLE) {
            heart1.setVisibility(View.INVISIBLE);
        } else if (heart3.getVisibility() == View.VISIBLE) {
            heart3.setVisibility(View.INVISIBLE);
        } else if (heart2.getVisibility() == View.VISIBLE) {
            heart2.setVisibility(View.INVISIBLE);
            // Si se pierden todas las vidas, mostrar un mensaje de game over o realizar alguna acción adicional
            gameOver();
        }
    }

    private void gameOver() {
        // Guardar el puntaje en las preferencias
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ElUltimoPuntajeObtenido", puntaje);
        editor.apply();

        if (puntaje > mejorPuntajeGuardado) {
            // El puntaje obtenido es mayor que el mejor puntaje guardado, actualizar el mejor puntaje
            editor = sharedPreferences.edit();
            editor.putInt("MejorPuntajeObtenido", puntaje);
            editor.apply();
        }

        Intent Perdiste = new Intent(this, Perdiste.class);
        startActivity(Perdiste);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayerMusicaFondo.seekTo(paused);
        mediaPlayerMusicaFondo.start();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Iniciar el temporizador al reanudar la actividad
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayerMusicaFondo.pause();
        paused = mediaPlayerMusicaFondo.getCurrentPosition();
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
                // Descuentar un corazón
                updateLives();
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
        mediaPlayerMusicaFondo.release();
        mediaPlayercorrecto.release();
        mediaPlayerincorrecto.release();
    }
}
