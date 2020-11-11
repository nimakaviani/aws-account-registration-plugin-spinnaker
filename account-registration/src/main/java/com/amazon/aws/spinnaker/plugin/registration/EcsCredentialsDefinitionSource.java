package com.amazon.aws.spinnaker.plugin.registration;

import com.google.common.collect.ImmutableList;
import com.netflix.spinnaker.clouddriver.ecs.security.ECSCredentialsConfig;
import com.netflix.spinnaker.credentials.definition.CredentialsDefinitionSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Slf4j
public class EcsCredentialsDefinitionSource implements CredentialsDefinitionSource<ECSCredentialsConfig.Account> {
    private final AccountsStatus accountsStatus;
    private final ECSCredentialsConfig ecsCredentialsConfig;
    private List<ECSCredentialsConfig.Account> ecsCredentialsDefinitions;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public EcsCredentialsDefinitionSource(AccountsStatus accountsStatus, ECSCredentialsConfig ecsCredentialsConfig) {
        log.info("starting the ECS credential definition source");
        this.accountsStatus = accountsStatus;
        this.ecsCredentialsConfig = ecsCredentialsConfig;
    }

    @Override
    public List<ECSCredentialsConfig.Account> getCredentialsDefinitions() {
        if (ecsCredentialsDefinitions == null) {
            ecsCredentialsDefinitions = ecsCredentialsConfig.getAccounts();
        }
        ecsCredentialsDefinitions = accountsStatus.getECSAccountsAsList();

        return ImmutableList.copyOf(ecsCredentialsDefinitions);
    }
}
