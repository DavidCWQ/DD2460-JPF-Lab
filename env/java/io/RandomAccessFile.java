package env.java.io;

import java.io.*;

/*  class of RandomAccessFile; multiple instances access
   the <em>same</em> file. */
public class RandomAccessFile {
	final static int MAX_SIZE = 8;
	private boolean canWrite = false;
	private boolean isClosed = false;
	private static int size = 0;
	private int pos = 0;
	private static byte[] data = new byte[MAX_SIZE];

	public RandomAccessFile(File file, String mode) throws FileNotFoundException {
		// ignore file name to keep the model as simple as possible
		if (mode.indexOf('w') != -1) {
			canWrite = true;
		}
	}

	public void close() throws IOException {
		// Fix(#2): Insert an extra assertion to ensure the file is not closed twice.
    	assert (!isClosed) : "ERROR: File is already closed!";
		isClosed = true;
	}

	public int length() throws IOException {
		if (isClosed) throw new IOException ("File is closed");
		return size;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		if (isClosed) throw new IOException ("File is closed");
		if (pos == size) {
			return -1;
		}
		int read = 0;
		while (off < len && pos < size) {
			b[off++] = data[pos++];
			read++;
		}
		return read;
	}

	public void seek(long pos) throws IOException {
		if (isClosed) throw new IOException ("File is closed");
		if (pos < 0) throw new IOException("Position < 0");
		this.pos = (int)pos;
	}

	public void write(byte[]b, int off, int len) throws IOException {
		if (!canWrite) throw new IOException("Write called on read-only file");
		if (isClosed) throw new IOException ("File is closed");
		while (off < len && pos < MAX_SIZE) {
			data[pos++] = b[off++];
			if (pos >= size)
				size = pos;
		}
		if (pos == MAX_SIZE && off < len) {
			throw new IOException("Simulated disk full");
		}
	}

	// Fix(#3): a function oracle to check file contents
    public static void oracle(File f) throws IOException {

        RandomAccessFile file = new RandomAccessFile(f, "r"); // read-only mode
        // assert (file.length() == 3) : "ERROR: File length is not 3!";

        byte[] buffer = new byte[3];
        int bytesRead = file.read(buffer, 0, 3);

        // Assert all bytes are read
        assert (bytesRead == 3) : "ERROR: Unable to read 3 bytes from file!";

        // Assert the content matches expected 'a', 'b', 'c'
        assert (buffer[0] == 'a') : "ERROR: 1st byte is not 'a'!";
        assert (buffer[1] == 'b') : "ERROR: 2nd byte is not 'b'!";
        assert (buffer[2] == 'c') : "ERROR: 3rd byte is not 'c'!";

        file.close();
    }
}
