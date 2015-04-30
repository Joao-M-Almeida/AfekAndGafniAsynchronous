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
			AfekAndGafniRMI stub = (AfekAndGafniRMI) this; // changed from Maekawa to Afek's
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
	
	
	public Server(OrdinaryThread[] o) throws RemoteException{
		// TODO Setup do server, acrescentar ponteiros para a Thread Ordinária e o Candidato.
	}

	public void sendToCandidate(ProcessID to, int level, ProcessID id)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	public void sendToOrdinary(ProcessID to, int level, ProcessID id)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	
}

