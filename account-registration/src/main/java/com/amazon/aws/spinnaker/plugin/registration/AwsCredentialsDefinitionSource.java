package com.amazon.aws.spinnaker.plugin.registration;

import com.google.common.collect.ImmutableList;
import com.netflix.spinnaker.clouddriver.aws.security.config.CredentialsConfig;
import com.netflix.spinnaker.credentials.definition.CredentialsDefinitionSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class AwsCredentialsDefinitionSource implements CredentialsDefinitionSource<CredentialsConfig.Account> {
    private final AccountsStatus accountsStatus;
    private final CredentialsConfig credentialsConfig;
    private List<CredentialsConfig.Account> awsCredentialsDefinitions;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    AwsCredentialsDefinitionSource(AccountsStatus accountsStatus, CredentialsConfig credentialsConfig) {
        log.info("starting the aws credential definition source");
        this.accountsStatus = accountsStatus;
        this.credentialsConfig = credentialsConfig;
    }

    @Override
    public List<CredentialsConfig.Account> getCredentialsDefinitions() {
        if (awsCredentialsDefinitions == null) {
            awsCredentialsDefinitions = credentialsConfig.getAccounts();
        }
        if (accountsStatus.getDesiredAccounts()) {
            awsCredentialsDefinitions = accountsStatus.getEC2AccountsAsList();
        }
        return ImmutableList.copyOf(awsCredentialsDefinitions);
    }
}
