package com.example.app_taller;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RegistrarFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    RequestQueue rq;
    JsonRequest jrq;
    EditText txtCorreo, txtPassword, txtNombre, txtDireccion, txtTelefono;
    Button btnRegistrar, btnSesion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_sesion, container, false);
        txtCorreo = (EditText) vista.findViewById(R.id.txtCorreo);
        txtPassword = (EditText) vista.findViewById(R.id.txtPassword);
        txtNombre = (EditText) vista.findViewById(R.id.txtNombre);
        txtDireccion= (EditText) vista.findViewById(R.id.txtDireccion);
        txtTelefono=(EditText)vista.findViewById(R.id.txtTelefono);
        btnSesion=(Button) vista.findViewById(R.id.btnSesion);

        btnRegistrar = (Button) vista.findViewById(R.id.btnRegistrar);
        rq = Volley.newRequestQueue(getContext());




        btnSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciar_sesion();
            }
        });


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar_usuario();
            }
        });


        // Inflate the layout for this fragment
        return  inflater.inflate(R.layout.fragment_registrar, container, false);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No Se puedo registrar el usuario " +error.toString()+ txtCorreo.getText().toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(getContext(), "Se registró correctamente el usuario " + txtCorreo.getText().toString(), Toast.LENGTH_SHORT).show();
      limpiarCajas();



    }


    void limpiarCajas() {
        txtCorreo.setText("");
        txtPassword.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");

    }




    void iniciar_sesion() {

        SesionFragment fr=new SesionFragment();
        //fr.setArguments(bn);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.escenario,fr)
                .addToBackStack(null)
                .commit();
    }

    void registrar_usuario(){
        //192.168.1.66(172.29.243.3
        String url = "https://apptallerya.000webhostapp.com//registrar.php?&correo_cliente="+ txtCorreo.getText().toString() + "&password_cliente=" + txtPassword.getText().toString()
                +"&nombre_cliente=" + txtNombre.getText().toString() + "&direccion_cliente=" +txtDireccion.getText().toString() +"&telefono_cliente=" +txtTelefono.getText().toString();

        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        rq.add(jrq);
    }
}