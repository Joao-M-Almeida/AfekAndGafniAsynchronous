package AfekAndGafni;

import java.rmi.*;

public interface AfekAndGafniRMI extends Remote{
	
	public void sendToCandidate()
	
	
	public void sendRequest(int T, ProcessID From, ProcessID To)  throws java.rmi.RemoteException;

	public void sendGrant(ProcessID To) throws java.rmi.RemoteException;
	
	public void sendInquire(ProcessID From, ProcessID To) throws java.rmi.RemoteException;
	
	public void sendRelinquish(ProcessID To) throws java.rmi.RemoteException;
	
	public void sendRelease(ProcessID To) throws java.rmi.RemoteException;
	
	public void sendPostponed(ProcessID To) throws java.rmi.RemoteException;
}
