#!/bin/bash
docker compose --profile standalone down -v
docker compose --profile standalone up -d
