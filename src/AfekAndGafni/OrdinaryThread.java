package AfekAndGafni;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class OrdinaryThread implements Runnable {
	ArrayList<Integer> LevelList;
	ArrayList<ProcessID> IdList;
	ProcessID OrdinaryId;
	int OrdinaryLevel = 0;
	ProcessID PotencialOwner;
	ProcessID Owner = null;
	ProcessID Owner_Id;
	AfekAndGafniRMI[] Stubs;
	private ArrayList<ProcessID> fromList;
	
	
	public OrdinaryThread(ProcessID aux_pid, AfekAndGafniRMI[] stub) {
		this.Stubs = stub;
		this.OrdinaryId = aux_pid;
		this.Owner_Id = aux_pid;
		
		this.LevelList = new ArrayList<Integer>();
		this.IdList = new ArrayList<ProcessID>();
		fromList = new ArrayList<ProcessID>();
	}

	public void run() {
		Integer LevelAux;
		ProcessID IdAux;
		ProcessID fromAux;
		while(true) {
			
			while(!Init.ConnectionsReady){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if(!this.LevelList.isEmpty() && !this.IdList.isEmpty() && !this.fromList.isEmpty()){
				/*Process Messages and remove them from the list*/

				LevelAux = this.RemoveElementLevel();
				IdAux = this.RemoveElementId();
				fromAux = this.RemoveElementfrom();
				
				if( LevelAux < OrdinaryLevel || ( LevelAux == OrdinaryLevel && IdAux.getId() < Owner_Id.getId() ) ){
					/* if (level', id') < (level, id) */
					/* Ignore */
					if(Init.DEBUG)System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\t" + "Ignoring received Message: ("+LevelAux.intValue()+","+ IdAux.getId()+").");
				}else if( LevelAux > OrdinaryLevel || ( LevelAux == OrdinaryLevel && IdAux.getId() > Owner_Id.getId() ) ){
					/* if (level', id') > (level, id) */
					if(Init.DEBUG)System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\t(level', id') > (level, owner-id). Message received: (" + LevelAux + "," + IdAux.getId()  + ").");
					PotencialOwner = fromAux;
					/* (level, owner-id) = (level', id') */
					OrdinaryLevel = LevelAux;
					Owner_Id = IdAux;
					if(Owner == null){
						Owner = PotencialOwner;
						System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\tCaptured by Candidate " + Owner_Id + ".");
					}
					SendToOwner(Owner, LevelAux, IdAux);

				}else if( LevelAux == OrdinaryLevel && IdAux.getId() == Owner_Id.getId() ){
					/* if (level', id') = (level, id) */
					if(true)System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\t(level', id') = (level, owner-id). Message received: (" + LevelAux + "," + IdAux.getId() + ") from "+ fromAux +".");
					Owner = PotencialOwner;
					System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\tCaptured by Candidate " +Owner +".");
					SendToOwner(Owner, LevelAux, IdAux);
				}

			}
			Thread.yield();
		}
	}

	public synchronized void SendToOwner(ProcessID Owner, Integer Level, ProcessID id){
		try {
			if(Init.DEBUG)System.out.println("[" + OrdinaryId + "]\t[O]\tSent to Owner " + Owner + " (Level, ID): ("+Level.intValue() +","+id.getId() +").");
			Stubs[id.getId()-1].sendToCandidate(Owner, Level, id, OrdinaryId);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return;
	}

	public synchronized void receiveOrdinaryMessage(int level, ProcessID id,ProcessID from) {
		if(Init.DEBUG) System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\tReceived Message (Level,ID): (" + level + "," +id.getId() + ") from: "+from+"." );
		this.LevelList.add(level);
		this.IdList.add(id);
		this.fromList.add(from);
	}

	public synchronized Integer RemoveElementLevel(){
		return LevelList.remove(0);
	}
	public synchronized ProcessID RemoveElementId(){
		return IdList.remove(0);
	}
	
	private synchronized ProcessID RemoveElementfrom() {
		return fromList.remove(0);
	}
	
	public void Kill(){
		
	}

}
