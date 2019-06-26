package com.example.app_taller;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    public static final String nombre_cliente="nombre_cliente";
    TextView txtBienvenido;
    @Override
    //jeje
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intencion =  new Intent(getApplicationContext(), MenuSlideActivity.class);
        startActivity(intencion);

        txtBienvenido=(TextView)findViewById(R.id.txtbienvenido);
        String cliente=getIntent().getStringExtra("nombre_cliente");
        txtBienvenido.setText("¡Bienvenido "+ cliente + "!");

    }
}
