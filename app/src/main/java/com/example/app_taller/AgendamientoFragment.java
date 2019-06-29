package com.example.app_taller;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.hardware.Camera;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;


public class AgendamientoFragment extends Fragment {
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    private static final String CARPETA_PRINCIPAL = "misImagenesApp/";
    private static final String CARPETA_IMAGEN = "imagenes";
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;
    private String path;
    File fileImagen;
    Bitmap bitmap;


    private EditText txtData;
    private ImageView ImgID;
    private EditText txtObs;
    private ImageButton btnSubir;
    private Button btnEnviar;


    ProgressDialog progreso;
    RequestQueue request;
   StringRequest stringRequest;

    public AgendamientoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_agendamiento, container, false);
        txtData = (EditText) vista.findViewById(R.id.txtFecha);
        txtObs = (EditText) vista.findViewById(R.id.txtObservaciones);
        ImgID = (ImageView) vista.findViewById(R.id.imgID);
        btnSubir = (ImageButton) vista.findViewById(R.id.ImgBtnSubir);
        btnEnviar = (Button) vista.findViewById(R.id.btnEnviar);
        request = Volley.newRequestQueue(getContext());

        if(solicitaPermisosVersionesSuperiores()){
            btnSubir.setEnabled(true);

        }else{
            btnEnviar.setEnabled(false);

        }

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarWebService();
            }
        });

        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogOpciones();
            }
        });
        return vista;
    }

    private boolean solicitaPermisosVersionesSuperiores() {
    if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
        return true;

    }
        if (( getActivity().checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED)&&( getActivity().checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
return true;
        }
        if ((shouldShowRequestPermissionRationale(CAMERA)) || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))) {
        cargarDialogoRecomendacion();
        }else
        {

            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA},100);
        }


        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults.length==2&&grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED){
               btnSubir.setEnabled(true);
            }else{
                solicitarPermisosManual();
                
            }

        }
    }

    private void solicitarPermisosManual() {

        final CharSequence[] opciones = {"SI", "NO"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Desea configurar los permisos de forma manual?");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (opciones[i].equals("SI")) {
                    Intent intent= new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri= Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Los permisos no fueron aceptados", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    }

                }

        });
        builder.show();

    }

    private void cargarDialogoRecomendacion() {
    AlertDialog.Builder dialogo= new AlertDialog.Builder(getContext());
    dialogo.setTitle("Permisos Desactivados");
    dialogo.setMessage("Debo aceptar os permisos para el correcto funcionamiento del app");
    dialogo.setPositiveButton("Aceptar",  new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int i) {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA},100);
        }
    });
    dialogo.show();
    }


    private void mostrarDialogOpciones() {
        final CharSequence[] opciones = {"Tomar foto", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Elige una Opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (opciones[i].equals("Tomar foto")) {
                    abrirCamara();
                }
                /*else {
                    if (opciones[i].equals("Elegir de Galeria")) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("/image");
                        startActivityForResult(intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);
                    } */

                else {
                        dialog.dismiss();

                    }

                }

        });
        builder.show();
    }

    private void abrirCamara() {
        File miFile = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isCreada = miFile.exists();
        if (isCreada == false) {
            isCreada = miFile.mkdirs();

        }
        if (isCreada == true) {
            Long consecutivo = System.currentTimeMillis() / 1000;
            String nombre = consecutivo.toString() + ".jpg";


            path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN + File.separator + nombre;
            fileImagen = new File(path);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                String authorities=getContext().getApplicationContext().getPackageName()+".provider";
                Uri imageUri= FileProvider.getUriForFile(getActivity().getApplicationContext(), authorities, fileImagen);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }else{

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            }
            startActivityForResult(intent, COD_FOTO);

        }
    }


    private void cargarWebService() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String url = "https://apptallerya.000webhostapp.com//agendamiento.php?";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener <String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();
                if (response.trim().equalsIgnoreCase("registra")) {
                    txtData.setText("");
                    txtObs.setText("");
                    Toast.makeText(getContext(), "Su pedido ha sido registrado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No se puedo procesar su pedido, intente más tarde", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No se ha podido conectar. Faltan datos", Toast.LENGTH_SHORT).show();
                progreso.hide();
            }
        }) {
            @Override
            protected Map <String, String> getParams() throws AuthFailureError {
                String fecha = txtData.getText().toString();
                String observacion = txtObs.getText().toString();

                String foto_vehiculo = convertirImgString(bitmap);

                Map <String, String> parametros = new HashMap <>();
                parametros.put("fecha", fecha);
                parametros.put("foto_vehiculo", foto_vehiculo);
                parametros.put("observacion", observacion);
                return parametros;

            }
        }
        ;
        request.add(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap) {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imageBytes = array.toByteArray();
        String imagenString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return imagenString;

    }

    private void subirImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("Image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione la Aplicación"), 10);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case COD_SELECCIONA:
                Uri miPath = data.getData();
                ImgID.setImageURI(miPath);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), miPath);
                    ImgID.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case COD_FOTO:
                MediaScannerConnection.scanFile(getContext(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("Path", "" + path);
                    }
                });

                bitmap = BitmapFactory.decodeFile(path);
                ImgID.setImageBitmap(bitmap);

                break;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }


}

