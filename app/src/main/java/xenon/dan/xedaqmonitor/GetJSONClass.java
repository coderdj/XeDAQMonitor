package xenon.dan.xedaqmonitor;

import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Jacked entire class from here http://stackoverflow.com/questions/14670677/getjsonfromurl-null-pointer-exception
 * on 3/16/15.Modified to fit my needs
*/

public class GetJSONClass extends AsyncTask<String, Integer, JSONArray> {


    protected JSONArray doInBackground(String... strings ) {
        // Should be strings[0] URL [1] CSRF [2] SESSION [3] BASE_URL
        InputStream is = null;
        String result = "";
        JSONArray jArray = null;

        // Store the cookies in a new cookie store
        CookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie scookie = new BasicClientCookie("sessionid", strings[2]);
        BasicClientCookie ccookie = new BasicClientCookie("csrftoken", strings[1]);
        ccookie.setDomain("130.92.139.69");
        scookie.setDomain("130.92.139.69");
        ccookie.setPath("/");
        scookie.setPath("/");
        cookieStore.addCookie(ccookie);
        cookieStore.addCookie(scookie);

        // http post
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.setCookieStore(cookieStore);

            HttpPost httppost = new HttpPost(strings[0]);
            Log.d("cookiestore",cookieStore.toString());
            httppost.addHeader("Cookie","csrftoken="+strings[1]+"; sessionid="+strings[2]+";");
            httppost.addHeader("X-CSRFToken", strings[1]);

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            HttpResponse response = httpclient.execute(httppost, localContext);

            HttpEntity entity= response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("get data string ",
                    "Error converting result " + e.toString());
        }
        Log.e("log_tag printstring",result);

        try {

            jArray = new JSONArray(result);
        } catch (JSONException e) {
            Log.e("log_tag create object ",
                    "Error parsing data " + e.toString());
        }

        return jArray;
    }

    protected void onPostExecute(JSONArray result) {
        try {
            Log.e("output", result.toString());
        }
        catch ( Exception e ) {
            Log.e("error", "Your result is not JSON format.");
        }
     }
}