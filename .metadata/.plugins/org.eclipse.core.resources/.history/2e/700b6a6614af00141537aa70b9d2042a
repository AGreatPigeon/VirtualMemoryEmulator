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
	private VirtualMemory bs;
	
	private Queue<Integer> pagesStored;
	private Map<Integer, byte[]> ramMap;
	
	int curFreeFrame;
	final int capacity;

	public PhysicalMemory(VirtualMemory bs, final int frames) {
		
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
		
		this.bs = bs;
		curFreeFrame = 0;
	}

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
		ramMap.put(curFreeFrame, bs.get(frameNum));
		
		curFreeFrame++;
		return curFreeFrame - 1;
	}
	
	public synchronized int getMemoryValue(int frameNum, int offset) {
		byte[]value = ramMap.get(frameNum);
		return value[offset];
	}
}