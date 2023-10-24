
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

    sm --start CTC_TRADERS_P5_ACCEPTANCE -r
    sm --stop MANAGE_TRANSIT_MOVEMENTS_CANCELLATION_FRONTEND
    sbt run

We then need to post an IE015 (Declaration Data). From the `/view-departure-declarations` page click the `Cancel declaration` link for the relevant movement.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

