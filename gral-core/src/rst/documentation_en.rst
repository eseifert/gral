Data administration
===================

The first step, before we are able to plot anything, is to load or create data,
a process which GRAL provides several ways for. The basic interface that will be
used when you have to provide data is ``DataSource``. Think of it as a table
with a (theoretically) arbitrary number of rows and columns. Every column has
its own data type, but only ``Comparable`` values are allowed. The functions a
``DataSource`` provides are solely for retrieving data rows, but it also
supports statistics on the contained data, as well as the capability to listen
for data changes. The following sections describe the different ways for
providing and manipulating data.

Creating data
-------------

Assuming you have computed some values you want to plot, how can you store your
data in a ``DataSource``?

You can do so through the class ``DataTable``. ``DataTable`` is an
implementation of ``DataSource`` (or ``AbstractDataSource`` to be more precise)
and supports operations like adding and removing rows. The values you must match
the number of columns and their types, which both had already been specified in
the constructor.

.. code:: java

    // Create the table with the specified column types
    DataTable table = new DataTable(Double.class, Double.class);

    // Iterate your available data. In this case, an Iterable<Double[]> or Double[][]
    for (Double[] coords : data) {
        double x = coords[0];
        double y = coords[1];
        table.add(x, y);
    }

You might want to have a ``DataTable`` for testing purposes only and do not want
to create random values for it at each start. In this case, the class
``DummyData`` is your remedy. ``DummyData`` is a ``DataSource`` filled with a
single value. As I already mentioned, this is not used very often, but suited
very well for testing due to its efficiency, especially when dealing with large
tables.

.. code:: java

    DummyData data = new DummyData(3, 100, 42.0);

Sorting data
------------

Often, data has to be reordered, e.g. for filtering. The sorting criteria in
GRAL can be handled very flexible: for example the rows of a data source could
be rearranged so that the first column is sorted ascending and as a second
criterion the second column is sorted descending when values of the first column
are equal. GRAL uses two classes to define the sorting options: ``Ascending``
and ``Descending``. In order to create one of them you must pass the index of
the column that should be sorted to the constructor.

.. code:: java

    // Sort the primary column (1) ascending,
    // the secondary column (0) descending, and
    // the ternary column (2) ascending
    table.sort(new Ascending(1), new Descending(0), new Ascending(2));

Filtering data
--------------

``DataTables`` are not the only data source for plots. A plot also accepts
filtered data. In GRAL data can be filtered either by columns or by rows.

Filtering columns
~~~~~~~~~~~~~~~~~

The main tasks for filtering by columns are to create subsets (*series*) of
columns and to reorder columns for certain plot types. Using the class
``DataSeries`` a data source with four columns *A, B, C, D* could be divided
into two series: one containing columns *A, B* and one containing columns
*C, D*. The series could also overlap, for example the first series could
contain the columns *A, B* and the second the columns *B, C*. Furthermore, data
series can be used to reorder columns. It is possible to map columns
*A, B, C, D* to *D, B, C, A* or just *D, C*.

A ``DataSeries`` is created using at least two parameters: the original data
source and a number of columns, which will appear in the order they are passed.
Optionally a name can be passed as first argument. In Plots this name is used
for example to display captions in the plot legend.

.. code:: java

    // Create a new series from columns 0 and 1
    DataSeries series1 = new DataSeries("Series 1", table, 0, 1);
    // Create a new series from columns 2 and 0
    DataSeries series2 = new DataSeries("Series 2", table, 2, 0);
    // Create a new series from column 1
    DataSeries series3 = new DataSeries("Series 3", table, 1);

Filtering rows
~~~~~~~~~~~~~~

Another way of filtering is to filter data sources by rows. This way a data
subset which matches certain criteria can be extracted. For example, this could
be used to form clusters which could then be plotted or processed separately.

In order to use the ``DataSubset`` a new class has to be created which
implements the method ``accept(Row)``. This method is used to decide whether
a certain row should be kept in the subset.

.. code:: java

    // Keep only rows where the first column has an even value
    RowSubset data = new RowSubset(table) {
        @Override
        public boolean accept(Row row) {
            Number n = (Number) row.get(0);
            return (n.doubleValue() % 2.0) == 0.0;
        }
    };

Processing data
---------------

