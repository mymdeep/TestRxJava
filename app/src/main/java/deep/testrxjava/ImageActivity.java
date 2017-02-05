package deep.testrxjava;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by wangfei on 17/2/4.
 */
public class ImageActivity extends AppCompatActivity {
    public static String imageurl = "https://mobile.umeng.com/images/pic/home/social/img-1.png";
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (ImageView) findViewById(R.id.img);
       // setImageByThread();
        setImageByRxJava();
    }
    public void setImageByThread(){
        new Thread() {
            @Override
            public void run() {
             final Bitmap bitmap = binary2Bitmap(getNetData(imageurl));
                ImageActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });

            }
        }.start();
    }
    public void setImageByRxJava(){
        Observer<Bitmap> observer = new Observer<Bitmap>() {
            @Override
            public void onNext(Bitmap s) {
                imageView.setImageBitmap(s);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
             Toast.makeText(ImageActivity.this,"error="+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        };
        Observable observable = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
               Bitmap bitmap =  binary2Bitmap(getNetData(imageurl));
                subscriber.onNext(bitmap);

                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
        .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                ;
        observable.subscribe(observer);
    }
    public static byte[] getNetData(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        ByteArrayOutputStream bos = null;
        InputStream in = null;
        try {
            bos = new ByteArrayOutputStream();
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setInstanceFollowRedirects(true);
            if (conn.getResponseCode() == 301){

                String location= conn.getURL().toString();
                return   getNetData(location);
            }else {
                in = conn.getInputStream();
                Log.d("image", "getting image from url" + url);
                byte[] buf = new byte[4 * 1024]; // 4K buffer
                int bytesRead;
                while ((bytesRead = in.read(buf)) != -1) {
                    bos.write(buf, 0, bytesRead);
                }
                return bos.toByteArray();
            }

        } catch (Exception e) {
            Log.e("mymdeep",e.getMessage());
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                } finally {
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
    }
    public static Bitmap binary2Bitmap(byte[] data) {
        if (data != null) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return null;
    }
}
