#!/bin/bash

yc iam key create \
  --service-account-name ydb-local-testing \
  --output ~/sakey 
