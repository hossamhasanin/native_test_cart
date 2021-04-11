package com.hossam.hasanin.test_cart;

import android.app.Activity;
import android.content.Intent;


public class Helper {

    static void share(Activity activity){
        String productDescription = "";
        String productId = "";
        String productUrl = "http://www.url.com/er-ar/"+productId;
        String content = productDescription + "\n \n "+productUrl;
        shareTo(content , activity);
    }

    static void shareTo(String contentToShare , Activity activity){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        i.putExtra(Intent.EXTRA_TEXT, "share to me \n http://www.url.com");
        activity.startActivity(Intent.createChooser(i, "Share URL"));
    }
}
