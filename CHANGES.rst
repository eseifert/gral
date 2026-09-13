GRAL 0.14 (unreleased)
======================

Plotting:
    - Box plots of non-positive observations no longer stretch the axis to zero
    - Drawing or exporting a box plot of constant observations no longer hangs
    - A point shape without extent no longer collapses a box plot's box
    - Auto-scaled axes no longer collapse when all values are equal (#142)
    - The grid is opaque light grey now, so that EPS, which has no alpha
      channel, no longer draws it as solid black lines (#145)
    - Plots, axes, renderers, legends and color mappers are no longer
      serializable (#151)

General:
    - Drawables, layouts and the utility types are no longer serializable
    - Only the ``data`` package is still serializable
    - Removed ``SerializationUtils`` and its ``Serializable`` AWT wrappers
    - Added an export test that rejects ``NaN`` coordinates in EPS, PDF and SVG
    - Tests assert the order and number of returned values, not just presence
    - The ``data`` serialization tests compare the restored properties
    - Tests use Hamcrest 2.2, which has order-sensitive matchers
    - The shared test helpers live in a Gradle test fixture
    - The Swing components moved into a ``gral-swing`` module
    - The library core no longer depends on ``javax.swing``
    - Added ``Navigables.getNavigableAt`` to look up a navigable
    - Added a ``gral-javafx`` module for JavaFX applications
    - Added a runnable JavaFX example application
    - Scrolling zooms the JavaFX canvas one step per wheel notch
    - Added an aggregate ``gral`` artifact for core and Swing
    - Documented how to draw a plot with any Graphics2D
    - CI installs the GTK libraries that the JavaFX tests need
    - Only ``gral-core`` declares the VectorGraphics2D dependency

GRAL 0.13 (2026-09-13)
======================

Data:
    - ``Kernel.mul`` sets the values outside of the specified kernel to zero
    - ``DataTable.add(Record)`` notifies the registered data listeners and
      discards the cached statistics
    - ``Row.hashCode`` is consistent with ``Row.equals``, so that rows can be
      used in hash-based collections
    - The last bin of ``Histogram`` and the last cell of ``Histogram2D``
      include their upper limit, so that the largest value is counted
    - Added ``DataTable.insert`` to insert a row at a given position (#75)
    - ``DataSeries.toString`` describes an unnamed series instead of returning
      ``null`` (#156)

Plotting:
    - Horizontally stacked layouts report the height of their tallest
      component as preferred height
    - Edge layouts subtract both vertical gaps from the height of their
      central row, so that its components no longer overlap the bottom row
    - Removing a data source from a plot, or clearing the plot, updates the
      ranges of auto-scaled axes
    - Hidden data sources no longer determine the ranges of auto-scaled axes
    - Bar plots with a single bar, or with several bars at the same position,
      are visible now (#146)
    - Zooming or panning a plot that hasn't been laid out yet no longer
      invalidates its axes, which resulted in an empty plot (#112)
    - Zooming, panning and resetting the view work after a new axis renderer
      has been set on a plot (#46)
    - Pie slices no longer overlap when the data source contains negative
      values (#148)
    - The slices of a pie plot are accumulated once per change of the data
      instead of once per read value, which made drawing a pie plot take a
      time that grows with the square of the number of values
    - A pie plot rejects data sources that have not been prepared with
      ``PiePlot.createPieData``, instead of failing later with an
      ``ArrayIndexOutOfBoundsException`` (#173)
    - Added ``QuasiRandomColors.setHue``, ``setSaturation`` and
      ``setBrightness``, which take the bounds of a range (#91)
    - A color of ``QuasiRandomColors`` depends on its index alone now, not on
      how often the mapper has been queried before

Data I/O:
    - ``CSVWriter`` quotes values that contain the column separator, a quote,
      or a line break, so that they can be read again

General:
    - The library sources use the syntax of the Java 11 baseline now
    - Releases are published through the Central Portal publisher API
    - The example applications are released to Maven Central as well, in
      the same deployment as the library (#182)
    - Added ``HaltonSequence.get`` to read the element at a given position
    - Expanded and corrected the API documentation and the manual

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
