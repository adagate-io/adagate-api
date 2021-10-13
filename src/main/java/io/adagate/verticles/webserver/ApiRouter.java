package io.adagate.verticles.webserver;

import io.adagate.CardanoApiModule;
import io.adagate.handlers.routes.FailureHandler;
import io.adagate.handlers.routes.accounts.*;
import io.adagate.handlers.routes.addresses.GetAddress;
import io.adagate.handlers.routes.addresses.GetAddressTotal;
import io.adagate.handlers.routes.addresses.GetAddressTransactions;
import io.adagate.handlers.routes.addresses.GetAddressUTXOs;
import io.adagate.handlers.routes.assets.GetAssetById;
import io.adagate.handlers.routes.assets.GetAssets;
import io.adagate.handlers.routes.blocks.*;
import io.adagate.handlers.routes.epochs.GetEpochById;
import io.adagate.handlers.routes.epochs.GetLatestEpoch;
import io.adagate.handlers.routes.genesis.GetGenesis;
import io.adagate.handlers.routes.pools.GetPoolByIdOrHash;
import io.adagate.handlers.routes.pools.GetPoolMetadata;
import io.adagate.verticles.database.DatabaseEventbusAddress;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.impl.RouterImpl;

public final class ApiRouter extends RouterImpl {
    final static Logger LOGGER = LoggerFactory.getLogger(CardanoApiModule.class);

    private final Vertx vertx;

    public ApiRouter(Vertx vertx, JsonObject config) {
        super(vertx);
        this.vertx = vertx;

        setupApiRoutes(config);
        setupWebsocketBridge();
    }

    /* Private */

