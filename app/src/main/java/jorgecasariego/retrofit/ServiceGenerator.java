package jorgecasariego.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Why Is Everything Declared Static Within the ServiceGenerator?
 * ---------------------------------------------------------------
 * Actually, it has one simple reason: we want to use the same objects (OkHttpClient, Retrofit, …)
 * throughout the app to just open one socket connection that handles all the request and responses
 * including caching and many more. It’s common practice to just use one OkHttpClient instance to
 * reuse open socket connections. That means, we either need to inject the OkHttpClient to this
 * class via dependency injection or use a static field. As you can see, we chose to use the static
 * field. And because we use the OkHttpClient throughout this class, we need to make all fields and
 * methods static.
 *
 * Logging
 * -------
 * Logging with Retrofit 2 is done by an interceptor called HttpLoggingInterceptor.
 * You'll need to add an instance of this interceptor to the OkHttpClient
 *
 * Log Levels
 * ----------
 * - None: Use this log level for production environments to enhance your apps performance by skipping
 * any logging operation.
 *
 * - Basic: Log request type, url, size of request body, response status and size of response body.
 * Using the log level BASIC will only log minimal information about your request. If you’re just
 * interested in the request body size, response body size and response status, this log level is
 * the right one.
 *
 * - Headers: Log request and response headers, request type, url, response status.
 *
 * - Body: Log request and response headers and body. This is the most complete log level and will print
 * out every related information for your request and response. However, the BODY log level will
 * clutter your Android monitor if you’re receiving large data sets. Use this level only if necessary.
 */
public class ServiceGenerator {

    // We should always end our base url with a trailing slash: /
    private static String apiBaseUrl = "https://api.github.com/";

    private static Retrofit retrofit;

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create());

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();


    public ServiceGenerator() {

    }


    // Example: https://futurestud.io/tutorials/retrofit-2-how-to-change-api-base-url-at-runtime-2
    public static void changeApiBaseUrl(String newApiBaseUrl){
        apiBaseUrl = newApiBaseUrl;

        builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(apiBaseUrl);
    }

    // The createService method takes a serviceClass, which is the annotated interface for API
    // requests, as a parameter and creates a usable client from it.
    // On the resulting client you'll be able to execute your network requests.
    public static <S> S createService(Class<S> serviceClass) {

        // We need to make sure you're not accidentally adding the interceptor multiple times!
        // Also, we need to make sure to not build the retrofit object on every createService
        if(!httpClient.interceptors().contains(logging)){
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return  retrofit.create(serviceClass);
    }

    /**
     * Hawk Authentication: https://alexbilbie.com/2012/11/hawk-a-new-http-authentication-scheme/
     */
//    public static <S> S createService(
//            Class<S> serviceClass, final HawkCredentials credentials) {
//        if (credentials != null) {
//            HawkAuthenticationInterceptor interceptor =
//                    new HawkAuthenticationInterceptor(credentials);
//
//            if (!httpClient.interceptors().contains(interceptor)) {
//                httpClient.addInterceptor(interceptor);
//
//                builder.client(httpClient.build());
//                retrofit = builder.build();
//            }
//        }
//
//        return retrofit.create(serviceClass);
//    }
}
