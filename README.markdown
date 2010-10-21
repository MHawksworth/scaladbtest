# scaladbtest

A new, light-weight database population framework for scala to replace
DBUnit.

##Goals

- Must be really easy to setup, such as having a collection of traits to mix
  into various test frameworks. Make it work out of the box as much as possible,
  preferably with just a Connection object and a reference to either a markup
  language file or a DSL Class. There can be ways to specify how the data is
  loaded and cleaned, but in my experience, every project tends to do the same
  pattern so why not give good defaults.

- DBunit populates rows in a different order than how you write them in the XML
  file. A better method is populate the data based on the order its written so
  that foreign key constraints won't cause problems.

- Should automatically disable foreign key constraints when the first test runs.
  This requires knowledge for each specific database. Since it is common for
  data to have circular references, it is annoying for the user to have to
  disable it manually, especially for databases like hsqldb where the user may
  not be familiar with how to do it. This wastes time and detracts from writing
  tests. This should be done by the framework automatically (a sensible default)
  with an option to disable it.

- Offer a way to use symbols/labels to identity individual records rather than
  use ids. We can get the hash code and make that a unique identifier instead.
  The rational is that when data gets large, it is difficult to keep track of
  all the ids and map their relationships, especially if you haven't worked on
  a project in a long time. Labels are better than arbitrary numbers.

- Make it as fast as possible. This means placing them all in a transaction and
  using batch inserts and flushing them to the database every 20 or so inserts.

- Gives records the ability to inherit default values for a given table, so
  that it's easier to create relevant test data. It also reduces typing and
  cognitive load since you don't have to specify every column.

- Allow for an expression language, to point to other values such as labels to
  avoid duplication as well as a way to create dates easily (this often wastes
  a lot of time as you may not care about the date).

- Allow for the ability to express "NULL" very easily, as this is very common
  to do. DBUnit has to be configured to translate strings like [NULL], which is
  a pain to setup initially. We want to make this very easy to get started for
  users.

- Allow for flexibility in the columns that are specified. With DBUnit, the
  first record must contain all the columns, even when they are just null
  values. This turns out to be a royal pain. Insert statements will be
  individually crafted per record. This should make polymorphic objects (both
  single-table and multi-table) much easier to build test-data for.

- Provide support for nosql databases using the same dsls

## Getting Started is Simple

Scaladbtest uses a simple file format called DBT that is used to write and
load test data. Unlike DBUnit, it does not use XML, depend on Schemas
or other bloated text-based formats. I believe that XML schemas are maintenance
heavy and violate the DRY principle. There must be a better way, and there is!

### Introducing DBT

DBT is scaladbtest's way of specifying test data. When comparing DBT to an
XML file of 500 lines that was used by DBUnit, there was nearly a
**30% Reduction** in the number of characters used to express the same test
data!

There is currently only one way to load data in ScalaDBTest, and we made it a
good! There's suddenly **no more confusion** between dozens of formats to pick
from and how to configure them!

## DBT Usage

Make a file in your tests' resources directory. Usually it's located in
*"src/test/resources"* as part of your project structure if you're using SBT or
Maven.

You can call the DBT file whatever you want, but *"data.dbt"* is a pretty good
name to get started until you start splitting up your files.

## Inserting Records

Here's a simple example that inserts a single record of a country:

    country:
    - country_id: 1, name: "Canada"

Table declarations start with an identifier like *country*, and are then
followed by an immediate *:* character.

From there, you can list a record for this table with *-*'s, followed by a
comma separated list of name/values for the record's data.

Whitespace is not important, so feel free to shape the syntax however you like.

## Numbers

Numbers can be represented with or without quotes. Most databases have no
issues accepting '1' and converting it to the number 1, so you are free to
choose whichever format pleases your eye.

For example, you *could* place the number 1 in quotes, like this:

    country:
    - country_id: "1", name: "Canada"

## Strings

Strings in DBT must be surrounded in quotes. This is to allow for spaces in the
actual text, such as the string *"United States"*:

    country:
    - country_id: 2, name: "United States"

## Booleans

Booleans are represented by the literals *true* and *false*, like this:

    user_account:
    - user_account_id: 1, name: "Ken", is_enabled: true

If you specify the *"true"* instead of *true*, scaladbtest will think you mean
the string "true" instead of the boolean value, which is probably not what you
want.

## Dates

Dates are represented just like strings, so there's no special mechanics for
you to remember with DBT. However, scaladbtest goes one step further by
providing you with an easy way to acess today's date and time with the
expression *$now*.

So instead of specifying a lengthy record declaration like the following...

    task:
    - task_id: 1, name: "My Task", creation_date: "2010-05-15 01:00:00.00"

Scaladbtest will let you specify it like this instead:

    task:
    - task_id: 1, name: "My Task", creation_date: $now

## Null Values

One of the annoying things with DBUnit is that you had instruct the framework
to replace a special string, such as "[NULL]", to an actual null value in Java.
This required a few lines of setup code... and to the unweary, you'd probably
be scratching your head for a bit until you figured it out.

With DBT, you can plant null literals anywhere you like. Here's an example:

    education_facility:
    - id: 1, name: "Star Trek University", province_id: 5, director_id: null

Scaladbtest also doesn't force you to specify ALL the columns int he first
record definition, so if you don't specify a value - it **is** null, unlike
DBUnit, which just pretends you didn't specify the column at all.

    education_facility:
    - id: 1, name: "Scala University", province_id: 2
    - id: 1, name: "Star Trek University", province_id: 5, director_id: 1

In this case, Scaladbtest will correctly assign null to *director_id* for the
1st record, and will assign the value *1* to the second. Finally a tool that
does the right thing with no complex formats or extra work!

## Optional Labels

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

### Labels Generating Primary Keys (Not Yet Implemented)

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
simply refer to the *"Canada".hashCode* in your code. This instantly improves
the readability of your tests, and helps people get back in the groove if
they've been out of a project for far too long.

### Multiple Records

You can specify multiple records that belong to the same table using *-*, like
this:

    province:
    - province_id: 1, name: "Alberta", country_id: 1
    - province_id: 2, name: "British Columbia", country_id: 1
    - province_id: 3, name: "Manitoba", country_id: 1

### Default Columns

Of course, specifying columns that are the same for all of the records is
redundant, and increases cognitive load. A better way is to tell scaladbtest
to use *default values*.

In this next example, Scaladbtest will make sure that all 3 of these records
will have the value *1* for the column *country_id*:

    province:
    ? country_id: 1
    - province_id: 1, name: "Alberta"
    - province_id: 2, name: "British Columbia"
    - province_id: 3, name: "Manitoba"

To specify defaults, you begin the line with a *?* instead of the regular *-*.
There can only be one ? definition per table declaration (although you can have
multiple declarations with the same name - more on this later). It also must be
the first declaration before any records are defined.

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