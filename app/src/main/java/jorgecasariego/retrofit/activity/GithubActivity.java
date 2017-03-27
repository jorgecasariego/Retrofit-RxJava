package jorgecasariego.retrofit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jorgecasariego.retrofit.GitHubClient;
import jorgecasariego.retrofit.R;
import jorgecasariego.retrofit.ServiceGenerator;
import jorgecasariego.retrofit.adapter.GithubAdapter;
import jorgecasariego.retrofit.model.GitHubRepo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GithubActivity extends AppCompatActivity {


    ListView listview;
    GithubAdapter adapter;
    List<GitHubRepo> gitHubRepos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.listview);
        adapter = new GithubAdapter(this, gitHubRepos);
        listview.setAdapter(adapter);

        GitHubClient client = ServiceGenerator.createService(GitHubClient.class);


        // Fetch a list of the Github repositories.
        Call<List<GitHubRepo>> call = client.reposForUser("jorgecasariego");

        /**
         *      Execute the call asynchronously. Get a positive or negative callback.
         *
         *      Get Raw HTTP Response
         *      ---------------------
         *      In case wee need the raw HTTP response object, just define it as the method’s return type.
         *      Response raw = response.raw();
          */

        call.enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                // The network call was a success and we got a response
                List<GitHubRepo> gitHubRepos = response.body();
                adapter.addAll(gitHubRepos);
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                // the network call was a failure
                Toast.makeText(GithubActivity.this, "Error al obtener repositorio: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Dynamic Request Headers Example
        /*
            TaskService taskService = ServiceGenerator.createService(TaskService.class);


            Map<String, String> map = new HashMap<>();
            map.put("Page", String.valueOf(page));

            if (BuildConfig.DEBUG) {
                map.put("Accept", "application/vnd.yourapi.v1.full+json");
                map.put("User-Agent", "Future Studio Debug");
            }
            else {
                map.put("Accept", "application/json");
                map.put("Accept-Charset", "utf-8");
                map.put("User-Agent", "Future Studio Release");
            }

            Call<List<Task>> call = taskService.getTasks(map);
            // Use it like any other Retrofit call
        */
    }
}
