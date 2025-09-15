#!/bin/bash
docker compose down -v
docker compose --profile standalone up -d
