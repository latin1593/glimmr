package com.bourke.glimmrpro.fragments.home;

import android.content.Context;
import android.content.SharedPreferences;

import android.util.Log;

import com.bourke.glimmrpro.common.Constants;
import com.bourke.glimmrpro.fragments.base.PhotoGridFragment;
import com.bourke.glimmrpro.tasks.LoadFavoritesTask;

import com.googlecode.flickrjandroid.photos.Photo;

public class FavoritesGridFragment extends PhotoGridFragment {

    private static final String TAG = "Glimmr/FavoritesGridFragment";

    private static final String KEY_NEWEST_FAVORITES_PHOTO_ID =
        "glimmr_newest_favorites_photo_id";

    public static FavoritesGridFragment newInstance() {
        return new FavoritesGridFragment();
    }

    /**
     * Once the parent binds the adapter it will trigger cacheInBackground
     * for us as it will be empty when first bound.  So we don't need to
     * override startTask().
     */
    @Override
    protected boolean cacheInBackground() {
        startTask(mPage++);
        return mMorePages;
    }

    private void startTask(int page) {
        super.startTask();
        new LoadFavoritesTask(this, mActivity.getUser(), page).execute(mOAuth);
    }

    @Override
    public String getNewestPhotoId() {
        SharedPreferences prefs = mActivity.getSharedPreferences(Constants
                .PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_NEWEST_FAVORITES_PHOTO_ID, null);
    }

    @Override
    public void storeNewestPhotoId(Photo photo) {
        SharedPreferences prefs = mActivity.getSharedPreferences(Constants
                .PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NEWEST_FAVORITES_PHOTO_ID, photo.getId());
        editor.commit();
        if (Constants.DEBUG)
            Log.d(getLogTag(), "Updated most recent favorites photo id to " +
                photo.getId());
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }
}
