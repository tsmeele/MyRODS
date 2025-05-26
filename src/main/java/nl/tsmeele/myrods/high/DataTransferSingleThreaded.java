package nl.tsmeele.myrods.high;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import nl.tsmeele.myrods.plumbing.MyRodsException;

public class DataTransferSingleThreaded extends DataTransfer {


	public DataTransferSingleThreaded(PosixFile source, PosixFile dest) throws MyRodsException {
		super(source, dest);
	}
	
	/**
	 * Transfers data from source to destination. 
	 * If files are already open, starts at current position in source and destination.
	 * If closed, source file and destination files are opened at start position (destination is truncated!)
	 * If destination file does not exist, will create it as new file.
	 * @throws IOException 
	 */
	@Override
	public void transfer() throws IOException, MyRodsException {
		openFiles();
		threadsActuallyUsed = 1;
		byte[] bytes = source.read(chunkSize);
		while (bytes.length > 0) {
			dest.write(bytes);
			bytes = source.read(chunkSize);
		}
		closeFiles();
	}
	
	private void openFiles() 
			throws FileNotFoundException, FileAlreadyExistsException, IOException {
		if (!source.isOpenRead()) {
			source.openRead();
		}
		if (!dest.isFile()) {
			dest.openCreate();
		} 
		if (!dest.isOpenWrite()) {
			dest.openWriteTrunc();
		}
	}
	
	private void closeFiles() throws MyRodsException, IOException {
		source.close();
		dest.close();
		
	}




}
