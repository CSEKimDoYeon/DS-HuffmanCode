
import java.io.IOException;
import java.util.Scanner;

public class TestHuffmanCode {

	public static void main(String[] args) throws IOException {

		HuffmanCode huffmanCode = new HuffmanCode
				("c:\\users\\KimDoYeon\\Desktop\\Caesar.txt");
		System.out.println("비트 스트림을 입력하세요.");

		String bc;
		Scanner scan = new Scanner(System.in);
		bc = scan.nextLine();
		huffmanCode.decodingInputedBitCode(bc);
	}
}