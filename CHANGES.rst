GRAL 0.13 (unreleased)
======================

Data:
    - ``Kernel.mul`` sets the values outside of the specified kernel to zero

Plotting:
    - Horizontally stacked layouts report the height of their tallest
      component as preferred height

General:
    - Releases are published through the Central Portal publisher API

GRAL 0.12 (2026-09-11)
======================

Data:
    - Data change events of ``DataSeries`` use the series' columns now (#178)
    - Fixed skewness, which was off by a constant value of three
    - Statistics of a data source are updated when its values change
    - Added ``Record.hashCode`` and an explicit ``serialVersionUID``
    - Median and convolution filters no longer fail on short or empty data

Plotting:
    - ``Label.equals`` no longer fails for unset text, font, or color

General:
    - ``SortedList.indexOf`` returns -1 for absent elements now
    - Modernized the build: Gradle 9, JDK 17 or later to build
    - The library is now compiled for Java 11 instead of Java 7
    - The published JAR is no longer processed by ProGuard, so it keeps its
      ``SourceFile`` attributes and IDEs can attach the sources JAR again
      (#139). Dropping ProGuard also removes the build failure on JDK 11 and
      later (#183)
    - VectorGraphics2D is a declared runtime dependency now, so EPS, PDF and
      SVG export no longer depend on the user adding it manually (#181)
    - Added support for the VectorGraphics2D variant that is continued as part
      of Eclipse SWTChart. Both variants can be used; the LGPL-licensed original
      stays the declared dependency
    - Added ``Automatic-Module-Name`` to the JARs for use on the module path
    - Replaced Cobertura with JaCoCo (#66) and Travis CI with GitHub Actions

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
