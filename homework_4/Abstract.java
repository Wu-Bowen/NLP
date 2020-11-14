import java.util.HashMap;

public class Abstract {
	private int ID;
	private String abs;
	public  String []  tokenizedQuery;
	public HashMap <String, Double> hm;
	// empty constructor
	public Abstract() {

	}
	// overloaded constructor
	public Abstract(int ID, String abs) {
		this.setID(ID);
		this.setQuery(abs);
	}
	
	// getter and setters
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getQuery() {
		return abs;
	}

	public void setQuery(String query) {
		this.abs = query;
	}
	
	// toString method
	
	public void toString(Query q) {
		System.out.println(q.getID() + "\t" + q.getQuery());
	}
}
