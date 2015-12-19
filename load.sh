#!/bin/bash
for i in {1..10000};
do
curl -X POST -d "{\"uri\":\"uri$i\",\"name\":\"label$i\"}" -H "Content-Type: application/json" http://localhost:8080/asset
done;
