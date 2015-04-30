package AfekAndGafni;



@SuppressWarnings("serial")
public class ProcessID implements java.io.Serializable{
	

	private int id;
	
	public ProcessID(int id) {
		this.id=id;
	}
	
	
	// Returns true if this is bigger than p
	public boolean isBigger(ProcessID p) {
		if(this.id>p.getId())
			return true;
		return false;
	}
	
	public boolean equals(Object pid) {
		if(pid instanceof ProcessID)
			if(this.getId()==((ProcessID) pid).getId())
				return true;
		return false;
	}
	
	public int getId() {
		return this.id;
	}

	public String toString() {
		return "Process: " + id;
	}
	

}
