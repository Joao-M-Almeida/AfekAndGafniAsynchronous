package AfekAndGafni;

import java.util.ArrayList;

public class OrdinaryThread implements Runnable {
	ArrayList<Integer> LevelList;
	ArrayList<ProcessID> IdList;
	ProcessID OrdinaryId;
	int OrdinaryLevel = 0;
	ProcessID PotencialOwner;
	ProcessID Owner = null;
	AfekAndGafniRMI[] Stubs;
	
	public OrdinaryThread(ProcessID aux_pid, AfekAndGafniRMI[] stub) {
		this.Stubs = stub;
		this.OrdinaryId = aux_pid;
	}

	public void run() {
		Integer LevelAux;
		ProcessID IdAux;
		
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
			
			if( LevelAux < OrdinaryLevel || ( LevelAux == OrdinaryLevel && IdAux.getId() < OrdinaryId.getId() ) ){
				/* (level', id') < (level, id) */
				/* Ignore */
			}else if( LevelAux > OrdinaryLevel || ( LevelAux == OrdinaryLevel && IdAux.getId() > OrdinaryId.getId() ) ){
				/* (level', id') < (level, id) */
			}else if( LevelAux == OrdinaryLevel && IdAux.getId() == OrdinaryId.getId() ){
				/* (level', id') = (level, id) */
			}
			
		}

	}

	public synchronized void receiveOrdinaryMessage(int level, ProcessID id) {
		this.LevelList.add(level);
		this.IdList.add(id);
	}

	public synchronized Integer RemoveElementLevel(){
		return LevelList.remove(0);
	}
	public synchronized ProcessID RemoveElementId(){
		return IdList.remove(0);
	}

}


