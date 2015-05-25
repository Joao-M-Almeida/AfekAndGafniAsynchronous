package AfekAndGafni;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class OrdinaryThread implements Runnable {
	ArrayList<Integer> LevelList;
	ArrayList<ProcessID> IdList;
	ArrayList<ProcessID> LinkList;
	ProcessID OrdinaryId;
	int OrdinaryLevel = 0;
	ProcessID PotencialOwner;
	ProcessID Owner = null;
	ProcessID Owner_Id;
	AfekAndGafniRMI[] Stubs;
	
	public OrdinaryThread(ProcessID aux_pid, AfekAndGafniRMI[] stub) {
		this.Stubs = stub;
		this.OrdinaryId = aux_pid;
		this.Owner_Id = aux_pid;
		
		this.LevelList = new ArrayList<Integer>();
		this.LinkList = new ArrayList<ProcessID>();
		this.IdList = new ArrayList<ProcessID>();
	}

	public void run() {
		Integer LevelAux;
		ProcessID IdAux;
		ProcessID LinkAux;
		while(true) {
			
			while(!Init.ConnectionsReady){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if(!this.LevelList.isEmpty() && !this.IdList.isEmpty()){
				/*Process Messages and remove them from the list*/

				LevelAux = this.RemoveElementLevel();
				IdAux = this.RemoveElementId();
				LinkAux = this.LinkList.remove(0);

				if( LevelAux < OrdinaryLevel || ( LevelAux == OrdinaryLevel && IdAux.getId() < Owner_Id.getId() ) ){
					/* if (level', id') < (level, id) */
					/* Ignore */
					if(Init.DEBUG)System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\t" + "Ignoring received Message.");
					try {
						Thread.sleep((long)(Math.random() * 100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else if( LevelAux > OrdinaryLevel || ( LevelAux == OrdinaryLevel && IdAux.getId() > Owner_Id.getId() ) ){
					/* if (level', id') > (level, id) */
					if(Init.DEBUG)System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\t(level', id') > (level, owner-id). Message received: (" + LevelAux + "," + IdAux.getId()  + ").");
					PotencialOwner = LinkAux;
					/* (level, owner-id) = (level', id') */
					OrdinaryLevel = LevelAux;
					Owner_Id = IdAux;
					if(Owner == null){
						Owner = PotencialOwner;
						System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\tCaptured by Candidate " + Owner_Id + ".");
					}
					try {
						Thread.sleep((long)(Math.random() * 100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					SendToOwner(Owner, LevelAux, IdAux);

				}else if( LevelAux == OrdinaryLevel && IdAux.getId() == Owner_Id.getId() ){
					/* if (level', id') = (level, id) */
					if(Init.DEBUG)System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\t(level', id') = (level, owner-id). Message received: (" + IdAux.getId() + "," + LevelAux + ").");
					Owner = PotencialOwner;
					System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\tCaptured by Candidate " + Owner_Id + ".");
					try {
						Thread.sleep((long)(Math.random() * 100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(Owner == null){
						System.err.println("(Level',Id') = (" + LevelAux + "," + IdAux.getId() + ")\t(Level, Id) = (" + OrdinaryLevel+","+ Owner_Id.getId());
					}
					SendToOwner(Owner, LevelAux, IdAux);
				}

			}
			Thread.yield();
		}
	}

	public synchronized void SendToOwner(ProcessID Owner, Integer Level, ProcessID id){
		try {
			Stubs[id.getId()-1].sendToCandidate(Owner, Level, id, OrdinaryId);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return;
	}

	public synchronized void receiveOrdinaryMessage(int level, ProcessID id, ProcessID link) {
		if(Init.DEBUG) System.out.println("[Process: " + OrdinaryId.getId() + "]\t[O]\tReceived Message (Level,ID): (" + level + "," +id.getId() + ")." );
		this.LevelList.add(level);
		this.IdList.add(id);
		this.LinkList.add(link);
	}

	public synchronized Integer RemoveElementLevel(){
		return LevelList.remove(0);
	}
	public synchronized ProcessID RemoveElementId(){
		return IdList.remove(0);
	}
	
	public void Kill(){
		
	}

}