    /**
     * Sets up the REST Api endpoints
     */
    private void setupApiRoutes(JsonObject config) {
        route()
            .handler(LoggerHandler.create(LoggerFormat.SHORT))
//            .handler(
//                CorsHandler
//                    .create()
//                    .allowedMethod(HttpMethod.GET)
//                    .allowedMethod(HttpMethod.POST)
//                    .allowedMethod(HttpMethod.HEAD)
//                    .allowedHeader(APIKEY_HEADER)
//                    .allowCredentials(false)
//                    .addOrigin(config.getString("origin", "*"))
//            )
//            .handler(new ApiAccessHandler(vertx, config))
            .failureHandler(new FailureHandler(vertx));

        /* Accounts */
        get("/accounts/:stakeAddress").handler(new GetAccountByStakeAddress(vertx))
                .setName("GET Account by Stake Address");
        get("/accounts/:stakeAddress/rewards").handler(new GetAccountRewards(vertx))
                .setName("GET Account Rewards");
        get("/accounts/:stakeAddress/history").handler(new GetAccountHistory(vertx))
                .setName("GET Account History");
        get("/accounts/:stakeAddress/delegations").handler(new GetAccountDelegations(vertx))
                .setName("GET Account Delegations"); // TODO: Add amount column
        get("/accounts/:stakeAddress/withdrawals").handler(new GetAccountWithdrawals(vertx))
                .setName("GET Account Withdrawals");
// TODO: get("/accounts/:stakeAddress/mirs").handler();
        get("/accounts/:stakeAddress/addresses").handler(new GetAccountAddresses(vertx))
                .setName("GET Account Addresses");
// TODO: get("/accounts/:stakeAddress/addresses/assets").handler();

        /* Addresses */
        get("/addresses/:address").handler(new GetAddress(vertx))
                .setName("GET Address Information");
        get("/addresses/:address/total").handler(new GetAddressTotal(vertx))
                .setName("GET Address Total");
        get("/addresses/:address/utxos").handler(new GetAddressUTXOs(vertx))
                .setName("GET Address UTxOs");
        get("/addresses/:address/transactions").handler(new GetAddressTransactions(vertx))
                .setName("GET Address Transactions");

        /* Assets */
        get("/assets").handler(new GetAssets(vertx))
                .setName("GET Asset Information");
        get("/assets/:assetId").handler(new GetAssetById(vertx))
                .setName("GET Specific Asset by id");
// TODO: get("/assets/:assetId/history").handler();
// TODO: get("/assets/:assetId/transactions").handler();
// TODO: get("/assets/:assetId/addresses").handler();
// TODO: get("/assets/policy/:policyId").handler();

        /* Blocks */
        get("/blocks/latest").handler(new GetLatestBlock(vertx))
                .setName("GET latest Block");
        get("/blocks/latest/transactions")
            .handler(new GetLatestBlockTransactions(vertx))
            .handler(new GetBlockTransactions(vertx))
            .setName("GET latest Block's Transactions");
        get("/blocks/:id").handler(new GetBlockByNumberOrHash(vertx))
                .setName("GET Block by id");
        get("/blocks/slot/:slotNumber").handler(new GetBlockBySlotNumber(vertx))
                .setName("GET Block by Slot Number");
        get("/blocks/epoch/:epochNumber/slot/:slotNumber").handler(new GetBlockByEpochSlotNumber(vertx))
                .setName("GET Block by Epoch and Epoch Slot Number");
        get("/blocks/:id/next").handler(new GetNextBlock(vertx))
                .setName("GET next Block");
        get("/blocks/:id/previous").handler(new GetPreviousBlock(vertx))
                .setName("Get previous Block");
        get("/blocks/:id/transactions").handler(new GetBlockTransactions(vertx))
                .setName("Get Block Transactions");

        /* Epochs */
        get("/epochs/latest").handler(new GetLatestEpoch(vertx))
                .setName("GET latest Epoch");
// TODO: get("/epochs/latest/parameters").handler();
        get("/epochs/:epochNumber").handler(new GetEpochById(vertx))
                .setName("GET Epoch by id");
// TODO: get("/epochs/:epochNumber/next").handler(new GetEpochById(vertx, e -> e + 1));
// TODO: get("/epochs/:epochNumber/previous").handler(new GetEpochById(vertx, e -> e - 1));
// TODO: get("/epochs/:epochNumber/stakes").handler();
// TODO: get("/epochs/:epochNumber/stakes/:poolId").handler();
// TODO: get("/epochs/:epochNumber/blocks").handler();
// TODO: get("/epochs/:epochNumber/blocks/:poolId").handler();
// TODO: get("/epochs/:epochNumber/parameters").handler();

        /* Ledger */
        get("/genesis").handler(new GetGenesis(config.getString("network", "mainnet"), vertx))
                .setName("GET Genesis of network");
// TODO: get("/metadata/transactions/labels").handler();
// TODO: get("/metadata/transactions/labels/:label").handler();

        /* Network */
// TODO: get("/network").handler();

        /* Pools */
// TODO: get("/pools").handler();
// TODO: get("/pools/retired").handler();
// TODO: get("/pools/retiring").handler();
        get("/pools/:poolId").handler(new GetPoolByIdOrHash(vertx))
                .setName("GET Pool by id"); // TODO: Periodically update total_live_stake
// TODO: get("/pools/:poolId/history").handler();
        get("/pools/:poolId/metadata").handler(new GetPoolMetadata(vertx))
                .setName("GET Pool Metadata");
// TODO: get("/pools/:poolId/relays").handler();
// TODO: get("/pools/:poolId/delegators").handler();
// TODO: get("/pools/:poolId/blocks").handler();
// TODO: get("/pools/:poolId/updates").handler();

        /* Transactions */
// TODO: get("/transactions/:hash").handler();
// TODO: get("/transactions/:hash/utxos").handler();
// TODO: get("/transactions/:hash/stakes").handler();
// TODO: get("/transactions/:hash/delegations").handler();
// TODO: get("/transactions/:hash/withdrawals").handler();
// TODO: get("/transactions/:hash/mirs").handler();
// TODO: get("/transactions/:hash/pool_updates").handler();
// TODO: get("/transactions/:hash/pool_retires").handler();
// TODO: get("/transactions/:hash/metadata").handler();
// TODO: get("/transactions/:hash/metadata/cbor").handler();
//        post("/transactions/submit").handler();

        /* IPFS */
//        post("/ipfs/add").handler();
// TODO: get("/ipfs/gateway/:ipfsPath").handler();
//        post("/ipfs/pin/add/:ipfsPath").handler();
// TODO: get("/ipfs/pin/list").handler();
// TODO: get("/ipfs/pin/list/:ipfsPath").handler();
//        delete("/ipfs/pin/remove/:ipfsPath").handler();

        /* Nut.link*/
// TODO: get("/nutlink/:address").handler();
// TODO: get("/nutlink/:address/tickers").handler();
// TODO: get("/nutlink/:address/tickers/:ticker").handler();
// TODO: get("/nutlink/tickers/:ticker").handler();
    }

    private void setupWebsocketBridge() {
        final SockJSBridgeOptions options = new SockJSBridgeOptions();
        for(DatabaseEventbusAddress address : DatabaseEventbusAddress.values()) {
            options.addOutboundPermitted(
                new PermittedOptions()
                    .setAddressRegex(String.format("^%s$", address.getAddress()))
            );
        }
        mountSubRouter("/eventbus", SockJSHandler.create(vertx).bridge(options));
    }
}
