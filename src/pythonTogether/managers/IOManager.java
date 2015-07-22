package pythonTogether.managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class IOManager {
	private Manager manager;
	final static Charset ENCODING = StandardCharsets.UTF_8;

	public IOManager(Manager manager){
		this.manager = manager;
	}
	public String readFile(String filename){
		Path path = Paths.get(filename);
		try {
			List<String> lines = Files.readAllLines(path, ENCODING);
			String doccumentToString = "";
			for(int i = 0; i < lines.size() ;i++){
				doccumentToString += lines.get(i) + "\n";
			}
			return doccumentToString;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public void saveFile(String filename, String content){
		
		final Path path = Paths.get(filename);
		try {
			final BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
			writer.write(content);
			writer.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
