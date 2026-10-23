package backend;

public class Candidate {
    private int id;
    private String name;
    private int voteCount;

    public Candidate(int id, String name) {
        this.id = id;
        this.name = name;
        this.voteCount = 0;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getVoteCount() { return voteCount; }
    public void addVote() { this.voteCount++; }
}