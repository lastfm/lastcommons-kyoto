package fm.last.commons.kyoto.factory;

import java.io.File;

import fm.last.commons.kyoto.mapreduce.Job;
import fm.last.commons.kyoto.mapreduce.Mapper;
import fm.last.commons.kyoto.mapreduce.Reducer;

public class KyotoJob implements Job {

  private final Mapper mapper;
  private final Reducer reducer;
  private File temporaryDbFolder;
  private boolean useLocks;
  private boolean compressTemporaryDb;

  public KyotoJob(Mapper mapper, Reducer reducer) {
    this.mapper = mapper;
    this.reducer = reducer;
  }

  @Override
  public Mapper getMapper() {
    return mapper;
  }

  @Override
  public Reducer getReducer() {
    return reducer;
  }

  @Override
  public File getTemporaryDbFolder() {
    return temporaryDbFolder;
  }

  @Override
  public void setTemporaryDbFolder(File temporaryDbFolder) {
    this.temporaryDbFolder = temporaryDbFolder;
  }

  @Override
  public void setUseLocks(boolean useLocks) {
    this.useLocks = useLocks;
  }

  @Override
  public boolean getUseLocks() {
    return useLocks;
  }

  @Override
  public void setCompressTemporaryDb(boolean compressTemporaryDb) {
    this.compressTemporaryDb = compressTemporaryDb;
  }

  @Override
  public boolean getCompressTemporaryDb() {
    return compressTemporaryDb;
  }

}
