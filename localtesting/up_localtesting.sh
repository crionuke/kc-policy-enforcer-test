#!/bin/bash
docker compose --profile localtesting down -v
docker compose --profile localtesting up -d