An integral part of GRAL's pipeline is preprocessing of data. The simplest case
would be to extract several statistics per column such as minimum, maximum,
arithmetic mean, or median. But GRAL also covers more complex cases such as
generating histograms or convolution filtering of data. The latter can be used
to smooth or sharpen data in various ways.

Statistics
~~~~~~~~~~

The most basic statistical functionality of GRAL is to query various aggregated
measures for columns using the class Statistics. It is part of every
``DataSource`` instance and can be easily accessed with the method
``getStatistics(int)``.

.. code:: java

    // Get the maximum for the second column
    double max = table.getColumn(1).getStatistics(Statistics.MAX);

``N``
    The number of values in the column.

``SUM``
    The sum of all column values.

``MIN``
    The smallest value of the column.

``MAX``
    The largest value of the column.

``MEAN``
    The arithmetic mean describing the average value of the column.

``MEAN_DEVIATION``
    The mean deviation describing the dispersion of the column's values.

``MEDIAN``
    The median value which divides the column values in two equal
    halves.

``VARIANCE``
    The variance value describing the dispersion of the column's values.

``SKEWNESS``
    The skewness value describing the asymmetry of the probability
    distribution of the column's values.

``KURTOSIS``
    The kurtosis value describing the "peakedness" of the probability
    distribution of the column's values.

``QUARTILE_1``
    The value that delimits the lower 25% of all data values.

``QUARTILE_2``
    The value that delimits 50% of all data values. This is the same as
    the median value.

``QUARTILE_3``
    The value that delimits the upper 25% of all data values.

Histograms are a more complex way for aggregating data. In a histogram all
values are assigned to specified categories. In GRAL's ``Histogram`` class
categories are defined as value ranges. For example all values from 0 to 5 would
be in category A and all values from 5 to 10 in category B. Then, the histogram
would generate two rows for each column that contain the number of values in
category A and B, respectively.

.. code:: java

    // Use 4 equally spaced breaks
    Histogram histogram = new Histogram1D(table, Orientation.VERTICAL, 4);

.. code:: java

    // Use custom breaks for each column
    Number[] breaksCol1 = {1.0, 2.0, 3.0, 4.0, 5.0};
    Number[] breaksCol2 = {1.0, 3.0, 5.0, 7.0, 9.0};
    Histogram histogram = new Histogram1D(table, Orientation.VERTICAL,
        breaksCol1, breaksCol2);

Convolution
~~~~~~~~~~~

Often, it is necessary to change existing data by smoothing it, so that noise or
fine-scale structures are reduced. Another frequent use case is to boost or
extract exactly those fine-scale structures. Both cases can be handled in GRAL
using the convolution operation. Mathematically, convolution is the combination
of two functions: the data function and a kernel function. By varying th kernel
function various operations can be achieved: smoothing (low-pass filter),
deriving (high-pass filter), sharpening, moving average, and much more.

Besides the class ``DataSource`` GRAL provides two additional classes for
convolution: the class ``Kernel`` defines the kernel function and the class
``Convolution`` is responsible for processing the data source. The
``Convolution`` instance can finally be used as a data source for plots.

.. code:: java

    // Create a moving average of width 3
    Kernel kernel = new Kernel(1.0, 1.0, 1.0).normalize();
    // Filter columns 0 and 1 and omit boundary values if necessary
    Convolution filter = new Convolution(table, kernel, Filter.Mode.OMIT, 0, 1);

.. code:: java

    // Create a smoothing kernel with a variance of 2
    Kernel kernel = KernelUtils.getBinomial(2.0).normalize();
    // Filter column 1 and start over for boundary values if necessary
    Convolution filter = new Convolution(table, kernel, Filter.Mode.CIRCULAR, 1);

.. code:: java

    // Create a smoothing kernel with a variance of 3
    Kernel kernel = KernelUtils.getBinomial(3.0).normalize()
    // Subtract the original values
    kernel = kernel.negate().add(new Kernel(1.0));
    // Filter column 1 and repeat boundary values if necessary
    Convolution filter = new Convolution(table, kernel, Filter.Mode.REPEAT, 1);

Exchanging data
---------------

GRAL allows you to interchange data values through its extensible plug-in
system. Arbitrary sources and sinks can be accessed, like plain files,
databases, or even web services. The current version of GRAL already supports
file formats like simple CSV files (*Comma-Separated Values*).

