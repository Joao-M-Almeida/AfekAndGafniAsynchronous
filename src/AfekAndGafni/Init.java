package AfekAndGafni;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;


public class Init {
	
	public static String[] IPs;
	public static Registry[] registry;
	public static AfekAndGafniRMI[] stub;
	//public static int Process_Number;
	public static int MachineNumber;
	public static int NumberOfMachines;
	public static int NumberOfProcesses;
	public static int NumberOfProcessesPerMachine;
	public static boolean ConnectionsReady = false;
	public static int msgN;
	public static int[] timestamp;
	
	public static boolean DEBUG;
	public static boolean ElectionOver;
	
	public static OrdinaryThread[] O; // Contains a pointer to the thread of each process on this machine. Process 1 on index 0
	public static CandidateThread[] C;
	public static boolean[] requested;
	public static Integer Lsum = 0;
	public static Integer Csum = 0;
	
	
	public static void createOThread(){
		int i,aux_int;
		Thread ot;
        ProcessID aux_pid;
		// Start the receiving Thread		
		for(i=0 ; i<NumberOfProcessesPerMachine ; i++){	
			// Iterates on the processes in this machine
			aux_int= MachineNumber*NumberOfProcessesPerMachine - NumberOfProcessesPerMachine + i; // starts at 0
			aux_pid=new ProcessID(aux_int + 1); 
            O[aux_int] = new OrdinaryThread(aux_pid,stub); // Start a OT per Process
            ot = new Thread(O[aux_int]);
            ot.start();
		}
	}

	public static void main(String args[]){
		msgN=1;
		int i = 1;
		int k = 0;
		int z;
		int st;
		boolean failed_connection = false;
		boolean failed_lookup = false;
		
		DEBUG=false;
		
		
		NumberOfMachines = Integer.parseInt(args[0]);
		MachineNumber = Integer.parseInt(args[NumberOfMachines+1]);
		NumberOfProcesses = Integer.parseInt(args[NumberOfMachines+2])*NumberOfMachines;
		NumberOfProcessesPerMachine = Integer.parseInt(args[NumberOfMachines+2]);
		O = new OrdinaryThread[NumberOfProcesses];
		C = new CandidateThread[NumberOfProcesses];
		
		timestamp = new int[NumberOfProcesses];
		
		requested = new boolean[NumberOfProcesses];
		
		Arrays.fill(timestamp, 1);
		
		if(DEBUG) System.out.println("Number of Machines: " + NumberOfMachines + ". Machine Number: " + MachineNumber + ". Total Number of Processes: " + NumberOfProcesses + ".");
		
		IPs = new String[NumberOfMachines];
		registry = new Registry[NumberOfMachines];
		stub = new AfekAndGafniRMI[NumberOfProcesses];
		
		System.out.println("Use of arguments: [number of machines (n)] [ip_1]...[ip_n] [process number of the corresponding machine] [number of processes in each machine]");
		
		try {
			if( NumberOfMachines != args.length - 3){
				System.err.println("Use of args incorrect...");
				System.exit(0);
			}
			
			i=0;
			while( i  < NumberOfMachines ){
				IPs[i] = args[i+1];
				i++;
			}
			
			// Create all the OrdinaryCandidate threads and send them to the server class
			Init.createOThread(); // Doesn't need to be here
			Server s = new Server(O,C);
			new Thread(s).start();
			if(DEBUG)System.out.print("Creating Server . .");
			while(!s.server_rdy){
				if(DEBUG)System.out.print(" .");
				try {
					Thread.sleep(100);                 
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
			if(DEBUG)System.out.println("\nServer Ready");
			
			for( i=0 ; i<NumberOfMachines ; i++ ){
				k=0;
				if(DEBUG)System.out.print("Trying to Connect with " + IPs[i] + " .");
				while (true) {
					failed_connection=false;
					try{
						registry[i] = LocateRegistry.getRegistry(IPs[i]);
					} catch (RemoteException er){
						failed_connection = true;
					}
					
					if(!failed_connection){
						break;
					}
					
					if(DEBUG)System.out.print(" .");
					
					try {
						Thread.sleep(1000);                 
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					
					k++;
					if(k > 10){
						System.err.println("\nCouldn't Connect with " + IPs[i] + ".Exiting...");
						System.exit(0);
					}
					
					
				}
				failed_connection = false;
				if(DEBUG)System.out.println("");
				
				k=0;
				if(DEBUG)System.out.println("Connected to " + IPs[i] + ".");
				if(DEBUG)System.out.print("Trying to Bind with " + IPs[i] + " .");
				for(z=0 ; z<NumberOfProcessesPerMachine ; z++){
					while( true ){
						st=0;
						try{
							// Para cada máquina, cria o stub para cada um dos processos.
							st = (i+1)*NumberOfProcessesPerMachine - NumberOfProcessesPerMachine + z + 1;
							stub[st-1] = (AfekAndGafniRMI) registry[i].lookup("rmi://localhost:1099/Process" + st);
							if(DEBUG)System.out.printf("\nBinded with rmi://localhost:1099/Process" + st);
						} catch (RemoteException estub) {
							failed_lookup = true;
							//System.out.println(st);
							//estub.printStackTrace();
						} catch (Exception E){
							E.printStackTrace();	
							failed_lookup = true;
							System.out.println(st);
						} 		
						if(!failed_lookup){
							break;
						}
						if(DEBUG)System.out.print(" .");
						try {
							Thread.sleep(1000);                 
						} catch(InterruptedException ex) {
							Thread.currentThread().interrupt();
						}
						k++;
						if(k > 10){
							System.err.println("\nCouldn't Bind with " + IPs[i] + ". Exiting...");
							System.exit(0);
						}
						failed_lookup = false;	
						
					}
				}
				   
				if(DEBUG)System.out.println("\nBinded with " + IPs[i] + ".");
				failed_lookup = false;
			
			}
			ConnectionsReady = true;
			
		
			System.out.println("[INFO]\t  \tConnections Ready");
			
			
			ProcessID id_g;
			int aux_int;
			for(i=0 ; i<NumberOfProcessesPerMachine ; i++ ){
				aux_int= MachineNumber*NumberOfProcessesPerMachine - NumberOfProcessesPerMachine + i;
				id_g = new ProcessID(aux_int + 1);
				C[aux_int]=new CandidateThread(stub, id_g);
				new Thread(C[aux_int]).start();
			}
			
			while(true){
				Thread.sleep(1000);			
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
	
	
}
