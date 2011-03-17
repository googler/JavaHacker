package basic.reg;

import junit.framework.TestCase;

public class RegexTest extends TestCase {
	public void regZhCNTest() {
		// assert ("中".matches("\u4e00-\u9fa5"));
		assert ("a".matches("[^\\x00-\\xff]"));
	}

	public void testDemoTest() {
		assert (1 == 2);
	}

	public static void main(String[] args) {
		int c = (int) '赏';
		System.out.println(c);
		System.out.println((char)167247);
		System.out.println(c <= 171941 && c >= 151040);

		for (int i = 151040; i <= 171941; i++) {
			System.out.print((char) i + " ");
			if ((char) i == '赏') {
				System.out.println(i);
				break;
			}
			if (i % 100 == 0)
				System.out.println();
		}

	}
}
