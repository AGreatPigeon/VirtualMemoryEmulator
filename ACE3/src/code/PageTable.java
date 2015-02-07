package code;

public class PageTable {
	
	private PhysicalMemory mem;
	private int[] table;
	private boolean[] valid;
	private double pageFault;
	
	public PageTable(PhysicalMemory mem){
		this.mem = mem;
		table = new int[256];
		valid = new boolean[256];
		
		pageFault = 0;
	}
	
	public synchronized int getFrameNumber(int page_num){	
		if (!valid[page_num]){
			valid[page_num] = true;
			pageFault++;
			return table[page_num] = mem.insertPage(page_num);
		} else {
			return table[page_num];
		}
	}

//	public int findIndex(int frameNum){
//		for (int i = 0; i < table.length; i++){
//			if (table[i] == frameNum){
//				System.out.println("Index: " + i);
//				return i;
//			}
//		}
//		return -1;
//	}
	
	public double getPageMisses(){
		return (pageFault / 1000) * 100;
	}
}