Importing
~~~~~~~~~

Loading data from a source in GRAL needs two steps: First, you have to get a
``DataReader`` the desired file format via its MIME type from an instance of
``DataReaderFactory``. Then, you have to call the method read of the reader with
two (or more) parameters: an ``InputStream`` instance to read from and the
column data types. The method finally reads the data and returns a new
``DataSource`` containing all values that have been extracted from the source.

.. code:: java

    DataReader reader = DataReaderFactory.getInstance().get("text/csv");
    InputStream file = new FileInputStream("foobar.csv");
    DataSource data = reader.read(file, Integer.class, Double.class, Double.class);

.. code:: java

    DataReader reader = DataReaderFactory.getInstance().get("image/png");
    reader.setSetting("factor", 1.0/255.0);
    reader.setSetting("offset", 1);
    InputStream file = new FileInputStream("foobar.png");
    DataSource data = reader.read(file);

Exporting
~~~~~~~~~

Saving data in GRAL is even easier than loading. First, you need to get an
instance of ``DataWriterFactory`` and then you fetch a ``DataWriter`` for the
desired file format via its MIME type. Then, you have to call the method
``write`` with two parameters: a ``DataSource`` and an ``OutputStream`` instance
to write to.

.. code:: java

    DataWriter writer = DataWriterFactory.getInstance().get("text/csv");
    FileOutputStream file = new FileOutputStream("foobar.csv");
    writer.write(table, file);

.. code:: java

    DataWriter writer = DataWriterFactory.getInstance().get("image/png");
    writer.setSetting("factor",  255);
    writer.setSetting("offset", -255);
    FileOutputStream file = new FileOutputStream("foobar.png");
    writer.write(table, file);

Displaying data
===============

The main purpose of GRAL is to plot diagrams. It offers several types of plots
which can be customized and exported for publishing. In this chapter you will
find an overview of plot types and their options for customization as well as
examples how to export the plotted graphics in various formats.

The components of a plot in GRAL are:

- Each plot has one or more instances of ``DataSource``

- The area where the actual data is plotted is called plot area and the class
  used to display the area is ``PlotArea2D``, correspondingly.

- Depending on its type a plot can have an arbitrary number of axes which are
  created with the class Axis. Each Axis is displayed by an instance of
  ``AxisRenderer``.

- Data points on the plot area are rendered by an instance of ``PointRenderer``.

- Connections between data points are rendered by an instance of
  ``LineRenderer``

- In order to fill the area below data points an instance of ``AreaRenderer`` is
  used.

Plot types
----------

Currently, GRAL has three main plot types: xy-plot, bar plot, and pie plot.
This section presents those plot types and show how to adjust their visual
settings and to derive many more types. For example, xy-plots can be turned into
line plots, or area plots and a pie chart can be made into a doughnut plot with
just one command.

XY-Plot
~~~~~~~

``XYPlot``: XY-Plots are usually the most common plot type. Used it whenever you
want to create a line plot, a scatter plot, a bubble plot, or an area plot.

.. code:: java

    Plot plot = new XYPlot(series1, series2);

Legends
^^^^^^^

Legends explain the symbols used in plot by the symbols and a description in a
table-like representation. GRAL's ``Legend`` class can be either vertically
(default) or horizontally oriented. Its options determine the positioning inside
the plot as well as its background color, its border, or its spacings.

.. code:: java

    plot.setLegendVisible(true);

Bar plot
~~~~~~~~

Usually, bar plots are used to show rectangular bars with lengths proportional
to their corresponding values. GRAL provides a ``BarPlot`` class which is in
fact a special case of an xy-plot.

.. code:: java

    Plot plot = new BarPlot(series);

Box-and-whisker plot
~~~~~~~~~~~~~~~~~~~~

Box-and-whisker, or short box plots, are used to display the statistics like
minimum, maximum, median, or quantiles in a concise plot. GRAL's class
``BoxPlot`` is used to create this type of plot.

.. code:: java

    Plot plot = new BoxPlot(series);

The data series must provide six columns for each plot element:

- x position of the box-whisker-plot
- y position of the center bar (e.g. median)
- y position of the lower whisker (e.g. minimum)
- upper edge of the box (e.g. first quartile)
- lower edge of the box (e.g. third quartile)
- y position of the upper whisker (e.g. maximum)

