# club-manager
Management software for small clubs like our http://www.loscaracoles.ch

Let's see where it leads. For now I'm using it to test some new stuff like event sourcing and springboot.

# Setup
You need an eventstore running (see http://geteventstore.com).

```
docker run --name eventstore-node -it -p 2113:2113 -p 1113:1113 eventstore/eventstore
```

After that you have to run the $by_category projection. This can be done using the EventStore UI. Go to http://localhost:2113, login ('admin' / 'changeit' with the default config) and go to 'Projections'.