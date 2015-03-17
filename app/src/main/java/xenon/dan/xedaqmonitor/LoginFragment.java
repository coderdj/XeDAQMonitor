package xenon.dan.xedaqmonitor;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.apache.http.impl.client.BasicCookieStore;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private OnLoginListener mListener;
    private WebView mwebview;


    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if ( mwebview != null )
            mwebview.destroy();
        mwebview = new WebView(getActivity());
        mwebview.setWebViewClient(new InnerWebViewClient()); // forces it to open in app
        mwebview.loadUrl("http://130.92.139.69:8000/login");
Log.d("out", "loaded!");

        String cs = CookieManager.getInstance().getCookie("http://130.92.139.69:8000/login");
        Log.d("out",""+cs);
return mwebview;
        //return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private class InnerWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            MainActivity activity = (MainActivity) getActivity();
            Log.d("out", "shouldOverride:" + activity.GetID() + " " + activity.GetToken() );
            Log.d("test", "LOADING URL: " + url);
            if( url.contains("130.92.139.69:8000/login")) {
            //    if (url.contains("130.92.139.69:8000") ) {
                    view.loadUrl(url);
              //  }
            }
            else {
                String summary = "<html><body><h4>You Already Logged In!</h4><br> " +
                        "<h6>If things aren't working well try closing the app completely " +
                        "and logging in again.</h6></body></html>";
                view.loadData(summary, "text/html", null);
            }
            return true;
        }
         public void onPageFinished(WebView view, String url) {
             if (url.contains("130.92.139.69:8000")) {
                 String cs = CookieManager.getInstance().getCookie("http://130.92.139.69");
                 Log.d("cookie", cs);
                 // Here we parse our cookie. As soon as we receive a 'session_id' we can signal that
                 // this fragment's job is finished
                 String[] rawCookieParams = cs.split(";");
                 String csrf_token = "";
                 String session_id = "";
                 for (int i = 0; i < rawCookieParams.length; i += 1) {
                     String[] rawCookieNameAndValue = rawCookieParams[i].split("=");
                     if (rawCookieNameAndValue.length < 2)
                         continue;
                     String cName = rawCookieNameAndValue[0].trim();
                     String cValue = rawCookieNameAndValue[1].trim();

                     if (cName.contains("csrftoken") )
                         csrf_token = cValue;
                     else if (cName.contains("sessionid") )
                         session_id = cValue;
                 }

                 if ( session_id != "" && csrf_token != "" ) {
                     ((MainActivity) getActivity()).SetCredentials(csrf_token, session_id);
                     Log.d("out","Found tokens!");


                    // String summary = "<html><body>Successfully logged in! <br> " +
                    //         "Please go back to the main app to use functionality.</body></html>";
                    // view.loadData(summary, "text/html", null);
                 }
             }
         }


    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLogin(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        String cs = CookieManager.getInstance().getCookie("http://130.92.139.69:8000/login");
        Log.d("out",""+cs);
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLoginListener {
        // TODO: Update argument type and name
        public void onLogin(Uri uri);
    }

}
