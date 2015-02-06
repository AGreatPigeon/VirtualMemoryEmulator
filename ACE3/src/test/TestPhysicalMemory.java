/**
 * 
 */
package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.VirtualMemory;
import code.PhysicalMemory;

/**
 * @author Paul Burns
 *
 */
public class TestPhysicalMemory {

	PhysicalMemory pmem;
	VirtualMemory bs;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
        bs = new VirtualMemory();
        pmem = new PhysicalMemory(bs, 256);
	}

	/**
	 * Test method for {@link code.PhysicalMemory#insertPage(int)}.
	 */
	@Test
	public void testInsertPage() {
		int page_num = 0;
		
		long timeStart = System.nanoTime();
		int page = pmem.insertPage(page_num);
		long timeEnd = System.nanoTime();
		
		assertEquals(0, page);
		
		System.out.println("Time taken to insert into Physical Memory: " + (timeEnd - timeStart) + " nanoseconds.");
	}

	/**
	 * Test method for {@link code.PhysicalMemory#getMemoryValue(int, int)}.
	 */
	@Test
	public void testGetValue() {
		int addr = 16916;
        int offset = addr & 0xFF;
        int page_num = (addr & 0xFFFF) >>> 8;
		
		long timeStart = System.nanoTime();
		int result = pmem.getMemoryValue(page_num, offset);
		long timeEnd = System.nanoTime();
		
		assertEquals(0, result);
		
		System.out.println("Time taken to get value from Physical Memory: " + (timeEnd - timeStart) + " nanoseconds.");
		
		addr = 12107;
		offset = addr & 0xFF;
        page_num = (addr & 0xFFFF) >>> 8;
		
		timeStart = System.nanoTime();
		result = pmem.getMemoryValue(page_num, offset);
		timeEnd = System.nanoTime();
		
		assertEquals(-46, result);
		
		System.out.println("Time taken to get value from Physical Memory: " + (timeEnd - timeStart) + " nanoseconds.");
		
	}

}
