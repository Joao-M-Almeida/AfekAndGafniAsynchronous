package AfekAndGafni;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements AfekAndGafniRMI, Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean server_rdy = false;
	
	private OrdinaryThread[] O;
	private CandidateThread[] C;
	

	public void run() {
		int i;
		int j;
		try {
			AfekAndGafniRMI stub = (AfekAndGafniRMI) this; 
			Registry registry = LocateRegistry.createRegistry(1099);
			for(i=0 ; i<Init.NumberOfProcessesPerMachine ; i++){
				j = Init.MachineNumber*Init.NumberOfProcessesPerMachine - Init.NumberOfProcessesPerMachine + 1 + i;
				registry.rebind("rmi://localhost:1099/Process" + j, stub);
				if(Init.DEBUG)System.out.printf("\nCreated: rmi://localhost:1099/Process" + j);
			}
			server_rdy = true;		
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	public Server(OrdinaryThread[] o, CandidateThread[] c) throws RemoteException{
		C=c;
		O=o;
	}

	public void sendToCandidate(ProcessID to, int level, ProcessID id, ProcessID link)
			throws RemoteException {
		int yolo;
		//yolo = to.getId()-1;
		System.err.println("to,level,id,link = " +  to.getId()+","+level+","+id.getId()+","+link.getId());
		C[to.getId()-1].receiveCandidateMessage(level,id,link);		
		//System.err.println("to.detId()-1 = " +  yolo);
	}
	
	public void ElectionOver(int id) throws RemoteException {
		C[id].Kill();
	}


	public void sendToOrdinary(ProcessID to, int level, ProcessID id, ProcessID link)
			throws RemoteException {
		System.err.println("to,level,id,link = " +  to.getId()+","+level+","+id.getId()+","+link.getId());
		O[to.getId()-1].receiveOrdinaryMessage(level,id, link);
	}


	
}

