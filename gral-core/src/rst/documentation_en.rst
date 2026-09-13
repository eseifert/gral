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

In order to use the ``RowSubset`` a new class has to be created which
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
measures using the class ``Statistics``. Measures are named by string constants
on that class and computed the first time they are asked for.

Every ``DataSource`` can report measures over all of its values at once, per
column, or per row:

.. code:: java

    // Over all values of the data source
    double meanAll = table.getStatistics().get(Statistics.MEAN);

    // For one column
    double max = table.getColumn(1).getStatistics(Statistics.MAX);

    // One value per column, as a single-row data source
    DataSource means = table.getColumnStatistics(Statistics.MEAN);

Values that are not numbers, and numbers that are ``null``, ``NaN`` or
infinite, are skipped; ``N`` therefore counts only the values that actually
contributed. Asking for an unknown measure, or for one that cannot be computed
because there is no usable value, yields ``NaN`` rather than an error.

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

``MEDIAN``
    The median value which divides the column values in two equal
    halves.

``VARIANCE``
    The sample variance describing the dispersion of the column's values.

``POPULATION_VARIANCE``
    The population variance, i.e. the same sum of squared differences divided
    by ``N`` instead of ``N - 1``.

``SKEWNESS``
    The skewness value describing the asymmetry of the probability
    distribution of the column's values.

``KURTOSIS``
    The excess kurtosis value describing the "peakedness" of the probability
    distribution of the column's values. It is zero for a normal
    distribution.

``QUARTILE_1``
    The value that delimits the lower 25% of all data values.

``QUARTILE_2``
    The value that delimits 50% of all data values. This is the same as
    the median value.

``QUARTILE_3``
    The value that delimits the upper 25% of all data values.

Histograms are a more complex way for aggregating data. In a histogram all
values are assigned to bins that are defined as value ranges. For example all
values from 0 to 5 would fall into the first bin and all values from 5 to 10
into the second. A bin holds the values that are greater than or equal to its
lower limit and smaller than its upper limit; the last bin also includes its
upper limit, so that the largest value is counted.

GRAL has two histogram classes. ``Histogram2D`` aggregates a whole data source
and is a ``DataSource`` itself, so its counts can be handed straight to a plot:

.. code:: java

    // Use 4 equally wide bins per column
    DataSource histogram = new Histogram2D(table, Orientation.VERTICAL, 4);

.. code:: java

    // Use custom breaks for each column
    Number[] breaksCol1 = {1.0, 2.0, 3.0, 4.0, 5.0};
    Number[] breaksCol2 = {1.0, 3.0, 5.0, 7.0, 9.0};
    DataSource histogram = new Histogram2D(table, Orientation.VERTICAL,
        breaksCol1, breaksCol2);

The ``Orientation`` decides in which direction the values are aggregated:
``VERTICAL`` produces one histogram per column, ``HORIZONTAL`` one per row.
Since a histogram has counts but no positions, an ``EnumeratedData`` view is
usually wrapped around it to supply the bin index as a leading column before it
is plotted.

``Histogram`` is the simpler variant. It counts a plain sequence of values, is
not a data source, and is iterated to read the counts:

.. code:: java

    Histogram histogram = new Histogram(table.getColumn(1), 4);
    for (int count : histogram) {
        // one count per bin, in order
    }

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
    Convolution filter = new Convolution(table, kernel, Filter2D.Mode.OMIT, 0, 1);

.. code:: java

    // Create a smoothing kernel with a variance of 2
    Kernel kernel = Kernel.getBinomial(2.0);
    // Filter column 1 and start over for boundary values if necessary
    Convolution filter = new Convolution(table, kernel, Filter2D.Mode.CIRCULAR, 1);

.. code:: java

    // Create a smoothing kernel with a variance of 3
    Kernel kernel = Kernel.getBinomial(3.0);
    // Subtract the original values, which turns smoothing into sharpening
    kernel = kernel.negate().add(new Kernel(1.0));
    // Filter column 1 and repeat boundary values if necessary
    Convolution filter = new Convolution(table, kernel, Filter2D.Mode.REPEAT, 1);

The ``Filter2D.Mode`` decides what stands in for the neighboring values that
are missing at the start and the end of a column: ``OMIT`` produces ``null``,
``ZERO`` substitutes zero, ``REPEAT`` the nearest value, ``MIRROR`` the values
reflected at that end, and ``CIRCULAR`` the values from the other end of the
column.

