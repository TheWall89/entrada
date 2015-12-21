package nl.sidn.pcap;

import nl.sidn.pcap.util.FileUtil;
import nl.sidn.pcap.util.Settings;
import nl.sidn.stats.MetricManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


	public static void main(String[] args) {
		new Main().run(args);
	}
	
	public void run(String[] args){
		long start = System.currentTimeMillis();
		
		debug(args);
		if(args == null || args.length < 5){
			throw new RuntimeException("Incorrect number of parameters found.");
		}
		//path to config file
		Settings.setPath(args[1]);
		//name server name
		Settings.getInstance().setName(args[0]);
		//set paths to input dir
		Settings.getInstance().setSetting(Settings.INPUT_LOCATION, args[2]);
		//set paths to output dir
		Settings.getInstance().setSetting(Settings.OUTPUT_LOCATION, args[3]);
		//set state location
		Settings.getInstance().setSetting(Settings.STATE_LOCATION, args[4]);
		
		//do sanity check to see if files are present
		if (FileUtil.countFiles(Settings.getInstance().getSetting(Settings.INPUT_LOCATION) + System.getProperty("file.separator") + Settings.getInstance().getName()) == 0 ){
			LOGGER.info("No new PCAP files found, stop.");
			return;
		}
		//start en close the metric manager from this thread, otherwise closing
		//the connection to rabbitmq might cause hanging threads
		MetricManager mm = MetricManager.getInstance();
		Controller controller = null;
		try{
			controller = new Controller();
			controller.start();
		}catch(Exception e){
			LOGGER.error("Error while loading data:",e);
			mm.send(MetricManager.METRIC_IMPORT_RUN_ERROR_COUNT, 1);
			//return non-zero status will allow script calling this program
			//stop further processing and goto abort path.
			System.exit(-1);
		}
		finally{	
			//always send stats to monitoring
			long end = System.currentTimeMillis();
			int runtimeSecs = (int)(end-start)/1000;
			mm.send(MetricManager.METRIC_IMPORT_RUN_TIME, runtimeSecs);
			mm.send(MetricManager.METRIC_IMPORT_RUN_ERROR_COUNT, 0);
			mm.flush();
			controller.close();
		}
		
		LOGGER.info("Done loading data");
	}

	
	private void debug(String[] args){
		for (int i = 0; i < args.length; i++) {
			LOGGER.info("arg " + i + " = " + args[i]);
		}
	}

}