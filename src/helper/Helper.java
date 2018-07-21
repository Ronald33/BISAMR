package helper;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Random;

public abstract class Helper
{
	public static int getRandom(int start, int end)
	{
		Random rand = new Random();
	    int randomNum = rand.nextInt((end - start) + 1) + start;
	    return randomNum;
	}
	public static String getTimestamp()
	{
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return Long.toString(timestamp.getTime());
	}
	public static String createFolder(String path) throws Exception
	{
		Path _path = Paths.get(path);
		if(!Files.exists(_path)) { Files.createDirectories(_path); }
		return path;
	}
	public static void writeFile(String path, String filename, String content) throws Exception
	{
		Helper.createFolder(path);
		String file = path + "/" + filename;
		if(existsFile(file)) { throw new Exception("The file " + file + " already exists"); }
		else
		{
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.close();
		}
	}
	public static void setExecutable(String file) throws Exception
	{
		if(existsFile(file))
		{
//			File f = new File(file);
//			f.setExecutable(true);
			
            HashSet<PosixFilePermission> set = new HashSet<PosixFilePermission>();
            
            //Adding owner's file permissions
            set.add(PosixFilePermission.OWNER_EXECUTE);
            set.add(PosixFilePermission.OWNER_READ);
            set.add(PosixFilePermission.OWNER_WRITE);
             
            //Adding group's file permissions
            set.add(PosixFilePermission.GROUP_EXECUTE);
            set.add(PosixFilePermission.GROUP_READ);
            set.add(PosixFilePermission.GROUP_WRITE);
             
            //Adding other's file permissions
            set.add(PosixFilePermission.OTHERS_EXECUTE);
            set.add(PosixFilePermission.OTHERS_READ);
            set.add(PosixFilePermission.OTHERS_WRITE);
             
            Files.setPosixFilePermissions(Paths.get(file), set);
		}
		else { throw new Exception("The file " + file + " does not exists"); }
	}
	public static void createLink(String path, String filename, String link_name) throws Exception
	{
		String file = path + "/" + filename;
		if(existsFile(file))
		{
			String file_link = path + "/" + link_name;
			deleteFile(file_link);
			Files.createSymbolicLink(Paths.get(file_link), Paths.get(filename));
		}
		else { throw new Exception("The file " + file + " does not exists"); }

	}
	public static boolean existsFile(String file)
	{
		File f = new File(file);
		return f.exists();
	}
	public static void deleteFile(String path)
	{
		File f = new File(path);
		f.delete();
	}
}
