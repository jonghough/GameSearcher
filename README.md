# GameSearcher
Android application to search http://thegamesdb.net/ public API for games

Sample application that uses thegamesdb.net API to search for games and show game information based on user searches. Performs most of the logic using JavaRx and uses SimpleXML to parse the API data.
Lazy loads images (fan art or box art) for the results of each search and allows users to filter results by platform or release date.

## Dependencies
* AndroidRx (Reactive Java)
* SimpleXML (thegamesdb.net's API is all XML)
* okHttp (for http requests )


