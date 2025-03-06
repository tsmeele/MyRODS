package main;

import nl.tsmeele.myrods.high.DataTransfer;
import nl.tsmeele.myrods.high.DataTransferMultiThreaded;
import nl.tsmeele.myrods.high.DataTransferSingleThreaded;
import nl.tsmeele.myrods.high.Hirods;
import nl.tsmeele.myrods.high.LocalFile;
import nl.tsmeele.myrods.high.PosixFile;
import nl.tsmeele.myrods.high.Replica;

/** Demonstrator class to show data transfers between local files and data objects.
 *  Precondition: user has logged in via the Session class  
 * @author Ton Smeele
 *
 */
public class GetOrPutFile {
	private Hirods session;
	private int threadCount = 1;
	private int threadsUsed = 0;

	
	public GetOrPutFile(Hirods session) {
		this.session = session;
	}
	
	public void setThreads(int threadCount) {
		this.threadCount = threadCount;
	}
	
	public int threadsUsed() {
		return threadsUsed;
	}
	
	public void get(String objectPath, String localPath) {
		execute(localPath, objectPath, false);
	}
	
	public void put(String localPath, String objectPath) {
		execute(localPath, objectPath, true);
	}
	
	private void execute(String localPath, String objectPath, boolean put) {
		LocalFile local = new LocalFile();
		local.setPath(localPath);
		Replica replica = new Replica();
		replica.setReplica(session, objectPath, null);
		PosixFile source, dest;
		if (put) {
			source = local;
			dest = replica;
		} else {
			source = replica;
			dest = local;
		}
		if (failsPrecondition(source, dest)) {
			return;
		}
		
		DataTransfer tx;
		try {
			if (threadCount != 1) {
				tx = new DataTransferMultiThreaded(source, dest);
				((DataTransferMultiThreaded)tx).setThreads(threadCount);
			} else {
				tx = new DataTransferSingleThreaded(source, dest);
			}
			tx.transfer();
			threadsUsed = tx.threadsLastTransfer();
			System.out.println("File transfer from " + source + " to " + dest + " was successful");
		} catch (Exception e) {
			System.out.println("Error during file transfer:\n" + e.getMessage());
		}
	}
	
	private boolean failsPrecondition(PosixFile source, PosixFile dest) {
		String sourceType = source.getClass().getSimpleName();
		String destType = dest.getClass().getSimpleName();
		if (source.isFile()) {
			System.out.println("Source (" + sourceType + ") exists.");
		}
		else {
			System.out.println("Source (" + sourceType + ") does not exist, unable to continue!");
			return true;
		}
		if (dest.isFile()) {
			System.out.println("Destination (" + destType + ") exists, will overwrite!");
		} else {
			System.out.println("Destination (" + destType + ") does not yet exist, will create!");
		}
		return false;
	}
	


}
