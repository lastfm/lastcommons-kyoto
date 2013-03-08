#About
A better Java wrapper around the
[Kyoto Cabinet](http://fallabs.com/kyotocabinet/ "Kyoto Cabinet: a straightforwardimplementation of DBM")
library. It's great to be able to easily access kyoto-cabinet from Java, however the
[default Java bindings](http://fallabs.com/kyotocabinet/javadoc/ "kyotocabinet-java Javadoc")
are missing some features we've come to expect in a modern Java developement environment. lastcommons-kyoto addresses
this by wrapping the default bindings in an API that should be more familiar to most Java developers.

#Dependencies
This wrapper uses `kyotocabinet-java` version
[1.24](http://fallabs.com/kyotocabinet/javapkg/kyotocabinet-java-1.24.tar.gz "kyotocabinet-java packages")
which in turn requires `kyotocabinet` version [
1.2.65 or greater](http://fallabs.com/kyotocabinet/pkg/ "kyotocabinet packages").
At this time you may need to build both the native libraries yourself. The JNI bindings are in Maven Central.

#Start using
You can [download](https://github.com/lastfm/lastcommons-kyoto/downloads) a JAR file or obtain lastcommons-kyoto from
Maven Central using the following identifier:

* [fm.last.commons:lastcommons-kyoto:1.24.0](http://search.maven.org/#artifactdetails%7Cfm.last.commons%7Clastcommons-kyoto%7C1.24.0%7Cjar)
                                            
#Features
* Cleaner, more Java-like API.
* Error conditions represented with exceptions instead of magic return values.
* Implicit file suffix handling for the different Kyoto database types.
* Descriptive builder pattern for creating and validating Kyoto database configurations. 

#Usage
**Note:** We have taken the liberty of statically importing some enum constants for readability.
####Create a new file hash database:
          File dbFile = FILE_HASH.createFile("my-new-db");
          KyotoDb db = new KyotoDbBuilder(dbFile)
                         .modes(READ_WRITE)
                         .buckets(42000)
                         .memoryMapSize(2, MEBIBYTES)
                         .compressor(LZO)
                         .build();
          db.open();
####Create a new cache tree database:
          KyotoDb db = new KyotoDbBuilder(CACHE_TREE).build();
####Open an existing file tree database:
          KyotoDb db = new KyotoDbBuilder("another-db.kct")
                         .modes(READ_ONLY)
                         .memoryMapSizeFromFile()
                         .build();
####Resources implement `java.io.Closeable`
With Java 7:

          try (KyotoCursor cursor = db.cursor()) {
            ...
          } catch (IOException e) {
            ...
          }
or with Apache Commons IO:

          IOUtils.closeQuietly(db); // from Apache Commons IO
####Work with exceptions - not error codes
          try {
            db.append(key, value); // returns void
          } catch (KyotoException e) {
            // You decide what happens next!
          }
####Return values represent outcomes, not errors
          boolean recordAlreadyExists = db.putIfAbsent("myKey", "myValue");
          long removed = db.remove(keys, ATOMIC);
          long records = db.recordCount() // Never Long.MIN_VALUE, never < 0
####Conversion from kyoto's 16 byte decimal representation
          db.set("doubleValue", 463.94738d);
          db.increment("doubleValue", 0.00123d);
          double value = db.getDouble("doubleValue"); // value == 463.94861000000003d
####Clearer transaction management
          try {
            db.begin(Synchronization.PHYSICAL);
            // Do stuff
            db.commit();
          } catch (KyotoException e) {
            db.rollback();
          }
####Validation of Kyoto database configuration
          File dbFile = FILE_HASH.newFile(parent, "an-existing-db");          
          KyotoDb db = new KyotoDbBuilder(dbFile)
            .modes(READ_ONLY)
            .pageComparator(LEXICAL)
            .build();

          // The call to pageComparator() will fail with an
          // IllegalArgumentException as file-hash does not
          // support the 'pcom' option.
####MapReduce wrapper
          // A classic word count across the values
          final SortedMap<String, Integer> wordCounts = new TreeMap(); 
          JobExecutor executor = KyotoJobExecutorFactory.INSTANCE.newExecutor(kyotoDb);
          executor.execute(new KyotoJob(new Mapper() {
            public void map(byte[] key, byte[] value, Collector collector) {
              String[] words = new String(value).split(" ");
              for (String word : words) {
                collector.collect(word.getBytes(), new byte[] { 1 });
              }
            }
          }, new Reducer() {
            public void reduce(byte[] key, Iterable<byte[]> values) {
              int count = 0;
              for (byte[] value : values) {
                count += value[0];
              }
              wordCounts.put(new String(key), count);
            }
          }));
#Building
This project uses the [Maven](http://maven.apache.org/) build system. See notes in the 'Dependencies' section on building the dependencies.

#Further work
* Implement a Spring [`PlatformTransactionManager`](http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/transaction/PlatformTransactionManager.html "Spring Framework Javadoc - PlatformTransactionManager") for simple integration with Spring's transaction management framework.

# Contributing
All contributions are welcome. Please use the [Last.fm codeformatting profile](https://github.com/lastfm/lastfm-oss-config/blob/master/src/main/resources/fm/last/last.fm.eclipse-codeformatter-profile.xml) found in the `lastfm-oss-config` project for formatting your changes.

#Legal
Copyright 2012 [Last.fm](http://www.last.fm/)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
