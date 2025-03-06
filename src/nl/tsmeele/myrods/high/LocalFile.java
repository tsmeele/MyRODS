package nl.tsmeele.myrods.high;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;

public class LocalFile implements PosixFile {
	private boolean isOpenWrite = false;
	private boolean isOpenRead = false;
	private String path = null;
	private RandomAccessFile file = null;
	
	protected LocalFile() {
	}
	
	protected void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}

	public String toString() {
		return getName();
	}
	
	@Override
	public String getName() {
		return "file:" + path;
	}
	
	@Override
	public void lseek(long offset) throws IOException {
		file.seek(offset);
	}

	@Override
	public byte[] read(int amountRequested) throws IOException {
		byte[] buf = new byte[amountRequested];
		int actuallyRead = file.read(buf, 0, amountRequested);
		if (actuallyRead == amountRequested) {
			return buf;
		}
		if (actuallyRead < 0) {
			return new byte[0];  // we have reached EOF
		}
		byte[] bytes = new byte[actuallyRead];
		for (int i = 0; i < actuallyRead; i++) {
			bytes[i] = buf[i];
		}
		return bytes;
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		file.write(bytes);
	}

	@Override
	public void close()  {
		try {
			file.close();
		} catch (IOException e) {
			// ignore close errors
		}
		isOpenRead = false;
		isOpenWrite = false;		
	}

	@Override
	public void openRead() throws FileNotFoundException {
		file = new RandomAccessFile(path, "r");
		isOpenRead = true;
	}

	@Override
	public void openCreate() throws  FileAlreadyExistsException, IOException {
		File f = new File(path);
		if(f.exists()) {
			throw new FileAlreadyExistsException(path);
		}
		f.createNewFile();
		openWrite();
	}

	@Override
	public void openWriteTrunc() throws FileNotFoundException, IOException {
		file = new RandomAccessFile(path, "rwd");
		isOpenWrite = true;	
		file.setLength(0L);
	}

	@Override
	public void openWriteAppend() throws FileNotFoundException, IOException {
		file = new RandomAccessFile(path, "rwd");
		isOpenWrite = true;	
		file.seek(file.length());
	}

	@Override
	public void openWrite() throws FileNotFoundException {
		file = new RandomAccessFile(path, "rwd");
		isOpenWrite = true;	
	}

	@Override
	public boolean isFile() {
		File f = new File(path);
		return f.isFile();
	}
	
	@Override
	public Long getFileSize() {
		File f = new File(path);
		if (!f.isFile()) {
			return null;
		}
		return f.length();
	}

	@Override
	public boolean isOpenWrite() {
		return isOpenWrite;
	}

	@Override
	public boolean isOpenRead() {
		return isOpenRead;
	}

	@Override
	public int maxThreads() {
		return 0;	// 0 means no limitation
	}

	@Override
	public PosixFile cloneProperties() {
		LocalFile clone = new LocalFile();
		clone.setPath(path);
		return clone;
	}





}
