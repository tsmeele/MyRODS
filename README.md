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

Ton

P.S: This is work in progress!

## Installation
Some parts of the iRODS protocol require communication via SSL protocol.
Using those features will require your Java Truststore to trust 
the server certificate of the iRODS server that you connect to.

# Architecture
The abstract Class Data in package irodsType accommodates arbitrary compositions
of iRODS data types such as Int, Double, String etc. Base data types are
implemented as subclasses of Data.

Communication mechanisms between iRODS client and server are classes in
the package plumbing. For instance a Session manages the client connection
to an iRODs server, while IrodsMessage contains the fixed set of data structures
that is exchanged (although being packed into IrodsPackedMessage prior to transfer).

The class PackMap manages a concrete list of Pack Instructions that describe 
predefined data structures used and expected in communications. The instructions
are used to unpack a packed binary message into an agreed-upon data structure.

The package Api is a collection of classes that represent predefined
requests from client to server along with the server reply.  
This allows client applications to interact with an
iRODs server without the need to have detailed knowledge of message structures.
  


