---
--- Created by oliver.
--- DateTime: 03/06/18 12:24
---

-- keys
local eventsKey = KEYS[1]
local streamKey = KEYS[2]
-- argv
local streamId = ARGV[1]
-- -2 means 'do not check version'
-- -1 means 'there must not be a stream yet'
-- 0 means 'stream exists but no event is stored yet'
local expected = tonumber(ARGV[2])
local numberOfEvents = tonumber(ARGV[3])
-- ARGV[4] and following are events

--local decodedEvent = cjson.decode(ARGV[5]);
--if 1 == 1 then
--    return { 'decoded event', decodedEvent.eventId }
--end
local exists = redis.call('exists', streamKey);
if expected == -1 and exists == 1 then
    return { 'streamExistsAlready', streamId }
end

local currentRevision = tonumber(redis.call('llen', streamKey)) - 1

if expected ~= -2 and currentRevision ~= expected then
    return { 'conflict', tostring(expected), tostring(currentRevision) }
end

--local storeRevision = tonumber(redis.call('hlen', eventsKey))
--string.format('{"eventId":%s,"timestamp":%d,"streamId":%s,"eventType":%s,"streamRevision":%d,"event":%s,"metadata":%s}',cjson.encode(eventId), timestamp, cjson.encode(streamId), cjson.encode(eventType), currentRevision + 1, event, metadata)

for i = 4, (4 + numberOfEvents - 1), 1 do

    local decodedEvent = cjson.decode(ARGV[i])

    redis.call('hset', eventsKey, decodedEvent.eventId, ARGV[i])
    redis.call('rpush', streamKey, decodedEvent.eventId)
    redis.call('publish', eventsKey, ARGV[i])
end

local firstEvent = cjson.decode(ARGV[4])

return { 'commit', (firstEvent.streamRevision) }