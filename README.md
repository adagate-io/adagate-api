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

# Endpoints
The following REST endpoints are available (swagger Api is under development).

## Accounts

GET Account by Stake Address:
```/accounts/:stakeAddress```

GET Account Rewards:
```/accounts/:stakeAddress/rewards```

GET Account History:
```/accounts/:stakeAddress/history```

GET Account Delegations:
```/accounts/:stakeAddress/delegations```

GET Account Withdrawals:
```/accounts/:stakeAddress/withdrawals```

GET Account Addresses:
```/accounts/:stakeAddress/addresses```

## Addresses

GET Address Information:
```/addresses/:address```

GET Address Total:
```/addresses/:address/total```

GET Address UTxOs:
```/addresses/:address/utxos```

GET Address Transactions:
```/addresses/:address/transactions```

## Assets

GET Asset Information:
```/assets```

GET Specific Asset by id:
```/assets/:assetId```

## Blocks

GET latest Block:
```/blocks/latest```

GET latest Block's Transactions:
```/blocks/latest/transactions```

GET Block by id:
```/blocks/:id```

GET Block by Slot Number:
```/blocks/slot/:slotNumber```

GET Block by Epoch and Epoch Slot Number:
```/blocks/epoch/:epochNumber/slot/:slotNumber```

GET next Block:
```/blocks/:id/next```

Get previous Block:
```/blocks/:id/previous```

Get Block Transactions:
```/blocks/:id/transactions```

## Epochs

GET latest Epoch:
```/epochs/latest```

GET Epoch by id:
```/epochs/:epochNumber```

## Genesis

GET Genesis of network:
```/genesis```

## Pools

GET Pool by id:
```/pools/:poolId```

GET Pool Metadata:
```/pools/:poolId/metadata```
