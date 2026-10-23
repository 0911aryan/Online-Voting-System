package backend;
import java.util.HashSet;
import java.util.Set;

public class Voter {
    private String voterId;
    private String name;
    private Set<String> votedPosts; // Tracks which polls this voter has completed

    public Voter(String voterId, String name) {
        this.voterId = voterId;
        this.name = name;
        this.votedPosts = new HashSet<>();
    }

    public String getVoterId() { return voterId; }
    public String getName() { return name; }
    public boolean hasVotedFor(String post) { return votedPosts.contains(post); }
    public void markAsVoted(String post) { this.votedPosts.add(post); }
}