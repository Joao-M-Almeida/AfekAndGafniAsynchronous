package AfekAndGafni;

import java.rmi.RemoteException;
import java.util.ArrayList;



public class CandidateThread implements Runnable {

	ArrayList<Integer> LevelList;
	ArrayList<ProcessID> IdList;
	AfekAndGafniRMI[] stubSet;
	ArrayList<ProcessID> untraversed;
	ProcessID me;
	int myLevel;
	 
	public CandidateThread(AfekAndGafniRMI[] stubSet, ProcessID me) {
		
		this.stubSet = stubSet;
		this.me = me;
		untraversed= new ArrayList<ProcessID>();
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
		myLevel=1;
		for(int i=0;i<Init.NumberOfProcesses;i++) {
			if((i+1)!=me.getId())
				untraversed.add(new ProcessID(i+1));	
		}
		
		ProcessID nextVictim;
		while(!untraversed.isEmpty()){
			nextVictim=untraversed.remove(0);
			try {
				if(Init.DEBUG)
					System.out.println( me + " Sent Level: "+ myLevel + " to " +nextVictim );
				stubSet[nextVictim.getId()-1].sendToOrdinary(nextVictim, myLevel, me);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			
			
			Thread.yield();
		}
		
	}

	public void receiveCandidateMessage(int level, ProcessID id) {
		if(Init.DEBUG)
			System.out.println( me + " Received Level: "+ level + " from " +id );
		LevelList.add(level);
		IdList.add(id);
	}

}
