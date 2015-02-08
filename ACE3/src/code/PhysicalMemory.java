/**
 * Version History
 * 
 * v0.1: Initial Implementation
 * 
 * 		 Thought Process:
 * 		 Upon review of the Java SDK, I chose to implement the Physical Memory using a LinkedHashMap.
 * 		 In doing so, it provides that the information will be stored utilising LRU.
 * 		 Initially wrote, in pseudocode, how to implement a LRU using two queues but felt that would
 * 		 be incredibly inefficient so chose to review the SDK for an implementation using a hashmap.
 * 
 * 		 Implementation:
 * 		 InsertPage, getMemoryValue and constructor created.
 * 
 * v0.2: Bonus Marks
 * 		 Thought Process:
 * 		 Upon reducing the size to 128, there were a number of NullPointerExceptions irregardless
 * 		 over whether it was FIFO or LRU.
 * 		 Upon reviewing the code, it was determined that the error lay in how to attain the key
 * 		 from LinkedHashMap and the manner to which the keys were being stored 
 * 		 (i.e. iteratively using curFreeFrame).
 * 		 To correct this issue and to maintain performance, I chose to use a ConcurrentHashMap instead 
 * 		 of LinkedHashMap to allow for keys to be inspected easily and a ConcurrentQueue to keep track
 * 		 of the keys that have been removed and used (Thus continuing to use LRU).
 * 
 * 		 Implementation:
 * 		 Modified ramMap to use ConcurrentHashMap and created a new queue to be used as our
 * 		 LRU reference.
 */

package code;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PhysicalMemory {
	private VirtualMemory vm;
	
	private Queue<Integer> pagesStored;
	private Map<Integer, byte[]> ramMap;
	
	int curFreeFrame;
	final int capacity;

	public PhysicalMemory(VirtualMemory vm, final int frames) {
		
		capacity = frames;
		pagesStored = new ConcurrentLinkedQueue<Integer>();
		ramMap = new ConcurrentHashMap<Integer, byte[]>(capacity);
		
//		this.ramMap = new LinkedHashMap<Integer, byte[]>(capacity, 1f, true){
//
//			private static final long serialVersionUID = 8674981914305698482L;
//
//			@Override
//			protected boolean removeEldestEntry(Map.Entry<Integer, byte[]> eldest){
//				// Stipulate when to remove the eldest entry
//				return size() > capacity;
//			}
//		};
		
		this.vm = vm;
		curFreeFrame = 0;
	}

	/**
	* Method: insertPage
	* Arguments: (int frameNum)
	*
	* Checks the ramMap to see if we have the page, if so remove from the queue
	* (In preperation to add it to the bottom of the queue.)
	*
	* If the size of the queue is greater than that of the set capacity,
	* remove LRU items until the queue is less than the capacity size and decrement the
	* currentFreeFrame counter.
	*
	* Add the currentFreeFrame to the queue, then add the currentFreeFrame and the byte array
	* associated with the frameNumber from the VirtualMemory to the hashmap.
	* 
	* Return the currentFreeFrame.
	**/
	public synchronized int insertPage(int frameNum) {
		
		if (ramMap.containsKey(frameNum)){
			pagesStored.remove(frameNum);
		}
		
		while (pagesStored.size() >= capacity){
			int expiredKey = pagesStored.poll();
			ramMap.remove(expiredKey);
			curFreeFrame--;	
		}
		
		pagesStored.add(curFreeFrame);
		ramMap.put(curFreeFrame, vm.get(frameNum));
		
		curFreeFrame++;
		return curFreeFrame - 1;
	}
	
	/**
	* Method: getMemoryValue
	* Arguments: (int frameNum, int offset)
	* 
	* Retrieve byte array stored at frameNum's key position in ramMap.
	* If the byte array is null, throw NullPointerException.
	* 
	* Return the values contained in the array using the offset.
	**/
	public synchronized int getMemoryValue(int frameNum, int offset) {
		byte[]value = ramMap.get(frameNum);
		if (value == null){
			throw new NullPointerException();
		}
		return value[offset];
	}
}
