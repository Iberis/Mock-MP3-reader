import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
	
	private static String path = "C:/Users/tobia/Desktop/";
	
	public static void main(String[] args) {
		// Generate, Save and Load first FakeMp3
		testFakeMp3("Alphabet", FakeMp3.AlphabetGenerator.generate());
		
		// Second FakeMp3
		testFakeMp3("Fibonacci", 
				(new FakeMp3("Fibonacci-Zahlen", "1,1,2,3,5,8,13,21,34,55")).generate());
	}
	
	private static void testFakeMp3(String fileName, byte[] bytes) {
		writeFile(path + fileName, bytes);
		System.out.println("File generated and saved.");
		
		byte[] arr = loadFile(path + fileName);
		System.out.println("FileStream loaded:");
		for (byte b : arr) {
			System.out.println(b);
		}
		
		FakeMp3 first = new FakeMp3(arr);
		System.out.println("ByteStream parsed:");
		System.out.println(first.metadata());
		System.out.println(first.data());
	}

	private static void writeFile(String path, byte[] byteArray) {
		try (FileOutputStream os = new FileOutputStream(path)) {
			os.write(byteArray);
			os.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private static byte[] loadFile(String path) {
		File file = new File(path);
		try (DataInputStream is = new DataInputStream(new FileInputStream(file))) {
			byte[] arr = new byte[(int) file.length()];
			is.readFully(arr);
			is.close();
			return arr;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	/*
	 * Zusammenfassung:
	 * 
	 * ey, geil! Saustark! Ist doch fast perfekt!
	 * 
	 * Auf den ersten (naja, schon auch zweiten, dritten usw.) Blick, sieht hier
	 * alles perfekt aus.
	 * Das einzige, was nicht passt, ist, dass du die Daten an der falschen Stelle 
	 * parsen willst.
	 * Beim Lesen ist das byte-Array data einfach ein Byte-Array...
	 * 
	 * Du müsstest dir theorethisch jetzt die Nutzdaten-Klassen Fibonacci, usw.
	 * bauen, die dann bei der Erzeugung von data, die Ints, Strings etc. in byte-Arrays
	 * wandeln und so die Nutzdaten als großes Byte-Array erzeugen.
	 * Beim Lesen dann umgekehrt: du bekommst ein großes Byte-Array und da stecken
	 * dann die Nutzdaten irgendwie drin.
	 * In der Nutzdatenklasse Fibonacci werden dann eben immer 4 Bytes zu einem Int 
	 * zusammengebaut und schon kannst du die Daten wieder lesen.
	 * 
	 * Aber hey! Absolut geil, dass du das hinbekommen hast!
	 * 
	 */
}
