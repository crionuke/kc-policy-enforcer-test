#!/bin/bash
docker compose -f localtesting/compose.yaml --profile localtesting down -v
docker compose -f localtesting/compose.yaml --profile localtesting up -d
