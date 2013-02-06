package fm.last.commons.kyoto.mapreduce;

import java.io.File;

public interface Job {

  Mapper getMapper();

  Reducer getReducer();

  File getTemporaryDbFolder();

  void setTemporaryDbFolder(File folder);

  void setUseLocks(boolean useLocks);

  boolean getUseLocks();

  void setCompressTemporaryDb(boolean compressTemporaryDb);

  boolean getCompressTemporaryDb();

}
