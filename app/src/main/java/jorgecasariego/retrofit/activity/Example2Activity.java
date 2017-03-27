package jorgecasariego.retrofit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;
import java.util.concurrent.Callable;

import jorgecasariego.retrofit.R;
import jorgecasariego.retrofit.RestClient;
import jorgecasariego.retrofit.adapter.SimpleStringAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Example from: https://medium.com/@kurtisnusbaum/rxandroid-basics-part-1-c0d5edcf6850
 */
public class Example2Activity extends AppCompatActivity {

    /**
     * What’s this Subscription?
     *
     * There’s one last thing. What’s this mTvShowSubscription thing? When an Observer subscribes
     * to an Observable a Subscription is created. A Subscription represents a connection between
     * an Observer and an Observable. Sometimes we need to sever this connection and this goes in
     * our Activity’s onDestroy() method
     */
    private Subscription mTvShowSubscription;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SimpleStringAdapter adapter;
    private RestClient mRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example2);

        mRestClient = new RestClient(this);

        configureLayout();

        createObservable();
    }

    private void configureLayout() {
        progressBar = (ProgressBar) findViewById(R.id.loader);
        recyclerView = (RecyclerView) findViewById(R.id.tv_show_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SimpleStringAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * In this example we can't use Observable.just because mRestClient.getFavoriteTvShows() is a
     * blocking network call. If we use it with Observable.just(), mRestClient.getFavoriteTvShows()
     * will be evaluated immediately and block the UI thread.
     */
    private void createObservable() {
        /**
         * Enter the Observable.fromCallable() method. It gives us two important things:
         *  1. The code for creating the emitted value is not run until someone subscribes to the Observer.
         *
         *  2. The creation code can be run on a different thread.
         *
         *  ---
         *  subscribeOn() essentially alters the Observable. All of the code that this Observable
         *  would normally run, including the code the gets run upon subscription, will now run on
         *  a different thread. This means the logic in our Callable object, including the call
         *  to getFavoriteTvShows(), will run on a different thread.
         *
         *
         */
        Observable<List<String>> tvShowObservable = Observable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return mRestClient.getFavoriteTvShows();
            }
        });

        /**
         * Since our Observable is set to run on the IO Scheduler, this means it’s going to
         * interact with our Observer on the IO Scheduler as well. This is a problem because
         * it means our onNext() method is going to get called on the IO Scheduler. But the code
         * in our onNext() calls methods on some of our views. View methods can only be called
         * on the UI thread.
         *
         * We can tell RxJava that we want to observe this Observable on the UI thread,
         * i.e. we want our onNext() callback to be called on the UI thread.
         *
         * We do this by specifying a different scheduler in the observeOn() method, namely
         * the scheduler returned by AndroidSchedules.mainThread()
         */
        mTvShowSubscription = tvShowObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<String> tvShows) {
                        displayTvShows(tvShows);
                    }
                });
    }

    private void displayTvShows(List<String> tvShows) {
        adapter.setStrings(tvShows);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * If you’ve ever done threading work on Android before you know there’s typically one huge
     * problem: what happens if your thread finishes (or never finishes) it’s execution after
     * an Activity has been torn down. This can cause a whole host of problems including memory
     * leaks and NullPointerExceptions.
     *
     * Subscriptions allow us to deal with this problem. We can say “Hey, Observable, this
     * Observer doesn’t want to receive your emissions anymore. Please disconnect from the Observer.”
     * We do this by calling unsubscribe(). After calling unsubscribe(), the Observer we created
     * above will no longer receive emissions and in doing so avoid all the problems associated
     * with a thread completing it’s work (or not completing at all) after the Activity has been destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * We also wrap our unsubscribe in an if statement. We need make sure that:
         *  a) we’re not trying to unsubscribe something we never properly initialized (the null check), and
         *
         *  b) that we didn’t somehow unsubscribe somewhere else (the isUnsubscribed() check).
         */
        if(mTvShowSubscription != null && !mTvShowSubscription.isUnsubscribed()){
            mTvShowSubscription.unsubscribe();
        }
    }
}