Kernels are immutable, so ``normalize``, ``negate``, ``add`` and ``mul`` all
return a new kernel. A kernel that does not sum to one scales the data as well
as filtering it; binomial kernels are already normalized.

There is a second, newer filter API alongside this one. Its filters --
``ConvolutionFilter``, ``MedianFilter`` and ``Accumulation`` -- implement
``Filter`` and are plain sequences of values rather than data sources. They need
no mode, because instead of substituting values at the boundaries they simply
yield fewer results than they consume:

.. code:: java

    for (double smoothed : new ConvolutionFilter<>(values, kernel)) {
        // m - n + 1 values for m inputs and a kernel of n weights
    }

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
  used to display the area is ``PlotArea``, correspondingly.

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

GRAL has five plot types: xy-plot, bar plot, box-and-whisker plot, pie plot,
and raster plot. The first four of these cover many more chart types than their
names suggest, because what a plot looks like is decided by the renderers set on
its series rather than by the plot class: an xy-plot becomes a line plot, a
scatter plot, a bubble plot or an area plot depending on which renderers it is
given, and a pie plot becomes a doughnut plot by setting an inner radius.

All plot types except ``PiePlot`` derive from ``XYPlot``.

XY-Plot
~~~~~~~

``XYPlot`` is the most common plot type. Each series contributes one set of
data points, whose first mapped column is the x coordinate and whose second is
the y coordinate.

.. code:: java

    Plot plot = new XYPlot(series1, series2);

A series added this way starts out with a point renderer only, so the plot shows
marks but no connecting line. Lines and filled areas are switched on by giving
the series a renderer, which is what turns the same plot class into a line plot
or an area plot:

.. code:: java

    XYPlot plot = new XYPlot(series1);
    plot.setLineRenderers(series1, new DefaultLineRenderer2D());
    plot.setAreaRenderers(series1, new DefaultAreaRenderer2D());

Each of the three layers accepts several renderers for one series, which are
drawn in the order given; that is how effects like drop shadows are built. Areas
are drawn first, then lines, then points.

An ``XYPlot`` offers four axes by name: ``AXIS_X`` and ``AXIS_Y``, which are
created and mapped for every series that is added, and the secondary axes
``AXIS_X2`` and ``AXIS_Y2``, which are only drawn once a series is mapped to
them:

.. code:: java

    plot.setAxis(XYPlot.AXIS_Y2, new Axis(0.0, 100.0));
    plot.setAxisRenderer(XYPlot.AXIS_Y2, new LinearRenderer2D());
    plot.setMapping(series2, XYPlot.AXIS_X, XYPlot.AXIS_Y2);

Legends
^^^^^^^

Legends explain the series of a plot by pairing a symbol with a description.
Every plot already builds and fills its own legend; it only has to be switched
on, since it is hidden by default. The text of an entry is the name of the data
source, so naming the series is the step that is easy to forget:

.. code:: java

    DataSeries series = new DataSeries("Temperature", table, 0, 1);
    XYPlot plot = new XYPlot(series);
    plot.setLegendVisible(true);

A legend can be oriented vertically (the default) or horizontally, and placed at
any of the nine positions of ``Location``:

.. code:: java

    plot.setLegendLocation(Location.NORTH_EAST);
    plot.getLegend().setOrientation(Orientation.HORIZONTAL);

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

- x position of the box-and-whisker element
- y position of the center bar (e.g. median)
- y position of the lower whisker (e.g. minimum)
- lower edge of the box (e.g. first quartile)
- upper edge of the box (e.g. third quartile)
- y position of the upper whisker (e.g. maximum)

A utility method of ``BoxPlot`` generates exactly that layout from raw
observations, producing one box per *column* of the original data source:

.. code:: java

    DataSource series = BoxPlot.createBoxData(data);
    Plot plot = new BoxPlot(series);

Supplying the six columns yourself is what allows other summaries, for example
whiskers at the 5th and 95th percentile instead of at the extremes.

Pie plot
~~~~~~~~

Pie plots are circles divided into sectors to illustrate the proportions of the
corresponding data values. GRAL's class ``PiePlot`` is used to create this type
of plot. It does not derive from ``XYPlot`` and has no visible axes; one row of
a single-column data source becomes one slice.

The data source has to be prepared with a utility method that computes the
running total the renderer needs:

.. code:: java

    DataSource series = PiePlot.createPieData(data);
    PiePlot plot = new PiePlot(series);

