import java.util.HashMap;

public class Query {
	private int ID;
	private String query;
	public  String []  tokenizedQuery;
	public HashMap <String, Double> hm;
	// empty constructor
	public Query() {

	}
	// overloaded constructor
	public Query(int ID, String query) {
		this.setID(ID);
		this.setQuery(query);
	}
	
	// getter and setters
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	// toString method
	
	public void toString(Query q) {
		System.out.println(q.getID() + "\t" + q.getQuery());
	}
}
