import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;


public class OpSys {
	//stores select pages in main memory
	//main memory represented as a 2D array of Pages and counter
	//usedPages array for keeping track of which page index is accessed
	Page2[] mainMem;
	Disk disk;
	int[] usedPages;
	static int numPages;
	static int numBytes;
	Page2 leastRecentlyUsed;
	int LRUIndex;
	int counter = 0;	//for the FIFO Algorithm, stores num pgs in mainMem
	int pageFaults;
	int pageIndex;
	static int setAlgorithm = 0;
	
	public OpSys( String filename ) {
		//reads in the data from the file being tested
		String line = null;
		try {
			BufferedReader br = new BufferedReader( new FileReader(filename) );
			String firstline = br.readLine();
			if ((line = firstline) != null) {
				String[] info = firstline.split(" ");
				numPages = Integer.parseInt(info[0]);
				numBytes = Integer.parseInt(info[1]);
				int mainMemSize = numPages / 4;		
				disk = new Disk(numPages);
				mainMem = new Page2[mainMemSize ];
				usedPages = new int[mainMemSize];
				for (int i = 0; i < numPages; i++) {
					String currline = br.readLine();
					Page2 currPg = new Page2();
					currPg.pgData = currline.getBytes();
					disk.disk[i] = currPg;	
				}
				//THIS IS FOR COMPLETELY RANDOM FILL OF MAIN MEMORY
				Random random = new Random();
				for (int i = 0; i < mainMemSize; i++) {
					int nextPage = random.nextInt(numPages);
					mainMem[i] = disk.disk[nextPage];
				}
				//THIS IS FOR RANDOM + SEQUENTIAL FILL OF MAIN MEMORY
//				Random random1 = new Random();
//				int start = random1.nextInt(numPages);
//				int j = 0;
//				for (int i = start; i < (start + mainMemSize); i++ ) {
//					int n;
//					n = i % numPages;
//					mainMem[j] = disk.disk[n];
//					j++;
//				}
				setUsedPages();
			}
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public Page2 getLeastRecentlyUsed() {
		//loops through usedPages for lowest number
		//returns Page in mainMem with that number
		int lowest = usedPages[0];
		leastRecentlyUsed = mainMem[0];
		for (int i = 0; i < mainMem.length; i++) {
			if (usedPages[i++] < lowest) {
				lowest = usedPages[i++];
				leastRecentlyUsed = mainMem[i++];
				LRUIndex = i++;
			}
		}
		return leastRecentlyUsed;
	}
	
	public byte getData( int pg, int offset) {
		//returns the char stored in the Page at offset 
		byte data = 0;
		try {
			
			if (isPageInMainMem(pg)) {
				data = mainMem[pageIndex].getData(offset);
				usedPages[pageIndex] = usedPages[pageIndex] + 1;
			}
		
			else {
				pageFaults++;	//page not found in main memory
				Page2 replace = disk.disk[pg];	//goes into disk to retrieve pg
				if (setAlgorithm == 0) {
					data = runFIFO(replace, offset);
				}
				else {
					data = runLRU(replace, offset);
				}
			}
		}
		catch (Exception e) {
			e.getMessage();
		}
		return data;	
	}
	
	public boolean isPageInMainMem( int pg ) {
		//checks if the page requested is already in mainMem array
		boolean value = false;
		for (int i = 0; i < mainMem.length; i++ ) {
			if ( mainMem[i] == disk.disk[pg] ) {
				pageIndex = i;
				value = true;
			}
		}
		return value;
	}

	public byte runLRU( Page2 newpg, int offset ) throws Exception {
		//replaces lru page in mainMem with new page
		Page2 toReplace = getLeastRecentlyUsed();
		mainMem[LRUIndex] = newpg;
		return mainMem[LRUIndex].getData(offset);	
	}
	
	public byte runFIFO( Page2 newpg, int offset ) throws Exception {
		//replaces pages in order that they were put in
		counter = counter % mainMem.length;		//gets correct index to replace
		mainMem[counter] = newpg;
		byte ch = mainMem[counter].getData(offset);
		usedPages[counter] = 1;
		counter++;
		return ch;
	}
	
	public void setUsedPages() {
		for (int i = 0; i <usedPages.length; i++) {
			usedPages[i] = 0;
		}
	}
	
	public void runRandomTest(String filename) throws IOException {
		//RANDOM PAGE TESTING
		//reads in the random page requests and executes them from files created
		String line = null;
		BufferedReader b = new BufferedReader (new FileReader(filename));
		String nextline = b.readLine();
		while ( (line = nextline) != null) {
			//System.out.println(nextline);
			run(nextline);
			nextline = b.readLine();
		}
		b.close();
		System.out.println("Number of Page Faults: " + pageFaults);
	}
	public void runSequentialTest(int numRequests) {
		//SEQUENTIAL PAGE REQUESTS TESTING
		//generates sequential page requests with random offsets
		Random rand1 = new Random();
		int n;
		for (int i = 0; i < numRequests; i++ ) {
			n = i % numPages;
			String command = "";
			command = command + n + " " + rand1.nextInt(numBytes);
			run(command);
		}
		System.out.println("Number of Page Faults: " + pageFaults);		
	}
	
	public void run(String command) {
		//reads line from file, and gathers info about the page and offset
		String[] info = command.split(" ");
		int page = Integer.parseInt(info[0]);
		int offset = Integer.parseInt(info[1]);
		getData(page, offset);
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("This program will be testing the number of page faults\nthat occur for Page Replacement algorithms, LRU and FIFO");
		System.out.println("\nCurrently testing FIFO on Random Page Requests (with 16 Page file Tester.txt):");
		//FIFO RANDOM REQUESTS - 16 PAGE FILE
		OpSys os1 = new OpSys("Tester.txt");
		OpSys os2 = new OpSys("Tester.txt");
		OpSys os3 = new OpSys("Tester.txt");
		OpSys os4 = new OpSys("Tester.txt");
		OpSys os5 = new OpSys("Tester.txt");
		OpSys os6 = new OpSys("Tester.txt");
		OpSys os7 = new OpSys("Tester.txt");
		OpSys os8 = new OpSys("Tester.txt");
		OpSys os9 = new OpSys("Tester.txt");
		OpSys os10 = new OpSys("Tester.txt");
		System.out.println("Number of Random Page Requests: 20");
		os1.runRandomTest("RandomInputs1.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 40");
		os2.runRandomTest("RandomInputs2.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 60");
		os3.runRandomTest("RandomInputs3.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 80");
		os4.runRandomTest("RandomInputs4.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 100");
		os5.runRandomTest("RandomInputs5.txt");
		//FIFO SEQUENTIAL REQUESTS - 16 PAGE FILE
		System.out.println("\n");
		System.out.println("\nCurrently testing FIFO on Sequential Page Requests (with 16 Page file Tester.txt):");
		System.out.println("Number of Sequential Page Requests: 20");
		os6.runSequentialTest(20);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 40");
		os7.runSequentialTest(40);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 60");
		os8.runSequentialTest(60);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 80");
		os9.runSequentialTest(80);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 100");
		os10.runSequentialTest(100);
		//NOW GOING TO RUN LRU ALGORITHM
		System.out.println("\nCurrently testing LRU on Random Page Request (with 16 page file Tester.txt):");
		setAlgorithm = 1;
		OpSys os11 = new OpSys("Tester.txt");
		OpSys os12 = new OpSys("Tester.txt");
		OpSys os13 = new OpSys("Tester.txt");
		OpSys os14 = new OpSys("Tester.txt");
		OpSys os15 = new OpSys("Tester.txt");
		OpSys os16 = new OpSys("Tester.txt");
		OpSys os17 = new OpSys("Tester.txt");
		OpSys os18 = new OpSys("Tester.txt");
		OpSys os19 = new OpSys("Tester.txt");
		OpSys os20 = new OpSys("Tester.txt");
		System.out.println("Number of Random Page Requests: 20");
		os11.runRandomTest("RandomInputs1.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 40");
		os12.runRandomTest("RandomInputs2.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 60");
		os13.runRandomTest("RandomInputs3.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 80");
		os14.runRandomTest("RandomInputs4.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 100");
		os15.runRandomTest("RandomInputs5.txt");
		//LRU SEQUENTIAL REQUESTS - 16 PAGE FILE
		System.out.println("\n");
		System.out.println("\nCurrently testing LRU on Sequential Page Requests (with 16 Page file Tester.txt):");
		System.out.println("Number of Sequential Page Requests: 20");
		os16.runSequentialTest(20);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 40");
		os17.runSequentialTest(40);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 60");
		os18.runSequentialTest(60);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 80");
		os19.runSequentialTest(80);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 100");
		os20.runSequentialTest(100);
		
		//NOW GOING TO TEST FIFO ON 32 PAGE FILE TESTER2.TXT
		System.out.println("\nCurrently testing FIFO on Random Page Requests (with 32 Page file Tester2.txt):");
		//FIFO RANDOM REQUESTS - 32 PAGE FILE
		OpSys os21 = new OpSys("Tester2.txt");
		OpSys os22 = new OpSys("Tester2.txt");
		OpSys os23 = new OpSys("Tester2.txt");
		OpSys os24 = new OpSys("Tester2.txt");
		OpSys os25 = new OpSys("Tester2.txt");
		OpSys os26 = new OpSys("Tester2.txt");
		OpSys os27 = new OpSys("Tester2.txt");
		OpSys os28 = new OpSys("Tester2.txt");
		OpSys os29 = new OpSys("Tester2.txt");
		OpSys os30 = new OpSys("Tester2.txt");
		System.out.println("Number of Random Page Requests: 20");
		os21.runRandomTest("Tester2RANDIN1.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 40");
		os22.runRandomTest("Tester2RANDIN2.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 60");
		os23.runRandomTest("Tester2RANDIN3.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 80");
		os24.runRandomTest("Tester2RANDIN4.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 100");
		os25.runRandomTest("Tester2RANDIN5.txt");
		//FIFO SEQUENTIAL REQUESTS - 32 PAGE FILE
		System.out.println("\n");
		System.out.println("\nCurrently testing FIFO on Sequential Page Requests (with 32 Page file Tester2.txt):");
		System.out.println("Number of Sequential Page Requests: 20");
		os26.runSequentialTest(20);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 40");
		os27.runSequentialTest(40);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 60");
		os28.runSequentialTest(60);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 80");
		os29.runSequentialTest(80);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 100");
		os30.runSequentialTest(100);
		System.out.println("\nCurrently testing LRU on Random Page Requests (with 32 Page file Tester2.txt):");
		//LRU RANDOM REQUESTS - 32 PAGE FILE
		OpSys os31 = new OpSys("Tester2.txt");
		OpSys os32 = new OpSys("Tester2.txt");
		OpSys os33 = new OpSys("Tester2.txt");
		OpSys os34 = new OpSys("Tester2.txt");
		OpSys os35 = new OpSys("Tester2.txt");
		OpSys os36 = new OpSys("Tester2.txt");
		OpSys os37 = new OpSys("Tester2.txt");
		OpSys os38 = new OpSys("Tester2.txt");
		OpSys os39 = new OpSys("Tester2.txt");
		OpSys os40 = new OpSys("Tester2.txt");
		System.out.println("Number of Random Page Requests: 20");
		os31.runRandomTest("Tester2RANDIN1.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 40");
		os32.runRandomTest("Tester2RANDIN2.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 60");
		os33.runRandomTest("Tester2RANDIN3.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 80");
		os34.runRandomTest("Tester2RANDIN4.txt");
		System.out.println("\n");
		System.out.println("Number of Random Page Requests: 100");
		os35.runRandomTest("Tester2RANDIN5.txt");
		//LRU SEQUENTIAL REQUESTS - 32 PAGE FILE
		System.out.println("\n");
		System.out.println("\nCurrently testing LRU on Sequential Page Requests (with 32 Page file Tester2.txt):");
		System.out.println("Number of Sequential Page Requests: 20");
		os36.runSequentialTest(20);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 40");
		os37.runSequentialTest(40);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 60");
		os38.runSequentialTest(60);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 80");
		os39.runSequentialTest(80);
		System.out.println("\n");
		System.out.println("Number of Sequential Page Requests: 100");
		os40.runSequentialTest(100);
	
	}
	
}
