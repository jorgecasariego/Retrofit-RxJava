package jorgecasariego.retrofit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jorgecasariego.retrofit.R;
import jorgecasariego.retrofit.adapter.SimpleStringAdapter;
import rx.Observable;
import rx.Observer;

public class Example1Activity extends AppCompatActivity {

    RecyclerView mColorListView;
    SimpleStringAdapter mSimpleStringAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example1);
        configureLayout();
        createObservable();
    }

    private void configureLayout() {
        mColorListView = (RecyclerView) findViewById(R.id.color_list);
        mColorListView.setLayoutManager(new LinearLayoutManager(this));
        mSimpleStringAdapter = new SimpleStringAdapter(this);
        mColorListView.setAdapter(mSimpleStringAdapter);
    }

    /**
     * Observables --> “emit” values
     *
     * Observers --> watch Observables by  --> “subscribing” to them.
     *
     * We’re going to make an Observable that emits a single value, a list of strings, and then completes.
     * We’ll then use the emitted value to populate the list.
     */
    private void createObservable() {
        // Observable.just(): This method creates an Observable such that when an Observer subscribes,
        // the onNext() of the Observer is immediately called with the argument provided to Observable.just().
        Observable<List<String>> listObservable = Observable.just(getColorList());

        listObservable.subscribe(new Observer<List<String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<String> colors) {
                mSimpleStringAdapter.setStrings(colors);
            }
        });
    }

    private static List<String> getColorList() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("blue");
        colors.add("green");
        colors.add("red");
        colors.add("chartreuse");
        colors.add("Van Dyke Brown");
        return colors;
    }




}
