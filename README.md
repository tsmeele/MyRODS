# MyRODS
This personal project aims to create a Java library that implements
the iRODS protocol, in particular the native protocol.
Although I have found no record of publication, 
an early release of this protocol has been documented in a DICE-UNC article
(probably May 2008)
by the authors Michael Wan, Reagan Moore and Arcot Rajasekar. 
The article is titled "Distributed Shared Collection Communication Protocol".

By taking the effort to create an implementation, I learned a lot 
about the current state of this protocol, its benefits and limitations.

Ton


## Installation
Some parts of the iRODS protocol require communication via SSL protocol.
Using those features will require your Java Truststore to trust 
the server certificate of the iRODS server that you connect to.
The MyRODS library should only depend on classes provided with the
standard Java Runtime Environment. JRE openjdk version 11.0.23 
has been used to test MyRODS. 

# Architecture
The package *plumbing* takes care of communication protocols, message
(de)serialization and message exchange. Should you be interested
in the details of operation, then include in your main program
a line "Log.setLogLevel(LogLevel.DEBUG);" to keep track of internal
operations.  The class Log is located in package *log*.

The package *irodsStructures* holds internally used data structures 
and similar API message structures. 
The **DataXXX** classes mimic all supported C/C++ iRODS base data types,
such as char, str, int, double, etc. The class DataStruct is used to
hold arbitrary nested compositions of the base data types.
The **RcXXX** classes each mimic a message layout structure as used by an 
iRODS API call. These classes can also specify exceptional behavior
associated with the message, for instance to model multiple client-server
interactions where such is required to complete a single API call.

The package *api* has classes available for use by client applications.
The class **Irods** is the central class in this package. 
It mimics the functions provided by the C/C++ iRODS Client library. 
Each method represents a named iRODS API call.
These methods often require a composed data structure
as input, or return a composed structure as output.
To unburden client applications, the remaining classes represent
prepared data structures. Instances of 'input parameter' classes 
compose a data structure from constructor values specified using
regular Java types. 
The 'output parameter' classes perform an opposite transformation.
More recently, JSON has been introduced in iRODS, for parameter 
exchange in API calls. 
A lightweight JSON library is included in package *json*.

# Bonus
The MyRODS library provides a low-level interface to access
an iRODS server.  Which is great, yet many operations will require you to 
execute not just one but a sequence of API calls.  
The package *high* contains classes that can be used as macro functions, 
they chain a few API calls together. 

The class **Hirods** is a subclass of the class Irods (which was mentioned earlier). 
Hirods supports: 
- PAM and Native scheme authentication.
- Single and multi-threaded data transfers, where multi-threaded transfers use port 1247.

Data transfers are supported between any type of datafile that is an 
implementation of the interface PosixFile.  Currently there are
two implementations of PosixFile: **LocalFile** and **Replica**.  
Hence the equivalent of an 'iput' is achieved by using a LocalFile as 
source object and a Replica as destination object upon a data transfer. 
The opposite is used to realize an 'iget',
while copying replicas across zones is done by using two Replica objects.

Note that the primary purpose of the library is to provide an interface
at the iRODS API level. The classes provided in the high package can
be mixed with regular API calls, they are included for convenience
reasons (and for my research).

Lastly, package *main* contains some classes that demonstrate how to use
the library. 

# Concluding remarks
MyRODS is able to communicate with iRODS release 3 and 4 servers. 
The current set of API classes should be able to support many client 
application use cases. 

A notable limitation is that MyRODS (only) performs parallel data transport over
port 1247, similar to the iRODS Python Client. It does not attempt to 
use any "high" ports.
As a consequence, parallel data transports will require an iRODS server not
older than release 4.2.8.

Each iRODS release may bring forward new API calls. 
This implies some maintenance effort on the MyRODS library. 
As the iRODS server functions are documented partly in-line, 
adding support for new API calls will often require some exploration of 
server source code, to find and implement the 
pack instruction specifications needed to support input and output parameters. 
Should you need support for a particular API call, please let me know.  


