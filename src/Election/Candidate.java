package Election;

public class Candidate {

	String name;
	Integer id;
	Integer votes;
	
	public Candidate() {
		// TODO Auto-generated constructor stub
	}

	public Candidate(String name, String id, String votes) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.id = Integer.parseInt(id);
		this.votes = Integer.parseInt(votes);
	}

	public void updateVote(){
		this.votes++;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVotes() {
		return votes;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}

	
}
