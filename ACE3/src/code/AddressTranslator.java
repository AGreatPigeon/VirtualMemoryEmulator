package code;

import java.io.File;
import java.util.Scanner;

public class AddressTranslator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFile = "InputFile.txt";
        final int MASK8 = 0xFF;
        final int MASK16 = 0xFFFF;
        
		try{
            Scanner sc = new Scanner(new File(inputFile));
            
            VirtualMemory bs = new VirtualMemory();
            PhysicalMemory pmem = new PhysicalMemory(bs, 128);
            PageTable pt = new PageTable(pmem);
            TLB<Integer, Integer> tlb = new TLB<Integer, Integer>(pt, 16);
            
            while(sc.hasNextInt()){
                int addr = sc.nextInt();
                int offset = addr & MASK8;
                int page_num = (addr & MASK16) >>> 8;
                
                int f_num = tlb.get(page_num);
                int phy_addr = f_num * 256 + offset;
                int value = pmem.getMemoryValue(f_num, offset);
                
                System.out.println("Virtual Address: " + addr + " Physical Address: " + phy_addr + " Value: " + value);
            }
            sc.close();
            System.out.println("Miss Rate <TLB>: " + tlb.getTLBMissRate() + 
            					"%. Miss rate <Pages>: " + pt.getPageMisses() + "%.");
		} catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
		
	}

}
