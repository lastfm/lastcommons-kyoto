#About
A better Java wrapper around the [Kyoto Cabinet](http://fallabs.com/kyotocabinet/ "Kyoto Cabinet: a straightforward implementation of DBM") library. It's great
to be able to easily access kyoto-cabinet from Java, however the [default Java bindings](http://fallabs.com/kyotocabinet/javadoc/ "kyotocabinet-java Javadoc") are missing some features we've come to expect in a modern Java developement environment. lastcommons-kyoto addresses this by wrapping the default bindings in an API that should be more familiar to most Java developers.

#Start using
You can [download](https://github.com/lastfm/lastcommons-kyoto/downloads) a JAR file or obtain lastcommons-kyoto from Maven Central using the following identifier:

* [fm.last.commons:lastcommons-kyoto:1.0.1](http://search.maven.org/#artifactdetails%7Cfm.last.commons%7Clastcommons-kyoto%7C1.0.1%7Cjar)
                                            
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
####Clearer transaction management
          try {
            db.begin(Synchronization.PHYSICAL);
            // Do stuff
            db.commit();
          } catch (KyotoException e) {
            db.rollback();
          }
####Return values represent outcomes, not errors
          boolean recordAlreadyExists = db.putIfAbsent("myKey", "myValue");
          long removed = db.remove(keys, ATOMIC);
          long records = db.recordCount() // Never Long.MIN_VALUE, never < 0
####Validation of Kyoto database configuration
          File dbFile = FILE_HASH.newFile(parent, "an-existing-db");          
          KyotoDb db = new KyotoDbBuilder(dbFile)
            .modes(READ_ONLY)
            .pageComparator(LEXICAL)
            .build();

          // The call to pageComparator() will fail with an
          // IllegalArgumentException as file-hash does not
          // support the 'pcom' option.
#Building
This project uses the [Maven](http://maven.apache.org/) build system.
#Further work
We might implement a Spring [`PlatformTransactionManager`](http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/transaction/PlatformTransactionManager.html "Spring Framework Javadoc - PlatformTransactionManager") for simple integration with Spring's transaction management framework.

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