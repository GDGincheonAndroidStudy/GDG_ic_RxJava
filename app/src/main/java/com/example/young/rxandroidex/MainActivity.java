package com.example.young.rxandroidex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.ViewObservable;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        ((Button) findViewById(R.id.button)).setOnClickListener(e -> rxAndroidTest());
        mergeTest();
        scanTest();
        ((Button) findViewById(R.id.button_scheduler)).setOnClickListener(e -> schdulerTest());
        ((Button) findViewById(R.id.button_interval)).setOnClickListener(e -> intervalTest());
        ((Button) findViewById(R.id.button_zip)).setOnClickListener(e -> zipTest());
    }

    //test1
    public void rxAndroidTest() {
        Observable<String> observable = Observable.just("Hello test");
        observable.subscribe(text -> ((TextView) findViewById(R.id.tv_test)).setText(text),
                e -> Log.d("test", "onError:" + e.getMessage()),
                () -> Log.d("test", "onCompleted")
        );
    }

    public void mergeTest() {
        Observable<String> first = ViewObservable.clicks(findViewById(R.id.button_first)).map(event -> "first");
        Observable<String> second = ViewObservable.clicks(findViewById(R.id.button_second)).map(event -> "second");
        Observable<String> together = Observable.merge(first, second);
        together.subscribe();
        together.map(text -> text.toUpperCase()).subscribe(text -> Toast.makeText(this, text, Toast.LENGTH_SHORT).show());
    }

    public void scanTest() {
        Observable<Integer> minuses = ViewObservable.clicks(findViewById(R.id.button_minus)).map(event -> -1);
        Observable<Integer> pluses = ViewObservable.clicks(findViewById(R.id.button_plus)).map(event -> 1);
        Observable<Integer> together = Observable.merge(minuses, pluses);
        together.scan(0, (sum, number) -> sum + 1).subscribe(count -> ((TextView) findViewById(R.id.tv_scan)).setText("count : " + count.toString()));
        together.scan(0, (sum, number) -> sum + number).subscribe(number -> ((TextView) findViewById(R.id.number)).setText("num : " + number.toString()));
    }

    public void schdulerTest() {
        TextView tvSchduler = (TextView)findViewById(R.id.text_schduler);
        tvSchduler.setText("");
        Observable.from(new String[]{"one", "two", "three", "four", "five"})
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> ((TextView) findViewById(R.id.text_schduler)).setText(tvSchduler.getText().toString() +"\n"+ Thread.currentThread().getName() + ":" + text ));
    }

    public void intervalTest() {
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> ((TextView) findViewById(R.id.tv_interval)).setText("" + new Date()));
    }

    public void zipTest() {
        Observable.zip(
                Observable.just("A"),
                Observable.just("A.png"),
                (name, image) -> "name : " + name + ", image : " + image
        ).subscribe(print -> ((TextView) findViewById(R.id.tv_zip)).setText(print));
    }
}
