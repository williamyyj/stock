package org.cc.allow;

import org.apache.arrow.memory.ArrowBuf;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;

public class AllowTest {

	public static void main(String[] args) {
		try(BufferAllocator bufferAllocator = new RootAllocator(8 * 1024)){
		    ArrowBuf arrowBuf = bufferAllocator.buffer(4 * 1024);
		    System.out.println(arrowBuf);
		    arrowBuf.close();
		}
	}
	
}
