package AfekAndGafni;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;



public class CandidateThread implements Runnable {

	ArrayList<Integer> LevelList;
	ArrayList<ProcessID> IdList;
	AfekAndGafniRMI[] stubSet;
	ArrayList<ProcessID> untraversed;
	ProcessID me;
	boolean killed = false;
	boolean elected = false;
	Integer myLevel;
	private ProcessID nextVictim;
	private int nextVictimIndex;
	Random r;
	 
	public CandidateThread(AfekAndGafniRMI[] stubSet, ProcessID me) {
		
		this.stubSet = stubSet;
		this.me = me;
		untraversed= new ArrayList<ProcessID>();
		LevelList = new ArrayList<Integer>();
		IdList = new ArrayList<ProcessID>();
		r=new Random();
	}
	
	public void run() {
		/* Wait up to 5 seconds */
		try {
			Thread.sleep((long)(Math.random() * 4000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		/* Start Candidate process */
		
		if(!Init.ElectionOver){
			System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Is now a Candidate" + ".");
			Candidate();
		}

	}

	private void Candidate() {
		myLevel=1;
		
		for(int i=0;i<Init.NumberOfProcesses;i++) {
			if(me.getId() != i){
				untraversed.add(new ProcessID(i+1));
			}
		}
		
		//if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" +  "Has to traverse " + untraversed + ".");
		
		if(!untraversed.isEmpty()){
			nextVictimIndex = r.nextInt(untraversed.size());
			nextVictim = untraversed.get(nextVictimIndex);

			try {
				if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Sent Message (Level, ID): ("+ myLevel + "," + me.getId() + ") to Candidate " + nextVictim  + ".");
				stubSet[nextVictim.getId()-1].sendToOrdinary(nextVictim, myLevel, me);
				WaitAnswer();
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
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
					Init.O[me.getId()-1].OrdinaryLevel = myLevel;
					
					if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Incremented level to: " + myLevel + ".");
					untraversed.remove(nextVictimIndex);

					if(untraversed.isEmpty()){
						if(killed == false){
							System.err.println("[Process: " + me.getId() + "]\t[C]\t" + "Elected!! ");
							elected = true;
							Init.ElectionOver = true;
							killall();
							break;
						}
					}else{

						nextVictimIndex = r.nextInt(untraversed.size());
						nextVictim = untraversed.get(nextVictimIndex);
						try {
							try {
								Thread.sleep((long)(Math.random() * 100));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Trying to capture Process " + nextVictim + " with Message (Level,ID): (" + myLevel + "," + me.getId() + ").");
							stubSet[nextVictim.getId()-1].sendToOrdinary(nextVictim, myLevel, me);

						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}else{
					if(CompareLevelId(me,myLevel,IdAux,LevelAux)){
						// When does this happen?
						if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Something probably went wrong... Check me.");

					}else{
						try {
							Thread.sleep((long)(Math.random() * 100));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Sent Message (Level, ID): ("+ myLevel + "," + me.getId() + ") to Candidate " + IdAux  + ".");
						stubSet[IdAux.getId()-1].sendToOrdinary(IdAux, LevelAux, IdAux);
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
	
	public void killall() throws RemoteException{
		int i;
		for(i=0;i<Init.NumberOfProcesses;i++){
			stubSet[i].ElectionOver(i);
		}
	}
	
	public synchronized void Kill(){
		Init.ElectionOver = true;
	}


}