A slice always covers a positive part of the pie, so the values are taken by
absolute value when the sizes are computed; the sign decides only whether a
slice is filled or left empty. Setting an inner radius turns the pie into a
doughnut:

.. code:: java

    PieSliceRenderer slices = (PieSliceRenderer) plot.getPointRenderer(series);
    slices.setInnerRadius(0.4);

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

A utility method of ``RasterPlot`` converts a matrix of values -- one where the
position of a value is its position in the table rather than a pair of
coordinates -- into that form:

.. code:: java

    DataSource series = RasterPlot.createRasterData(data);
    RasterPlot plot = new RasterPlot(series);

The conversion also rescales the values to the range from 0 to 1, so the color
mapping does not have to know the range of the original data:

.. code:: java

    plot.setColors(new HeatMap());

An image file can be read straight into the matrix form with ``ImageReader``,
which is how a bitmap is displayed as a raster plot.

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

    Plot plot = new PiePlot(PiePlot.createPieData(data));
    Paint gradient = new LinearGradientPaint(
        0f,0f,                 // Coordinates of gradient start point
        1f,0f,                 // Coordinates of gradient end point
        new float[] {0f, 1f},  // Relative fractions
        new Color[] {Color.GRAY, Color.WHITE}  // Gradient colors
    );
    plot.setBackground(gradient);

The ``PlotArea`` is the region in which the data itself is drawn, i.e. the plot
without its title, legend and axis labels. Each plot type has its own plot area
class, so it is fetched from the plot rather than constructed. In the following
example you can see how to hide the plot area frame completely.

.. code:: java

    PlotArea plotArea = plot.getPlotArea();
    plotArea.setBackground(null);
    plotArea.setBorderStroke(null);

The space the plot keeps free around the plot area -- for the tick labels and
the axis titles -- is set as insets on the plot itself. The arguments start at
the top and go clockwise, so the left inset, usually the largest because it has
to hold the y tick labels, is the second value:

.. code:: java

    plot.setInsets(new Insets2D.Double(20.0, 60.0, 40.0, 20.0));

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

Note the division of labour: an ``Axis`` holds nothing but the displayed value
range, while the ``AxisRenderer`` owns the scale, the ticks and the drawing.
Because the scale lives in the renderer, switching from a linear to a
logarithmic axis means substituting one object. Two implementations ship with
GRAL: ``LinearRenderer2D`` for a linear scale, which is what plots use by
default, and ``LogarithmicRenderer2D`` for a base-10 logarithmic one.

.. code:: java

    XYPlot plot = new XYPlot(seriesLog, seriesLin);
    AxisRenderer axisRendererX = new LogarithmicRenderer2D();
    axisRendererX.setLabel(new Label("Logarithmic data"));
    plot.setAxisRenderer(XYPlot.AXIS_X, axisRendererX);

A logarithm is only defined for positive values, so the range of a logarithmic
axis must not be negative; converting a value against a negative bound throws an
``IllegalStateException``.

The tick labels are produced by a ``java.text.Format``, so an axis of
timestamps can be labeled with dates:

.. code:: java

    AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
    Format dateFormat = DateFormat.getTimeInstance();
    axisRendererX.setTickLabelFormat(dateFormat);

Individual positions can also be labeled explicitly. Custom ticks are drawn in
addition to the regular ones:

.. code:: java

    Map<Double, String> labels = new HashMap<>();
    labels.put(2.0, "Doubled");
    labels.put(1.5, "One and a half times");
    axisRendererX.setCustomTicks(labels);

The renderer also converts between data values and screen positions, which is
what a program needs when it has to place something of its own on the plot:

.. code:: java

    Axis axisX = plot.getAxis(XYPlot.AXIS_X);
    // Distance along the axis, measured from its start
    double pixels = axisRendererX.worldToView(axisX, 4.2, false);
    // And back again
    Number value = axisRendererX.viewToWorld(axisX, pixels, false);

The last argument decides what happens to values outside the axis range: with
``false`` the result is clamped to the ends of the axis, with ``true`` the
transform continues beyond them.

Customizing points
~~~~~~~~~~~~~~~~~~

The display of data points in a plot is done by instances of ``PointRenderer``.
A point renderer defines the shape, the color, the size, and even the position
of each point. Before writing one, check whether configuring an existing
renderer is enough:

