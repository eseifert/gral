.. image:: https://eseifert.github.io/gral/logo.png

.. image:: https://travis-ci.org/eseifert/gral.svg?branch=master
    :target: https://travis-ci.org/eseifert/gral

GRAL
####

GRAL is a free Java library for displaying plots (graphs, diagrams, and
charts). The acronym GRAL simply stands for *GRAphing Library*.


Maintainer wanted
================
GRAL is looking for a new maintainer. If you are interested, please contact `dev@erichseifert.de <mailto:dev@erichseifert.de>`_.


Features
========

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
- Small footprint (about 300 kilobytes)


Usage
=====

Without build management system
-------------------------------

You can just add ``gral-core.jar`` to the classpath of your project.

Using GRAL with Maven
---------------------

If you want to use GRAL with your Maven project you will have to include it as
a dependency in your ``pom.xml``:

.. code:: xml

    <dependency>
        <groupId>de.erichseifert.gral</groupId>
        <artifactId>gral-core</artifactId>
        <version>0.11</version>
    </dependency>

Using GRAL with Gradle
----------------------

.. code:: groovy

    dependencies {
        compile group: 'de.erichseifert.gral', name: 'gral-core', version: '0.11'
    }

Using GRAL with sbt
-------------------

.. code:: scala

    libraryDependencies += "de.erichseifert.gral" % "gral-core" % "0.11"


Building GRAL from source code
==============================
The source package contains all files necessary to build GRAL from scratch using
the `Gradle <http://www.gradle.org>`__ software project management and
comprehension tool. Like ``Makefile`` files the ``build.gradle`` files are used by
Gradle to generate various distribution or documentation files.

Building a JAR file of the library core
---------------------------------------
In case you just want to build the core of the library to get started execute
the following command in the ``gral-core`` directory::

  $ gradle assemble

This will generate a JAR archive named ``gral-core`` in the ``build/libs`` directory.
This JAR file can be added to the class path of your application.

Building a JAR file of the examples
-----------------------------------
In case you just want to build the core of the library to get started execute
the following command in the ``gral-examples`` directory::

  $ gradle assemble

This will generate a JAR archive for the examples in the ``build/libs`` directory
which can be used together with the library core to run example applications.

Building the documentation
--------------------------
The GRAL Gradle project offers three sources for documentation:

1. The JavaDoc files that can be generated with::

     $ gradle javadoc

2. The reports found in ``build/reports`` containing a project various
   information like test results, test coverage, etc. To build these files
   just execute::

     $ gradle report

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
To build GRAL from source, you need a Gradle version higher than 4.0 and at least Java 7.
