package jorgecasariego.retrofit;

import java.util.List;

import jorgecasariego.retrofit.model.GitHubRepo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by jorgecasariego on 21/3/17.
 *
 * the GitHubClient interface defines a method named reposForUser with return type List<GitHubRepo>.
 * Retrofit makes sure the server response gets mapped correctly (in case the response matches the given class).
 *
 *
 * return type
 * -----------
 * the method's return type is critical. You have to define what kind of data you expect from the server.
 * For example, when you're requesting some user information, you might specify it as Call<UserInfo>.
 * The UserInfo class contains the properties that will hold the user data. Retrofit will map it
 * automatically and you won't have to do any manual parsing. If you want the raw response, you can
 * use ResponseBody instead of a specific class like UserInfo. If you don't care at all what the server
 * responds, you can use Void. In all those cases, you'll have to wrap it into a typed Retrofit Call<> class.
 *
 * pass parameters
 * ---------------
 * @Body: send Java objects as request body.
 * @Url: use dynamic URLs.
 * @Field: send data as form-urlencoded.
 *
 * Path Parameters
 * ---------------
 * the {user} parameter indicates to Retrofit that the value is dynamic and will be set when the
 * request is being made. If you include a path parameter in the URL, you need to add a @Path() function
 * parameter, where the @Path value matches the placeholder in the URL (in this case it'd be @Path("user")).
 *
 * Query Parameters
 * ----------------
 * Our website: https://futurestud.io/tutorials?filter=video. The ?filter=video is a query parameter
 * which further describes the request resource. Unlike the path parameters, you don't need to add
 * them to the annotation URL. You can simply add a method parameter with @Query() and a query
 * parameter name, describe the type and you're good to go. Retrofit will automatically attach it to
 * the request. If you pass a null value as query parameter, Retrofit will ignore it.
 *
 * Example
 * -------
    @GET("/tutorials")
    Call<List<Tutorial>> getTutorials(
            @Query("page") Integer page,
            @Query("order") String order,
            @Query("author") String author,
            @Query("published_at") Date date
    );

 * Within Retrofit 2, every request is wrapped into a Call object. The actual synchronous or
 * asynchronous request is executed differently using the desired method on a later created call
 * object. However, the interface definition for synchronous and asynchronous requests are the same within Retrofit 2.
 *
 * SYnchronous call
 * ----------------
 * Synchronous methods are executed on the main thread. That means the UI blocks during request
 * execution and no interaction is possible for this period.
 *
 * synchronous requests trigger app crashes on Android 4.0 or newer. You’ll run into the
 * `NetworkOnMainThreadException` error.
 *
 * Send Objects as Request Body
 * ----------------------------
 * Retrofit offers the ability to pass objects within the request body. Objects can be specified
 * for use as HTTP request body by using the @Body annotation.

    @POST("/tasks")
    Call<Task> createTask(@Body Task task);

    How to use
    ----------
    Task task = new Task(1, "my task title");
    Call<Task> call = taskService.createTask(task);
    call.enqueue(new Callback<Task>() {});

    ----------

    Calling the service method createTask will convert the properties of task into JSON representation.

    Custom Request Headers
    ----------------------

    Retrofit provides two options to define HTTP request header fields: static and dynamic.
    Static headers can’t be changed for different requests. The header’s key and value are fixed and
    initiated with the app startup.

    In contrast, dynamic headers must be set for each request.

    Static Request Header
    ---------------------
    The first option to add a static header is to define the header and respective value for your API
    method as an annotation. The header gets automatically added by Retrofit for every request using
    this method. The annotation can be either key-value-pair as one string or as a list of strings.

    Example
    -------
    @Headers("Cache-Control: max-age=640000")
    @GET("/tasks")
    Call<List<Task>> getTasks();

    Further, you can pass multiple key-value-strings as a list encapsulated in curly brackets {} to
    the @Headers annotation.

    @Headers({
        "Accept: application/vnd.yourapi.v1.full+json",
        "User-Agent: Your-App-Name"
    })
    @GET("/tasks/{task_id}")
    Call<Task> getTask(@Path("task_id") long taskId);

    Additionally, you can define static headers via the intercept method: https://futurestud.io/tutorials/retrofit-add-custom-request-header

    Dynamic Header
    --------------
    A dynamic header is passed like a parameter to the method. The provided parameter value gets
    mapped by Retrofit before executing the request.

    Example
    -------
    public interface UserService {
        @GET("/tasks")
        Call<List<Task>> getTasks(@Header("Content-Range") String contentRange);
    }

    Add Request Headers
    -------------------
    Adding HTTP request headers is a good practice to add information for API requests. A common
    example is authorization using the Authorization header field. If you need the header field
    including its value on almost every request, you can use an interceptor to add this piece of
    information. This way, you don’t need to add the @Header annotation to every endpoint declaration.

    OkHttp interceptors offer two ways to add header fields and values. You can either override
    existing headers with the same key or just add them without checking if there is another header
    key-value-pair already existing.

    Override Headers --> https://futurestud.io/tutorials/retrofit-2-manage-request-headers-in-okhttp-interceptor
    ----------------
    Retrofit, and especially OkHttp, allow you to add multiple headers with the same key.
    The .header method will replace all existing headers with the defined key identifier.
    Using Retrofit 2 and an OkHttp interceptor, you can add multiple request headers with the same key.
    The method you need to use is .addHeader.

    .header(key, val): will override preexisting headers identified by key
    .addHeader(key, val): will add the header and don’t override preexisting ones

    Customizing the request and adding request header enable a simple and effective way to pass data
    with every request. This method will result in much cleaner code than adding header fields with
    the @Header annotation for every request.


    Dynamic Request Headers
    -----------------------
    If you need to decide at runtime which headers are added to your request, @HeaderMap is a possible solution.
    Similar to the @Header annotation, you need to declare the @HeaderMap as one of the interface parameters.

    public interface TaskService {
        @GET("/tasks")
        Call<List<Task>> getTasks(
            @HeaderMap Map<String, String> headers
        );
    }

    Using the interface we've declared above is quite simple. You can just create a Map instance and
    fill it with values depending on your needs. Retrofit will add every non-null element of
    the @HeaderMap as a request header.


 */

public interface GitHubClient {

    @GET("/users/{user}/repos")
    Call<List<GitHubRepo>> reposForUser(
            @Path("user") String user
    );

    /**
        Query Parameters
        ----------------

        Query parameters are a common way to pass data from clients to servers. We all know them for
        requests. Let's face the example below which requests a specific task with id=123

        https://api.example.com/tasks?id=123

         @GET("/tasks")
         Call<Task> getTask(@Query("id") long taskId);

         The method getTask(…) requires the parameter taskId. This parameter will be mapped by
         Retrofit to given @Query() parameter name. In this case the parameter name is id which
         will result in a final request url part like

         /tasks?id=<taskId>

         Multiple Query Parameters
        --------------------------
         Some use cases require to pass multiple query parameters of the same name.

         https://api.example.com/tasks?id=123&id=124&id=125

         The Retrofit method to perform requests with multiple query parameters of the same name is
         done by providing a list of ids as a parameter. Retrofit concatenates the given list to
         multiple query parameters of same name.

         @GET("/tasks")
         Call<List<Task>> getTask(@Query("id") List<Long> taskIds);

         Optional Query Parameters
        --------------------------
         Assuming that your base url to your API is https://your.api.com, the request using the
         TaskService from above and calling the getTasks method results in this request
         url: https://your.api.com/tasks?sort=value

         Depending on the API design, the sort parameter might be optional. In case you don’t want
         to pass it with the request, just pass null as the value for order during method call.

         service.getTasks(null);

         Retrofit skips null parameters and ignores them while assembling the request. Keep in mind,
         that you can’t pass null for primitive data types like int, float, long, etc.
         Instead, use Integer, Float, Long, etc and the compiler won’t be grumpy.

         @GET("/tasks")
         Call<List<Task>> getTasks(
            @Query("sort") String order,
            @Query("page") Integer page);
         }

        service.getTasks(null, null);


        Form Encoded Requests
        ---------------------
        Performing form-urlencoded requests using Retrofit is sort of straight forward. It’s just
        another Retrofit annotation, which will adjust the proper mime type of your request
        automatically to application/x-www-form-urlencoded.

        public interface TaskService {
            @FormUrlEncoded
            @POST("tasks")
            Call<Task> createTask(@Field("title") String title);
        }

        Please be aware of the fact, that you can’t use this annotation for GET requests.
        Form encoded requests are intended to send data to the server!

        Additionally, you need to use the @Field annotation for the parameters which you’re going to
        send with your request. Place your desired key inside the @Field("key") annotation to
        define the parameter name. Further, add the type of your value as the method parameter.
        If you don’t use String, Retrofit will create a string value using Java’s
        String.valueOf(yourObject) method.

        Example
        -------
        service.createTask("Research Retrofit form encoded requests");

        Form Encoded Requests Using an Array of Values
        ----------------------------------------------
        You can pass an array of values using the same key for your form encoded requests.

        public interface TaskService {
            @FormUrlEncoded
            @POST("tasks")
            Call<List<Task>> createTasks(@Field("title") List<String> titles);
        }

        Example
        -------
        List<String> titles = new ArrayList<>();
        titles.add("Research Retrofit");
        titles.add("Retrofit Form Encoded")

        service.createTasks(titles);

        results in the following form encoded request body:
        title=Research+Retrofit&title=Retrofit+Form+Encoded

        Field Options
        -------------
        The @Field annotation has an option field for encoding:
        - encoded: can be either true or false; default is false

        The encoded option defines whether your provided key-value-pair is already url encoded.

        @Field(value = "title", encoded = true) String title

        Form-Urlencoded vs. Query Parameter
        -----------------------------------
        In essence: the request type.

        - form-urlencoded: POST
        - query parameter: GET

        --> Use form-urlencoded requests to send data to a server or API. The data is sent within
        the request body and not as an url parameter.

        --> Query parameters are used when requesting data from an API or server using specific fields or filter.

        What Is @Fieldmap in Retrofit?
        ------------------------------
        Assuming that you have the option to update user data within your Android app, you want to
        call an API endpoint that takes an object of key-value-pairs.

        We want to use a form-urlencoded request, because the API accepts a JSON object representing
        the fields that should be updated. You’re tempted to define the API endpoint on client-side
        using the following interface definition.

        public interface UserService {
            @FormUrlEncoded
            @PUT("user")
            Call<User> update(
                    @Field("username") String username,
                    @Field("name") String name,
                    @Field("email") String email,
                    @Field("homepage") String homepage,
                    @Field("location") String location
            );
        }

        The PUT request requires values for multiple parameters like username, email, homepage, etc.

        The downside: every time we want to send an update with the new user data, we have to provide
        a value for each parameter even though they didn’t change. It blows up your code and imagine
        that the number of parameters doubles or even triples evolutionary! The length of the method
        call text will explode Android Studio’s canvas.

        Actually, there’s a more elegant solution already integrated with Retrofit: @FieldMap.


        Form Encoded Requests Using FieldMap
        ------------------------------------
        In situations where you just need to send selected fields that should be updated for a given
        user, using Retrofit’s @FieldMap is a lot more elegant. You can add the desired key-value-pairs
        to a standard Java Map implementation and they will be translated as you go.

        public interface UserService {
            @FormUrlEncoded
            @PUT("user")
            Call<User> update(@FieldMap Map<String, String> fields);
        }

        Specifically, if you only want to update the username, there’s no need to add any other field
        than the username. Your request payload only consists of the single field!

        The @FieldMap annotation has an option field for encoding:
     */
}
