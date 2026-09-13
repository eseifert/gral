.. image:: https://eseifert.github.io/gral/logo.png

.. image:: https://github.com/eseifert/gral/actions/workflows/build.yml/badge.svg?branch=master
    :target: https://github.com/eseifert/gral/actions/workflows/build.yml

GRAL
####

GRAL is a free Java library for displaying plots (graphs, diagrams, and
charts). The acronym GRAL simply stands for *GRAphing Library*.


Features
========

- Renders through ``java.awt.Graphics2D`` alone, with no dependency on a user
  interface toolkit; the Swing and JavaFX components are separate modules
- Ready-to-use classes for data management
- Data processing and filtering (smoothing, rescaling, statistics, histograms)
- Many different plot types: xy/scatter plot, bubble plot, line plot,
  area plot, bar plot, pie plot, donut plot, box-and-whisker plot, raster plot
- Legends: horizontal and vertical
- Various axis types: linear axes, logarithmic axes, arbitrary number of axes
- Several file formats are supported as data sources or data sinks (CSV,
  bitmap image data, audio file data)
- Exporting plots in bitmap and vector file formats (PNG, GIF, JPEG, EPS, PDF,
  SVG)
- Small footprint (about 350 kilobytes)


Getting started
===============

A plot is built in three steps: put the values into a table, say which columns
form a series, and hand the series to a plot.

.. code:: java

    // 1. One column per variable.
    DataTable data = new DataTable(Double.class, Double.class);
    for (double x = 0.0; x < 10.0; x += 0.25) {
        data.add(x, Math.sin(x));
    }

    // 2. Column 0 is x, column 1 is y. The name shows up in the legend.
    DataSeries series = new DataSeries("sin(x)", data, 0, 1);

    // 3. Create the plot and configure it with plain bean setters.
    XYPlot plot = new XYPlot(series);
    plot.setLineRenderers(series, new DefaultLineRenderer2D());
    plot.setLegendVisible(true);

Displaying it in a window takes an adapter from the ``gral-swing`` module,
because a GRAL plot is not a Swing component:

.. code:: java

    JFrame frame = new JFrame("Example");
    frame.getContentPane().add(new InteractivePanel(plot));
    frame.setSize(600, 400);
    frame.setVisible(true);

Writing it to a file needs no window, no display and no toolkit, which is what
makes GRAL usable for generating figures on a server:

.. code:: java

    DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/png");
    try (OutputStream out = new FileOutputStream("plot.png")) {
        writer.write(plot, out, 600.0, 400.0);
    }

The ``gral-examples`` module contains a runnable example for every plot type;
``./gradlew :gral-examples:run`` opens a browser for all of them.
``./gradlew :gral-javafx-examples:run`` opens the same browser, with the same
examples, built with JavaFX instead of Swing. Either one takes the name of an
example to start on, for example ``--args=ScatterPlot``.


Usage
=====

Without build management system
-------------------------------

You can just add ``gral-core.jar`` to the classpath of your project, plus
``gral-swing.jar`` if you want to display plots in a Swing window.

Using GRAL with Maven
---------------------

If you want to use GRAL with your Maven project you will have to include it as
a dependency in your ``pom.xml``:

.. code:: xml

    <dependency>
        <groupId>de.erichseifert.gral</groupId>
        <artifactId>gral-core</artifactId>
        <version>0.14</version>
    </dependency>

Displaying a plot in a Swing window additionally requires ``gral-swing``, and
in a JavaFX application ``gral-javafx``:

.. code:: xml

    <dependency>
        <groupId>de.erichseifert.gral</groupId>
        <artifactId>gral-swing</artifactId>
        <version>0.14</version>
    </dependency>

A project that used ``gral-core`` before the Swing components were split off can
depend on ``de.erichseifert.gral:gral`` instead, which is the two of them
together:

.. code:: xml

    <dependency>
        <groupId>de.erichseifert.gral</groupId>
        <artifactId>gral</artifactId>
        <version>0.13</version>
    </dependency>

Using GRAL with Gradle
----------------------

