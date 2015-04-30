package AfekAndGafni;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

import javax.swing.DebugGraphics;


public class Init {
	
	public static String[] IPs;
	public static Registry[] registry;
	public static AfekAndGafniRMI[] stub;
	public static int Process_Number;
	public static int MachineNumber;
	public static int NumberOfMachines;
	public static int NumberOfProcesses;
	public static int NumberOfProcessesPerMachine;
	public static boolean ConnectionsReady = false;
	public static int msgN;
	public static int[] timestamp;
	
	public static boolean DEBUG;
	
	public static OrdinaryThread[] O;
	public static boolean[] requested;
	
	
	// TODO Redo this create OThread
	public static void createOThread(){
		int i;
		Thread ot;
        int aux_int;
        ProcessID aux_pid;
        ProcessID[] auxRequestSet;
		// Start the receiving Thread		
		for(i=0 ; i<NumberOfProcessesPerMachine ; i++){	
			aux_int= MachineNumber*NumberOfProcessesPerMachine - NumberOfProcessesPerMachine + i;
			aux_pid=new ProcessID(aux_int + 1);
//			System.out.println("Request set of :" + aux_pid);
//			for(int g=0;g<auxRequestSet.length;g++)
//				System.out.println(auxRequestSet[g]);
            O[aux_int] = new OrdinaryThread(aux_pid);
            ot = new Thread(O[MachineNumber*NumberOfProcessesPerMachine - NumberOfProcessesPerMachine + i]);
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
		
		DEBUG=true;
		
		
		NumberOfMachines = Integer.parseInt(args[0]);
		MachineNumber = Integer.parseInt(args[NumberOfMachines+1]);
		NumberOfProcesses = Integer.parseInt(args[NumberOfMachines+2])*NumberOfMachines;
		NumberOfProcessesPerMachine = Integer.parseInt(args[NumberOfMachines+2]);
		O = new OrdinaryThread[NumberOfProcesses];
		
		timestamp = new int[NumberOfProcesses];
		
		requested = new boolean[NumberOfProcesses];
		
		Arrays.fill(timestamp, 1);
		
		System.out.println("Number of Machines: " + NumberOfMachines + ". Machine Number: " + MachineNumber + ". Total Number of Processes: " + NumberOfProcesses + ".");
		
		IPs = new String[NumberOfMachines];
		registry = new Registry[NumberOfMachines];
		stub = new AfekAndGafniRMI[NumberOfProcesses];
		
		System.out.println("Use of arguments: [number of machines (n)] [ip_1]...[ip_n] [process number of the corresponding machine] [number of processes in each machine]");
		
		try {
			if( NumberOfMachines != args.length - 3){
				System.out.println("Use of args incorrect...");
				System.exit(0);
			}
			
			i=0;
			while( i  < NumberOfMachines ){
				IPs[i] = args[i+1];
				i++;
			}
			
			// Create all the receiving threads and send them to the server class
			Init.createOThread();
			Server s = new Server(O);
			new Thread(s).start();
			System.out.print("Creating Server . .");
			while(!s.server_rdy){
				System.out.print(" .");
				try {
					Thread.sleep(100);                 
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
			System.out.println("\nServer Ready");
			
			for( i=0 ; i<NumberOfMachines ; i++ ){
				k=0;
				System.out.print("Trying to Connect with " + IPs[i] + " .");
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
					
					System.out.print(" .");
					
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
				System.out.println("");
				
				k=0;
				System.out.println("Connected to " + IPs[i] + ".");
				System.out.print("Trying to Bind with " + IPs[i] + " .");
				for(z=0 ; z<NumberOfProcessesPerMachine ; z++){
					while( true ){
						st=0;
						try{
							st = (i+1)*NumberOfProcessesPerMachine - NumberOfProcessesPerMachine + z + 1;
							stub[st-1] = (AfekAndGafniRMI) registry[i].lookup("rmi://localhost:1099/Process" + st);
							System.out.printf("\nBinded with rmi://localhost:1099/Process" + st);
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
						System.out.print(" .");
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
				   
				System.out.println("\nBinded with " + IPs[i] + ".");
				failed_lookup = false;
			
			}
			ConnectionsReady = true;
			
		
			System.out.println("Connections Ready");
			
			ProcessID[] id_vect;
			ProcessID id_g;
			
			// TODO Creation of CandidateThreads
			/*for(i=0 ; i<NumberOfProcessesPerMachine ; i++ ){
				id_g = new ProcessID(MachineNumber*NumberOfProcessesPerMachine - NumberOfProcessesPerMachine + i + 1);
				id_vect = generateRequestSet(NumberOfProcesses,id_g);
				new Thread(new RandomCSAcess(id_vect, generateStubset(id_vect, stub), id_g)).start();
			}*/ 
			
			while(true){
				Thread.sleep(1000);			
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
	
	
}

