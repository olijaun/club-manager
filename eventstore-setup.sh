#!/bin/sh

# enable the $by-category projection
curl -d -i 'http://localhost:2113/projection/$by_category/command/enable' -u admin:changeit

# create the membership projection which combines membership and contact events
curl -i --data-binary "@membership-projection.json" http://localhost:2113/projections/continuous?name=MembershipProjection%26type=js%26enabled=true%26emit=true%26trackemittedstreams=true -u admin:changeit

