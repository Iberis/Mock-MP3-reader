import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FakeMp3 {

	private final static byte seperatorByte = (byte)0b11111111;

	/*
	 * Die Methode hätte ich auch eher in den Bitshifter gezogen
	 */
	private final static Charset charset = StandardCharsets.UTF_8;
	private static String byteArrayToString(final byte[] byteArray) {
		return new String(byteArray, charset);
	}

	/*
	 * die Sortierung finde ich gewöhnungsbedürftig.
	 * Ich würde alle Felder oben zusammenfassen und die get-Methoden unten ans 
	 * Ende hängen oder hinter den Konstruktor.
	 * 
	 * Alternative Sortierung:
	 * oben alles, was public ist, mit den Feldern beginnend, dann Konstruktor und
	 * die public-Methoden.
	 * gerne einen Kommentar-Strich ziehen und danach alles, was private ist,
	 * beginnend mit den Feldern und dann die Methoden.
	 * 
	 * Aber: nach Clean Code sollten oben die allgemeinsten (abstraktesten) Methoden
	 * stehen, somit auch alle Zugriffsmethoden und nach unten hin immer konkreter
	 * (detaillierter, mehr low-level) werden. Das passt zwar meist in die public-first
	 * Sortierung, aber eben nicht immer
	 */
	private String metadata;
	public String metadata() {
		return metadata;
	}

	private String data;
	public String data() {
		return data;
	}

	public FakeMp3(final String metadata, final String data) {
		this.metadata = metadata;
		this.data = data;
		/*
		 * du ignorierst hier den validate()-Rückgabewert.
		 * Das schreit schon fast nach einer Factory-Methode ODER
		 * weil es eh von innen kommt, ignorierst du das an dieser Stelle....
		 * weil du erwartest, dass validate() vom User aufgerufen wird
		 */
		validate();
	}

	/*
	 * auch hier.... Factory Methode statt Konstruktor, weil ja Rotz geladen werden kann
	 */
	public FakeMp3(final byte[] fileStreamArray) {
		int error = loadFileStream(fileStreamArray);
		if (error != 0) {
			System.err.println("Error parsing mp3 byte[]. Error Code: " + error);
			if (validate()) {
				System.err.println("Some data has been lost.");
			}
		}
	}

	private boolean validate() {
		boolean returnValue = true;
		if (metadata == null) {
			metadata = "ERROR";
			returnValue = false;
		}
		if (data == null) {
			data = "ERROR";
			returnValue = false;
		}
		return returnValue;
	}

	/**
	 * Parses the byte array according to specification
	 * @param fileStreamArray The complete byte[] representation of the file.
	 * @return 0 if successful.
	 * 1 if version not supported.
	 * 2 if length encoded parse error.
	 * -1 if seperator byte not found when expected.
	 * -2 if unresolved data at end of array.
	 */
	/*
	 * Ich würde hier eher eigene Exceptions bauen und die Exceptions
	 * werfen (mit throws Angabe), statt den C-Rotz mit den Rückgabewerten
	 * aufzubauen.
	 * Macht es zwar nicht wesentlich besser.... ist aber eher Java-Style
	 */
	private int loadFileStream(final byte[] fileStreamArray) {
		int position = 0;

		//region Version Check
		/*
		 * Vorsicht mit den ++-Operatoren. Erst greifst du auf das Array zu, dann
		 * inkrementierst du die Position. Lieber position++ danach in eigene Zeile 
		 * schreiben.
		 * Ok ok, wir machen hier eh C-Schweine-Kram, dann kann man das auch akzeptieren ;-)
		 */
		int version = Byte.toUnsignedInt(fileStreamArray[position++]);
		if (version != 1) {
			return 1;
		}
		//endregion

		//region Data Lengths
		int metadataLength = Bitshifter.byteArrayToInt(new byte[] {
				fileStreamArray[position++],
				fileStreamArray[position++]
		});

		int dataLength = Bitshifter.byteArrayToInt(new byte[] {
				fileStreamArray[position++],
				fileStreamArray[position++],
				fileStreamArray[position++],
				fileStreamArray[position++]
		});

		if ((metadataLength <= 0) || (dataLength <= 0)) {
			return 2;
		}
		//endregion

		//region Metadata
		// Copy metadata
		byte[] metadata = new byte[metadataLength];
		for (int i = 0; i < metadataLength; i++) {
			metadata[i] = fileStreamArray[position++];
		}

		// Conversion
		this.metadata = byteArrayToString(metadata);
		//endregion

		//region Data
		// Confirm seperator byte is present
		if (Byte.compare(seperatorByte, fileStreamArray[position++]) != 0) {
			return -1;
		}

		// Copy data
		byte[] data = new byte[dataLength];
		for (int i = 0; i < dataLength; i++) {
			data[i] = fileStreamArray[position++];
		}

		// Conversion
		this.data = byteArrayToString(data);
		/*
		 * Data Conversion ....
		 * bis hierher hast du das doch alles prima zusammengebastelt.
		 * Der Arbeitsauftrag besagt, dass hier einfach die Nutzdaten folgen. 
		 * D.h. dass an dieser Stelle die Daten gar nicht weiter verarbeitet werden.
		 * 
		 * Die Nutzdaten und die Metadaten können also faktisch in ein Feld geschrieben 
		 * werden, und diese kann man dann raus ziehen.
		 * Wenn jetzt also Fibonacci-Zahlen in dem Format drin stehen und diese 4 Byte
		 * beanspruchen, so ist dies ja nur eine Interpretation der Daten ->
		 * dies erfolgt dann quasi außerhalb, in dem die Daten wieder mit Hilfe der 
		 * Methode Bitshifter.byteArrayToInt in Ints gewandelt werden.
		 * Also alles bestens bis hierher!!!
		 */

		// Confirm seperator byte is present
		if (Byte.compare(seperatorByte, fileStreamArray[position++]) != 0) {
			return -1;
		}
		//endregion

		// Check for end of array
		if (position != fileStreamArray.length) {
			return -2;
		}

		return 0;
	}

	public byte[] generate() {
		return generate(metadata, data);
	}

	private static byte[] generate(final String metadata_str, final String data_str) {
		byte[] metadata = (byteArrayToString(metadata_str.getBytes()).getBytes());
		/* 
		 * data sollte hier theoretisch direkt als byte[] reinkommen
		 */
		byte[] data = (byteArrayToString(data_str.getBytes()).getBytes());
		byte[] metadataLength = Bitshifter.intToByteArray(metadata.length);
		byte[] dataLength = Bitshifter.intToByteArray(data.length);
		ArrayList<Byte> list = new ArrayList<>();

		list.add((byte)1);

		list.add(metadataLength[2]);
		list.add(metadataLength[3]);

		list.add(dataLength[0]);
		list.add(dataLength[1]);
		list.add(dataLength[2]);
		list.add(dataLength[3]);

		for (byte m : metadata) {
			list.add(m);
		}

		list.add(seperatorByte);
		for (byte d : data) {
			list.add(d);
		}
		list.add(seperatorByte);

		byte[] arr = new byte[list.size()];
		int counter = 0;
		for (byte b : list) {
			arr[counter] = b;
			counter++;
		}

		return arr;
	}

	static class AlphabetGenerator {
		private final static String metadata = "alphabet";
		private final static String data = "abcdefghijklmnopqrstuvwxyz";

		public static byte[] generate() {
			return FakeMp3.generate(metadata, data);
		}
	}

	static class FibonacciGenerator {
		private final static String metadata = "Fibonacci-Zahlen";
		/*
		 * Ah, ich sehe dein Problem....
		 * Du bräuchtest hier:
		 * String[] fibNumbers = new String[] {"1","1","2","3",...}
		 * oder noch besser:
		 * int[] fibNumbers = new int[] {1,1,2,3,5,8,13,21,34,55};
		 * und dann
		 * byte[] data = new byte[4 * fibNumbers.length];
		 * for (int i = 0; i < fibNumbers.length; i++) {
		 * 		data[4*i] = Bitshifter.intToByteArray(fibNumbers[i]);
		 * } 
		 */
		private final static String data = "1,1,2,3,5,8,13,21,34,55";

		public static byte[] generate() {
			return FakeMp3.generate(metadata, data);
		}
	}
}
