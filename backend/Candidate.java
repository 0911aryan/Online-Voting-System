package backend;

public class Candidate {
    private int id;
    private String name;
    private String post; // Bonus: Multiple simultaneous polls
    private int voteCount;

    public Candidate(int id, String name, String post) {
        this.id = id;
        this.name = name;
        this.post = post;
        this.voteCount = 0;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPost() { return post; }
    public int getVoteCount() { return voteCount; }
    public void addVote() { this.voteCount++; }
}