A utility method of ``BoxPlot`` can be used to generate a suitable data source
from an existing data source:

.. code:: java

    DataSource series = BoxPlot.createBoxData(data);
    Plot plot = new BoxPlot(series);

Pie plot
~~~~~~~~

Pie plots are circles divided into sectors to illustrate the proportions of the
corresponding data values. GRAL's class ``PiePlot`` is used to create this type
of plot.

.. code:: java

    Plot plot = new PiePlot(series);

Raster plot
~~~~~~~~~~~

Raster plots are used to display a two-dimensional grid with filled grid tiles.
GRAL's class ``RasterPlot`` is used to create this type of plot.

.. code:: java

    Plot plot = new RasterPlot(series);

The data series must provide three columns for each grid tile:

- x position of the grid tile
- y position of the grid tile
- value of the grid tile

A utility method of ``RasterPlot`` can be used to generate a suitable data
source from an existing data source:

.. code:: java

    DataSource series = RasterPlot.createBoxData(data);
    Plot plot = new RasterPlot(series);

Customization
-------------

The visual appearance of most classes in GRAL can be queried and changed using
the ``get`` and ``set`` methods for each property. This way, properties like
colors, borders, margins, or positions can be easily customized.

Customizing the plot
~~~~~~~~~~~~~~~~~~~~

Plots provide the canvas for painting all plot components (see section
"Plotting"). It controls how the background will be drawn the way the components
are positioned. Another important setting is the plot title. The following
example shows how to set the title of an xy-plot.

.. code:: java

    Plot plot = new XYPlot(data);
    plot.getTitle().setText("My First XY Plot");

In the next example you can see how a background gradient can be assigned to the
whole plot.

.. code:: java

    Plot plot = new PiePlot(data);
    Paint gradient = new LinearGradientPaint(
        0f,0f,                 // Coordinates of gradient start point
        1f,0f,                 // Coordinates of gradient end point
        new float[] {0f, 1f},  // Relative fractions
        new Color[] {Color.GRAY, Color.WHITE}  // Gradient colors
    );
    plot.setBackground(gradient);

The ``PlotArea2D`` is the container for plotting the data. It must be fetched
from a plot with the method, ``getPlotArea`` as each ``Plot`` type can also have
its own plot area type. In the following example you can see how to hide the
plot area itself completely.

.. code:: java

    PlotArea2D plotArea = plot.getPlotArea();
    plotArea.setBackground(null);
    plotArea.setBorder(null);

Often, a legend has to added to a plot. Every plot already has a ``Legend``
which just has to be turned on explicitly. Then, the positioning, orientation,
spacing, as well as the legend background can be changed. The following example
shows how to add a horizontal legend to the the bottom left corner of a plot.

.. code:: java

    plot.setLegendVisible(true);
    plot.setLegendLocation(Location.SOUTH_WEST);
    Legend legend = plot.getLegend();
    legend.setOrientation(Orientation.HORIZONTAL);

Customizing axes
~~~~~~~~~~~~~~~~

Axes of a plot have reasonable defaults for displaying. Sometimes however, it's
necessary to add an axis title, adjust the spacing of the tick marks, or change
the formatting of the data values along an axis. All those properties are
controlled by the interface ``AxisRenderer``. Each axis in a plot has its own
instance and can have different settings.

.. code:: java

    AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
    axisRendererX.setTickSpacing(5.0);

There are to implementations of ``AxisRenderer``: for axes with a linear scale
(the default case) ``LinearRenderer2D`` is used; for axes with a logarithmic
scale the class ``LogarithmicRenderer2D`` is used:

.. code:: java

    XYPlot plot = new XYPlot(seriesLog, seriesLin);
    AxisRenderer2D axisRendererX = new LogarithmicRenderer2D();
    axisRendererX.setLabel("Logarithmic data");
    plot.setAxisRenderer(XYPlot.AXIS_X, axisRendererX);

.. code:: java

    AxisRenderer2D axisRendererX = new LogarithmicRenderer2D();
    Format dateFormat = DateFormat.getTimeInstance();
    axisRendererX.setTickLabelFormat(dateFormat);

.. code:: java

    Map<Double, String> labels = new HashMap<Double, String>();
    labels.put(2.0, "Doubled");
    labels.put(1.5, "One and a half times");
    axisRendererX.setCustomTicks(labels);

