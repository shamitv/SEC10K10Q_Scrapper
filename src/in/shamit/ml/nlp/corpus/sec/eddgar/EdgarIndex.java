package in.shamit.ml.nlp.corpus.sec.eddgar;

import java.io.File;
import java.util.logging.Logger;

import in.shamit.ml.nlp.corpus.sec.eddgar.data.EdgarFileData;
import in.shamit.ml.nlp.corpus.sec.eddgar.fs.DirManager;
import in.shamit.ml.nlp.corpus.sec.eddgar.net.Downloader;
import in.shamit.ml.nlp.corpus.sec.eddgar.time.TimePeriod;

public class EdgarIndex {

	static {
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }
	
	Logger log = Logger.getLogger("EdgarIndex");
	String indexUrl(TimePeriod p){
		String uri=p.getYear()+"/QTR"+p.getQuarter()+"/master.zip";
		String url= Config.indexBaseUrl+uri;
		return url;
	}
	EdgarFileData data = new EdgarFileData();
	
	void downLoadIndex(int start_year, int end_year){
		DirManager dirMgr = new DirManager();
		Downloader downloader=new Downloader();
		for(int year=start_year;year <= end_year;year++){
			for(int quarter=1;quarter<=4;quarter++){
				TimePeriod p = new TimePeriod(year, quarter);
				String url = indexUrl(p);
				File f = dirMgr.getIndexFile(p, true);
				String message = url+"==>"+f.getAbsolutePath();
				if(f.exists()){
					log.info("SKIP :: "+message);
				}else{
					log.info(message);
					downloader.download(url, f);
				}
			}
		}
	}
	
	void loadIndex(){
		data.load();
	}
	
	public static void main (String args[]){
		EdgarIndex idx=new EdgarIndex();
		idx.downLoadIndex(1993, 2016);
		idx.loadIndex();
	}
}
