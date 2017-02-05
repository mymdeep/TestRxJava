package deep.testrxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import junit.framework.Test;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

/**
 * Created by wangfei on 17/2/4.
 */
public class TestActivity extends AppCompatActivity {
    private String tag = "TestActivity_mymdeep";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        test();
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_test:
                test();
                break;
            case R.id.btn_just:
                just();
                break;
            case R.id.btn_from:
                from();
                break;
            case R.id.btn_action:
                action();
                break;
            case R.id.btn_flatmap:
                flatMap();
                break;
            case R.id.btn_filter:
                filter();
                break;
        }
    }
    public void test(){
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onNext(String s) {
                Log.d(tag, "onNext: " + s);
            }

            @Override
            public void onCompleted() {
                Log.d(tag, "Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(tag, "Error!");
            }
        };
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onStart() {
                Log.d(tag,"start");
            }

            @Override
            public void onNext(String s) {
                Log.d(tag, "subscriber onNext: " + s);
            }

            @Override
            public void onCompleted() {
                Log.d(tag, "subscriber onCompleted!");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(tag, "subscriber Error!");
            }
        };
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("aaaa");
                subscriber.onNext("bbbb");
                subscriber.onNext("cccc");
                subscriber.onCompleted();
            }
        });
        observable.subscribe(observer);

    }
    public void just(){
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onNext(String s) {
                Log.d(tag, " just onNext: " + s);
            }

            @Override
            public void onCompleted() {
                Log.d(tag, "just Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(tag, "just Error!");
            }
        };

        Observable observable = Observable.just("aaaa","bbbb","cccc");
        observable.subscribe(observer);

    }
    public void from(){
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onNext(String s) {
                Log.d(tag, " from onNext: " + s);
            }

            @Override
            public void onCompleted() {
                Log.d(tag, "from Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(tag, "from Error!");
            }
        };
        String[] words = {"aaaa", "bbbb", "cccc"};
        Observable observable = Observable.from(words);
        observable.subscribe(observer);

    }
    public void action(){

        Action1<String> onNextAction = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(tag, s);
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.d(tag, "error");
            }
        };
        Action0 onCompletedAction = new Action0() {
            @Override
            public void call() {
                Log.d(tag, "completed");
            }
        };

        String[] words = {"aaaa", "bbbb", "cccc"};
        Observable observable = Observable.from(words);
        observable.subscribe(onNextAction);
        observable.subscribe(onNextAction, onErrorAction);
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);

    }
    public void map(){
        Observable.just("aaaa","bbb","cc") // 输入类型 String
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) { // 参数类型 String
                        return s.length(); // 返回类型 Bitmap
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer i) { // 参数类型 Bitmap
                        Log.e(tag,"length = "+i);
                    }
                });

    }
    public void flatMap(){
        Observable.just("aaaa","bbb","cc") // 输入类型 String
                .flatMap(new Func1<String, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(String s) {
                        Integer[] info = new Integer[3];
                        info[0] = s.length();
                        info[1] = s.hashCode();
                        info[2] = s.getBytes().length;
                        return Observable.from(info);
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer i) { // 参数类型 Bitmap
                        Log.e(tag,"length = "+i);
                    }
                });

    }
    public void filter(){
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onNext(String s) {
                Log.d(tag, " filter onNext: " + s);
            }

            @Override
            public void onCompleted() {
                Log.d(tag, "filter Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(tag, "filter Error!");
            }
        };

        Observable observable = Observable.just("aaaa","bbbb","cccc")
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s.contains("a");
                    }
                });
        observable.subscribe(observer);
    }
}
