package AfekAndGafni;

import java.rmi.RemoteException;
import java.util.ArrayList;



public class CandidateThread implements Runnable {

	ArrayList<Integer> LevelList;
	ArrayList<ProcessID> IdList;
	AfekAndGafniRMI[] stubSet;
	ArrayList<ProcessID> untraversed;
	ProcessID me;
	boolean killed = false;
	boolean elected = false;
	Integer myLevel;
	 
	public CandidateThread(AfekAndGafniRMI[] stubSet, ProcessID me) {
		
		this.stubSet = stubSet;
		this.me = me;
		untraversed= new ArrayList<ProcessID>();
		LevelList = new ArrayList<Integer>();
		IdList = new ArrayList<ProcessID>();
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
			/*<   JOÃO  Acho que aqui não se pode remover
			 *  o elemento, só depois de receberes uma 
			 *  mensagem dele com o mesmo Id*/
			//nextVictim=untraversed.remove(0);
			nextVictim = untraversed.get(0);
			try {
				if(Init.DEBUG)
					System.out.println( "Candidate " + me + " Sent Level: "+ myLevel + " to " +nextVictim );
				stubSet[nextVictim.getId()-1].sendToOrdinary(nextVictim, myLevel, me);
				WaitAnswer();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
	
			Thread.yield();
		}
		if(killed == false){
			System.out.println(me + " elected!");
			elected = true;
		}
		
	}
	
	public void WaitAnswer() throws RemoteException{
		Integer LevelAux;
		ProcessID IdAux;
		while(true){
			try {
				// Should we sleep? maybe adds to much delay? 
				// Change to yield?
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
			if(!this.LevelList.isEmpty() && !this.IdList.isEmpty()){
				/* Answer Received */
				LevelAux = this.RemoveElementLevel();
				IdAux = this.RemoveElementId();
								
				if( me.getId() == IdAux.getId() && killed == false){
					myLevel++;
					if(Init.DEBUG)
						System.out.println("Candidate: " + me + " incremented level to: " + myLevel);
					untraversed.remove(0);
					if(untraversed.isEmpty()){
						if(killed == false){
							System.out.println(me + " elected!");
							elected = true;
							break;
						}
					}
				}else{
					if(CompareLevelId(me,myLevel,IdAux,LevelAux)){
						// When does this happen?
					}else{
						if(Init.DEBUG)
							System.out.println( me + " sent Id" + IdAux.getId() + " and level :" + LevelAux + " to " + IdAux + " ordinary" );
						stubSet[IdAux.getId()-1].sendToOrdinary(IdAux, LevelAux, IdAux);
						if(Init.DEBUG)
							System.out.println( me + " received kill" );
						killed = true;
					}
				}
			}
		}
	}
	
	/*Returns true if Candidate's (level,id) is bigger, false otherwise*/
	public boolean CompareLevelId(ProcessID me, Integer myLevel, ProcessID IdAux, Integer LevelAux ){
		if(myLevel  > LevelAux){
			return true;
		}else if(myLevel == LevelAux && me.getId() > IdAux.getId()){
			return true;
		}
		
		return false;
	}
	
	public synchronized Integer RemoveElementLevel(){
		return LevelList.remove(0);
	}
	public synchronized ProcessID RemoveElementId(){
		return IdList.remove(0);
	}

	public synchronized void receiveCandidateMessage(int level, ProcessID id) {
		if(Init.DEBUG)
			System.out.println("Candidate "+ me + " Received Level: "+ level + " and Id " +id.getId() );
		LevelList.add(level);
		IdList.add(id);
		LevelList.add(level);
	}

}