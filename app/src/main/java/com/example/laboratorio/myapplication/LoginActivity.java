package com.example.laboratorio.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    private Button btnIngreso;
    private EditText user,contra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = findViewById(R.id.et_email);
        contra = findViewById(R.id.et_pass);
        btnIngreso = findViewById(R.id.btn_login);

        btnIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread tr = new Thread(){
                    @Override
                    public void run() {
                        final String resultado = enviarPost(user.getText().toString() , contra.getText().toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int r = objJSON(resultado);
                                if (r > 0){
                                    Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                } else{
                                    Toast.makeText(getApplicationContext(), "Usuario o Contraseña incorrectos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };
                tr.start();
            }
        });

    }

    public String enviarPost(String email, String pass){
        String parametros= "email="+email+"&pass="+pass;
        HttpURLConnection connection = null;
        String respuesta="";
        try{

            // Ir a la web y envia los datos

            URL url = new URL("http://192.168.56.1/forestal/validar.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Leng",""+Integer.toString(parametros.getBytes().length));

            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(parametros);
            wr.close();

            // Permite tomar la respuesta que envia el WebService y recorrerla para ponerla en la variable

            Scanner inStream = new Scanner(connection.getInputStream());

            while (inStream.hasNextLine()){
                respuesta+=(inStream.nextLine());
            }

        } catch (Exception e){

        }

        return respuesta.toString();
    }

    //Este metodo convierte el return del metodo anterior a JSON para poder contarlo
    public int objJSON(String rspta){
        int res=0;
        try {
            JSONArray jsonArray=new JSONArray(rspta);
            if (jsonArray.length()>0){
                res = 1;
            }
        } catch (Exception e){

        }
        return res;
    }

    /*public String enviarDatosGet(String email , String pass){

        URL url=null;
        String linea ="";
        int respuesta=0;
        StringBuilder resul = null;

        try {
            url = new URL("http://192.168.56.1/forestal/validar.php?email="+email+"&pass="+pass);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            respuesta = connection.getResponseCode();
            resul = new StringBuilder();

            if (respuesta == HttpURLConnection.HTTP_OK){
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                while((linea=reader.readLine())!=null){
                    resul.append(linea);
                }
            }
        } catch (Exception e){

        }

        return resul.toString();
    }*/

    /*public int obtDatosJSON(String response){
        int res=0;
        try {
            JSONArray json = new JSONArray(response);
            if(json.length()>0){
                res=1;
            }
        } catch (Exception e){

        }

        return res;
    }*/
}
