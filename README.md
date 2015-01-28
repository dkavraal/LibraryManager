LibraryManager
==============

LibraryManager test project.


![Screenshot](https://raw.githubusercontent.com/dkavraal/LibraryManager/master/SS_LibMan.PNG)

##Purpose
A simple library manager, with support of adding, deleting, updating and removing to/from a Mongo collection through Spring MVC also using Angular. Tests with Mockito FW

##Data
Data is on MongoDB and managed through DAO level manipulations via Spring interfaces

##Services
Services use a simple one-paged JSP beneath

##Controllers
In terms of service, controlling utility is heavily on Java app side. AngularJS is only manipulating data for view.

#Outcome
For a long time now, mockito is the best living thing/lib/imagination/idea I have ever came across.

#Missing Points
* update/remove item on the web interface haven't been added
* MongoDB connection pooling is not investigated fully
* MongoDB connection controller should be covering old and new libraries in a better understanding
* After inserting a new item, results should be refreshed. Despite using emit/broadcast, couldn't manage it to work
* Input masks should better be managed centrally with a possible solution out there.
* Input masks should have a better UX to inform user before the "event" occurs rather while editing
* Rows should have step-color to make it easier to read
* IDs of the items are not important mostly, but couldn't decide to remove or leave. Just left.
* Paging is a must. There should be an indicator to show how many books there are in the library and which page is shown
*  ...
