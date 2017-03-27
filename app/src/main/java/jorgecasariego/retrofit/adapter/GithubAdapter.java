package jorgecasariego.retrofit.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jorgecasariego.retrofit.model.GitHubRepo;
import jorgecasariego.retrofit.R;

/**
 * Created by jorgecasariego on 21/3/17.
 */

public class GithubAdapter extends ArrayAdapter<GitHubRepo> {

    Context context;
    List<GitHubRepo> gitHubRepos = new ArrayList<>();

    public GithubAdapter(Context context, List<GitHubRepo> gitHubRepos) {
        super(context, -1, gitHubRepos);
        this.context = context;
        this.gitHubRepos = gitHubRepos;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

        TextView nombreRepo = (TextView) rowView.findViewById(R.id.nombre);
        nombreRepo.setText(gitHubRepos.get(position).getName());

        return rowView;
    }
}
