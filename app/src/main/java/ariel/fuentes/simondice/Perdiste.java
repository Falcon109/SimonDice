package ariel.fuentes.simondice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

public class Perdiste extends AppCompatActivity {

    private TextView ElMejorPuntaje;
    private TextView UltimoPuntajeObtenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perdiste);

        ElMejorPuntaje = findViewById(R.id.PuntajeMejor);
        UltimoPuntajeObtenido = findViewById(R.id.PuntajeObtenido);

        // Cargar las preferencias
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Obtener los valores del mejor puntaje y último puntaje guardados
        int mejorPuntaje = sharedPreferences.getInt("MejorPuntajeObtenido", 0);
        int ultimoPuntaje = sharedPreferences.getInt("ElUltimoPuntajeObtenido", 0);

        // Establecer los valores en los TextView
        ElMejorPuntaje.setText(String.valueOf(mejorPuntaje));
        UltimoPuntajeObtenido.setText(String.valueOf(ultimoPuntaje));
    }

    public void VolveraIntentarlo(View clic) {
        Intent Volver = new Intent(this, ModoNormal.class);
        startActivity(Volver);
    }

}
