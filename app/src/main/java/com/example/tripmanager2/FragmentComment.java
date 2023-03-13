package com.example.tripmanager2;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.annotations.NonNull;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FragmentComment extends Fragment {

    MainActivity mainActivity;
    View mainView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context; //拿mainActivity
    }

    public FragmentComment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_comment, container, false);

        final LinearLayout lm = (LinearLayout) mainView.findViewById(R.id.linearMain);
        // create the layout params that will be used to define how your
        // button will be displayed
        LinearLayout.LayoutParams paramsIcon = new LinearLayout.LayoutParams(
                0, ActionBar.LayoutParams.WRAP_CONTENT, 3);
        LinearLayout.LayoutParams paramsTxt = new LinearLayout.LayoutParams(
                0, ActionBar.LayoutParams.WRAP_CONTENT, 14);
        LinearLayout.LayoutParams paramsStar = new LinearLayout.LayoutParams(
                0, ActionBar.LayoutParams.WRAP_CONTENT, 2);

        //Create four
        for(int j=0;j<mainActivity.comments.size();j++)
        {
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(mainActivity);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setPadding(0,15, 10, 0);


            ImageView image = new ImageView(mainActivity);
            image.setLayoutParams(paramsIcon);
            new DownloadImageTask(image).execute(mainActivity.comments.get(j).iconUrl);
            ll.addView(image);

            // Create TextView
            TextView text = new TextView(mainActivity);
            String htmlStr = "<b>" + mainActivity.comments.get(j).author + "</b>";
            text.setText(Html.fromHtml(htmlStr) + "     " + mainActivity.comments.get(j).relativeTime + "\n" + mainActivity.comments.get(j).text);
            text.setLayoutParams(paramsTxt);
            text.setPadding(10, 0, 20, 0);
            ll.addView(text);

            // Create TextView
            TextView rating = new TextView(mainActivity);
            rating.setText("\n" + mainActivity.comments.get(j).rating + "\u2B50");
            rating.setTextColor(getResources().getColor(com.google.android.libraries.places.R.color.quantum_orange));
            rating.setLayoutParams(paramsStar);
            ll.addView(rating);
//            // Create Button
//            final Button btn = new Button(this);
//            // Give button an ID
//            btn.setId(j+1);
//            btn.setText("Add To Cart");
//            // set the layoutParams on the button
//            btn.setLayoutParams(params);
//
//            final int index = j;
//            // Set click listener for button
//            btn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//
//                    Log.i("TAG", "index :" + index);
//
//                    Toast.makeText(getApplicationContext(),
//                            "Clicked Button Index :" + index,
//                            Toast.LENGTH_LONG).show();
//
//                }
//            });

            //Add button to LinearLayout
            //ll.addView(btn);
            //Add button to LinearLayout defined in XML
            lm.addView(ll);
        }
        return  mainView;
    }

    // 負責下載照片的class (整段copy自網路上，不需修改)
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            Response response = null;
            Bitmap mIcon11 = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()) {
                try {
                    mIcon11 = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}