package AfekAndGafni;

import java.rmi.RemoteException;



public class RandomCSAcess implements Runnable {

	ProcessID[] requestSet;
	AfekAndGafniRMI[] stubSet;
	 ProcessID me;
	 
	public RandomCSAcess(ProcessID[] requestSet, AfekAndGafniRMI[] stubSet, ProcessID me) {
		this.requestSet = requestSet;
		this.stubSet = stubSet;
		this.me = me;
	}
	
	public void run() {
		System.out.println(me + " started random CS acess.");
		while(true){
			try {
				
					Thread.sleep((long)(Math.random() * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!Init.requested[me.getId()-1])
				SendRequest();		
			}
	}

	private void SendRequest() {
	/*	Init.R[me.getId()-1].nGrants=0;
		int t = Init.timestamp[me.getId()-1];
		
		System.out.println("                        " + me + " Requesting Acess" );
		System.out.flush();
		
		for(int i=0;i<requestSet.length;i++) {
			try {
				stubSet[i].sendRequest(t, me, requestSet[i]);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		Init.timestamp[me.getId()-1]++;
		Init.requested[me.getId()-1]=true;*/
	}

}
