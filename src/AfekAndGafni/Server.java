package AfekAndGafni;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements AfekAndGafniRMI, Runnable {
	
	public boolean server_rdy = false;

	public void run() {
		int i;
		int j;
		try {
			MaekawaRemoteInterface stub = (MaekawaRemoteInterface) this;
			Registry registry = LocateRegistry.createRegistry(1099);
			for(i=0 ; i<Init.NumberOfProcessesPerMachine ; i++){
				j = Init.MachineNumber*Init.NumberOfProcessesPerMachine - Init.NumberOfProcessesPerMachine + 1 + i;
				registry.rebind("rmi://localhost:1099/Process" + j, stub);
				System.out.printf("\nCreated: rmi://localhost:1099/Process" + j);
			}
			server_rdy = true;		
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private ReceivingThread[] rt;
	
	public Server(ReceivingThread[] rt) throws RemoteException{
		this.rt=rt;
	}

	 
	public void sendRequest(int T, ProcessID From, ProcessID To) throws RemoteException {
		rt[To.getId()-1].incoming("Request",T,From);
	}

	 
	public void sendGrant(ProcessID To) throws RemoteException {
		rt[To.getId()-1].incoming("Grant",-1, new ProcessID(-1));
		
	}

	 
	public void sendInquire(ProcessID From, ProcessID To) throws RemoteException {
		rt[To.getId()-1].incoming("Inquire",-1,From);
		
	}

	 
	public void sendRelinquish(ProcessID To) throws RemoteException {
		rt[To.getId()-1].incoming("Relinquish",-1,new ProcessID(-1));
		
	}

	 
	public void sendRelease(ProcessID To) throws RemoteException {
		rt[To.getId()-1].incoming("Release",-1,new ProcessID(-1));
		
	}

	 
	public void sendPostponed(ProcessID To) throws RemoteException {
		rt[To.getId()-1].incoming("Postponed",-1,new ProcessID(-1));
		
	}


	
}

