package bn.blaszczyk.roseapp.tools;

import java.io.File;
import static bn.blaszczyk.rosecommon.tools.Preferences.*;

public class FileConverter {
	
	private final File baseDir;
	private final String baseDirName;
	
	public FileConverter()
	{
		baseDirName = getStringValue(BASE_DIRECTORY, "C:");
		baseDir = new File(baseDirName);
	}
	
	public boolean fileInBaseDir(File file)
	{
		return file.toPath().startsWith(baseDir.toPath());
	}
	
	public File fromPath(String path)
	{
		File file = new File(path);
		if(file.exists())
			return file;
		else
			return new File( baseDirName + path);
	}
	
	public String relativePath(File file)
	{
		String fullPath = file.getAbsolutePath();
		if(fileInBaseDir(file))
			return fullPath.substring(baseDirName.length());
		return fullPath;
	}
	
}
