# MyRODS
This personal project aims to create a Java library that implements
the iRODS protocol, in particular the native protocol.
Although I have found no record of publication, 
an early release of this protocol has been documented in a DICE-UNC article
(probably May 2008)
by the authors Michael Wan, Reagan Moore and Arcot Rajasekar. 
The article is titled "Distributed Shared Collection Communication Protocol".

By taking the effort to create an implementation, I intend to learn 
more about the current state of this protocol, its benefits and limitations.

This is work in progress!

Ton


## Installation
Some parts of the iRODS protocol require communication via SSL protocol.
Using those features will require your Java Truststore to trust 
the server certificate of the iRODS server that you connect to.

# Architecture
The package *plumbing* takes care of communication protocols, message
(de)serialization and message exchange. Should you be interested
in the details of operation, then include in your main program
a line "Log.setLogLevel(LogLevel.DEBUG);" to keep track of internal
operations.  The class Log is located in package *log*.

The package *irodsDataTypes* includes all supported iRODS base data types,
such as char, str, int, double, etc. These types can be composed into
data structures just like the base data type struct.
The data type classes are primarily used internally by MyRods itself, although
unavoidably applications of the library will sometimes need to work
with them as well. 

The package *api* has classes that provide for the client-side api calls 
and attempts to closely match the functions provided in 
iRODS C++ clientlibrary. For each supported API call there exists a 
separate class.

The package *apiDataStructures* lists classes that provide data schemes used
by the API classes. These classes are all subclasses of DataStruct. They
represent composed data structures needed as input argument to an API, or returned
as output of an API. The constructor allows a developer to populate the
input-scheme classes with concrete values of standard Java types.
More recently, JSON has been introduced in iRODS for parameter exchange in API calls. 
A lightweight JSON library is included in package *json*.

The MyRods iRODS API library provides a low-level interface to access
an iRODS server.  Which is great, yet many operations will require you to 
execute not just one but a sequence of API calls.  
The package *high* contains classes that can be used as macro functions, 
they chain a few API calls together. 

For instance it contains class Session with method "pamLogin"
that can be used to have MyRODS execute the chain of API calls needed to
connect to a server and arrange for a PAM scheme based authentication, resulting
in an authenticated IrodsSession.

Another example is the abstract class DataTransfer. Its subclasses implement 
single and multithreaded data transfers over port 1247 between any type of datafile
that is an implementation of the interface PosixFile.  Currently there are
two implementations of PosixFile: LocalFile and Replica.  Hence the equivalent
of an 'iput' is achieved by using a LocalFile as source object and a Replica as 
destination object to perform a DataTransfer. And vice versa to realize an 'iget',
while copying replicas across zones is done by using two Replica objects.

Note that the primary purpose of the library is to provide an interface
at the iRODS API level. The classes provided in the high package can
be mixed with regular API calls, they are included for convenience
reasons (and for my research).

Lastly, package *main* contains some classes that demonstrate how to
develop applications that use the library. 

# Concluding remarks
Well, this library certainly is not finished yet. Building and deploying the 
demonstrator examples and the macro functions have helped me greatly to iron out
bugs in the protocol implementation. I think the plumbing is pretty solid by now,
but no guarantees, okay? Please let me know if you suspect a bug.  

The current set of API classes will support basic application use cases. 
I plan to gradually add support for more API calls. 
As the iRODS server functions are documented partly inline, this often requires 
some time to explore server source code, and find and implement the 
pack instruction specifications needed to support input and output parameters.     


