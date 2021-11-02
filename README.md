<div align="center">
 <img width="100px" src="https://i.ibb.co/1nX9hC4/logo-03-small.png" align="center" />
 <h2 align="center">₳dagate<br />Cardano Blockchain API</h2>
 <h5 align="center">Get real-time blockchain data via REST and websockets!</h5>
</div>
<p align="center">
    <a href="https://github.com/anuraghazra/github-readme-stats/actions">
      <img alt="Tests Passing" src="https://img.shields.io/badge/Test-passing-brightgreen" />
    </a>
    <a href="https://codecov.io/gh/anuraghazra/github-readme-stats">
      <img src="https://img.shields.io/badge/Coverage-97%25-brightgreen" />
    </a>
    <a href="https://github.com/anuraghazra/github-readme-stats/issues">
      <img alt="Issues" src="https://img.shields.io/badge/Issues-1-blue" />
    </a>
  </p>
<p align="center">
    <a href="https://twitter.com/stakingberlin" target="_blank" style="padding: 0 20px">
        <img width="30px" align="center" src="https://static.cdnlogo.com/logos/t/96/twitter-icon.svg" />
        Twitter - stakingberlin BRLN 
    </a>
    <a href="https://t.me/BRLNStakePool" target="_blank">
        <img width="30px" align="center" src="https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Telegram_alternative_logo.svg/2048px-Telegram_alternative_logo.svg.png" />
        Telegram - Berlin Pool
    </a>
<p/>

<p align="center">Love the project? Please consider donating to help it improve!</p>

```
addr1qykpnhatcgyuh7kvz7k4k3k2cqllmzqskkzdp5qe22y88gtwkusnp8r253ca68jjpgdl67yckkv47tjqnrg56nyq0pzsl0x8tf
```

# Setup & Requirements
You require running [cardano-db-sync](https://github.com/input-output-hk/cardano-db-sync) for the API to be able to retrieve data
live from the blockchain. If you do not want to set up or maintain your own infrastructure, you will soon be able to use [adagate.io](https://adagate.io) services.

# Configuration
You must provide a configuration file in the following format in order to define the database connection parameters.
It will also allow you to define how many worker verticles will be deployed interacting with the database (for parallel execution).
The default location is defined in the <a href="https://github.com/adagate-io/adagate-api/blob/main/pom.xml#L110" target="_blank"><code>pom.xml</code></a> file.

```
{
  "network": "mainnet|testnet",
  "workers": 3,
  "workerPoolSize": 20,
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

GET Assets by Account Addresses:
```/accounts/:stakeAddress/addresses/assets```

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

GET Specific Asset History by id:
```/assets/:assetId/history```

GET Specific Asset Transactions by id:
```/assets/:assetId/transactions```

GET Specific Asset addresses:
```/assets/:assetId/addresses```

GET Specific Asset by policy id:
```/assets/policy/:policyId```

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

GET previous Block:
```/blocks/:id/previous```

GET Block Transactions:
```/blocks/:id/transactions```

## Epochs

GET latest Epoch:
```/epochs/latest```

GET Epoch by Epoch Number:
```/epochs/:epochNumber```

GET Next Epochs
```/epochs/:epochNumber/next```

GET Previous Epochs
```/epochs/:epochNumber/previous```

GET Epochs Stakes
```/epochs/:epochNumber/stakes```

GET Epochs Stakes for specific Pool
```/epochs/:epochNumber/stakes/:poolId```

GET Epochs Blocks
```/epochs/:epochNumber/blocks```

GET Epochs Blocks for specific Pool
```/epochs/:epochNumber/blocks/:poolId```

GET Epochs Parameters by Epoch Number
```/epochs/:epochNumber/parameters```

## Genesis

GET Genesis of network:
```/genesis```

## Pools

GET Pool by id:
```/pools/:poolId```

GET Pool Metadata:
```/pools/:poolId/metadata```

GET List of Pool Ids:
```/pools```

# Donate

If you like to support the idea with a donation, the address is:

```
addr1qykpnhatcgyuh7kvz7k4k3k2cqllmzqskkzdp5qe22y88gtwkusnp8r253ca68jjpgdl67yckkv47tjqnrg56nyq0pzsl0x8tf
```

Thank you.