---
--- Created by oliver.
--- DateTime: 03/06/18 12:24
---

-- keys
local eventsKey = KEYS[1]
local streamKey = KEYS[2]
local allKey = KEYS[3]
local categoryStreamKey = KEYS[4]
local eventTypeKey = KEYS[4]
-- argv
local streamId = ARGV[1]
-- -2 means 'do not check version'
-- -1 means 'there must not be a stream yet'
-- 0 means 'stream exists but no event is stored yet'
local expected = tonumber(ARGV[2])
local numberOfEvents = tonumber(ARGV[3])
-- ARGV[4] and following are events

local exists = redis.call('exists', streamKey);
if expected == -1 and exists == 1 then
    return { 'streamExistsAlready', streamId }
end

local currentRevision = tonumber(redis.call('llen', streamKey)) - 1

if expected ~= -2 and currentRevision ~= expected then
    return { 'conflict', tostring(expected), tostring(currentRevision) }
end

for i = 4, (4 + numberOfEvents - 1), 1 do

    local decodedEvent = cjson.decode(ARGV[i])

    -- only add event if it does not exist yet
    local hasBeenSet = redis.call('hsetnx', eventsKey, decodedEvent.eventId, ARGV[i])
    if hasBeenSet == 1 then
        redis.call('rpush', allKey, decodedEvent.eventId)
        if categoryStreamKey ~= "" then
            redis.call('rpush', categoryStreamKey, decodedEvent.eventId)
        end
        redis.call('rpush', streamKey, decodedEvent.eventId)
        redis.call('publish', eventsKey, ARGV[i])
    end
end

local firstEvent = cjson.decode(ARGV[4])

return { 'commit', (firstEvent.streamRevision) }