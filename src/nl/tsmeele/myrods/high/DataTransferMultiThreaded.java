package nl.tsmeele.myrods.high;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import nl.tsmeele.log.Log;
import nl.tsmeele.myrods.plumbing.MyRodsException;

public class DataTransferMultiThreaded extends DataTransfer {
	private int threads;
	
	public DataTransferMultiThreaded(PosixFile source, PosixFile dest) {
		super(source, dest);
		threads = 0;
	}
	
	/**
	 * Negotiate the number of threads to use
	 * @param threads	number of threads requested (0 means no preference)
	 */
	public void setThreads(int threads) {
		// never use more threads than source and dest can handle
		int maxThreads = minimumOfDefined(source.maxThreads(), dest.maxThreads());
		// never use more threads than requested
		maxThreads = minimumOfDefined(maxThreads, threads);
		if (threads < 1) {
			// no specific threadcount was requested, we need to be creative
			threads = findOptimalThreadCount(source.getFileSize(), maxThreads);
		}
		this.threads = threads;
	}
	

	
	private int minimumOfDefined(int a, int b) {
		if (a <= 0) {
			return b;
		}
		if (b <= 0) {
			return a;
		}
		return Math.min(a, b);
	}
	
	private int findOptimalThreadCount(long fileSize, int maxThreads)  {
		// use the filesize to determine an optimal number of threads
		// each thread should transfer a significant set of bytes to minimize overhead
		// here we assume that a thread should process at least (2 * chunkSize) bytes
		long minimumToProcess = 2 * chunkSize;
		int threadCount = 1 + (int) (fileSize / minimumToProcess);
		return minimumOfDefined(threadCount, maxThreads);
	}
	
	
	/**
	 * Transfers data from source to destination, potentially using multiple threads.
	 * 
	 * The current thread acts as a coordinator/primary channel and will be first to open a replica,
	 * and the last to close the same replica, as that close action will finalize the replica.
	 * This first open action returns a replica-token that is presented upon subsequent replica open
	 * actions in secondary channels to indicate that secondary closure should not finalize the replica.
	 *	
	 * @throws IOException 
	 */
	@Override
	public void transfer() throws IOException, MyRodsException {
		openFilesPrimaryChannel();
		// recalculate threads as opening a replica may influence the maximum thread count
		setThreads(threads);
		
		// setup thread management 
		int subChannels = threads - 1;
		ExecutorService executor = Executors.newFixedThreadPool(Math.max(subChannels, 1));
		@SuppressWarnings("rawtypes")
		Future[] future = new Future[subChannels]; 
		threadsActuallyUsed = 1;
		
		
		long remainingBytesToTransfer = source.getFileSize();
		long partSize = remainingBytesToTransfer / threads;
		long offset = 0;
		
		for (int t = 0; t < subChannels; t++) {
			// establish offset and bytecount for this thread
			long bytesToTransfer = Math.min(remainingBytesToTransfer, partSize);
			if (bytesToTransfer <= 0) {
				// apparently all data has been transfered, no need for this and further threads
				// (even though the user has requested more threads to be used)
				subChannels = t;	// adjust so that we know less threads need to be monitored
				break;
			}
			// we need a fresh filehandle for our files
			// note that in the case of a replica, the replica token is passed on,
			// and hence will be presented at the replica open action
			PosixFile localSource = null;
			PosixFile localDest = null;
			try {
				localSource = allocateFileHandle(source);
				localDest = allocateFileHandle(dest);
			} catch (MyRodsException myRods) {
				// unable to allocate a session for a subchannel, will need to transfer remainder via the
				// primary channel instead
				releaseFileHandle(localSource);
				releaseFileHandle(localDest);
				subChannels = t;
				break;
			}
			threadsActuallyUsed++;
			// open source, dest in thread, start transfer
			Callable<Long> task = new TransferThread(t, localSource, offset, bytesToTransfer,
					localDest, offset);
			future[t] = executor.submit(task);
			// prepare for next channel
			offset = offset + bytesToTransfer;
			remainingBytesToTransfer -= bytesToTransfer;
		}
		// coordinator transfers the remaining bytes part
		long bytesDone = 0;
		try {
			bytesDone = seekAndTransferBytes(source, offset, dest, offset, remainingBytesToTransfer, "MAIN");
		} catch (IOException e) {
			Log.debug(e.getMessage());
		}

		// coordinator waits until all other treads have terminated (and closed their files)
		for (int t = 0; t < subChannels; t++) {
			@SuppressWarnings("unchecked")
			Future<Long> f = (Future<Long>)future[t];
			try {
				long b = f.get();	// will block until thread has finished
				bytesDone += b;		// register transferred count of bytes 
			} catch (ExecutionException e) {
				// thread has finished by throwing an exception
			} catch (InterruptedException e) {
				// thread has finished because it was interrupted???
			}
		}	
		executor.shutdown();
		// now close primary channel files, this will finalize any replicas involved
		closeFilesPrimaryChannel();
		if (bytesDone < source.getFileSize()) {
			throw new MyRodsException("Transfer incomplete, only " + bytesDone + " bytes were transfered");
		}
	}
	
	
	class TransferThread implements Callable<Long> {
		private int id;
		private PosixFile localSource;
		private long sourceOffset;
		private long byteCount;
		private PosixFile localDest;
		private long destOffset;
		
