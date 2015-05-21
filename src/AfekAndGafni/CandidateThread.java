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
		/* Wait up to 5 seconds */
		try {
			Thread.sleep((long)(Math.random() * 5000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		/* Start Candidate process */
		System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Is now a Candidate" + ".");
		Candidate();
	}

	private void Candidate() {
		myLevel=1;
		
		for(int i=0;i<Init.NumberOfProcesses;i++) {
			untraversed.add(new ProcessID(i+1));	
		}
		
		if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" +  "Has to traverse " + untraversed + ".");
		
		ProcessID nextVictim;
		if(!untraversed.isEmpty()){
			nextVictim = untraversed.get(0);
			try {
				if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Sent Message (Level, ID): ("+ myLevel + "," + me.getId() + ") to Candidate " + nextVictim  + ".");
				stubSet[nextVictim.getId()-1].sendToOrdinary(nextVictim, myLevel, me);
				WaitAnswer();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
	
			Thread.yield();
		}
		if(killed == false){
			System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Elected!! Fuck you bitches!! ");
			elected = true;
		}
	}
	
	public void WaitAnswer() throws RemoteException{
		Integer LevelAux;
		ProcessID IdAux;
		while(true){
			
			try {
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
					if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Incremented level to: " + myLevel + ".");
					untraversed.remove(0);
					if(untraversed.isEmpty()){
						if(killed == false){
							System.out.println(me + " elected!");
							elected = true;
							break;
						}
					} else {
						IdAux = untraversed.get(0);
						try {
							if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Trying to capture Process " + IdAux + " with Message (Level,ID): (" + myLevel + "," + me.getId() + ").");
							stubSet[IdAux.getId()-1].sendToOrdinary(IdAux, myLevel, me);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}else{
					if(CompareLevelId(me,myLevel,IdAux,LevelAux)){
						// When does this happen?
						if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Something probably went wrong... Check me.");

					}else{
						if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Sent Message (Level, ID): ("+ myLevel + "," + me.getId() + ") to Candidate " + IdAux  + ".");
						stubSet[IdAux.getId()-1].sendToOrdinary(IdAux, LevelAux, IdAux);
						if(Init.DEBUG)
							System.out.println( "[Process: " + me.getId() + "]\t[C]\tWas Killed" );
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
		if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Received Message (Level, ID): (" + level + "," +id.getId() + ")." );
		LevelList.add(level);
		IdList.add(id);
		LevelList.add(level);
	}

}