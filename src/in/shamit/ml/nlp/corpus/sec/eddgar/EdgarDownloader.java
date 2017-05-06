package in.shamit.ml.nlp.corpus.sec.eddgar;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import in.shamit.ml.nlp.corpus.sec.eddgar.data.EdgarFileData;
import in.shamit.ml.nlp.corpus.sec.eddgar.fs.DirManager;
import in.shamit.ml.nlp.corpus.sec.eddgar.net.Downloader;
import in.shamit.ml.nlp.corpus.sec.eddgar.time.TimePeriod;

public class EdgarDownloader {

	static {
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }
	
	static Logger log = Logger.getLogger("EdgarDownload");
	
	
	
	public static void main(String[] args) {
		Downloader d= new Downloader();
		EdgarIndex idx=new EdgarIndex();
		DirManager dirMgr = new DirManager();
		idx.downLoadIndex(1993, 2016);
		idx.loadIndex();
		EdgarFileData data = idx.data;
		Set<TimePeriod> quarters = data.getQuarters();
		for(TimePeriod q : quarters){
			List<String> lines=data.getStatements(q);
			for(String l:lines){
				File f=dirMgr.getFile(q, l);
				String url = getUrl(l);
				if(!f.exists()){
					log.info(url+"==>"+f.getAbsolutePath());
					d.download(url, f);
				}else{
					log.info("SKIP :: "+url+"==>"+f.getAbsolutePath());
				}
			}
		}
	}
	
	static String getUrl(String uri){
		String ret=Config.baseUrl+uri;
		return ret;
	}
	

}
