package io.adagate.verticles.webserver;

import io.adagate.handlers.routes.FailureHandler;
import io.adagate.handlers.routes.accounts.*;
import io.adagate.handlers.routes.addresses.GetAddress;
import io.adagate.handlers.routes.addresses.GetAddressTotal;
import io.adagate.handlers.routes.assets.GetAssetById;
import io.adagate.handlers.routes.assets.GetAssets;
import io.adagate.handlers.routes.blocks.*;
import io.adagate.handlers.routes.epochs.GetEpochById;
import io.adagate.handlers.routes.epochs.GetLatestEpoch;
import io.adagate.handlers.routes.genesis.GetGenesis;
import io.adagate.handlers.routes.pools.GetPoolByIdOrHash;
import io.adagate.handlers.security.ApiAccessHandler;
import io.adagate.verticles.database.DatabaseEventbusAddress;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.impl.RouterImpl;

public final class ApiRouter extends RouterImpl {
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
            .handler(new ApiAccessHandler(vertx, config))
            .failureHandler(new FailureHandler(vertx));

        /* Accounts */
        get("/accounts/:stakeAddress").handler(new GetAccountByStakeAddress(vertx)); // TODO: Add controlled amount
        get("/accounts/:stakeAddress/rewards").handler(new GetAccountRewards(vertx));
        get("/accounts/:stakeAddress/history").handler(new GetAccountHistory(vertx)); // TODO: Add amount column
        get("/accounts/:stakeAddress/delegations").handler(new GetAccountDelegations(vertx)); // TODO: Add amount column
        get("/accounts/:stakeAddress/withdrawals").handler(new GetAccountWithdrawals(vertx));
//        get("/accounts/:stakeAddress/mirs").handler();
        get("/accounts/:stakeAddress/addresses").handler(new GetAccountAddresses(vertx));
//        get("/accounts/:stakeAddress/addresses/assets").handler();

        /* Addresses */
        get("/addresses/:address").handler(new GetAddress(vertx));
        get("/addresses/:address/total").handler(new GetAddressTotal(vertx));
//        get("/addresses/:address/utxos").handler();
//        get("/addresses/:address/transactions").handler();

        /* Assets */
        get("/assets").handler(new GetAssets(vertx));
        get("/assets/:assetId").handler(new GetAssetById(vertx));
//        get("/assets/:assetId/history").handler();
//        get("/assets/:assetId/transactions").handler();
//        get("/assets/:assetId/addresses").handler();
//        get("/assets/policy/:policyId").handler();

        /* Blocks */
        get("/blocks/latest").handler(new GetLatestBlock(vertx));
        get("/blocks/latest/transactions")
            .handler(new GetLatestBlockTransactions(vertx))
            .handler(new GetBlockTransactions(vertx));
        get("/blocks/:id").handler(new GetBlockByNumberOrHash(vertx));
        get("/blocks/slot/:slotNumber").handler(new GetBlockBySlotNumber(vertx));
        get("/blocks/epoch/:epochNumber/slot/:slotNumber").handler(new GetBlockByEpochSlotNumber(vertx));
        get("/blocks/:id/next").handler(new GetNextBlock(vertx));
        get("/blocks/:id/previous").handler(new GetPreviousBlock(vertx));
        get("/blocks/:id/transactions").handler(new GetBlockTransactions(vertx));

        /* Epochs */
        get("/epochs/latest").handler(new GetLatestEpoch(vertx));
//        get("/epochs/latest/parameters").handler();
        get("/epochs/:epochNumber").handler(new GetEpochById(vertx));
//        get("/epochs/:epochNumber/next").handler(new GetEpochById(vertx, e -> e + 1));
//        get("/epochs/:epochNumber/previous").handler(new GetEpochById(vertx, e -> e - 1));
//        get("/epochs/:epochNumber/stakes").handler();
//        get("/epochs/:epochNumber/stakes/:poolId").handler();
//        get("/epochs/:epochNumber/blocks").handler();
//        get("/epochs/:epochNumber/blocks/:poolId").handler();
//        get("/epochs/:epochNumber/parameters").handler();

        /* Ledger */
        get("/genesis").handler(new GetGenesis(config.getString("network", "mainnet"), vertx));
//        get("/metadata/transactions/labels").handler();
//        get("/metadata/transactions/labels/:label").handler();

        /* Network */
//        get("/network").handler();

        /* Pools */
//        get("/pools").handler();
//        get("/pools/retired").handler();
//        get("/pools/retiring").handler();
        get("/pools/:poolId").handler(new GetPoolByIdOrHash(vertx)); // TODO: Periodically update total_live_stake
//        get("/pools/:poolId/history").handler();
//        get("/pools/:poolId/metadata").handler();
//        get("/pools/:poolId/relays").handler();
//        get("/pools/:poolId/delegators").handler();
//        get("/pools/:poolId/blocks").handler();
//        get("/pools/:poolId/updates").handler();

        /* Transactions */
//        get("/transactions/:hash").handler();
//        get("/transactions/:hash/utxos").handler();
//        get("/transactions/:hash/stakes").handler();
//        get("/transactions/:hash/delegations").handler();
//        get("/transactions/:hash/withdrawals").handler();
//        get("/transactions/:hash/mirs").handler();
//        get("/transactions/:hash/pool_updates").handler();
//        get("/transactions/:hash/pool_retires").handler();
//        get("/transactions/:hash/metadata").handler();
//        get("/transactions/:hash/metadata/cbor").handler();
//        post("/transactions/submit").handler();

        /* IPFS */
//        post("/ipfs/add").handler();
//        get("/ipfs/gateway/:ipfsPath").handler();
//        post("/ipfs/pin/add/:ipfsPath").handler();
//        get("/ipfs/pin/list").handler();
//        get("/ipfs/pin/list/:ipfsPath").handler();
//        delete("/ipfs/pin/remove/:ipfsPath").handler();

        /* Nut.link*/
//        get("/nutlink/:address").handler();
//        get("/nutlink/:address/tickers").handler();
//        get("/nutlink/:address/tickers/:ticker").handler();
//        get("/nutlink/tickers/:ticker").handler();

        // TODO: Remove template
        get("/blocks").handler(ctx -> ctx.response().sendFile("public/blocks.html"));
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
