How to setup and test the auction system:
1. Unzip 2082041s.zip to desired location. I will use “C:\DAS” as location for reference
2. Open 3 terminals and go into “C:\DAS”
3. Start rmiregistry in one of those terminals. Type “start rmiregistry” if you are using windows otherwise “rmiregistry &”. Make sure you have path to the rmiregistry file
4. Compile all files by typing “javac *.java”
5. Run server on a terminal by typing “java Server”
6. Notice that the server bootstrapped 10 auctions.
7. On the other terminals run Client by typing “java Client”
8. Notice that the clients are given instructions on how to create an auction, bid , see available auctions and store auctions.
9. Test those commands and check that server and client respond accordingly
10. Test the fault tolerance of the system by disconnecting the server at any time or the client after he made a bid or created and auction.