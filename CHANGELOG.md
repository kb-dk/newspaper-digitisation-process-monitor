1.10
* Fix faulty caching of pages
* Remember pids from Summa index instead of looking them up

1.9
* Cache values for duration and pages
* Update to version 2.2 of mfpak module
* Update to version 2.6 of batch event framework

1.8
* Requires java7 or java8
* Remove old retry logic
* add different colors for even and odd rows in the process monitor frontend
* Integration tests actually run with jetty and java 7 again. Use the jettyTest profile to enable these
* logback.xml files updated to better pattern including thread
* Remember the pdf editions in csv
* Added "Pages" column to the web view.
* CSV: download with number of pages, avidid, duration
* CSV: Include start and end date in CSV download
* CSV: Fixed CSVDownload headers.

1.7
* Add PDF edition dissemantion copy column
* Use newest version of item event framework. No functional changes for this module.
* Update Jersey framework to newest version
* Configuration has been extended and changed and example config has been updated. Please update your configuration files.

1.6
* Update generated CSV files to include all known states
* Update columns in process monitor, including improved headings

1.5
* Add 'avisid', start date and end date to monitor page
* Update to newspaper-mfpak-integration 1.6
* Update to version 1.7 of batch event framework

1.4
* Updated to newspaper-parent 1.2
* Update to newspaper-mfpak-integration 1.5
* Update to version 1.6 of batch event framework

1.3
* Added filters for the last two columns
* Updated to version 1.4.1 of Batch Event Framework

1.2
 * Added the Histogrammed column
 * Removed the Automatic QA OK column
 * Added the Presentation Copies generated column
 * Use version 1.3 of MFPak and version 1.4.1 of Batch Event Framework

1.1
* Expose the dates of the events to the frontend
* Expose details on the frontent
* Add the possibility of downloading a CSV file contaning the events, details and timestamps
* Update to the version 1.4 of batch event framework. Some data is acquired directly from DOMS rather than from the summaindex, 
  this means that the config needs parameters to DOMS too. The DOMS credentials can be readonly.

1.0
* Initial release 

