How to setup and test the auction system:
1. Unzip 2082041s.zip to desired location. I will use “C:\DAS” as location for reference
2. Open 3 terminals and go into “C:\DAS”
3. Compile all files by typing “javac *.java”
4. Run server on a terminal by typing “java Server”
5. Notice that the server bootstrapped 10 auctions.
6. On the other terminals run Client by typing “java Client”
7. Notice that the clients are given instructions on how to create an auction, bid , see available auctions and store auctions.
8. Test those commands and check that server and client respond accordingly
9. Test the fault tolerance of the system by disconnecting the server at any time or the client after he made a bid or created and auction.