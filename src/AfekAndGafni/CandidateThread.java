package AfekAndGafni;

import java.rmi.RemoteException;



public class CandidateThread implements Runnable {

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

	private void Candidate() {
		// TODO Auto-generated method stub
		
	}

	public void receiveCandidateMessage(int level, ProcessID id) {
		// TODO Auto-generated method stub
		
	}

}