.. code:: java

    DefaultPointRenderer2D points = new DefaultPointRenderer2D();
    points.setShape(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
    points.setColor(Color.RED);
    points.setValueVisible(true);
    plot.setPointRenderers(series, points);

Shapes are expressed in the coordinate system of the point, with (0, 0) at the
data point itself, which is why the ellipse above is offset by half its size in
order to be centered.

A custom renderer implements two methods:
``Shape getPointShape(PointData)`` returns the vector shape of a specified data
point, and ``Drawable getPoint(PointData, Shape)`` returns a drawable component
which then renders the point. Everything about the current point -- the row, the
column holding the value, and the axes needed to project it -- arrives in the
``PointData`` argument. One renderer instance serves every row of a series, so
it must not keep per-point state.

The class ``AbstractPointRenderer`` implements the interface ``PointRenderer``
and provides everything that is necessary to manage settings and draw basic
elements, so custom renderers usually derive from it or from an existing
implementation.

In the following example you can see how to implement a simple renderer. Note
that a point renderer's color is a ``ColorMapper`` rather than a ``Paint``, so
that it can depend on the value:

.. code:: java

    public class SimplePointRenderer extends DefaultPointRenderer2D {
        @Override
        public Drawable getPoint(final PointData data, final Shape shape) {
            Drawable drawable = new AbstractDrawable() {
                @Override
                public void draw(DrawingContext context) {
                    ColorMapper colors = SimplePointRenderer.this.getColor();
                    Paint paint = colors.get(data.index);
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
display the line. The data points arrive already projected, so a line renderer
works in screen coordinates and never touches the axes. Splitting shape and
drawable apart is what allows the shape to be reused, which is how an area
renderer builds on a line.

The ``punch`` method used in the example below is provided by
``AbstractLineRenderer2D``: it cuts a hole out of the shape around every data
point, so that the point marks stay visible through the line.

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

Every area renderer has to implement two methods:
``Shape getAreaShape(List<DataPoint>)`` returns a vector shape for the area, and
``Drawable getArea(List<DataPoint>, Shape)`` returns a drawable component to
display the area. As with lines, the data points arrive already projected. Areas
are drawn before lines and points, so a translucent fill does not hide the marks
on top of it.

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

Showing a plot on screen
~~~~~~~~~~~~~~~~~~~~~~~~

A plot is a ``Drawable``, not a ``java.awt.Component``, so it is put on screen
through an adapter. The Swing adapters are not part of the library core; they
are the module ``gral-swing``, which has to be on the class path in addition to
``gral-core``. ``DrawablePanel`` is a ``JPanel`` that displays one drawable and
keeps its bounds in step with its own size:

.. code:: java

    JFrame frame = new JFrame();
    frame.getContentPane().add(new DrawablePanel(plot));
    frame.setSize(800, 600);
    frame.setVisible(true);

``InteractivePanel`` adds the behavior expected of a plot on screen: dragging
pans, the mouse wheel and a double click zoom, and a right click opens a context
menu offering to reset the view, to print, and to export to any supported file
format.

.. code:: java

    frame.getContentPane().add(new InteractivePanel(plot));

Zooming and panning are applied through the ``Navigator`` of the plot, which
works by changing the ranges of its axes. Navigators can be driven directly, and
two of them can be connected so that several plots move together:

.. code:: java

    Navigator navigator1 = plot1.getNavigator();
    Navigator navigator2 = plot2.getNavigator();
    navigator1.connect(navigator2);

    // Restrict interaction to one direction, or switch it off entirely
    navigator1.setDirection(XYPlot.XYNavigationDirection.HORIZONTAL);
    navigator1.setPannable(false);

    // Back to the default state
    navigator1.reset();

Showing a plot in JavaFX
~~~~~~~~~~~~~~~~~~~~~~~~

The module ``gral-javafx`` is the same bridge for JavaFX. ``DrawableCanvas`` is
a resizable ``Canvas`` that paints one drawable, and ``InteractiveCanvas`` adds
dragging to pan and scrolling or double-clicking to zoom:

.. code:: java

    StackPane root = new StackPane(new InteractiveCanvas(plot));
    stage.setScene(new Scene(root, 800.0, 600.0));
    stage.show();

The plot is built exactly as it is for a Swing window, for a PNG or for a PDF.
Painting goes through `FXGraphics2D <https://github.com/jfree/fxgraphics2d>`__,
a ``Graphics2D`` implementation that writes to a JavaFX canvas.

JavaFX itself is not a dependency of the module: the OpenJFX artifacts are
specific to a platform, and which version to run on is the decision of the
application, which has a JavaFX runtime anyway.

Zooming and panning go through the same ``Navigator`` as in Swing, so the
navigators of a JavaFX view and a Swing view of the same data can be connected
and will move together.

Showing a plot in other toolkits
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Neither adapter is privileged. A ``Drawable`` paints itself into a
``java.awt.Graphics2D`` and knows nothing else, so every toolkit that has a
``Graphics2D`` implementation can display a GRAL plot in about ten lines: set
the bounds of the drawable to the area to fill, wrap the graphics in a
``DrawingContext`` and call ``draw``.

For Compose Desktop, which draws through Skia, that implementation is
`SkikoGraphics2D <https://github.com/jfree/skikographics2d>`__. It wraps the
``org.jetbrains.skia.Canvas`` that a Compose ``Canvas`` hands out:

.. code:: kotlin

    @Composable
    fun PlotCanvas(plot: Drawable, modifier: Modifier = Modifier) {
        Canvas(modifier) {
            drawIntoCanvas { canvas ->
                plot.bounds = Rectangle2D.Double(
                    0.0, 0.0, size.width.toDouble(), size.height.toDouble())
                plot.draw(DrawingContext(SkikoGraphics2D(canvas.nativeCanvas)))
            }
        }
    }

This is a documented example rather than a module of the build: Compose Desktop
is built with the Kotlin and Compose Gradle plugins, and SkikoGraphics2D is not
published to Maven Central, so it has to be built from its sources. What the
example shows is that nothing beyond a ``Graphics2D`` is needed.

Exporting plot images
~~~~~~~~~~~~~~~~~~~~~

Writing a plot to a file works like writing data: a writer is fetched from a
factory by MIME type. ``DrawableWriterFactory`` supports the bitmap formats
``image/png``, ``image/jpeg``, ``image/bmp``, ``image/gif`` and
``image/vnd.wap.wbmp``, and the vector formats ``image/svg+xml``,
``application/pdf`` and ``application/postscript`` (EPS).

.. code:: java

    XYPlot plot = new XYPlot(data);
    DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/svg+xml");
    double width = 320.0, height = 240.0;
    try (OutputStream file = new FileOutputStream("xyplot.svg")) {
        writer.write(plot, file, width, height);
    }

The writer sets the bounds of the plot to the requested size for the duration of
the call and restores them afterwards, so the same plot object can be shown on
screen and exported at a different size without interference. No window and no
display are involved, which makes this the way to produce figures on a headless
machine.

The vector formats need the VectorGraphics2D library on the runtime class path.
GRAL loads it reflectively, so it compiles and runs without it; a vector format
then fails at the moment it is written, not at start-up.

Extending GRAL
==============

GRAL can be extended in numerous ways to better suit your needs. For example,
you can write your own plot types, line types, axes, or data exchange plug-ins.
The following chapter shows you how to use GRAL's application programming
interface to tailor it for your requirements.

Writing a new plot type
-----------------------

A plot derives from ``AbstractPlot``, which already provides the axes, the
column-to-axis mapping, the title, the legend and the layout. Two things have to
be supplied: a ``PlotArea`` subclass that knows how to draw the data, and the
axes the plot uses. ``AbstractPlot`` implements ``DataListener`` and registers
itself on every data source that is added, so a plot is notified of changes to
its data without any further work.

.. code:: java

    public class MyPlot extends AbstractPlot {
        /** Name of the horizontal axis of this plot type. */
        public static final String AXIS_X = "x";

        public MyPlot(DataSource... data) {
            super(data);
            setPlotArea(new MyPlotArea(this));
            for (DataSource source : data) {
                setMapping(source, AXIS_X);
            }
            createDefaultAxes();
            autoscaleAxes();
        }

        @Override
        protected void dataChanged(DataSource source, DataChangeEvent... events) {
            super.dataChanged(source, events);
            autoscaleAxes();
        }
    }

Note that ``DataListener`` has three methods -- ``dataAdded``, ``dataUpdated``
and ``dataRemoved``, each taking a ``DataSource`` and a varargs array of
``DataChangeEvent`` -- so it cannot be implemented with a lambda. Overriding the
``dataChanged`` hook of ``AbstractPlot``, as above, covers all three at once.

Before reaching for a new plot type, consider whether a new ``PointRenderer``
is enough: ``BarPlot`` and ``BoxPlot`` are both ordinary xy-plots that differ
from a scatter plot only in the renderer they use.

Writing a data importer
-----------------------

A reader derives from ``AbstractDataReader``, which handles the MIME type and
the settings, and implements the single method ``read``. Its capabilities are
announced in a static initializer, which is how the factory can list the format
without loading a file first:

.. code:: java

    public class MyReader extends AbstractDataReader {
        static {
            addCapabilities(new IOCapabilities(
                "My Format",
                "My custom file format",
                "application/x-myformat",
                new String[] {"myf"}
            ));
        }

        public MyReader(String mimeType) {
            super(mimeType);
            setDefault("my setting", "foobar");
        }

        @Override
        public DataSource read(InputStream input, Class<? extends Comparable<?>>... types)
                throws IOException {
            String setting = this.<String>getSetting("my setting");
            DataTable data = new DataTable(types);
            // Read the values from the stream and add them to the table ...
            return data;
        }
    }

The class alone is not enough: the factory builds its mapping from properties
files on the class path, so the format also has to be registered by adding a
line to ``datareaders.properties``::

    application/x-myformat=com.example.MyReader

All copies of that file that are visible on the class path are read, so a
separate JAR can contribute formats without any change to GRAL itself. After
that, the new format is available like any other:

.. code:: java

    DataReader reader = DataReaderFactory.getInstance().get("application/x-myformat");

Writing a data exporter
-----------------------

A writer works the same way, deriving from ``AbstractDataWriter`` and
implementing ``write``:

.. code:: java

    public class MyWriter extends AbstractDataWriter {
        static {
            addCapabilities(new IOCapabilities(
                "My Format",
                "My custom file format",
                "application/x-myformat",
                new String[] {"myf"}
            ));
        }

        public MyWriter(String mimeType) {
            super(mimeType);
            setDefault("my setting", "foobar");
        }

        @Override
        public void write(DataSource data, OutputStream output) throws IOException {
            // Write the values of the data source to the stream ...
        }
    }

It is registered in ``datawriters.properties``::

    application/x-myformat=com.example.MyWriter

Writing a plot exporter
-----------------------

Writers for whole plots implement ``DrawableWriter`` and are looked up through
``DrawableWriterFactory``. The two shipped implementations show the two
approaches: ``BitmapWriter`` renders into a ``BufferedImage`` and encodes it
with ``javax.imageio``, while ``VectorWriter`` passes a ``Graphics2D`` that
records its commands and turns them into a vector document.

The recurring detail is the bounds: a writer sets the bounds of the drawable to
the requested size, draws it, and restores the previous bounds, so that
exporting does not disturb a plot that is also on screen.

.. code:: java

    public class MyDrawableWriter extends IOCapabilitiesStorage implements DrawableWriter {
        static {
            addCapabilities(new IOCapabilities(
                "My Format",
                "My custom vector format",
                "application/x-mydrawable",
                new String[] {"myd"}
            ));
        }

        private final String mimeType;

        public MyDrawableWriter(String mimeType) {
            this.mimeType = mimeType;
        }

        @Override
        public String getMimeType() {
            return mimeType;
        }

        @Override
        public void write(Drawable d, OutputStream destination,
                double width, double height) throws IOException {
            write(d, destination, 0.0, 0.0, width, height);
        }

        @Override
        public void write(Drawable d, OutputStream destination,
                double x, double y, double width, double height) throws IOException {
            Rectangle2D boundsOld = d.getBounds();
            d.setBounds(x, y, width, height);
            try {
                // Draw the plot and write the result to the stream. Check
                // context.getTarget() in your renderers if the distinction
                // between raster and vector output matters.
                d.draw(new DrawingContext(graphics, Quality.QUALITY, Target.VECTOR));
            } finally {
                d.setBounds(boundsOld);
            }
        }
    }

The format is registered in ``drawablewriters.properties``::

    application/x-mydrawable=com.example.MyDrawableWriter

Note that the factory instantiates a writer through a constructor taking the
MIME type as its only argument, so that one class can serve several formats;
that constructor has to exist.

Limitations
===========

GRAL has not reached version 1.0, and a few limitations are worth knowing about
before you commit to it:

- Rendering is not optimized for very large data sets. Every visible point is
  projected and drawn on each repaint, so plots of hundreds of thousands of
  points are noticeably slow to pan and zoom.

- The API is not stable yet. Names and signatures can still change between
  releases, and two generations of the filter API currently coexist.

- Sorting a data source does not fire a change notification, so a plot that is
  already on screen has to be repainted by the caller.

- Despite our best efforts to ensure code quality, there can always be bugs.
