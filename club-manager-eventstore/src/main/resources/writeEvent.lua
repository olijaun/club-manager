---
--- Created by oliver.
--- DateTime: 03/06/18 12:24
---

local commitsKey = KEYS[1]
local streamKey = KEYS[2]
local timestamp = tonumber(ARGV[1])
local streamId = ARGV[2]
local eventId = ARGV[3]
local eventType = ARGV[4]
-- -2 means 'do not check version'
-- -1 means 'there must not be a stream yet'
-- 0 means 'stream exists but no event is stored yet'
local expected = tonumber(ARGV[5])
local event = ARGV[6]
local metadata = ARGV[7]

local exists = redis.call('exists', streamKey);
if expected == -1 and exists == 1 then
    return {'streamExistsAlready', streamId}
end

local currentRevision = tonumber(redis.call('llen', streamKey)) - 1

if expected ~= -2 and currentRevision ~= expected then
    return {'conflict', tostring(expected), tostring(currentRevision)}
end

local storeRevision = tonumber(redis.call('hlen', commitsKey))
local commitData = string.format('{"eventId":%s,"timestamp":%d,"streamId":%s,"eventType":%s,"streamRevision":%d,"event":%s,"metadata":%s}',cjson.encode(eventId), timestamp, cjson.encode(streamId), cjson.encode(eventType), currentRevision + 1, event, metadata)

redis.call('hset', commitsKey, eventId, commitData)
redis.call('rpush', streamKey, eventId)
redis.call('publish', commitsKey, commitData)

return {'commit', tostring(eventId)}