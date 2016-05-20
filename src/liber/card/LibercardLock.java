package liber.card;

import liber.exception.LibercardException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

class LibercardLock extends Thread {
	static private final String lockExtension = ".locked";
	static private final long TIME_LIMIT = 3000;
	private File lockFile;
	private boolean run;
	public LibercardLock(File libercardFile) throws Exception {
		lockFile = new File(libercardFile.getAbsolutePath() + lockExtension);
		if(lockFile.exists()) {
			if(!lockFile.isFile())
				throw new LibercardException("Le chemin vers l'observateur de libercarte existe mais ne mène pas à un fichier.");
			long previousTime = readLock();
			long currentTime = System.currentTimeMillis();
			long difference = currentTime - previousTime;
			if(difference < TIME_LIMIT)
				throw new LibercardException("La libercarte semble actuellement ouverte dans une autre instance de Libersaurus.\n" +
						"Veuillez fermer cette instance, l'autre instance, et/ou réessayer plus tard!");
		}
		run = true;
	}
	@Override
	public void run() {
		do {
			try {
				updateLock();
				Thread.sleep(TIME_LIMIT);
			} catch (Exception e) {
				run = false;
			}
		} while (run);
		if(lockFile.delete())
			System.err.println("(libercarte fermée)");
	}
	public void unlock() {
		run = false;
	}
	private void updateLock() throws Exception {
		try (FileOutputStream fileOutputStream = new FileOutputStream(lockFile)) {
			fileOutputStream.write(String.valueOf(System.currentTimeMillis()).getBytes());
		}
		System.err.println("(libercarte ouverte)");
	}
	private long readLock() throws Exception {
		long value;
		try (FileInputStream fileInputStream = new FileInputStream(lockFile)) {
			StringBuilder s = new StringBuilder();
			int read;
			while((read = fileInputStream.read()) != -1) {
				s.append((char)read);
			}
			value = Long.parseLong(s.toString());
		}
		return value;
	}
}
