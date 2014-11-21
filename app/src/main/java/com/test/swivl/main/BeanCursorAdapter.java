package com.test.swivl.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.swivl.R;
import com.test.swivl.storage.BeansDBAdapter;

import java.util.HashMap;
import java.util.Map;

public class BeanCursorAdapter extends CursorAdapter {
    private static class ViewHolder {
        protected TextView mLoginTextView;
        protected TextView mHtmlUrlTextView;
        protected ImageView mAvatarImageView;
    }
    private MainActivity mMainActivity;
    private Map<ImageView, String> mVisibleImageViewsToIds;

    public BeanCursorAdapter(MainActivity mainActivity, Cursor cursor) {
        super(mainActivity.getBaseContext(), cursor, false);
        mMainActivity = mainActivity;
        mVisibleImageViewsToIds = new HashMap<ImageView, String>();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final int drawableId;
        if (cursor.getPosition() % 2 == 0) {
            drawableId = R.drawable.beige_item_background;
        } else {
            drawableId = R.drawable.white_item_background;
        }
        view.setBackgroundResource(drawableId);

        viewHolder.mLoginTextView.setText(cursor.getString(BeansDBAdapter.LOGIN_COLUMN_INDEX));
        viewHolder.mHtmlUrlTextView.setText(cursor.getString(BeansDBAdapter.HTML_URL_COLUMN_INDEX));

        int id = cursor.getInt(BeansDBAdapter.ID_COLUMN_INDEX);
        String idString = String.valueOf(id);

        // update map of visible views to ids
        mVisibleImageViewsToIds.put(viewHolder.mAvatarImageView, idString);

        // get avatar image from cache if it presents there
        if (mMainActivity.getCache().containsKey(idString)) {
            viewHolder.mAvatarImageView.setImageBitmap(mMainActivity.getCache().get(idString));
        } else {
            // get avatar image from DB or download it asynchronously and set default image
            if (cursor.getBlob(BeansDBAdapter.AVATAR_COLUMN_INDEX) != null) {
                byte[] imageBytes = cursor.getBlob(BeansDBAdapter.AVATAR_COLUMN_INDEX);
                Bitmap bitmap = setBytesToImageView(imageBytes, viewHolder.mAvatarImageView);
                mMainActivity.getCache().put(idString, bitmap);
            } else {
                mMainActivity.downloadImageInBackground(id);
                viewHolder.mAvatarImageView.setImageDrawable(mMainActivity.getResources()
                        .getDrawable(R.drawable.photo_default));
            }
        }
    }

    private Bitmap setBytesToImageView(byte[] imageBytes, ImageView imageView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outHeight = MainActivity.SMALL_AVATAR_SIZE;
        options.outWidth = MainActivity.SMALL_AVATAR_SIZE;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0,
                imageBytes.length, options);
        imageView.setImageBitmap(bitmap);
        return bitmap;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        final View itemView;
        LayoutInflater inflater = LayoutInflater.from(context);
        itemView = inflater.inflate(R.layout.listview_item, null, true);
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.mLoginTextView = (TextView) itemView.findViewById(R.id.listview_item_login_textview);
        viewHolder.mHtmlUrlTextView = (TextView) itemView.findViewById(R.id.listview_item_html_url_textview);
        viewHolder.mAvatarImageView = (ImageView) itemView.findViewById(R.id.listview_item_avatar_url_imageview);
        itemView.setTag(viewHolder);
        return itemView;
    }

    public void updateDownloadedAvatar(int id, byte[] imageBytes) {
        String idString = Integer.toString(id);

        if(mVisibleImageViewsToIds.containsValue(Integer.toString(id))) {
            for (HashMap.Entry entry : mVisibleImageViewsToIds.entrySet()) {
                if(entry.getValue().equals(idString)) {
                    Bitmap bitmap = setBytesToImageView(imageBytes, (ImageView) entry.getKey());
                    mMainActivity.getCache().put(idString, bitmap);
                    break;
                }
            }
        }
    }
}