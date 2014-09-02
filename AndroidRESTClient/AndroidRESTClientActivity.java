package com.example.givemeabookapp;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
 
public class AndroidRESTClientActivity extends Activity {
 /*
  En realidad, * no se puede * utilizar "localhost".
  "Localhost" en este contexto se referiría al propio dispositivo Android.
   Utilice la dirección IP del ordenador de la red que se está ejecutando el servicio Tomcat.. 
   Vea este enlace StackOverflow para más detalles.) 
   http://stackoverflow.com/questions/4905315/error-connection-refused
  */
    private static final String SERVICE_URL = "http://192.168.1.33:8080/RestWebServiceDemo/rest/directory";
    //private static final String SERVICE_URL = "http://10.0.2.2:8080/RestWebServiceDemo/rest/person";
    //private static final String SERVICE_URL = "http://localhost:8080/RestWebServiceDemo/rest/person";
    
    
 
    private static final String TAG = "AndroidRESTClientActivity";
     
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.book_list);
        setContentView(R.layout.activity_main);// R.layout.main);
    }
 
    public void retrieveSampleData(View vw) {
 
        String sampleURL = SERVICE_URL + "/person/2";
 
        WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "GETting data...");
         
        wst.execute(new String[] { sampleURL });
         
    }
 
    public void clearControls(View vw) {
    	EditText edIDPerson = (EditText) findViewById(R.id.id_person);
        EditText edFirstName = (EditText) findViewById(R.id.first_name);
        EditText edLastName = (EditText) findViewById(R.id.last_name);
        EditText edEmail = (EditText) findViewById(R.id.email);
 
        edIDPerson.setText("");
        edFirstName.setText("");
        edLastName.setText("");
        edEmail.setText("");
                 
    }
     
    public void postData(View vw) {
 
    	EditText edIDPerson = (EditText) findViewById(R.id.id_person);
        EditText edFirstName = (EditText) findViewById(R.id.first_name);
        EditText edLastName = (EditText) findViewById(R.id.last_name);
        EditText edEmail = (EditText) findViewById(R.id.email);
 
        String idPerson = edIDPerson.getText().toString();
        String firstName = edFirstName.getText().toString();
        String lastName = edLastName.getText().toString();
        String email = edEmail.getText().toString();
 
        System.out.println("aquii"+ edFirstName);
        if (firstName.equals("") || lastName.equals("") || email.equals("")) {
            Toast.makeText(this, "Please enter in all required fields.",
                    Toast.LENGTH_LONG).show();
            return;
        }
 
        WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Posting data...");
        wst.addNameValuePair("id", idPerson);
        wst.addNameValuePair("firstName", firstName);
        wst.addNameValuePair("lastName", lastName);
        wst.addNameValuePair("email", email);
 
        // the passed String is the URL we will POST to
        wst.execute(new String[] { SERVICE_URL });
 
    }
 
    public void handleResponse(String response) {
    	EditText edIDPerson = (EditText) findViewById(R.id.id_person);
        EditText edFirstName = (EditText) findViewById(R.id.first_name);
        EditText edLastName = (EditText) findViewById(R.id.last_name);
        EditText edEmail = (EditText) findViewById(R.id.email);
        
        edIDPerson.setText("");
        edFirstName.setText("");
        edLastName.setText("");
        edEmail.setText("");
         
        try {
             
            JSONObject jso = new JSONObject(response);
            String idPerson = jso.getString("id");
            String firstName = jso.getString("firstName");
            String lastName = jso.getString("lastName");
            String email = jso.getString("email");
             
            edIDPerson.setText(idPerson);
            edFirstName.setText(firstName);
            edLastName.setText(lastName);
            edEmail.setText(email);         
             
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
         
    }
 
    private void hideKeyboard() {
 
        InputMethodManager inputManager = (InputMethodManager) AndroidRESTClientActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
 
        inputManager.hideSoftInputFromWindow(
                AndroidRESTClientActivity.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
     
     
    /*
     - Para este tutorial, el código más importante es la clase interna "WebServiceTask", 
     que se extiende desde una clase "AsyncTask". 
     - Un descendiente de clase AsyncTask permite un proceso se ejecute en un subproceso 
     independiente. 
     - Si nuestra comunicación con nuestro servicio fueron el hilo principal de la aplicación
      para Android, la interfaz de usuario sería bloqueada a medida que el proceso estaba
      esperando resultados del servidor. 
     */
    /*
     - Uno puede definir los tipos de parámetros que se pasan a uno de instancia de la AsyncTask.
     - La comunicación con el servicio web se produce en el código del WebServiceTask "doInBackground ()". Este 
       código utiliza objetos HttpClient de Android, y para el método GET, utiliza HttpGet, 
       y para el método POST, utiliza HttpPost
     */
    /*
     La clase AsyncTask incluye otros dos métodos que uno tiene la opción de sobrescribir:
     - Uno es OnPreExecute (), que se puede utilizar para preparar el proceso de fondo, y
     -  el otro es onPostExecute (), que se puede utilizar para hacer cualquier requerido limpieza
         después del proceso de fondo es completa.
     - Este código reemplaza los métodos para visualizar y eliminar un diálogo de progreso.  
     */
    
    /*
     - La tarea de fondo también incluye dos opciones de tiempo de espera.
     -- Se trata de un período de tiempo de espera para la conexión real con el servicio,
     -- y el otro es un período de tiempo de espera para la espera de la respuesta del servicio.
     */
    private class WebServiceTask extends AsyncTask<String, Integer, String> {
 
        public static final int POST_TASK = 1;
        public static final int GET_TASK = 2;
         
        private static final String TAG = "WebServiceTask";
 
        // connection timeout, in milliseconds (waiting to connect)
        private static final int CONN_TIMEOUT = 6000;
         
        // socket timeout, in milliseconds (waiting for data)
        private static final int SOCKET_TIMEOUT = 8000;
         
        private int taskType = GET_TASK;
        private Context mContext = null;
        private String processMessage = "Processing...";
 
        private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
 
        private ProgressDialog pDlg = null;
 
        public WebServiceTask(int taskType, Context mContext, String processMessage) {
 
            this.taskType = taskType;
            this.mContext = mContext;
            this.processMessage = processMessage;
        }
 
        public void addNameValuePair(String name, String value) {
 
            params.add(new BasicNameValuePair(name, value));
        }
 
        private void showProgressDialog() {
             
            pDlg = new ProgressDialog(mContext);
            pDlg.setMessage(processMessage);
            pDlg.setProgressDrawable(mContext.getWallpaper());
            pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDlg.setCancelable(false);
            pDlg.show();
 
        }
 
        @Override
        protected void onPreExecute() {
 
            hideKeyboard();
            showProgressDialog();
 
        }
 
        protected String doInBackground(String... urls) {
 
            String url = urls[0];
            String result = "";
 
            HttpResponse response = doResponse(url);
 
            if (response == null) {
                return result;
            } else {
 
                try {
 
                    result = inputStreamToString(response.getEntity().getContent());
 
                } catch (IllegalStateException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
 
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                }
 
            }
            
            Log.d("APP",result);
            return result;
        }
 
        @Override
        protected void onPostExecute(String response) {
             
            handleResponse(response);
            pDlg.dismiss();
             
        }
         
        // Establish connection and socket (data retrieval) timeouts
        private HttpParams getHttpParams() {
             
            HttpParams htpp = new BasicHttpParams();
             
            HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);
             
            return htpp;
        }
         
        private HttpResponse doResponse(String url) {
             
            // Use our connection and data timeouts as parameters for our
            // DefaultHttpClient
            HttpClient httpclient = new DefaultHttpClient(getHttpParams());
 
            HttpResponse response = null;
 
            try {
                switch (taskType) {
 
                case POST_TASK:
                    HttpPost httppost = new HttpPost(url);
                    // Add parameters
                    httppost.setEntity(new UrlEncodedFormEntity(params));
 
                    response = httpclient.execute(httppost);
                    break;
                case GET_TASK:
                    HttpGet httpget = new HttpGet(url);
                    response = httpclient.execute(httpget);
                    break;
                }
            } catch (Exception e) {
 
                Log.e(TAG, e.getLocalizedMessage(), e);
 
            }
 
            return response;
        }
         
        private String inputStreamToString(InputStream is) {
 
            String line = "";
            StringBuilder total = new StringBuilder();
 
            // Wrap a BufferedReader around the InputStream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
 
            try {
                // Read response until the end
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
 
            // Return full string
            return total.toString();
        }
 
    }
}