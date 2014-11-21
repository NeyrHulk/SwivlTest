package com.test.swivl.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;

import com.test.swivl.R;

public class LargeImageDialogFragment extends DialogFragment {
    public static LargeImageDialogFragment newInstance(String titleIdKey, String title,
                                                     String imageBytesKey, byte[] imageBytes) {
        LargeImageDialogFragment dialogFragment = new LargeImageDialogFragment();
        dialogFragment.setCancelable(true);

        // put arguments in order to use them in creation of dialog
        Bundle arguments = new Bundle();
        arguments.putString(titleIdKey, title);
        arguments.putByteArray(imageBytesKey, imageBytes);
        dialogFragment.setArguments(arguments);

        return dialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(getString(R.string.dialog_fragment_title_key));
        byte[] imageBytes = getArguments().getByteArray(getString(R.string.dialog_fragment_image_bytes_key));

        final View view = getActivity().getLayoutInflater().inflate(R.layout.large_image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.large_image_imageview);
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity()).setView(view)
                .setTitle(title);

        if (imageBytes != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outHeight = MainActivity.BIG_AVATAR_SIZE;
            options.outWidth = MainActivity.BIG_AVATAR_SIZE;
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0,
                    imageBytes.length, options);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(getActivity().getResources()
                    .getDrawable(R.drawable.photo_default));
        }

        return builder.create();
    }
}
