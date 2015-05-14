package AfekAndGafni;

import java.rmi.RemoteException;
import java.util.ArrayList;



public class CandidateThread implements Runnable {

	ArrayList<Integer> LevelList;
	ArrayList<ProcessID> IdList;
	AfekAndGafniRMI[] stubSet;
	 ProcessID me;
	 
	public CandidateThread(AfekAndGafniRMI[] stubSet, ProcessID me) {
		
		this.stubSet = stubSet;
		this.me = me;
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

	/* while (untraversed )= /) do
		link% any untraversed link
		send(level,id) on link
		R: receive(level’,id’) on link’
		if ((id=id’) and (killed=false)) then
		level% level+1
		untraversed% untraversed \ link
		else
		if ((level’,id’) < (level,id)) then goto R
		else
		send(level’,id’) on link’
		killed% true
		goto R
		if (killed = false) then elected% true

*/
	
	private void Candidate() {
		// TODO Auto-generated method stub
		while(true){
			
			Thread.yield();
		}
		
	}

	public void receiveCandidateMessage(int level, ProcessID id) {
		LevelList.add(level);
		IdList.add(id);
	}

}
