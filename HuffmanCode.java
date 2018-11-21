
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class HuffmanCode { // HuffmanCode 클래스

	public htree huffmanTree; // HuffmanTree
	public htree freqTable[]; // 알파벳과 빈도수가 저장되어있는 배열.
	public htree sortedFreqTable[]; // 빈도수를 바탕으로 htree 객체들을 정렬한 배열.

	public ArrayList<htree> bitCodeTable; // 비트코드들을 저장하는 ArrayList.
	public PriorityQueue<htree> priQueue; // htree를 저장할 우선순위 큐.

	public boolean isLeaf;
	public int freqIndex;
	public int lineNum;
	public int alphabetCount = 0;

	public HuffmanCode(String path) throws IOException { // HuffmanTree 생성자.
		readFile(path); // 파일 경로에 따른 파일을 읽음.
		addAllinQueue(); // htree들을 우선순위 큐에 삽입.
		htree huffmanTree = makeHuffmanTree(); // 우선순위 큐를 바탕으로 huffmanTree 완성.
		encodingReadedFile(path, huffmanTree); // huffmanTree를 바탕으로 Text파일 인코딩.
	}

	public void readFile(String path) { // 파일 경로에 따른 파일을 읽고, htree 객체들을 생성하는 메소드.

		freqTable = new htree[26];
		freqIndex = 0;

		System.out.println("입력 문자열 :");

		for (int i = 0; i < 26; i++)
			freqTable[i] = new htree(); // A~Z 순서대로 배열에 저장.
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = br.readLine();

			while (line != null) { // 다음 줄이 null일 때 까지 한줄 한줄 읽는다.
				System.out.println(line);
				++lineNum; // 다음 줄로 넘어간다.
				StringTokenizer st = new StringTokenizer(line, " ?!.,-:;\"");
				while (st.hasMoreTokens()) {
					String token = st.nextToken().toUpperCase(); // 토큰을 자르고 대문자로 변환.

					for (int i = 0; i < token.length(); i++) { // 하나의 토큰을 읽어서 한글자씩 반복.
						char temp = token.charAt(i);
						if (temp >= 'A' && temp <= 'Z' || temp >= 'a' && temp <= 'z') {
							// 읽어들인 토큰에서 A~Z 를 제외한 특수문자들은 제외한다.
							boolean found = false;

							for (int j = 0; j < 26; j++) // A~Z 인지 확인, 빈도수 카운팅.
								if (freqTable[j].alphabet == temp) {
									freqTable[j].freq++;
									found = true;
								}
							if (!found) {
								freqTable[freqIndex++] = new htree(token.charAt(i));
								alphabetCount++; // 더이상 빈도수가 추가되지 않는다면 저장된 빈도수를 바탕으로
								// htree 객체 생성하고, 몇 개의 알파벳이 등장했는지 카운트.
							}
						}
					}
				}
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			System.out.println(e);
		}

		for (int i = 0; i < freqTable.length; i++) { 
			// 배열을 freq를 바탕으로 내림차순으로 정렬
			for (int j = i + 1; j < freqTable.length; j++) {
				if (freqTable[i].freq > freqTable[j].freq) {
					htree tmep = freqTable[i];
					freqTable[i] = freqTable[j];
					freqTable[j] = tmep;
				}
			}
		}
		sortedFreqTable = new htree[alphabetCount]; 
		// 정렬된 freqTable에서 등장하지 않은 알파벳은 제외한
		// 최종 배열 생성.
		for (int i = (freqTable.length) - alphabetCount; i < freqTable.length; i++) {
			sortedFreqTable[alphabetCount - 1] = freqTable[i];
			alphabetCount--;
		}

		System.out.println("\n<<Sorted>>");
		for (int k = 0; k < sortedFreqTable.length; k++) {
			System.out.println(sortedFreqTable[k]);
		}
		// 내림차순 정렬 완료.
	}

	public void addAllinQueue() { // 알파벳, freq를 가지고 있는 모든 htree 객체들을 우선순위 큐에 삽입.
		priQueue = new PriorityQueue<htree>();
		for (int i = (sortedFreqTable.length) - 1; i >= 0; i--) {
			priQueue.add(sortedFreqTable[i]); // 큐의 바닥부터 빈도수가 제일 큰 htree부터 차곡차곡 들어간다.
		}

	}

	public htree makeHuffmanTree() { // 우선순위 큐를 바탕으로 HuffmanTree를 생성하는 메소드.
		System.out.println("\n<<Make HuffmanTree>>");
		while (priQueue.size() != 1) {
			htree tNode = new htree();
			/* 우선순위 큐에서 2개를 빼내고 각각 lc, rc에 저장 후 삭제. */
			tNode.lchild = priQueue.peek();
			priQueue.remove();
			tNode.rchild = priQueue.peek();
			priQueue.remove();

			/* lc, rc의 빈도수를 더해서 새로운 htree를 만들고 다시 큐에 저장. */
			tNode.freq = tNode.lchild.freq + tNode.rchild.freq;
			System.out.println("added! " + "(" + tNode.alphabet + "," + 
			tNode.freq + ")" + " Lc=" + "("
			+ tNode.lchild.alphabet + ","
			+ tNode.lchild.freq + ")" + " Rc="
			+ "(" + tNode.rchild.alphabet + ","
					+ tNode.rchild.freq + ")");
			priQueue.add(tNode);
		}
		huffmanTree = priQueue.peek();
		return huffmanTree;
	}

	public void encodingReadedFile(String path, htree huffmanTree)
			throws IOException {
		// 파일 경로와 위에서 만들어진 huffmanTree를 바탕으로 Text 파일 인코딩.

		StringBuffer encodedText = new StringBuffer();
		bitCodeTable = new ArrayList<htree>(); // 생성된 bitCode들을 저장할 ArrayList
		lineNum = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(path)); 
			// 파일 경로의 TextFile을 읽음.
			String line = in.readLine();
			while (line != null && lineNum < 4) { // 4번 째 줄까지 출력.
				++lineNum;
				StringTokenizer parser = new StringTokenizer(line, " ?!.,-:;\"");
				while (parser.hasMoreTokens()) {
					String word = parser.nextToken().toUpperCase(); // 토큰을 대문자로 변환.

					for (int i = 0; i < word.length(); i++) {
						char alpha = word.charAt(i);

						StringBuffer bitCode = new StringBuffer();
						isLeaf = false;
						bitCode = encode(huffmanTree, alpha, bitCode);
						// 현재 huffmanTree, 토큰, StringBuffer를 encode 하여 bitCode를 만듬.

						encodedText.append(bitCode);
						bitCodeTable.add(new htree(alpha, bitCode));
						// 생성된 bitCode를 ArrayList에 저장.

						for (int k = 0; k < sortedFreqTable.length; k++) {
							if (sortedFreqTable[k].alphabet == alpha) {
								sortedFreqTable[k].bitcode = bitCode;
							}
						} // sortedFreqTable에 저장되어있는 htree 객체들에 bitCode 추가.

					}
				}
				line = in.readLine();
				encodedText.append("\n");
			}
			in.close();
		} catch (IOException e) {
			System.out.println(e);
		}

		/* 각 알파벳들의 BitCode 값 출력. */
		System.out.println("");
		for (int k = 0; k < sortedFreqTable.length; k++) {
			System.out.println(sortedFreqTable[k].alphabet 
					+ " : " + sortedFreqTable[k].bitcode);
		}

		/* 인코딩이 완료된 텍스트를 출력. */
		System.out.println("\nEncoding : ");
		System.out.println(encodedText);
	}

	public StringBuffer encode(htree htree, char anAlphabet, StringBuffer bitCode) {
		// 파일의 각각의 토큰을 encode 하는 메소드.

		if (htree.alphabet == anAlphabet) {
			isLeaf = true;
			return bitCode; // leaf라면 bitCode 출력.
		}

		if (htree.lchild != null) { 
			// 서브트리 왼쪽에 객체가 존재한다면 스트링버퍼에 0을 찍고 다시 encode 하여 진행.
			bitCode.append("0");
			bitCode = encode(htree.lchild, anAlphabet, bitCode);
			if (isLeaf)
				return bitCode;
			else
				bitCode.deleteCharAt(bitCode.length() - 1);
		}

		if (htree.rchild != null) { 
			// 서브트리 오른쪽에 객체가 존재한다면 스트링버퍼에 1을 찍고 다시 encode 하여 진행.
			bitCode.append("1");
			bitCode = encode(htree.rchild, anAlphabet, bitCode);
			if (isLeaf)
				return bitCode;
			else
				bitCode.deleteCharAt(bitCode.length() - 1);
		}
		return bitCode;
	}

	public void decodingInputedBitCode(String bc) {
		// main에서 입력받은 bitCode를 decoding하는 메소드.

		StringBuffer decodedText = new StringBuffer();

		decode(huffmanTree, bc, decodedText);
		// buffmanTree를 바탕으로 사용자가 입력한 bitCode인 bc를 decode.

		System.out.println(bc + " Decoding : ");
		System.out.println(decodedText); // 완성된 decodedText 출력.
	}

	public void decode(htree htree, String bitCode, StringBuffer bitBuf) {
		// htree 객체와 bitcode를 바탕으로 decoding을 수행하는 메소드.
		htree root = htree;
		for (int i = 0; i <= bitCode.length(); i++) {
			if (htree.lchild == null && htree.rchild == null) {
				bitBuf.append(htree.alphabet);
				htree = root;
				i--;
				if (i == bitCode.length() - 1)
					break;
			} else if (bitCode.charAt(i) == '0') // 입력받은 bc가 0이라면 htree는 leftChild이다.
				htree = htree.lchild;
			else if (bitCode.charAt(i) == '1') // 입력받은 bc가 1이라면 htree는 rightChild이다.
				htree = htree.rchild;
		}
	}

	public class htree implements Comparable<htree> {

		htree lchild; // leftChild htree
		char alphabet; // 알파벳 정보.
		int freq; // 빈도수
		htree rchild; // rightChild htree
		StringBuffer bitcode;

		/* htree 생성자 정의. */
		public htree() {
			this.alphabet = ' ';
			this.freq = 0;
		}

		public htree(char anAlphabet) {
			this.alphabet = anAlphabet;
			this.freq = 1;
		}

		public htree(char anAlphabet, StringBuffer bitcode) {
			this.alphabet = anAlphabet;
			this.bitcode = bitcode;
		}

		public int compareTo(htree aTrecord) {
			// TODO Auto-generated method stub
			if (this.freq <= aTrecord.freq)
				return -1;
			else
				return 1;
		}

		/* htree 정보 출력 메소드. */
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("Alpha : " + alphabet + " / Freq : " + freq);
			return buf + "";
		}
	}
}
