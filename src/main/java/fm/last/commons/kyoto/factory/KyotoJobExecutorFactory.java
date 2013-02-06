package fm.last.commons.kyoto.factory;

import fm.last.commons.kyoto.KyotoDb;
import fm.last.commons.kyoto.mapreduce.JobExecutor;
import fm.last.commons.kyoto.mapreduce.JobExecutorFactory;

public class KyotoJobExecutorFactory implements JobExecutorFactory {

  @Override
  public JobExecutor newExecutor(KyotoDb database) {
    if (!(database instanceof KyotoDbImpl)) {
      throw new IllegalArgumentException(database + " must be an instance of " + KyotoDbImpl.class.getSimpleName());
    }
    return new JobExecutorImpl((KyotoDbImpl) database);
  }

}
