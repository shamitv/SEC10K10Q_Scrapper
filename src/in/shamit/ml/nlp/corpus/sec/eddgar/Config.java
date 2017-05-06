package in.shamit.ml.nlp.corpus.sec.eddgar;

import java.io.File;

public class Config {
	public static String baseDirLoc="K:/nlp/sec/edgar/";
	public static String baseDirLocUnix="/var/tmp/sec";
	public static File baseDir=null;
	public static File indexDir=null;
	public static File dataDir=null;
	public static String baseUrl="https://www.sec.gov/Archives/";
	public static String indexBaseUrl=baseUrl+"edgar/full-index/";
	
	static{
		reconfigure(baseDirLoc);
	}
	public static void reconfigure(String filePath){
		baseDirLoc=filePath;
		baseDir=new File(baseDirLoc);
		if(!baseDir.exists()){
			if(new File("/var/tmp/").exists()){
				baseDir=new File(baseDirLocUnix);
			}else{
				throw new RuntimeException("Base directory (and alternate) does not exist, locations ::"+baseDirLoc+","+baseDirLocUnix);
			}
		}
		indexDir=new File(baseDirLoc,"index");
		dataDir=new File(baseDirLoc,"data");
	}
}
