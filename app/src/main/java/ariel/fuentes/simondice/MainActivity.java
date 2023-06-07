package ariel.fuentes.simondice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView frace;
    private GestureDetector gestos;
    MediaPlayer mediaPlayer;
    int paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frace=findViewById(R.id.frace);
        gestos = new GestureDetector(this, new EscuchGestos());
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

    public void play (View view){
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

}