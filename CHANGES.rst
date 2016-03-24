GRAL 0.11 (2016-03-11)
======================

Data:
    - More robust import of CSV/TSV data
    - Added a name to all data sources

Plotting:
    - Data sources can have multiple renderers of the same type (point, line,
      area) now, which will allow effects like shadows, etc.
    - Improved quality of exported bitmap images

General:
    - Switched from ``mvn.erichseifert.de`` to Maven Central
    - Improved versioning scheme using Git describe
    - Many clean-ups and bug fixes

GRAL 0.10 (2013-12-09)
======================

Plotting:
    - Replaced all plot settings by regular Java properties

General:
    - Migrated the version control system from Subversion to git
    - Replaced the Maven build system by Gradle
    - Various bug fixes

GRAL 0.9 (2013-05-12)
=====================

Data:
    - New JDBC data source that can query data from a database connection
    - Support for arbitrary Comparable values and empty values (``null``) in
      data sources
    - Improved statistics (lazy calculation, quartile statistics, etc.)
    - More flexible parsing of CSV files
    - All plots are fully serializable

Plotting:
    - Two new plot types: box-and-whisker plot and raster plot
    - New line renderer for smooth curves
    - Automatic scaling for axes
    - Support for secondary axes
    - Displayed data is clipped to plotting area
    - Improved color mapping
    - Font and alignment settings for labels
    - Improved legends

Interaction:
    - All plots implement support mouse navigation
    - Interactions can be synchronized between plots
    - Panning can be restricted to work only horizontally or vertically
    - Improved concurrency
    - Localized texts

General:
    - Separate packages for library core and examples
    - Several fixes to build GRAL with Java 7
    - reStructuredText format for documentation instead of DocBook
    - More example plots
    - Updated and improved documentation
    - Various bug fixes

GRAL 0.8 (2010-07-31)
=====================

Data:
    - Extensible classes for data import and export
    - Vertical and horizontal statistics
    - Plots can be exported as bitmap or vector graphics with VectorGraphics2D
    - Printing support

Plotting:
    - Added area renderers
    - Added minor ticks and minor grid lines
    - Support for error bars
    - Segment gaps in pie plots
    - Data sources can be hidden

Interaction:
    - Interactive Swing component

General:
    - Renamed project to GRAL (GRAphing Library)
    - Using Maven as build system
    - Added more example plots
    - Updated and improved documentation

GRAL 0.7 (2010-01-04)
=====================

First public release as "OpenJChart" supporting x-y plots, bar plots, and
pie plots
