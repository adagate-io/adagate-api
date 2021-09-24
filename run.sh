#!/usr/bin/env bash
# execute: ssh -L 5555:127.0.0.1:5432 cardano@db
mvn clean compile vertx:run