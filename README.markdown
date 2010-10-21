# scaladbtest

A new, light-weight way to load test data into your database before individual
tests run. It is intended to replace DBUnit. It is written in Scala, but you
can use it in Java/Junit4 as well.

## Getting Started is Simple

Scaladbtest uses a simple file format called DBT that is used to write and
load test data. Unlike DBUnit, it does not use XML, depend on Schemas
or other bloated text-based formats. I believe that XML schemas are maintenance
heavy and violate the DRY principle. There must be a better way, and there is!

### Introducing DBT

DBT is scaladbtest's way of specifying test data. When comparing DBT to an
XML file that consisted of 500 lines and was used by DBUnit, there was nearly a
**30% Reduction** in the number of characters used to express the same test
data!

There is *only* one way to load data in ScalaDBTest, and I did my best
to make it a good one! ;) There's suddenly **no confusion** between which
format to use among dozens... or putting up with the hassle of trying to figure
out how to configure them! You also don't have to download a tool to generate
a schema just to make the XML format usable.

### DBT Usage

Make a file in your tests' resources directory. Usually it's located in
*"src/test/resources"* as part of your project structure if you're using SBT or
Maven.

You can name the DBT file whatever you want, but *"data.dbt"* is a pretty good
name to get started until you start splitting up your files.

### Inserting Records

Here's a simple example that inserts a single record for the table *country*:

    country:
    - country_id: 1, name: "Canada"

Table declarations start with an identifier like *country*, and are then
followed by an immediate *:* character.

From there, you can list a record for this table with *-*, followed by a
comma separated list of name/value pairs for the record's data as shown
above.

Whitespace is not important, so feel free to shape the syntax however you like.

#### Inserting Multiple Records for the same Table

You can specify multiple records that belong to the same table by using *-*
characters, like this:

    province:
    - province_id: 1, name: "Alberta", country_id: 1
    - province_id: 2, name: "British Columbia", country_id: 1
    - province_id: 3, name: "Manitoba", country_id: 1

See, you're already saving many characters compared to XML!

### Numbers

Numbers can be represented with or without quotes. Most databases have no
issues accepting the string '1' and converting it to the number 1, so you are
free to choose whichever format pleases your eye.

For example, you *could* place the number 1 in quotes, like this:

    country:
    - country_id: "1", name: "Canada"

### Strings

Strings in DBT must be surrounded in quotes. This is to allow for spaces in the
actual text, such as the string *"United States"*:

    country:
    - country_id: 2, name: "United States"

### Booleans

Booleans are represented by the literals *true* and *false*, like this:

    user_account:
    - user_account_id: 1, name: "Ken Egervari", is_enabled: true
    - user_account_id: 2, name: "George W. Bush", is_enabled: false

If you specify the value *"true"* instead of *true*, scaladbtest will think you
mean the string "true" instead of the boolean value, which is probably not what you
want.

### Dates

Dates are represented just like strings, so there's no special mechanics for
you to remember with DBT. However, scaladbtest goes one step further by
providing you with an easy way to acess today's date and time with the
expression *$now*.

So instead of specifying a lengthy record declaration like the following...

    task:
    - task_id: 1, name: "My Task", creation_date: "2010-05-15 01:00:00.00"

... Scaladbtest will let you specify it like this instead:

    task:
    - task_id: 1, name: "My Task", creation_date: $now

### Null Values

One of the annoying things with DBUnit is that you had to instruct the framework
to transform a special string, such as "[NULL]", to an actual *null* value in
Java. This trick required a few lines of setup code... and to the uninitiated,
you'd probably be scratching your head for a bit until you figured it out.

With DBT, you can plant *null* literals anywhere you like. Here's an example:

    education_facility:
    - id: 1, name: "Star Trek University", province_id: 5, director_id: null

Scaladbtest also doesn't force you to specify ALL the columns in the first
record definition like DBUnit does... so if you don't specify a value - it
**is** null (unlike DBUnit, which just pretends you didn't specify the column
at all.)

    education_facility:
    - id: 1, name: "Scala University", province_id: 2
    - id: 1, name: "Star Trek University", province_id: 5, director_id: 1

In this case, Scaladbtest will correctly assign null to *director_id* for the
1st record, and will assign the value *1* to the second record. Finally, we
have a tool that does the right thing with no complex formats or extra work!

### Optional Labels

You can use a new concept called a label, which can populate columns with data
as well as give yourself an alternate way to search for the data without
remembering the primary key in your java/scala code. You can define a label
like this:

    country:
    -[Canada] country_id: 1, name: $label

*$label* is an expression value. When it comes time to put this value in the
database, scaladbtest will replace it with the exact string you provided as a
label.

In the future, they'll be more functionality to maniupulate labels to make
maintaining and creating test data easier.

#### Labels Generating Primary Keys (Not Yet Implemented)

All the records we've been defining so far have been *anonymous records*. We've
been defining the individual ids on each record.

Imagine, however, that you've been away from the project for 3 months. You know
that there's a country called "Canada" in the database, but you can't quite
remember the ID. In fact, you can't remember what any of the ids relate to
anymore. Trust me, I've been there!

Labels offer a more intuitive way to pull out records from test data:

    country: [pk: country_id]
    -[Canada] name: $label
    -[United States] name: $label
    -[Mexico] name: $label

