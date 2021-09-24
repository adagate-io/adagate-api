# ₳dagate-io
Cardano API for real-time blockchain data either via REST Api or websockets.

# Run
There is a execution script included named `run.sh` in root directory of the project. You must have access to a postgres database where cardano-db-sync
is continously updating. Hence, you must adjust the script to point to your server in order to run the API.

# Config
You also need to provide a configuration file with the following format in order to define the database connection parameters.

```
{
  "network": "mainnet|testnet",
  "http": {
    "port": <api_port>
  },
  "database": {
    "host": "<localhost>",
    "port": <db_port>,
    "name": "<cexplorer by default>",
    "user": "<db_username>",
    "pw": "<db_passwd>"
  }
}
```
