package AfekAndGafni;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;



public class CandidateThread implements Runnable {
	
	ArrayList<Integer> LevelList;
	ArrayList<ProcessID> IdList;
	ArrayList<ProcessID> LinkList;
	AfekAndGafniRMI[] stubSet;
	ArrayList<ProcessID> untraversed;
	ProcessID me;
	boolean killed = false;
	boolean elected = false;
	Integer myLevel = 1;
	private ProcessID nextVictim;
	private int nextVictimIndex;
	Random r;
	int Lsum = 0;
	int Csum = 0;
	int count = 0;
	int ackR = 0;
	int ackS = 0;
	int kills = 0;
	 
	public CandidateThread(AfekAndGafniRMI[] stubSet, ProcessID me) {
		this.stubSet = stubSet;
		this.me = me;
		untraversed= new ArrayList<ProcessID>();
		LevelList = new ArrayList<Integer>();
		IdList = new ArrayList<ProcessID>();
		LinkList = new ArrayList<ProcessID>();
		r=new Random();
	}
	
	public void run() {
		/* Wait up to 5 seconds */
		try {
			Thread.sleep((long)(Math.random() * 10));
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
			if(me.getId() != i+1){
				untraversed.add(new ProcessID(i+1));
			}
		}
		
		//System.out.println("[Process: " + me.getId() + "]\t[C]\t" +  "Has to traverse " + untraversed + ".");
		
		if(!untraversed.isEmpty()){
			nextVictimIndex = r.nextInt(untraversed.size());
			nextVictim = untraversed.get(nextVictimIndex);

			try {
				if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Sent Message (Level, ID): ("+ myLevel + "," + me.getId() + ") to Candidate " + nextVictim  + ".");
				stubSet[nextVictim.getId()-1].sendToOrdinary(nextVictim, myLevel, me,me);
				WaitAnswer();
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}
	}
	
	public void WaitAnswer() throws RemoteException{
		Integer LevelAux;
		ProcessID IdAux;
		ProcessID LinkAux;
		while(true){
			
			Thread.yield();
		
			if(!this.LevelList.isEmpty() && !this.IdList.isEmpty()){
				/* Answer Received */
				LevelAux = this.RemoveElementLevel();
				IdAux = this.RemoveElementId();
				LinkAux = this.LinkList.remove(0);
		
				if( me.getId() == IdAux.getId() && killed == false){
					myLevel++;
					ackR++;
					//Init.O[me.getId()-1].OrdinaryLevel = myLevel;
					
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
							if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Trying to capture Process " + nextVictim + " with Message (Level,ID): (" + myLevel + "," + me.getId() + ").");
							stubSet[nextVictim.getId()-1].sendToOrdinary(nextVictim, myLevel, me, me);

						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}else{
					if(CompareLevelId(me,myLevel,IdAux,LevelAux)){
						// When does this happen?
						if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Something probably went wrong... Check me.");

					}else if(IdAux.getId() != me.getId()){
						if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Sent Message (Level, ID): ("+ LevelAux + "," + IdAux.getId() + ") to Ordinary " + LinkAux  + ".");
						stubSet[LinkAux.getId()-1].sendToOrdinary(LinkAux, LevelAux, IdAux, me);
						if(!killed){
							System.out.println( "[Process: " + me.getId() + "]\t[C]\tWas Killed" );
							
						}else{
							System.out.println( "[Process: " + me.getId() + "]\t[C]\tReceived a kill Message, but was already killed." );
						}
						kills++;
						ackS++;
						killed = true;
					}else{
						count ++;
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

	public synchronized void receiveCandidateMessage(int level, ProcessID id, ProcessID link) {
		if(Init.DEBUG) System.out.println("[Process: " + me.getId() + "]\t[C]\t" + "Received Message (Level, ID): (" + level + "," +id.getId() + ")." );
		LevelList.add(level);
		IdList.add(id);
		LinkList.add(link);
	}
	
	public void killall() throws RemoteException{
		int i;
		for(i=0;i<Init.NumberOfProcesses;i++){
			stubSet[i].ElectionOver(i);
		}
	}
	
	public synchronized void Kill(){
		int rn;
		int aux;
		Init.ElectionOver = true;
		aux = ackR  + ackS;
		System.out.println("[Process: " + me.getId() + "]\t[C]\tLevel = " + myLevel + ".\tTimes Captured = " + Init.O[me.getId()-1].captured + ".\tAcks = " + aux + ".");
		Init.lCounter(myLevel);
		Init.cCounter(Init.O[me.getId()-1].captured);
		Init.totalA = Init.totalA + aux;
		Init.totalC = Init.totalC + count;
		Init.totalK = Init.totalK + kills;
		if(me.getId() == Init.NumberOfProcesses){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			rn = Init.Lsum - Init.NumberOfProcesses;
			System.out.println("[INFO]\t\t[ ]\tLevel Sum - Number of Processes = " + rn+ ".\tCaptures Sum = " + Init.Csum + ".\tTotal Kills = " + Init.totalK + "\tTotal Acks = " + Init.totalA + ".");
			System.out.println("Missed captures: " + Init.totalC);
		}
	}


}