		public TransferThread(int id, PosixFile localSource, long sourceOffset, long byteCount, 
				PosixFile localDest, long destOffset) {
			this.id = id;
			this.localSource = localSource;
			this.sourceOffset = sourceOffset;
			this.byteCount = byteCount;
			this.localDest = localDest;
			this.destOffset = destOffset;
		}
		
		public Long call() throws IOException, MyRodsException {
			Log.debug("Thread " + id + " started, will tx " + byteCount + " at offset " + sourceOffset);
			System.out.println("Thread " + id + " started, will tx " + byteCount + " at offset " + sourceOffset);
			// open source and destination
			// as this is a secondary channel, both source and destination will already exist
			IOException exception = null;	// we will register any exceptions while processing, throw it finally
			long transferredBytes = 0;
			try {
				localSource.openRead();
				localDest.openWrite();
				// seek to our part of the file, and transfer relevant bytes
				transferredBytes = seekAndTransferBytes(localSource, sourceOffset, localDest, destOffset, byteCount, ("" + id));
			} catch (IOException e) {
				exception = e;
			}
			// make sure that resources are released, even in case of an exception
			try {
				localSource.close();
			} catch (IOException e) { 
				if (exception == null) exception = e;
			}
			try {
				localDest.close();
			} catch (IOException e) {
				if (exception == null) exception = e;
			}
			releaseFileHandle(localSource);
			releaseFileHandle(localDest);
			if (exception != null) {
				Log.debug("Thread " + id + " ending, throws exception: " + exception.getMessage());
				throw exception;
			} 
			Log.debug("Thread " + id + " ending");
			return (long) transferredBytes; 
		}
	}
	
	private long seekAndTransferBytes(PosixFile source, long sourceOffset, 
						PosixFile dest, long destOffset, long byteCount, String threadId) throws MyRodsException  {
		if (sourceOffset > 0) {
			try {
				source.lseek(sourceOffset);
			} catch (IOException e) {
				throw new MyRodsException(threadId + " IO error at source seek: " + e.getMessage());
			}
		}
		if (destOffset > 0) {
			try {
				dest.lseek(destOffset);
			} catch (IOException e) {
				throw new MyRodsException(threadId + " IO error at destination seek: " + e.getMessage());
			}
		}
		
		long bytesToTransfer = byteCount;
		int chunk = (int) Math.min(chunkSize, byteCount);
		byte[] bytes;
		try {
			bytes = source.read(chunk);
		} catch (IOException e) {
			throw new MyRodsException(threadId + " IO error while reading: " + e.getMessage());
		}
		while (bytes.length > 0) {
			try {
				dest.write(bytes);
			} catch (IOException e) {
				throw new MyRodsException(threadId + " IO error while writing: " + e.getMessage());
			}
			byteCount -= bytes.length;
			chunk = (int) Math.min(chunkSize,  byteCount);
			try {
				bytes = source.read(chunk);
			} catch (IOException e) {
				throw new MyRodsException(threadId + " IO error while reading: " + e.getMessage());
			}
		}
		return bytesToTransfer - byteCount;   // bytes actually transferred
	}
	
	
	private PosixFile allocateFileHandle(PosixFile file) throws MyRodsException, IOException {
		PosixFile local = file.cloneProperties();
		if (file.getClass() == Replica.class) {
			// allocate a new session for the localized replica
			IrodsPool pool = ((Replica)file).session.irodsPool;
			((Replica)local).session = pool.allocate();
		}
		return local;
	}
	
	private void releaseFileHandle(PosixFile file) {
		if (file == null) {
			return;
		}
		if (file.getClass() == Replica.class) {
			Replica r = (Replica) file;
			r.session.irodsPool.free(r.session);
		}
	}
	
	private void openFilesPrimaryChannel() 
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
	
	private void closeFilesPrimaryChannel() throws MyRodsException, IOException {
		source.close();
		dest.close();
	}



	
	
}
