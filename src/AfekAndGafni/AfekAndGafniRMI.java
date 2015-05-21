package AfekAndGafni;

import java.rmi.*;

public interface AfekAndGafniRMI extends Remote{
	
	public void sendToCandidate(ProcessID to, int level, ProcessID id) throws java.rmi.RemoteException;
	
	public void sendToOrdinary(ProcessID to, int level, ProcessID id) throws java.rmi.RemoteException;
	
	public void ElectionOver(int id) throws java.rmi.RemoteException;
	
}
