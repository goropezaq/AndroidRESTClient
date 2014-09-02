package com.rest.usuario.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AndroidRESTPeopleActivity extends Activity {
	 private  String SERVICE_URL = "";
	 private String host="";
	 private static final String TAG = "AndroidRESTPeopleActivity";
	 private ListView mainListView ;
	 private ArrayAdapter<String> listAdapter ;
	 private JSONObject jso ;
	 
	 	//llamada a la actividad cuando es creada
	 	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.list_users); 
	        host = getIntent().getExtras().getString("host");
	        SERVICE_URL = "http://"+host+":8080/GiveMeOneBookApp/rest/group";
	        }
	        	 
	 	public void obtenerDatos(View vw) {
	 		
	        String sampleURL = SERVICE_URL + "/users";	 
	        WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Obteniendo Datos...");
	        wst.execute(new String[] { sampleURL });	         
	    }
	 	

	 	public void handleResponse(final String response) {
	 		
	        try { 	

	            jso = new JSONObject(response);
	            JSONArray arr = jso.getJSONArray("user");
	            mainListView = (ListView) findViewById( R.id.mainListView );
            	String[] planets = new String[]{};  
            	ArrayList<String> planetList = new ArrayList<String>();            	
            	planetList.addAll( Arrays.asList(planets) );
            	listAdapter = new ArrayAdapter<String>(this, R.layout.list_simple_row, planetList);
            	
	            for (int i = 0; i < arr.length(); i++){
	            	//String emailUsu = arr.getJSONObject(i).getString("emaUsu");
	            	String nomUsu = arr.getJSONObject(i).getString("nomUsu");
	            	String apeUsu = arr.getJSONObject(i).getString("apeUsu");
	            	//String docIdeUsu = arr.getJSONObject(i).getString("docIdeUsu");
	            	//User user = new User(Integer.parseInt(id), firstName, lastName, email);

	            	listAdapter.add(nomUsu+" "+apeUsu);              	
	            	//Log.d("veeeer",i+": "+person.getEmail()+" "+person.getFirstName()+" "+person.getLastName()+" "+person.getId());
	            }
	            
	            mainListView.setAdapter( listAdapter );	            
	    		mainListView.setTextFilterEnabled(true);
	    		
	    		mainListView.setOnItemClickListener(new OnItemClickListener() {
	    			
	    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    				 
	    				try {
							//Log.d("LOGGGG",jso.toString());
							JSONArray arr = jso.getJSONArray("user");
							
							String email = arr.getJSONObject(position).getString("emaUsu");
			            	String nomUsu = arr.getJSONObject(position).getString("nomUsu");
			            	String apeUsu = arr.getJSONObject(position).getString("apeUsu");
			            	String docIdeUsu = arr.getJSONObject(position).getString("docIdeUsu");
			            	
			            	String[] arrPerson = {email,nomUsu,apeUsu,docIdeUsu,host};
			            	Intent intent = new Intent(AndroidRESTPeopleActivity.this,AndroidRESTPersonActivity.class);
			            	intent.putExtra("arrPerson", arrPerson);
			            	startActivity(intent);
			    		    finish();
						} catch (JSONException e) {

							e.printStackTrace();
						}
	    			}
	    		});  
   
	        } catch (Exception e) {
	            Log.e(TAG, e.getLocalizedMessage(), e);
	        }
	         
	    }
	 	

	 	 
	 	 private class WebServiceTask extends AsyncTask<String, Integer, String> {
	 		 
	         public static final int POST_TASK = 1;
	         public static final int GET_TASK = 2;
	          
	         private static final String TAG = "WebServiceTask";
	  
	         //tiempo de espera de conexion en milisegundos
	         private static final int CONN_TIMEOUT = 5000;
	          
	         // tiempo de espera para obtener data
	         private static final int SOCKET_TIMEOUT = 7000;
	          
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
	  
	  
	         @SuppressWarnings("deprecation")
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
	            // Log.d("APP",result);
	             return result;
	         }
	  
	         @Override
	         protected void onPostExecute(String response) {
	              
	             handleResponse(response);
	             pDlg.dismiss();
	              
	         }
	          
	         private HttpParams getHttpParams() {
	              
	             HttpParams htpp = new BasicHttpParams();
	              
	             HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
	             HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);	              
	             return htpp;
	         }
	          
	         private HttpResponse doResponse(String url) {

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

	             return total.toString();
	         }
	  
	     }
	 

}
