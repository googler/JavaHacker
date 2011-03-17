package basic.io.rundisk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Strings;

public class RunDisk {

	private static List<String> dataList2Write = new ArrayList<String>();
	private static FileChannel writeChannel;
	private static FileChannel readChannel;

	/**
	 * 建立索引
	 * 
	 * @throws IOException
	 */
	public static void index() {
		try {
			File dataFileEn = new File(ConstantsAndTools.indexFile);// 存储英文文件
			if (dataFileEn.exists() && dataFileEn.delete())
				dataFileEn.createNewFile();
			writeChannel = new FileOutputStream(dataFileEn).getChannel();
			File[] roots = File.listRoots();
			for (File file : roots)
				index(file);
			// 清除残余在list中的文件记录
			writeRec();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writeChannel != null && writeChannel.isOpen())
					writeChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void index(File dir) throws IOException {
		if (dir.listFiles() != null) {
			for (File f : dir.listFiles()) {
				StringBuilder data_line = new StringBuilder();
				String file_name = f.getName();
				data_line.append(file_name);
				String file_path = f.getPath();
				if (f.isFile()) {
					int index_of_dot = file_name.lastIndexOf('.');
					String file_type = "";
					if (index_of_dot > 0)
						file_type = file_name.substring(index_of_dot + 1, file_name.length()).toLowerCase();
					else
						file_type = "Any";
					if (ConstantsAndTools.excludeFileType.contains(file_type)) {
						data_line.setLength(0);
						continue;
					}
					data_line.append(ConstantsAndTools.split).append(file_type)
							.append(ConstantsAndTools.split).append(file_path).append("\r\n");
					writeRec(data_line.toString(), f.getName().charAt(0));
				} else {
					data_line.append(ConstantsAndTools.split).append("DIR").append(ConstantsAndTools.split)
							.append(file_path).append("\r\n");
					writeRec(data_line.toString(), f.getName().charAt(0));
					index(f);
				}
				data_line.setLength(0);
			}
		}
	}

	private static void writeRec(final String _data, final char _fileNameC1) throws IOException {
		dataList2Write.add(_data);
		if (dataList2Write.size() > ConstantsAndTools.lines2write) {
			for (String data : dataList2Write)
				writeChannel.write(ByteBuffer.wrap(data.toString().getBytes()));
			dataList2Write.clear();
		}
	}

	private static void writeRec() throws IOException {
		if (dataList2Write.size() != 0)
			for (String data : dataList2Write)
				writeChannel.write(ByteBuffer.wrap(data.toString().getBytes()));
	}

	/**
	 * search
	 * 
	 * @param _key
	 * @param _fileType
	 * @return
	 */
	public static List<String> search(String _key, String _fileType) {
		_key = _key.toUpperCase();
		_fileType = _fileType == null ? "" : _fileType.toUpperCase();
		List<String> dataList4Read = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			readChannel = new FileInputStream(new File(ConstantsAndTools.indexFile)).getChannel();
			reader = new BufferedReader(Channels.newReader(readChannel, "UTF-8"));
			String line = "";
			while (true) {
				line = reader.readLine();
				if (line == null)
					break;
				line = line.toUpperCase();
				String[] arr = line.split(ConstantsAndTools.split);
				if (Strings.isNullOrEmpty(_fileType) && line.indexOf(_key) >= 0)
					dataList4Read.add(line);
				else if (!Strings.isNullOrEmpty(_fileType) && arr[1].equals(_fileType)
						&& line.indexOf(_key) >= 0)
					dataList4Read.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (readChannel != null && readChannel.isOpen())
					readChannel.close();
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dataList4Read;
	}

	/**
	 * search
	 * 
	 * @param _key
	 * @return
	 */
	public static List<String> search(String _key) {
		return search(_key, null);
	}
}
class ConstantsAndTools {
	public static int lines2write = 10000;// 每10,000条记录写一次文件
	public static String split = ":";
	public static String indexFile = "D:/MyData/diskIndex.dat";
	public static List<String> excludeFileType = new ArrayList<String>();
	// 不检索的文件类型
	static {
		String[] arrExclude = new String[] { "dll", "svn", "class", "obj", "c", "h", "cpp", "vim", "sys",
				"cab", "ttf", "acm", "ocx", "ax", "com", "nls" };
		excludeFileType = Arrays.asList(arrExclude);
	}
}