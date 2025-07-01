
# manage-transit-movements-cancellation-frontend

This service allows a user to cancel a transit movement.

Service manager port: 10122

### Testing

Run unit tests:
<pre>sbt test</pre>
Run integration tests:
<pre>sbt it/test</pre>
Run accessibility linter tests:
<pre>sbt A11y/test</pre>

### Running manually or for journey tests

<pre>
sm2 --start CTC_TRADERS_P5_ACCEPTANCE
sm2 --stop MANAGE_TRANSIT_MOVEMENTS_CANCELLATION_FRONTEND
sbt run
</pre>

We then need to post an IE015 message followed by a message type that has the action to cancel a declaration such as an IE028 (MRN Allocated) or an IE928 (Positive Acknowledge).

From the `/view-departure-declarations` page click the `Cancel declaration` link for the relevant movement.

Completing the journey will submit an IE014 (Declaration Invalidation Request) message.

### Feature toggles

The following features can be toggled in [application.conf](conf/application.conf):

| Key                             | Argument type | sbt                                                           | Description                                                                                                                                                                                    |
|---------------------------------|---------------|---------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `feature-flags.phase-6-enabled` | `Boolean`     | `sbt -Dfeature-flags.phase-6-enabled=true run`                | If enabled, this will trigger customs-reference-data to retrieve reference data from crdl-cache.                                                                                               |
| `trader-test.enabled`           | `Boolean`     | `sbt -Dtrader-test.enabled=true run`                          | If enabled, this will override the behaviour of the "Is this page not working properly?" and "feedback" links. This is so we can receive feedback in the absence of Deskpro in `externaltest`. |
| `banners.showUserResearch`      | `Boolean`     | `sbt -Dbanners.showUserResearch=true run`                     | Controls whether or not we show the user research banner.                                                                                                                                      |
| `play.http.router`              | `String`      | `sbt -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes run` | Controls which router is used for the application, either `prod.Routes` or `testOnlyDoNotUseInAppConf.Routes`                                                                                  |


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

