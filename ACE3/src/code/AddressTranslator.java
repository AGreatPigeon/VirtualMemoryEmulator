/**
* Version History
*
* v0.1: Initial Implementation
*
* Thought Process:
* As this is an emulator of a VM manager, I took upon the decision to emulate how the CPU would
* interact with said system, i.e. the CPU will ask the TLB to get the frame number given the page
* number that has been calculated.
* 
* VM Hierarchy: TLB -> Page Table -> Physical Memory -> Virtual Memory
* 
* If the TLB does have the information, it will return the frame number.
* If not, the TLB (and not the CPU) will look to retrieve it from the Page Table.
* If the Page Table has the frame number, it will pass it back to the TLB and store it in the TLB.
* If the Page Table doesn't have the frame number, it will look to retrieve it from the Physical Memory.
* If the Physical Memory has the frame number, it will pass it to the Page Table which,
* once attained, will pass it to the TLB.
* If the Physical Memory doeso't have the frame number, then the VM must have it; thus the PM retrieves it
* When the memory attains the frame number from the VM, it recursively passes it back up the hierarchy.
* 
* As such, it keeps the main simple to follow and easier to implement due to this design decision.
* 
* Implementation:
* SingleThreaded method created.
*
* v0.2: Concurrency
* Thought Process:
* For a little challenge, thought I would make this project threaded.
* As such, I moved the kernel of the code from the while to its own Worker class.
* The bulk of the code remains the same due to FileInputSystem being thread safe from the outset.
* Both of the methods return the time taken to run the different implementations to demonstrate
* the differences in both implementations.
*
* Implementation:
* Used FileInputStream to read in the input file, a scanner to iterate down the file to 
* ensure that the output matches the expectant output.
*/

package code;

import java.io.FileInputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddressTranslator {

	public static void main(String[] args) {
		double serialResult = serialImplementation();
		double threadedResult = threadedImplementation();
		
		System.out.println("\nTime taken to execute Serial Implementation: " + serialResult + "ms.");
		System.out.println("Time taken to execute Threaded Implementation: " + threadedResult + "ms.");
	}
	
	public static double serialImplementation(){
		
		double startTime = 0, endTime = 0;
		
		String inputFile = "InputFile.txt";
        final int MASK8 = 0xFF;
        final int MASK16 = 0xFFFF;
        
        System.out.println("Start of Serial Implementation.");
		try{
			startTime = System.currentTimeMillis();
			FileInputStream inputStream = new FileInputStream(inputFile);
            Scanner sc = new Scanner(inputStream, "UTF-8");
            
            VirtualMemory bs = new VirtualMemory();
            PhysicalMemory pmem = new PhysicalMemory(bs, 128);
            PageTable pt = new PageTable(pmem);
            TLB<Integer, Integer> tlb = new TLB<Integer, Integer>(pt, 16);
            
            while(sc.hasNextInt()){
            	
            	int addr = sc.nextInt();
            			// To attain the offset, we mask using bitwise and.
				int offset = addr & MASK8;
				// To attain the Page Number, we need to bitwise 'and' mask the first 16
				// bits, then shift the offset off to attain the page_number
				// on its own.
				int page_num = (addr & MASK16) >>> 8;               
				int f_num = tlb.get(page_num);
				// To attain the physical address, we take the frame number, multiply it by 
				// 256 and finally add the offset to attain its "address" in memory.
				int phy_addr = f_num * 256 + offset;
				int value = pmem.getMemoryValue(f_num, offset);
                
				System.out.println("Virtual Address: " + addr + " Physical Address: " + phy_addr + " Value: " + value);
            }
            sc.close();
            System.out.println("Miss Rate <TLB>: " + tlb.getTLBMissRate() + 
            					"%. Miss rate <Pages>: " + pt.getPageMisses() + "%.\n\n");
            endTime = System.currentTimeMillis();
            
		} catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
		
		return (endTime - startTime);
	}
	
	public static double threadedImplementation(){
		
		ExecutorService service = Executors.newFixedThreadPool(256);
		String inputFile = "InputFile.txt";
        
		double startTime = 0, endTime = 0;
		
		System.out.println("Start of Threaded Implementation.");
		try{
			startTime = System.currentTimeMillis();
			
			FileInputStream inputStream = new FileInputStream(inputFile);
            Scanner sc = new Scanner(inputStream, "UTF-8");
            
            VirtualMemory bs = new VirtualMemory();
            PhysicalMemory pmem = new PhysicalMemory(bs, 128);
            PageTable pt = new PageTable(pmem);
            TLB<Integer, Integer> tlb = new TLB<Integer, Integer>(pt, 16);
            
            while(sc.hasNextInt()){
            	
            	int addr = sc.nextInt();
            	service.execute(new Worker(addr, tlb, pmem));
            }
            sc.close();
            System.out.println("Miss Rate <TLB>: " + tlb.getTLBMissRate() + 
            					"%. Miss rate <Pages>: " + pt.getPageMisses() + "%.");
            
            endTime = System.currentTimeMillis();
			service.shutdownNow();
		} catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
		
		return (endTime - startTime);
	}
	
	public static class Worker implements Runnable {

		int addr;
		final int MASK8 = 0xFF;
        final int MASK16 = 0xFFFF;
        
        TLB<Integer, Integer> tlb;
        PhysicalMemory pmem;
		
		public Worker(int nextValue, TLB<Integer, Integer> tlb, PhysicalMemory pmem) {
			synchronized(this){
			addr = nextValue;
			this.tlb = tlb;
			this.pmem = pmem;
			}
		}

		@Override
		public void run() {
			
			int offset = addr & MASK8;
            int page_num = (addr & MASK16) >>> 8;
            
            int f_num = tlb.get(page_num);
            int phy_addr = f_num * 256 + offset;
            int value = pmem.getMemoryValue(f_num, offset);
            
            System.out.println("Virtual Address: " + addr + " Physical Address: " + phy_addr + " Value: " + value);
			
		}
		
	}

}
