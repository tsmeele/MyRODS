package nl.tsmeele.myrods.high;

import java.io.IOException;

import nl.tsmeele.myrods.plumbing.MyRodsException;

/** Abstract class to transfer data from one PosixFile to another.
 * 
 * @author Ton Smeele
 *
 */
public abstract class DataTransfer {
	protected int chunkSize = 32 * 1024 * 1024;	// default is 32 MB per message
	protected PosixFile source;
	protected PosixFile dest;
	protected Integer threadsActuallyUsed = null;

	
	public DataTransfer(PosixFile source, PosixFile dest) {
		if (source == null || dest == null) {
			throw new NullPointerException("Error: Missing source or destination file");
		}
		this.source = source;
		this.dest = dest;
	}
	
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	
	public int getChunkSize() {
		return chunkSize;
	}
	
	
	/**
	 * Transfers data from source to destination. 
	 * If closed, source file and destination files are opened at start position (destination is truncated!)
	 * If destination file does not exist, will create it as new file.
	 *
	 * If files are already open, starts at current position in source and destination.
	 * NB: Note that a start at an offset position is currently only supported 
	 *     by the DataTransferSingleThreaded subclass
	 * 
	 * @throws IOException 
	 */
	public abstract void transfer() throws IOException, MyRodsException;
	
	/**
	 * Returns the actual number of threads used during the last executed transfer
	 * @return	threads. A value of 0 means no transfer has occurred.
	 */
	public int threadsLastTransfer() {
		if (threadsActuallyUsed == null) {
			return 0;
		}
		return threadsActuallyUsed;
	}
	
}
