package com.example.app_taller;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


import java.util.Calendar;


public class AgendamientoFragment extends Fragment implements Response.ErrorListener, Response.Listener <JSONObject> {

    private static final String CERO = "0";
    private static final String BARRA = "/";
    private static final String DOS_PUNTOS = ":";
    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //Widgets
    EditText etFecha;
    ImageButton ibObtenerFecha;


    private EditText txtObs;

    private Button btnEnviar;

    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    //Widgets
    EditText etHora;
    ImageButton ibObtenerHora;
    ProgressDialog progreso;
    RequestQueue request;
    StringRequest stringRequest;
    JsonObjectRequest jqr;

    public AgendamientoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_agendamiento, container, false);
        txtObs = (EditText) vista.findViewById(R.id.txtObservaciones);
        btnEnviar = (Button) vista.findViewById(R.id.btnEnviar);
        request = Volley.newRequestQueue(getContext());


        etFecha = (EditText) vista.findViewById(R.id.et_mostrar_fecha_picker);
        //Widget ImageButton del cual usaremos el evento clic para obtener la fecha
        ibObtenerFecha = (ImageButton) vista.findViewById(R.id.ib_obtener_fecha);
        //Evento setOnClickListener - clic
        etHora = (EditText) vista.findViewById(R.id.et_mostrar_hora_picker);
        //Widget ImageButton del cual usaremos el evento clic para obtener la hora
        ibObtenerHora = (ImageButton) vista.findViewById(R.id.ib_obtener_hora);
        //Evento setOnClickListener - clic

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {
                            Manifest.permission.SEND_SMS,
                    }, 1000);
        } else {

        }
        ;


        ibObtenerHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ib_obtener_hora:
                        obtenerHora();
                        break;
                }
            }
        });

        ibObtenerFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ib_obtener_fecha:
                        obtenerFecha();
                        break;
                }
            }
        });


        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargaconget();
                enviarMensaje("983638421", "Fecha : " + etFecha.getText().toString() + " Hora: " + etHora.getText().toString() + "OBS: " + txtObs.getText().toString());
                //cargarWebService();
            }
        });


        return vista;
    }


    private void obtenerHora() {
        TimePickerDialog recogerHora = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                String horaFormateada = (hourOfDay < 10) ? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                String minutoFormateado = (minute < 10) ? String.valueOf(CERO + minute) : String.valueOf(minute);
                //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                String AM_PM;
                if (hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                //Muestro la hora con el formato deseado
                etHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, false);

        recogerHora.show();
    }


    private void obtenerFecha() {
        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                etFecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);

            }
        }, anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();

    }


    private void cargaconget() {

        String url = "https://apptallerya.000webhostapp.com//agendamientoget.php?&fecha=" + etFecha.getText().toString() + "&observacion=" + txtObs.getText().toString() + "&hora=" + etHora.getText().toString();

        jqr = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jqr);
    }

    private void enviarMensaje(String numero, String mensaje) {
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero, null, mensaje, null, null);
            Toast.makeText(getContext().getApplicationContext(), "Mensaje Enviado", Toast.LENGTH_LONG).show();


        } catch (Exception e) {
            Toast.makeText(getContext().getApplicationContext(), "Mensaje no Enviado, Datos Incorrectos", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No Se pudo registrar la fecha: " + error.toString() + etFecha.getText().toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(getContext(), "Se registró correctamente. En breve el taller retornará la información " + etFecha.getText().toString(), Toast.LENGTH_SHORT).show();


    }
}

