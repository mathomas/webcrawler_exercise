Web Crawler Exercise
====================
My basic implementation of the "web crawler" exercise.  Here are the specs I received.

> Your task is to write a simple web crawler in a language of your choice.
The crawler should be limited to one domain. Given a starting URL – say wiprodigital.com - it should visit all pages within the domain, but not follow the links to external sites such as Google or Twitter.
The output should be a simple site map, showing links to other pages under the same domain, links to static content such as images, and to external URLs.

Running
-------
The exercise is built using Java version 1.8.  I use Java 1.8 features, so you _gotta_ have 1.8.

The exercise is built with Maven, so the easiest way to run it is under Maven.  I'm using the "Maven Wrapper"
approach, so in theory the below should work just fine even if you don't have Maven (yet).

 Build/test the crawler project:
 
 ```./mvnw test```
 
 Run the crawler:
 
 ```./mvnw compile exec:java -Dexec.args="<your URL here>"```
 
(note that the 'compile' is only needed if you haven't already run a compile, test, or install)

The first time you do the above, the internet will be downloaded.  Such is the nature of modern
software development.

And yes, the ugly Maven command-line syntax -- sorry.  Anyway, if you leave out the args when running, 
the crawler defaults to crawling my website.


Assumptions
-----------
There was no definition of "site map", so I took it literally for simplicity's sake :-).
I just crawl all the pages from the first page out, and collect the visited pages' URIs
in a Map, with related Set of the page's linked resource URIs as the values.  I then print
out the Map in a simple/reasonable way.  Would be interesting to plug in different output formats
for the display of the map.  A dotfile would be _very_ interesting, so the map could be graphed.
I considered doing it, but decided to hold off due to time, and due to the fact that one would
have to have the appropriate tools installed to do anything with it.

There was no specific requirement for handling HTTP errors, etc.  So, I took the easy way out
and quietly returned no resources for any oddball HTTP or content case.  Seemed OK at the time.

Approach
--------
My approach was to first do a sketch/spike, since I haven't done any scraping, and needed
to play with the concepts and tools.  I decided to use an external library (was not 
disallowed by the rules), to do the page scraping.

I then started again to create a more testable approach.  It's still pretty basic.  I made 
some very simplifying assumptions along the way, and commented in appropriate places.

Tuning
------
Doing scraping serially is pretty slow.  So I added an optional parallel() call in the 
code that recurses over the linked sub-pages.  This makes the crawler go much faster 
(nearly 50% improvement in my testing). However, I did nothing to make it threadsafe 
(the site Map that is being built would be the shared resourceReference in this case).  It's 
probably not the end of the world for this example, but definitely not perfection. 
