package jorgecasariego.retrofit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import jorgecasariego.retrofit.R;
import jorgecasariego.retrofit.RestClient;
import jorgecasariego.retrofit.adapter.SimpleStringAdapter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class Example6Activity extends AppCompatActivity {

    private RestClient mRestClient;
    private EditText mSearchInput;
    private TextView mNoResultsIndicator;
    private RecyclerView mSearchResults;
    private SimpleStringAdapter mSearchResultsAdapter;

    private PublishSubject<String> mSearchResultsSubject;
    private Subscription mTextWatchSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example6);

        mRestClient = new RestClient(this);
        configureLayout();
        createObservables();
        listenToSearchInput();
    }

    private void configureLayout() {
        mSearchInput = (EditText) findViewById(R.id.search_input);
        mNoResultsIndicator = (TextView) findViewById(R.id.no_results_indicator);
        mSearchResults = (RecyclerView) findViewById(R.id.search_results);
        mSearchResults.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsAdapter = new SimpleStringAdapter(this);
        mSearchResults.setAdapter(mSearchResultsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTextWatchSubscription != null && !mTextWatchSubscription.isUnsubscribed()) {
            mTextWatchSubscription.unsubscribe();
        }
    }

    /**
     *  debounce
     *  -----------
     *  The first thing you’ll notice is debounce(). What is this, and why do we need
     *  it? Well if you look at how we’ve setup the TextWatcher, you’ll notice a new value is going
     *  to come into our PublishSubject every, single time the user adds or removes a character from
     *  their search. This is neat, but we don’t want to send out a request to the server on every
     *  single keystroke. We’d like to wait a little bit for the user to stop typing (so that we’re
     *  sure we’ve got a good query) and then send our search request to the server.
     *
     *  This is what debounce() allows us to do. It tells mSearchResultsSubject to only emit the
     *  last value that came into it after nothing new has come into the mSearchResultsSubject
     *  for 400 milliseconds.
     *
     *  Essentially, this means our subject won’t emit the search string until the user hasn’t
     *  changed the string for 400 milliseconds, and at the end of the 400 milliseconds it will
     *  only emit the latest search string the user entered.
     *
     *  map
     *  ---
     *  We’re going to use map to “map” our search queries to a list of search results.
     *  Because map can run any arbitrary function, we’ll use our RestClient to transform our search
     *  query into the list of actual results we want to display.
     *
     *  Since our map was run on the IO Scheduler, and we want to use the results it emits to populate
     *  our views, we then need to switch back to the UI thread. So we add an
     *  observeOn(AndroidSchedulers.mainThread()). Now we’ve got the search results being emitted
     *  on the UI thread. Note the ordering of all our observerOn()s here. They’re critical.
     *
     *  mSearchResultsSubject
     *      |
     *      |
     *      V
     *   debounce
     *     |||
     *     |||
     *      V
     *     map
     *      |
     *      |
     *      V
     *   observer
     *
     *   The | represents emissions happening on the UI Thread and the ||| represents emissions
     *   happening on the IO Scheduler.
     */
    private void createObservables() {
        mSearchResultsSubject = PublishSubject.create();
        mTextWatchSubscription = mSearchResultsSubject
                .debounce(400, java.util.concurrent.TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map(new Func1<String, List<String>>() {
                    @Override
                    public List<String> call(String city) {
                        return mRestClient.searchForCity(city);
                    }
                })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<String> cities) {
                handleSearchResults(cities);
            }
        });

    }

    private void handleSearchResults(List<String> cities) {
        if(cities.isEmpty()){
            showNoSearchResults();
        } else {
            showSearchResults(cities);
        }
    }

    private void showNoSearchResults() {
        mNoResultsIndicator.setVisibility(View.VISIBLE);
        mSearchResults.setVisibility(View.GONE);
    }

    private void showSearchResults(List<String> cities) {
        mNoResultsIndicator.setVisibility(View.GONE);
        mSearchResults.setVisibility(View.VISIBLE);
        mSearchResultsAdapter.setStrings(cities);
    }

    private void listenToSearchInput() {
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSearchResultsSubject.onNext(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }




}
