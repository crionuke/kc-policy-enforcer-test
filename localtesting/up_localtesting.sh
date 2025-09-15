#!/bin/bash
docker compose down -v
docker compose --profile localtesting up -d
