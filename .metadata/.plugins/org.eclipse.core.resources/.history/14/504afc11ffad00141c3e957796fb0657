package code;

import java.util.LinkedHashMap;
import java.util.Map;

public class PhysicalMemory {
	private VirtualMemory bs;
	
	private final Map<Integer, byte[]> ramMap;

	int curFreeFrame, size;

	public PhysicalMemory(VirtualMemory bs, final int frames) {
		size = frames;
		
		this.ramMap = new LinkedHashMap<Integer, byte[]>(frames, 0.75F, false) {
			@Override
			protected boolean removeEldestEntry(
					Map.Entry<Integer, byte[]> eldest) {
				// Stipulate when to remove the eldest entry
				return size() > size;
			}
		};
		
		this.bs = bs;
		curFreeFrame = 0;
	}

	public int insertPage(int page_num) {
		ramMap.put(curFreeFrame, bs.get(page_num));
		curFreeFrame++;
		return curFreeFrame - 1;
	}
	
	public int getValue(int frame_num, int offset) {
		byte[] value = ramMap.get(frame_num);
		return value[offset];
	}
}