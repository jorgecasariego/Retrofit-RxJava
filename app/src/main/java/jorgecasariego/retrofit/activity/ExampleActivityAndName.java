package jorgecasariego.retrofit.activity;

import android.app.Activity;

/**
 * Created by jorgecasariego on 27/3/17.
 */

public class ExampleActivityAndName {

    public final Class<? extends Activity> mExampleActivityClass;
    public final String mExampleName;

    public ExampleActivityAndName(
            Class<? extends Activity> exampleActivityClass,
            String exampleName) {
        mExampleActivityClass = exampleActivityClass;
        mExampleName = exampleName;
    }
}
