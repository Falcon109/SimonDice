package ariel.fuentes.simondice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Titulo_inicio extends AppCompatActivity {

    MediaPlayer mediaPlayer3;
    ImageView altavoz2;
    boolean isMuted = false;
    int paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titulo_inicio);

        mediaPlayer3 = MediaPlayer.create(this, R.raw.emotionalorchestra);
        mediaPlayer3.setVolume(0.2f,0.2f);
        mediaPlayer3.setLooping(true);
        mediaPlayer3.start();

        altavoz2 = findViewById(R.id.volumen);
        altavoz2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMuted) {
                    // Si está silenciado, reanuda la reproducción y cambia la imagen a la de sonido activo
                    mediaPlayer3.setVolume(1.0f, 1.0f);
                    altavoz2.setImageResource(R.drawable.volume_up);
                    isMuted = false;
                } else {
                    // Si no está silenciado, silencia la reproducción y cambia la imagen a la de sonido silenciado
                    mediaPlayer3.setVolume(0.0f, 0.0f);
                    altavoz2.setImageResource(R.drawable.volume_mute);
                    isMuted = true;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer3.seekTo(paused);
        mediaPlayer3.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer3.pause();
        paused=mediaPlayer3.getCurrentPosition();
    }

    public void IraMenu (View clic1) {
        Intent IraMenu = new Intent(this, MainActivity.class);
        startActivity(IraMenu);
        mediaPlayer3.release();
        mediaPlayer3=null;
    }

}