.. code:: groovy

    dependencies {
        implementation group: 'de.erichseifert.gral', name: 'gral-core', version: '0.14'
        // Only needed for the Swing components
        implementation group: 'de.erichseifert.gral', name: 'gral-swing', version: '0.14'
    }

Using GRAL with sbt
-------------------

.. code:: scala

    libraryDependencies += "de.erichseifert.gral" % "gral-core" % "0.14"
    libraryDependencies += "de.erichseifert.gral" % "gral-swing" % "0.14"

The example applications are published as ``de.erichseifert.gral:gral-examples``
under the same version. The JAR is runnable and opens a browser for all example
plots.


Building GRAL from source code
==============================
The source package contains all files necessary to build GRAL from scratch using
the `Gradle <http://www.gradle.org>`__ software project management and
comprehension tool. Like ``Makefile`` files the ``build.gradle`` files are used by
Gradle to generate various distribution or documentation files.

All commands below use the Gradle wrapper ``./gradlew`` (``gradlew.bat`` on
Windows), which downloads the required Gradle version automatically. No
separate Gradle installation is needed.

Building a JAR file of the library core
---------------------------------------
In case you just want to build the core of the library to get started execute
the following command in the project directory::

  $ ./gradlew :gral-core:assemble

This will generate a JAR archive named ``gral-core`` in the
``gral-core/build/libs`` directory. This JAR file can be added to the class path
of your application. The components for a user interface toolkit are built
separately::

  $ ./gradlew :gral-swing:assemble
  $ ./gradlew :gral-javafx:assemble

Building a JAR file of the examples
-----------------------------------
The example applications are built with::

  $ ./gradlew :gral-examples:assemble

This will generate a JAR archive for the examples in the
``gral-examples/build/libs`` directory which can be used together with the
library core to run example applications. Alternatively, the example browser can
be started directly with::

  $ ./gradlew :gral-examples:run

The JavaFX browser is a module of its own::

  $ ./gradlew :gral-javafx-examples:run

Running the tests
-----------------
::

  $ ./gradlew build

A handful of tests in the ``gral-swing`` and ``gral-javafx`` modules need a
display and skip themselves when none is available. To run them on a headless machine, use a
virtual frame buffer::

  $ xvfb-run --auto-servernum ./gradlew build

Building the documentation
--------------------------
The GRAL Gradle project offers three sources for documentation:

1. The JavaDoc files that can be generated with::

     $ ./gradlew javadoc

2. The reports found in ``build/reports`` containing a project various
   information like test results, test coverage, etc. To build these files
   just execute::

     $ ./gradlew :gral-core:report

3. A book-like documentation in the reStructuredText format is available in the
   file ``documentation_en.rst``.


Using GRAL in an IDE
====================
The Gradle project can also be used in your favorite development environment like
Eclipse or NetBeans. For further information look at the following descriptions
on the Gradle website `http://www.gradle.org/tooling`

Once you have installed an appropriate Gradle plug-in for your IDE you will be
able to import the GRAL project found in this folder.


Requirements
============
Using GRAL requires Java 11 or later.

Building GRAL from source requires a JDK 17 or later, because that is what the
Gradle version used by the build needs. The library itself is still compiled for
Java 11, independently of the JDK used to build it.

Export to the vector formats EPS, PDF and SVG additionally requires
VectorGraphics2D on the runtime class path. GRAL loads it reflectively, so those
formats are simply unavailable when it is missing. Two variants of the library
are supported, and whichever is found will be used:

- `VectorGraphics2D <https://github.com/eseifert/vectorgraphics2d>`__
  (``de.erichseifert.vectorgraphics2d:VectorGraphics2D``) is the original
  library. It is licensed under the LGPL and is the variant that GRAL declares
  as a runtime dependency, so build tools pick it up automatically. It has been
  archived and is no longer maintained.
- `Eclipse SWTChart <https://github.com/eclipse/swtchart>`__
  (``org.eclipse.swtchart:org.eclipse.swtchart.vectorgraphics2d``) continues the
  library. It is licensed under the EPL-2.0 and pulls in Apache PDFBox for PDF
  output. To use it, exclude the original dependency and add this one instead.
