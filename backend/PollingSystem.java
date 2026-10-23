package backend;
import java.util.ArrayList;
import java.util.List;

public class PollingSystem {
    private List<Candidate> candidates = new ArrayList<>();
    private List<Voter> registeredVoters = new ArrayList<>();

    public PollingSystem() {
        candidates.add(new Candidate(1, "Alice Smith"));
        candidates.add(new Candidate(2, "Bob Jones"));
        registeredVoters.add(new Voter("V101", "Student One"));
        registeredVoters.add(new Voter("V102", "Student Two"));
    }

    public List<Candidate> getCandidates() { return candidates; }

    public String castVote(String voterId, int candidateId) {
        Voter currentVoter = null;
        for (Voter v : registeredVoters) {
            if (v.getVoterId().equals(voterId)) {
                currentVoter = v;
                break;
            }
        }
        if (currentVoter == null) return "Error: Unregistered Voter ID.";
        if (currentVoter.hasVoted()) return "Error: You have already voted.";

        for (Candidate c : candidates) {
            if (c.getId() == candidateId) {
                c.addVote();
                currentVoter.markAsVoted();
                return "Success: Vote recorded for " + c.getName();
            }
        }
        return "Error: Invalid Candidate.";
    }
}