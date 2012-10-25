package com.bourke.glimmrpro.tasks;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;

import android.net.Uri;

import android.os.AsyncTask;

import com.bourke.glimmrpro.common.Constants;
import com.bourke.glimmrpro.common.FlickrHelper;
import com.bourke.glimmrpro.event.Events.IRequestTokenReadyListener;
import com.bourke.glimmrpro.R;

import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import java.net.URL;

/**
 *
 */
public class GetRequestToken extends AsyncTask<Void, Integer, String> {

    private static final String TAG = "Glimmr/GetRequestToken";

    private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private IRequestTokenReadyListener mListener;
    private Uri mOAuthCallbackUri;

    public GetRequestToken(IRequestTokenReadyListener listener, Activity a) {
        super();
        mListener = listener;
        mActivity = a;
        mOAuthCallbackUri = Uri.parse(
                mActivity.getString(R.string.callback_scheme) + "://oauth");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mActivity, "",
                mActivity.getString(R.string.just_a_moment));
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                GetRequestToken.this.cancel(true);
            }
        });
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Flickr f = FlickrHelper.getInstance().getFlickr();

            OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(
                    mOAuthCallbackUri.toString());
            saveRequestToken(null, null, null,
                    oauthToken.getOauthTokenSecret());

            URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(
                    Permission.WRITE, oauthToken);

            return oauthUrl.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveRequestToken(String userName, String userId,
            String token, String tokenSecret) {
        SharedPreferences sp = mActivity.getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.KEY_OAUTH_TOKEN, token);
        editor.putString(Constants.KEY_TOKEN_SECRET, tokenSecret);
        editor.putString(Constants.KEY_USER_NAME, userName);
        editor.putString(Constants.KEY_USER_ID, userId);
        editor.commit();
    }

    @Override
    protected void onPostExecute(String result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mListener.onRequestTokenReady(result);
    }
}
