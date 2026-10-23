package backend;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PollingSystem {
    private List<Candidate> candidates = new ArrayList<>();
    private List<Voter> registeredVoters = new ArrayList<>();
    private List<String> auditLog = new ArrayList<>(); // Bonus: Audit log

    public PollingSystem() {
        // Bonus: Multiple polls (President & Secretary)
        candidates.add(new Candidate(1, "Alice Smith", "President"));
        candidates.add(new Candidate(2, "Bob Jones", "President"));
        candidates.add(new Candidate(3, "Charlie Brown", "Secretary"));
        candidates.add(new Candidate(4, "Diana Prince", "Secretary"));

        registeredVoters.add(new Voter("V101", "Student One"));
        registeredVoters.add(new Voter("V102", "Student Two"));
    }

    public List<Candidate> getCandidates() { return candidates; }
    public List<String> getAuditLog() { return auditLog; }

    // Bonus: Basic voter authentication (ID + name match)
    public String authenticate(String voterId, String name) {
        for (Voter v : registeredVoters) {
            if (v.getVoterId().equals(voterId)) {
                if (v.getName().equalsIgnoreCase(name)) {
                    return "Success";
                }
                return "Error: Name does not match the registered ID.";
            }
        }
        return "Error: Unregistered Voter ID.";
    }

    public String castVote(String voterId, int candidateId) {
        Voter currentVoter = null;
        for (Voter v : registeredVoters) {
            if (v.getVoterId().equals(voterId)) {
                currentVoter = v; break;
            }
        }
        if (currentVoter == null) return "Error: Unregistered Voter ID.";

        Candidate targetCandidate = null;
        for (Candidate c : candidates) {
            if (c.getId() == candidateId) {
                targetCandidate = c; break;
            }
        }
        if (targetCandidate == null) return "Error: Invalid Candidate.";
        
        // Block double voting per specific post
        if (currentVoter.hasVotedFor(targetCandidate.getPost())) {
            return "Error: You have already voted for the post of " + targetCandidate.getPost() + ".";
        }

        targetCandidate.addVote();
        currentVoter.markAsVoted(targetCandidate.getPost());
        
        // Bonus: Audit log of timestamps (anonymised)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        auditLog.add("Vote cast for " + targetCandidate.getPost() + " at " + timestamp);
        
        return "Success: Vote recorded for " + targetCandidate.getName();
    }
}