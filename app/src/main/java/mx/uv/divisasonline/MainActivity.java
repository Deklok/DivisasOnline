package mx.uv.divisasonline;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;

import mx.uv.divisasonline.ws.HttpUtils;
import mx.uv.divisasonline.ws.Response;
import mx.uv.divisasonline.beans.Divisas;

public class MainActivity extends AppCompatActivity {
    private EditText txt_importe;
    private TextView lbl_resultado;
    private Double cantidad;

    private Response resws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_importe = findViewById(R.id.txt_importe);
        lbl_resultado = findViewById(R.id.lbl_resultado);
    }

    public void consultar(View v) {
        if (txt_importe.getText().toString().isEmpty()) {
            txt_importe.setError("Introduce la cantidada convertir");
            return;
        }

        cantidad = Double.parseDouble(txt_importe.getText().toString());

        WSConsultaDivisasTask task = new WSConsultaDivisasTask();
        task.execute();
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

        private void procesarResultado(){
            if (resws!=null && !resws.isError()) {
                Divisas div = new Gson().fromJson(resws.getResult(), Divisas.class);
                RadioButton btn1 = (RadioButton)findViewById(R.id.btn_mx_us);
                Double resultado = 0.0;
                Double ratio = div.getRates().get("MXN");
                if(btn1.isChecked()) {
                    resultado = cantidad * ratio;
                } else {
                    resultado = cantidad / ratio;
                }
                lbl_resultado.setText(String.format("%.2f",resultado));
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

        public void cambiarActividad(View v) {
            Intent i = new Intent(this, ConvertidorMultipleActivity.class);
            startActivity(i);
        }

}
