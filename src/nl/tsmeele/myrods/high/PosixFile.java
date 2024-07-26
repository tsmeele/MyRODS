package nl.tsmeele.myrods.high;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import nl.tsmeele.myrods.plumbing.MyRodsException;

/**
 * Interface PosixFile provides an abstract data type to represent persistent datafiles that
 * are capable of executing Posix-compatible (random-access) operations.
 * @author Ton Smeele
 *
 */
public interface PosixFile {
	
	/**
	 * Returns the name of the file, usually a path prefixed by its type.
	 * @return	file name
	 */
	public String getName();
	
	
	/**
	 * Returns a new PosixFile object that has a copy of 'static' attributes of the current file,
	 * notably its name/path.
	 * Note that any file status information e.g. open, exists, is *not* copied. 
	 * @return	partially-cloned object
	 */
	public PosixFile cloneProperties();

	/**
	 * Creates and opens a datafile using attributes specified earlier in class specific methods.
	 * The file may not yet exist.
	 * @throws FileAlreadyExistsException 
	 * @throws IOException 
	 */	
	public void openCreate() throws FileAlreadyExistsException, IOException;
	
	// note that all below openXXX methods must reference an already existing file	
	public void openRead() throws FileNotFoundException, IOException;
	public void openWriteTrunc() throws FileNotFoundException, IOException;
	public void openWriteAppend() throws FileNotFoundException, IOException;
	public void openWrite() throws FileNotFoundException, IOException;
	
	
	/**
	 * Checks if path refers to an existing object of type file
	 * @return true if objects exists and is file type
	 */
	public boolean isFile();
	
	
	/**
	 * Checks if the file is opened for write purposes
	 * @return	true if file is opened for write
	 */
	public boolean isOpenWrite();
	
	
	/**
	 * Checks if the file is opened for read (only) purposes
	 * @return	true if file is opened for read
	 */
	public boolean isOpenRead();
	
	
	/**
	 * Retrieves file size in bytes.
	 * @return file size, null if unknown
	 */
	public Long getFileSize();
	
	
	/**
	 * Returns the maximum number of threads that is supported for file transfer.
	 * NB: It is recommended to query maxThreads() only *after* an initial thread has opened
	 *     the file. For instance, the Replica implementation may return an adjusted maxThreads() result
	 *     after an initial open Replica action has completed. 
	 * @return 	threads. A value of 0 means there is no limitation.
	 */
	public int maxThreads();
	
	
	/**
	 * Seeks the filepointer to the requested position.
	 * @param offset	offset from absolute position 0
	 * @throws IOException 
	 */
	public void lseek(long offset) throws IOException;
	
	
	/** Reads the requested number of bytes at current position.
	 *  returns 0 bytes if at EOF
	 * @param bytes	numbe rof bytes to read
	 * @return	bytes read
	 * @throws IOException 
	 */
	public byte[] read(int bytes) throws IOException;
	
	
	/** Writes the byte array at the current file position  
	 * @param bytes		data to be written
	 * @throws IOException
	 */
	public void write(byte[] bytes) throws IOException;
	
	
	/** Closes the file.
	 * Implementations may choose to throw an exception if the file status was not open.
	 * @throws MyRodsException
	 * @throws IOException
	 */
	public void close() throws MyRodsException, IOException;
	
	
}
