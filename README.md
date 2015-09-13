
#Overview

The maindemo code is in `webmvc-app`, a Spring Boot app at port 8080 that can be started via [App.java](webmvc-app/src/main/java/s2gx2015/App.java).

`cross-domain-app` is a simple Spring Boot app at port 9000 that can be started via [CrossDomainApp.java](webmvc-app/src/main/java/s2gx2015/CrossDomainApp.java) and used to make CORS requests to the app at port 8080.

# At a Glance

* `SseEmitterController` -- SSE stream of integers, enabled for CORS via `@CrossOrigin`.
* `StreamingResponseBodyController` -- streaming directly to the `OutputStream`
* `MessagingAdminController` -- start/stop publishing to connected WebSocket clients.
* STOMP/WebSocket endpoint exposed at `/messaging` through config in `App.java`.

# Running

Run `App.java`, visit [http://localhost:8080](http://localhost:8080), experiment with streams.

Run `CrossDomainApp.java`, visit [http://localhost:9000](http://localhost:9000), experiment with streams at 8080. Next modify the CORS configuration at 8080 (e.g. `SseEmitterController` and in `App.java`) and try again.

Run [Client](webmvc-app/src/main/java/s2gx2015/Client.java) to connect to the STOMP / WebSocket stream from Java.
