
(Work In Progress. DO NOT USE AS OF YET.)

# StreamStuff Admin

1. Alerts

2. Logs

3. Deployment

4. Jobs Viewer


# Usage

_package_ and then run the _fatjar_ :

````
> mvn package
> java -jar target/vertx-reverse-proxy-3.3.0-SNAPSHOT.jar
````

Docker containers are discovered using docker labels, for example:

````
docker run --rm -p 8082:8080 -l service.type=http.endpoint -l service.route=/hello ehazlett/docker-demo
````

Then backend server will be accessible on the proxy server under the `/hello` route.


## Behavior notes
