package jorgecasariego.retrofit.model;

/**
 * Created by jorgecasariego on 21/3/17.
 */

public class GitHubRepo {

    private int id;

    private String name;

    public GitHubRepo() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