Customizing points
~~~~~~~~~~~~~~~~~~

The display of data points in a plot is done by instances of ``PointRenderer``.
A point renderer defines the shape, the color, the size, and even the position
of each point. A custom renderer can be implemented using either the interface
``PointRenderer`` itself or using the abstract class ``AbstractPointRenderer``
which is the preferred way.

Every point renderer has to implement two methods:
``Shape getPointShape(PointData)`` returns the vector shape of a specified data
point, and ``Drawable getPoint(PointData, Shape)`` returns a drawable component
which then renders the points.

The class ``AbstractPointRenderer`` implements the interface ``PointRenderer``
and additionally it provides everything that's necessary to manage settings and
draw basic elements.

In the following example you can see how to implement a simple renderer.

.. code:: java

    public class SimplePointRenderer extends DefaultPointRenderer2D {
        @Override
        public Drawable getPoint(final PointData data, final Shape shape) {
            Drawable drawable = new AbstractDrawable() {
                @Override
                public void draw(DrawingContext context) {
                    Paint paint = SimplePointRenderer.this.getColor();
                    Shape point = getPointShape(data);

                    // Put your custom code here ...

                    GraphicsUtils.fillPaintedShape(context.getGraphics(), point, paint, null);
                }
            };
            return drawable;
        }
    }

Customizing lines
~~~~~~~~~~~~~~~~~

Data points can be connected using lines. To draw these lines instances of
``LineRenderer`` are used. A line renderer has full control over the line's
shape, the stroke patterns, and the colors which will be used when drawing.
Custom renderers can be easily implemented using either the interface
``LineRenderer`` itself or using the abstract class ``AbstractLineRenderer2D``
which is the preferred way for two-dimensional applications.

Every line renderer has to implement two methods:
``Shape getLineShape(List<DataPoint>)`` returns a vector shape for the line, and
``Drawable getLine(List<DataPoint>, Shape)`` returns a drawable component to
display the line.

The class ``AbstractLineRenderer2D`` implements the interface ``LineRenderer``
for two-dimensional data and additionally provides everything that's necessary
to manage settings and draw basic elements.

In the following example you can see how to implement a simple renderer.

.. code:: java

    public class SimpleLineRenderer2D extends AbstractLineRenderer2D {
        @Override
        public Shape getLineShape(final List<DataPoint> points) {
            Path2D line = new Path2D.Double();
            for (DataPoint point : points) {
                Point2D pos = point.position.getPoint2D();
                if (line.getCurrentPoint() == null) {
                    line.moveTo(pos.getX(), pos.getY());
                } else {
                    line.lineTo(pos.getX(), pos.getY());
                }
            }
            Shape lineShape = punch(line, points);
            return lineShape;
        }

        @Override
        public Drawable getLine(final List<DataPoint> points, final Shape shape) {
            Drawable d = new AbstractDrawable() {
                @Override
                public void draw(DrawingContext context) {
                    Paint paint = SimpleLineRenderer2D.this.getColor();
                    GraphicsUtils.fillPaintedShape(context.getGraphics(), shape, paint, null);
                }
            };
            return d;
        }
    }

Customizing areas
~~~~~~~~~~~~~~~~~

In order to display filled or hatched areas in plots so called area renderers
are used. They all derive from the interface ``AreaRenderer`` and they control
the colors, the fillings, and also the shape of the rendered area. Custom
renderers can be easily implemented using either the interface ``AreaRenderer``
itself or using the abstract class ``AbstractAreaRenderer`` which is the
preferred way.

Every area renderer has to implement one method:
``Shape getAreaShape(List<DataPoint>)`` returns a vector shape for the area, and
``Drawable getArea(List<DataPoint>, Shape)`` returns a drawable component to
display the area.

The class ``AbstractAreaRenderer`` implements the interface ``AreaRenderer`` and
additionally it provides everything that's necessary to manage settings and draw
basic elements.

In the following example you can see how to implement a simple renderer.

