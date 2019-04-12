package mx.uv.divisasonline;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.uv.divisasonline.beans.Divisas;
import mx.uv.divisasonline.ws.HttpUtils;
import mx.uv.divisasonline.ws.Response;

public class ConvertidorMultipleActivity extends AppCompatActivity {

    private EditText txt_importe;
    private TextView lbl_resultado;
    private Spinner spn_monedaOrigen;
    private Spinner spn_monedaDestino;

    private Double cantidad;
    private HashMap<String, String> monedas;

    private Response resws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convertidor_multiple);

        txt_importe = findViewById(R.id.txt_importe);
        lbl_resultado = findViewById(R.id.lbl_resultado);
        spn_monedaOrigen = findViewById(R.id.spn_monedaOrigen);
        spn_monedaDestino = findViewById(R.id.spn_monedaDestino);

        WSConsultaMonedasTask taskMonedas = new WSConsultaMonedasTask();
        taskMonedas.execute();
    }


    class WSConsultaMonedasTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground (String ... params) {
            resws = HttpUtils.getMonedas();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            cargarMonedasASpinners();
        }
    }

    private void cargarMonedasASpinners() {
        if (resws != null && !resws.isError()) {
            monedas = (HashMap<String,String>)
                    new Gson().fromJson(resws.getResult(), HashMap.class);
            if (monedas!=null) {
                List<String> listmonedas = new ArrayList<>();
                for (Map.Entry<String,String> entry : monedas.entrySet()) {
                    listmonedas.add(entry.getValue());
                }
                Collections.sort(listmonedas);
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listmonedas);
                this.spn_monedaDestino.setAdapter(adapter);
                this.spn_monedaOrigen.setAdapter(adapter);
            }
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage(resws.getResult());
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
    }

    public void calcular(View v) {
        if (txt_importe.getText().toString().isEmpty()) {
            txt_importe.setError("Introduce la cantidad a convertir");
            return;
        }

        cantidad = Double.parseDouble(txt_importe.getText().toString());

        WSConsultaDivisasTask taskDivisas = new WSConsultaDivisasTask();
        taskDivisas.execute();
    }

    class WSConsultaDivisasTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String ... params) {
            resws = HttpUtils.getDivisas();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            procesarResultado();
        }
    }

    private void procesarResultado() {
        if (resws != null && !resws.isError()) {
            Divisas div = new Gson().fromJson(resws.getResult(), Divisas.class);
            String key1 = getKeyMoneda(String.valueOf(this.spn_monedaOrigen.getSelectedItem()));
            String key2 = getKeyMoneda(String.valueOf(this.spn_monedaDestino.getSelectedItem()));

            Double usd = 0.0;
            Double resultado = 0.0;
            Double ratio = div.getRates().get(key1);
            usd = cantidad / ratio;

            Double ratio2 = div.getRates().get(key2);

            resultado = usd * ratio2;
            lbl_resultado.setText(String.format("%,.2f",resultado));
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage(resws.getResult());
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
    }

    private String getKeyMoneda(String moneda) {
        if(monedas != null) {
            for (Map.Entry<String,String> entry : monedas.entrySet()) {
                if (entry.getValue().compareToIgnoreCase(moneda)==0) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public void regresar(View v) {
        this.finish();
    }
}
