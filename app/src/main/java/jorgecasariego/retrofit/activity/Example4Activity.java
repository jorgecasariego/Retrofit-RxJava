package jorgecasariego.retrofit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jorgecasariego.retrofit.R;
import rx.Observer;
import rx.subjects.PublishSubject;

/**
 * Subjects are special objects that are both an Observable and an Observer.
 *
 * There are several types of Subjects, but we’re going to use the simplest one: a PublishSubject.
 * With a PublishSubject, as soon as you put something in one end of the pipe it immediately comes
 * out the other.

 */
public class Example4Activity extends AppCompatActivity {

    private TextView mCounterDisplay;
    private Button mIncrementButton;
    private PublishSubject<Integer> mCounterEmitter;

    private int mCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example4);

        configureLayout();
        createCounterEmitter();
    }

    private void createCounterEmitter() {
        mCounterEmitter = PublishSubject.create();
        mCounterEmitter.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                mCounterDisplay.setText(String.valueOf(integer));
            }
        });
    }

    private void configureLayout() {
        configureCounterDisplay();
        configureIncrementButton();
    }

    private void configureCounterDisplay() {
        mCounterDisplay = (TextView) findViewById(R.id.counter_display);
        mCounterDisplay.setText(String.valueOf(mCounter));
    }

    private void configureIncrementButton() {
        mIncrementButton = (Button) findViewById(R.id.increment_button);
        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onIncrementButtonClick();
            }
        });
    }

    private void onIncrementButtonClick() {
        mCounter++;
        mCounterEmitter.onNext(mCounter);
    }
}
