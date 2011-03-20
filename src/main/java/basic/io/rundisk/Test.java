package basic.io.rundisk;

import java.util.List;

public class Test {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RunDisk.index();
		List<String> list = RunDisk.search("scala", "DIR");
		for (String str : list) {
			System.out.println("hash is :" + str.hashCode() + " [==] " + str);
		}
	}
}
