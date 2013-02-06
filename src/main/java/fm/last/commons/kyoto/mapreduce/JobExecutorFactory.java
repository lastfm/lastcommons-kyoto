package fm.last.commons.kyoto.mapreduce;

import fm.last.commons.kyoto.KyotoDb;

public interface JobExecutorFactory {

  JobExecutor newExecutor(KyotoDb database);

}
