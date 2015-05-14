package AfekAndGafni;

import java.util.ArrayList;

public class OrdinaryThread implements Runnable {
	ArrayList<Integer> LevelList;
	ArrayList<Integer> IdList;
	Integer OrdinaryId = Init.Process_Number;
	Integer OrdinaryLevel = 0;
	
	public OrdinaryThread(ProcessID aux_pid, AfekAndGafniRMI[] stub) {
		// TODO Auto-generated constructor stub
	}

	public void run() {
		Integer LevelAux;
		Integer IdAux;
		
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
			
			if( LevelAux < OrdinaryLevel || ( LevelAux == OrdinaryLevel && IdAux < OrdinaryId ) ){
				/* (level', id') < (level, id) */
				/* Ignore */
			}else if( LevelAux > OrdinaryLevel || ( LevelAux == OrdinaryLevel && IdAux > OrdinaryId ) ){
				/* (level', id') < (level, id) */
			}else if( LevelAux == OrdinaryLevel && IdAux == OrdinaryId ){
				/* (level', id') = (level, id) */
			}
			
		}

	}

	public void receiveOrdinaryMessage(int level, ProcessID id) {
		// TODO Auto-generated method stub
		
	}


	public synchronized Integer RemoveElementLevel(){
		return LevelList.remove(0);
	}
	public synchronized Integer RemoveElementId(){
		return IdList.remove(0);
	}

}