By using the optional table parameter syntax, we can specify which column
is supposed to be the primary key, and scaladbtest will generate a key using
the hash code of the label for us!

Once the test data is loaded, whenever you want to lookup the record, you can
simply refer to the *"Canada".hashCode* in your code.

**BENEFIT**: This instantly improves the readability of your tests, and helps
people get back in the groove if they've been out of a project for far too long.

### Default Columns

Of course, specifying column values that are the same for all of the records is
redundant, and it increases the cognitive load. A better way is to tell
scaladbtest to use *default values* for all the values that are the same.

In this next example, Scaladbtest will make sure that all 3 of these records
will have the value *1* for the column *country_id*:

    province:
    ? country_id: 1
    - province_id: 1, name: "Alberta"
    - province_id: 2, name: "British Columbia"
    - province_id: 3, name: "Manitoba"

To specify defaults, you begin the line with a *?* instead of the regular *-*.
There can only be one ? definition per table declaration (although you can have
multiple declarations with the same name - more on this later). It must also be
the first declaration under the table name before any records are defined.

#### Overriding Defaults

You can also override the default values if you wish, and scaladbtest will
do the right thing that you would expect:

    province:
    ? country_id: 1, nice_weather: true
    - province_id: 1, name: "British Columbia"
    - province_id: 2, name: "Manitoba", nice_weather: false
    - province_id: 3, name: "New York", country_id: 2

All three of the province records will contain 4 columns. *Manitoba* will still
have bad weather while *New York* will belong to the United States (2) instead
of Canada (1).

This kind of expressive power can really save typing and space when your
actual tests only care about a small subset of columns on a given table.

### Record Insertion Order

DBUnit had a strange limitation where it inserted all the records grouped by the
table they belonged to, regardless of the order you wrote them in. This caused
a lot of headaches because foreign key constraints would get violated very
easily, forcing you to turn them off altogether.

Scaladbtest does not have this problem - it remembers the exact order you wrote
your records in and processes them exactly in that same order! Intuitive, eh?
(Yes, I'm from Canada).

By combining default values and the ability to specify column definitions
multiple times, it is *very easy* to maintain your test data:

    category:
    - category_id: 1, name: "Star Trek Characters"

    category_answer:
    ? category_id: 1
    - category_answer_id: 1, text: "Sisko"
    - category_answer_id: 2, text: "Quark"
    - category_answer_id: 3, text: "Odo"

    category:
    - category_id: 2, name: "Races"

    category_answer:
    ? category_id: 2
    - category_answer_id: 4, text: "Romulan",
    - category_answer_id: 5, text: "Federation"
    - category_answer_id: 6, text: "Klingon"

The data actually looks readable and you can infer the structure of it quite
easily. This is a natural synergy that occured when you group default values
with your own table orderings.

### Comments

Naturally, you might find it useful to insert comments into your file. You can
do so like this:

    # countries
    country:
    # Why did I have to be born in a socialist country for? :(
    - id: 1, name: "Canada"

## Executing scaladbtest from Unit Tests

Setting up scaladbtest is easy - even easier than in DBUnit.

Here's a small snippet of what you need to do to get scaladbunit to work with
Java, JUnit4 and the Spring Framework (taken from a real-world project). The
steps would be real similar for other testing frameworks.

	@Autowired
	protected DataSource dataSource;

	protected ScalaDbTester scalaDbTester;

	@PostConstruct
	public void initialize() {
		scalaDbTester = new ScalaDbTester(dataSource);
	}

	@Before
	public void loadTestData() {
		scalaDbTester.onBefore("src/test/resources/data.dbt");
	}

	@After
	public void cleanTestData() {
		scalaDbTester.onAfter();
	}

As long as you've specified your DBT file in the correct location, everything
should just work out of the box. Before your tests are run, scaladbtest will
parse your dbt files and load everything in the database. You can even specify
multiple DBT files to *scalaDbTester.onBefore()* if you wish.

After your tests are run, scaladbtest will naturally clean out all the data.
It's really that simple! DBUnit requires twice as much code to setup, requires
declaration of exceptions and has lots of other mandatory configuration
doo-dads that should have been optional and set as defaults.

Scaladbtest is truly a better way to load test data!

## Requirements

scaladbunit requires Scala 2.8.0, Spring Framework's 3.0's JDBC package and
Scalaj-collection library if you intended to use it in Java.

## Installation

### From source

Clone the repository from Github:

    git clone git://github.com/egervari/scaladbtest.git

Build the project and create the JAR (requires [sbt](http://code.google.com/p/simple-build-tool/) version 0.7.4 or greater):

    cd scaladbtest
    sbt package

### sbt (Not Yet In Central Repository)

If you're using simple-build-tool, simply add the following line to your project file:

    val scaladbtest = "scaladbtest" % "scaladbtest_2.8.0" % "0.1"

### Maven (Not Yet In Central Repository)

If you're using Maven, add the following to your pom.xml:

    <dependency>
      <groupId>scaladbtest</groupId>
      <artifactId>scaladbtest_${scala.version}</artifactId>
      <version>0.1</version>
    </dependency>

## In Closing

I hope scaladbtest gives you enough reasons to switch from dbunit and provides
you with the best database testing experience on the JVM!