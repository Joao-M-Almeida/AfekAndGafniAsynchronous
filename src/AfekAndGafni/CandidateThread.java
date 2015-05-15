package AfekAndGafni;

import java.rmi.RemoteException;
import java.util.ArrayList;



public class CandidateThread implements Runnable {

	ArrayList<Integer> LevelList;
	AfekAndGafniRMI[] stubSet;
	ProcessID me;
	int CandidateLevel;
	ArrayList<Integer> UsedIdList;
	ArrayList<ProcessID> IdList;
	int NumberOfProcesses;
	 
	public CandidateThread(AfekAndGafniRMI[] stubSet, ProcessID me, int NumberOfStubs) {
		
		this.stubSet = stubSet;
		this.me = me;
		this.NumberOfProcesses =  NumberOfStubs;
		
		this.UsedIdList = new ArrayList<Integer>();
	}
	
	public void run() {
		System.out.println(me + " started random wait for candidate");
		
		// Wait up to 10 seconds
		try {
			Thread.sleep((long)(Math.random() * 10000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Start Candidate process
		System.out.println(me + " started candidate");
		Candidate();
	}
	
	private void Candidate() {
		// TODO Auto-generated method stub
		
		/* YIELD HERE?   	<<<<<<<<<<<<<<<<<<<  	<<<<<<<<<<<<<<<<<<<		<<<<<<<<<<<<<<<<<<< 	<<<<<<<<<<<<<<<<<<<	JOÃO */
		/*while(true){
			
			Thread.yield();
		}*/

		/*Capture links*/
		int i = 0;
		int NextId = me.getId();
		
		while(i != NumberOfProcesses){
			try {
				Thread.sleep((long)(Math.random() * 2000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			/* Generate random Id, and make sure that the Id wasn't used or that is its own Id */
			while(NextId != me.getId() && !UsedIdList.contains(NextId)){
				NextId = (int)(Math.random() * NumberOfProcesses);
			}
			/* Send Candidate Message*/
			UsedIdList.add(NextId);
			SendCandidateMessage(NextId, CandidateLevel, me );
			i++;
		}
	}
	
	public synchronized void SendCandidateMessage(int to, int Level, ProcessID me){
		ProcessID sendto = new ProcessID(to+1);
		try {
			stubSet[sendto.getId()-1].sendToOrdinary( sendto, Level, me);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void receiveCandidateMessage(int level, ProcessID id) {
		LevelList.add(level);
		IdList.add(id);
	}

}
