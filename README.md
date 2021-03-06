
# manage-transit-movements-cancellation-frontend

This service allows a user to cancel a transit movement.

Service manager port: 10122

### Testing

Run unit tests:
<pre>sbt test</pre>  
Run integration tests:  
<pre>sbt it:test</pre>  
or
<pre>sbt IntegrationTest/test</pre> 

### Running manually or for journey tests

Note: The cancellation frontend is tested as part of the departures journey tests.

    sm --start CTC_TRADERS_PRELODGE -r
    sm --stop MANAGE_TRANSIT_MOVEMENTS_CANCELLATION_FRONTEND
    sbt run

If you hit an entry point before running the journey tests, it gets the compile out of the way and can help keep the first tests from failing.  

e.g.: http://localhost:10122/manage-transit-movements/cancellation/0/confirm-cancellation

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

