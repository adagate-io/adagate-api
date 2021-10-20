# ₳dagate-API
Cardano API for real-time blockchain data either via REST Api or websockets.

# Setup & Requirements
You require running [cardano-db-sync](https://github.com/input-output-hk/cardano-db-sync) for the API to be able to retrieve data
live from the blockchain. If you do not want to set up or maintain your own infrastructure, you will soon be able to use [adagate.io](https://adagate.io) services.

# Dependencies
- Maven
- Java >= 8

# Configuration
You may provide a configuration file in the following format in order to define the database connection parameters.
It will also allow you to define how many worker verticles will be deployed to scale the API in regards to your needs. 

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

# Run
Generally, you can execute the `run.sh` script in the root directory to start the application. 
But a system service is the recommended way.

## Run Locally
A simple local port forwarding can be used to create a connection to your remote machine hosting the blockchain database.

`ssh -L 5555:127.0.0.1:5432 <username>@<server_ip>`

Thereby the port `5555` should be matching the `<db_port>` defined in the configuration.

# Endpoints
The following REST endpoints are available (swagger Api is under development):

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

GET Next Epochs
```/epochs/:epochNumber/next```

GET Previous Epochs
```/epochs/:epochNumber/previous```

GET Epochs Stakes
```/epochs/:epochNumber/stakes```

## Genesis

GET Genesis of network:
```/genesis```

## Pools

GET Pool by id:
```/pools/:poolId```

GET Pool Metadata:
```/pools/:poolId/metadata```

# Donate

If you like to support the idea with a donation, the address is:

```
addr1qykpnhatcgyuh7kvz7k4k3k2cqllmzqskkzdp5qe22y88gtwkusnp8r253ca68jjpgdl67yckkv47tjqnrg56nyq0pzsl0x8tf
```

Thank you.