.. code:: java

    public class SimpleAreaRenderer extends AbstractAreaRenderer {
        @Override
        public Drawable getArea(final List<DataPoint> points, final Shape shape) {
            Shape path = getAreaShape(points);
            final Shape area = punch(path, points);

            return new AbstractDrawable() {
                @Override
                public void draw(DrawingContext context) {
                    Paint paint = SimpleAreaRenderer.this.getColor();
                    GraphicsUtils.fillPaintedShape(context.getGraphics(),
                            area, paint, area.getBounds2D());
                }
            };
        }

        public Shape getAreaShape(final List<DataPoint> points) {
            Shape shape = null;
            // Code to construct the shape
            return shape;
        }
    }

Exporting plot images
~~~~~~~~~~~~~~~~~~~~~

Usage similar to data import/export. Bitmap formats like PNG, JPEG, BMP, or GIF
and vector formats like SVG, PDF, or EPS.

.. code:: java

    XYPlot plot = new XYPlot(data);
    DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/svg+xml");
    FileOutputStream file = new FileOutputStream("xyplot.svg");
    double width = 320.0, height = 240.0;
    writer.write(plot, file, width, height);

Extending GRAL
==============

GRAL can be extended in numerous ways to better suit your needs. For example,
you can write your own plot types, line types, axes, or data exchange plug-ins.
The following chapter shows you how to use GRAL's application programming
interface to tailor it for your requirements.

Writing a new plot type
-----------------------

``Plot`` class, ``DataListener`` interface.

.. code:: java

    public class MyPlot extends Plot implements DataListener {
        private double mySetting;

        public MyPlot(DataSource data) {
            super(data);
            mySetting = 1.0;
            ...
            dataChanged(data);
            data.addDataListener(this);
        }

        @Override
        public void dataChanged(DataSource data) {
            ...
        }
    }

Writing a data importer
-----------------------

``DataReaderFactory`` and ``DataReader``.

.. code:: java

    public class MyReader extends IOCapabilitiesStorage implements DataReader {
        static {
            addCapabilities(new IOCapabilities(
                "My Format",
                "My custom file format",
                "application/x-myformat",
                "myf"
            ));
        }

        private final Map<String, Object> settings;
        private final Map<String, Object> defaults;
        private final String mimeType;

        public MyReader(String mimeType) {
            this.mimeType = mimeType;
            settings = new HashMap<String, Object>();
            defaults = new HashMap<String, Object>();
            defaults.put("my setting", "foobar");
        }

        @Override
        public DataSource read(InputStream input, Class<? extends Number>... types)
                throws IOException, ParseException;
            ...
        }

        /**
         * Returns the MIME type.
         * @return MIME type string.
         */
        public String getMimeType() {
            return mimeType;
        }

        @Override
        public <T> T getSetting(String key) {
            if (!settings.containsKey(key)) {
                return (T) defaults.get(key);
            }
            return (T) settings.get(key);
        }

        @Override
        public <T> void setSetting(String key, T value) {
            settings.put(key, value);
        }
    }

Writing a data exporter
-----------------------

``DataWriterFactory`` and ``DataWriter``.

.. code:: java

    public class MyWriter extends IOCapabilitiesStorage implements DataWriter {
        static {
            addCapabilities(new IOCapabilities(
                "My Format",
                "My custom file format",
                "application/x-myformat",
                "myf"
            ));
        }

        private final Map<String, Object> settings;
        private final Map<String, Object> defaults;
        private final String mimeType;

        public MyWriter(String mimeType) {
            this.mimeType = mimeType;
            settings = new HashMap<String, Object>();
            defaults = new HashMap<String, Object>();
            defaults.put("my setting", "foobar");
        }

        @Override
        public void write(DataSource data, OutputStream output) throws IOException {
            ...
        }

        /**
         * Returns the MIME type.
         * @return MIME type string.
         */
        public String getMimeType() {
            return mimeType;
        }

        @Override
        public <T> T getSetting(String key) {
            if (!settings.containsKey(key)) {
                return (T) defaults.get(key);
            }
            return (T) settings.get(key);
        }

        @Override
        public <T> void setSetting(String key, T value) {
            settings.put(key, value);
        }
    }

Writing a plot exporter
-----------------------

``DrawableWriterFactory`` and ``DrawableWriter``. Either ``BitmapWriter`` or
``VectorWriter``.

Limitations
===========

Due to its early stage of development, GRAL still has several limitations:

- At the moment it's not very fast for large data sets

- The API isn't stable yet, and major changes can happen before version 1.0

- Despite we try our best to ensure code quality there can always be bugs
