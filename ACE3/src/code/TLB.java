/**
* Version History
*
* v0.1: Initial Implementation
*
* Thought Process:
* Upon reviewing the Java SDK for a collection to be used as a LRU cache for the TLB, 
* I decided to plump for LinkedHashMap.
* Reasoning was 2-fold. Chief reason was performance, as the majority of the operations 
* will be get and insert which in LinkedHashMap are O(1).
* Secondly, LinkedHashMap can store the information in an ordered fashion and provides
* the means to remove the eldest entry, the main requirement of a LRU cache.
*
* Implementation:
* InsertPage, getMemoryValue and constructor created.
*
* v0.2: Concurrency
* Thought Process:
* For a little challenge, thought I would make this project threaded.
* As such, LinkedHashMap could not be used as it is non-concurrent. Chose to use ConcurrentHashMap
* to facilitate this need and thus not require to modify any of the underlying code. Used a Concurrent
* LinkedQueue to implement LRU by adding recently used pages to the bottom of the queue, causing
* least recently used entry to rise to the top of the queue.
*
* Implementation:
* Modified ramMap to use ConcurrentHashMap and created a new queue to be used as our
* LRU reference.
*/

package code;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TLB<K, V> {

	PageTable pt;
	
	private Queue<Integer> pagesStored;
	private Map<Integer, Integer> cacheMap;

	double tlb_miss;
	int capacity;
	
	public TLB (PageTable pt, final int cacheSize){
		this.pt = pt;
		tlb_miss = 0;
		capacity = cacheSize;
		
		pagesStored = new ConcurrentLinkedQueue<Integer>();
		cacheMap = new ConcurrentHashMap<Integer, Integer>(capacity);
	}
	
	/**
	 * Method: Put
	 * Arguments: (int pageNumber, int frameNumber)
	 * 
	 * Checks the cacheMap to see if we have the page, if so remove from the queue
	 * (In preperation to add it to the bottom of the queue.)
	 * 
	 * If the size of the queue is greater than that of the set capacity,
	 * remove LRU items until the queue is less than the capacity size.
	 * 
	 * Add the pagenumber to the queue, then add the pagenumber and framenumber
	 * to the hashmap.
	 **/
	private synchronized void put(int pageNumber, int frameNumber){
		if (cacheMap.containsKey(pageNumber)){
			pagesStored.remove(pageNumber);
		}
		
		while (pagesStored.size() >= capacity){
			int expiredKey = pagesStored.poll();
			cacheMap.remove(expiredKey);	
		}
		
		pagesStored.add(pageNumber);
		cacheMap.put(pageNumber, frameNumber);
	}
	
	/**
	 * Method: Get
	 * Arguments: (int pageNumber)
	 * 
	 * Checks the cacheMap to see if we have the page, if so, return the
	 * value contained in the respective key for the pageNumber in cacheMap.
	 * 
	 * If we do not, then we have a TLB Miss. Retrieve the frameNumber from the
	 * PageTable and store that frameNumber (Value) against the pageNumber (Key) 
	 * in the HashMap.
	 * Once complete, return the franeNumber
	 **/
	public synchronized int get(int pageNumber){
		if (cacheMap.containsKey(pageNumber)){
			return cacheMap.get(pageNumber);
		} else {
			tlb_miss++;
			int frameNumber = pt.getFrameNumber(pageNumber);
			put(pageNumber, frameNumber);
			return cacheMap.get(pageNumber);
		}
	}

	public double getTLBMissRate() {
		return (tlb_miss / 1000) * 100;
	}
}
