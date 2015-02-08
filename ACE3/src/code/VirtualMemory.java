/**
* Version History
*
* v0.1: Initial Implementation
*
* Thought Process:
* Upon reviewing the provided classes, and documentation for RandomAccessFile
* felt somewhat confident in how to approach storing the information provided in 
* MakeBACKING_STORE.
* Use a for to create entries into the array, multiplying the pagenumber by 256 to get the next
* "block".
* 
* Implementation:
* VirtualMemory class and get method.
*/

package code;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class VirtualMemory {

	byte[][] data = new byte[256][256];
	
	public VirtualMemory() {
				
		File fileName;
		RandomAccessFile disk = null;
		
		byte[] page = null;
		// the file representing the simulated disk
		try {
			fileName = new File("BACKING_STORE");
			disk = new RandomAccessFile(fileName, "r");
			
			for (int pageNumber = 0; pageNumber < 256; ++pageNumber) {
				disk.seek(pageNumber * 256);
				page = data[pageNumber];
				disk.read(page);
			}
		} catch (IOException e) {
			System.err.println("I/O Error: Issue with reading from BACKING_STORE.");
			System.exit(1);
		} finally {
			try {
				disk.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Method: Get
	 * Arguments: int page_num
	 * 
	 * Return the byte array stored at the specific position in the array denoted
	 * by page number.
	 **/
	public byte[] get(int page_num) {
		return data[page_num];
	}
}
