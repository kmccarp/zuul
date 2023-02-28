/*
 * Copyright 2018 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.netflix.zuul.netty.connectionpool;

import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.IClientConfigKey;
import com.netflix.config.CachedDynamicBooleanProperty;
import com.netflix.config.CachedDynamicIntProperty;
import com.netflix.zuul.origins.OriginName;
import java.util.Objects;

/**
 * Created by saroskar on 3/24/16.
 */
public class ConnectionPoolConfigImpl implements ConnectionPoolConfig {

    private static final int DEFAULT_BUFFER_SIZE = 32 * 1024;
    private static final int DEFAULT_CONNECT_TIMEOUT = 500;
    private static final int DEFAULT_IDLE_TIMEOUT = 60000;
    private static final int DEFAULT_MAX_CONNS_PER_HOST = 50;

    private final OriginName originName;
    private final IClientConfig clientConfig;

    private final CachedDynamicIntProperty maxRequestsPerConnection;
    private final CachedDynamicIntProperty perServerWaterline;

    private final CachedDynamicBooleanProperty socketKeepAlive;
    private final CachedDynamicBooleanProperty tcpNoDelay;
    private final CachedDynamicIntProperty writeBufferHighWaterMark;
    private final CachedDynamicIntProperty writeBufferLowWaterMark;
    private final CachedDynamicBooleanProperty autoRead;


    public ConnectionPoolConfigImpl(final OriginName originName, IClientConfig clientConfig) {
        this.originName = Objects.requireNonNull(originName, "originName");
        String niwsClientName = originName.getNiwsClientName();
        this.clientConfig = clientConfig;

        this.maxRequestsPerConnection =
                new CachedDynamicIntProperty(niwsClientName + ".netty.client.maxRequestsPerConnection", 1000);

        // NOTE that the each eventloop has it's own connection pool per host, and this is applied per event-loop.
        this.perServerWaterline =
                new CachedDynamicIntProperty(niwsClientName + ".netty.client.perServerWaterline", 4);

        this.socketKeepAlive =
                new CachedDynamicBooleanProperty(niwsClientName + ".netty.client.TcpKeepAlive", false);
        this.tcpNoDelay =
                new CachedDynamicBooleanProperty(niwsClientName + ".netty.client.TcpNoDelay", false);

        //TODO(argha-c): Document why these values were chosen, as opposed to defaults of 32k/64k
        this.writeBufferHighWaterMark =
                new CachedDynamicIntProperty(niwsClientName + ".netty.client.WriteBufferHighWaterMark", 32 * 1024);
        this.writeBufferLowWaterMark =
                new CachedDynamicIntProperty(niwsClientName + ".netty.client.WriteBufferLowWaterMark", 8 * 1024);
        this.autoRead = new CachedDynamicBooleanProperty(niwsClientName + ".netty.client.AutoRead", false);
    }

    @Override
    public OriginName getOriginName() {
        return originName;
    }

    @Override
    public int getConnectTimeout() {
        return clientConfig.getPropertyAsInteger(IClientConfigKey.Keys.ConnectTimeout, DEFAULT_CONNECT_TIMEOUT);
    }

    @Override
    public int getMaxRequestsPerConnection() {
        return maxRequestsPerConnection.get();
    }

    @Override
    public int maxConnectionsPerHost()
    {
        return clientConfig.getPropertyAsInteger(IClientConfigKey.Keys.MaxConnectionsPerHost, DEFAULT_MAX_CONNS_PER_HOST);
    }

    @Override
    public int perServerWaterline()
    {
        return perServerWaterline.get();
    }

    @Override
    public int getIdleTimeout() {
        return clientConfig.getPropertyAsInteger(IClientConfigKey.Keys.ConnIdleEvictTimeMilliSeconds, DEFAULT_IDLE_TIMEOUT);
    }

    @Override
    public boolean getTcpKeepAlive() {
        return socketKeepAlive.get();
    }

    @Override
    public boolean getTcpNoDelay() {
        return tcpNoDelay.get();
    }

    @Override
    public int getTcpReceiveBufferSize() {
        return clientConfig.getPropertyAsInteger(IClientConfigKey.Keys.ReceiveBufferSize, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public int getTcpSendBufferSize() {
        return clientConfig.getPropertyAsInteger(IClientConfigKey.Keys.SendBufferSize, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public int getNettyWriteBufferHighWaterMark() {
        return writeBufferHighWaterMark.get();
    }

    @Override
    public int getNettyWriteBufferLowWaterMark() {
        return writeBufferLowWaterMark.get();
    }

    @Override
    public boolean getNettyAutoRead() {
        return autoRead.get();
    }

    @Override
    public boolean isSecure()
    {
        return clientConfig.getPropertyAsBoolean(IClientConfigKey.Keys.IsSecure, false);
    }

    @Override
    public boolean useIPAddrForServer()
    {
        return clientConfig.getPropertyAsBoolean(IClientConfigKey.Keys.UseIPAddrForServer, true);
    }

}
