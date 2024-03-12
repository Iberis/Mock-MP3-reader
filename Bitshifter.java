// https://javadeveloperzone.com/java-basic/java-convert-int-to-byte-array/
public class Bitshifter {
	
	public static byte[] intToByteArray(final int integer) {
		return new byte[] {
				(byte)((integer >> 24) & 0xff),
		        (byte)((integer >> 16) & 0xff),
		        (byte)((integer >> 8) & 0xff),
		        (byte)((integer >> 0) & 0xff) 
			};
		}
		
	public static int byteArrayToInt(final byte[] byteArray) {
		// First the parameter is mapped to 4 bytes for a full Integer.
		byte[] bytes = new byte[4];
		
		/*
		 * for (int i = bytes.length - 1, counter = byteArray.length - 1;
		 * 		i >= 0;
		 * 		i--, counter--)
		 * hätte ich klarer gefunden....
		 * musste mir mühsam zusammenfriemeln, dass die Logik stimmt ;-)
		 */
		int counter = byteArray.length - 1;
		for (int i = bytes.length - 1; i >= 0; i--) {
			if (counter >= 0) {
				bytes[i] = byteArray[counter];
			} else {
				bytes[i] = (byte) 0b00000000;
			}
			counter--;
		}

		// Conversion logic
		return (int) (
			(0xff & bytes[0]) << 24  |
	        (0xff & bytes[1]) << 16  |
	        (0xff & bytes[2]) << 8   |
	        (0xff & bytes[3]) << 0
        );
	}
}
