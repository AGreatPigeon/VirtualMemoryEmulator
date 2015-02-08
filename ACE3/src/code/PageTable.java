/**
* Version History
*
* v0.1: Initial Implementation
*
* Thought Process:
* Upon review of the Java SDK, I chose to implement the PageTable using an int array.
* Floated with the idea of a HashMap for performance, but felt it was a contrivance that wasn't
* needed. 
* To keep track of whether the page is valid, we use a boolean array to be set true when the
* page has been accessed.
*
* Implementation:
* getFrameNumber and constructor created.
*/

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

	/**
	 * Method: GetFrameNumber
	 * Arguments: (int page_num)
	 * 
	 * Checks to see if page_num is valid by checking its respective position in
	 * the valid array to see if it is true.
	 * If not, then set it to be true, increase the pageFault count and set the value
	 * of its respective position in the table array to the pageNumber from memory.
	 * 
	 * IF the page number is valid, return the value stored in its respective position
	 * in the table array.
	 **/	
	public synchronized int getFrameNumber(int page_num){	
		if (!valid[page_num]){
			valid[page_num] = true;
			pageFault++;
			return table[page_num] = mem.insertPage(page_num);
		} else {
			return table[page_num];
		}
	}
	
	public double getPageMisses(){
		return (pageFault / 1000) * 100;
	}
}
