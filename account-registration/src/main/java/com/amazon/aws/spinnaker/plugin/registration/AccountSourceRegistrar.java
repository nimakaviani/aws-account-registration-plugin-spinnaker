package com.amazon.aws.spinnaker.plugin.registration;

import com.netflix.spinnaker.clouddriver.aws.security.config.CredentialsConfig;
import com.netflix.spinnaker.clouddriver.ecs.security.ECSCredentialsConfig;
import com.netflix.spinnaker.credentials.definition.BasicCredentialsLoader;
import com.netflix.spinnaker.credentials.definition.CredentialsDefinitionSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Slf4j
@Configuration
class AccountSourceRegistrar {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    enum SourceType {
        AWS,
        ECS
    }

    @Autowired(required = false)
    AwsCredentialsDefinitionSource registerAWSAccountSource(
            AccountsStatus accountsStatus,
            BasicCredentialsLoader amazonCredentialsLoader,
            CredentialsConfig credentialsConfig
    ){
        log.info("starting aws source ....");
        if (amazonCredentialsLoader == null) {
            return null;
        }

        AwsCredentialsDefinitionSource source = new AwsCredentialsDefinitionSource(accountsStatus, credentialsConfig);
        alterDefinitionSource(SourceType.AWS, amazonCredentialsLoader, source);
        return source;
    }

    @Autowired(required = false)
    EcsCredentialsDefinitionSource registerECSAccountSource(
            AccountsStatus accountsStatus,
            BasicCredentialsLoader ecsCredentialsLoader,
            ECSCredentialsConfig ecsCredentialsConfig
    ){
        log.info("starting ecs source ....");
        if (ecsCredentialsLoader == null) {
            return null;
        }

        EcsCredentialsDefinitionSource source = new EcsCredentialsDefinitionSource(accountsStatus, ecsCredentialsConfig);
        alterDefinitionSource(SourceType.ECS, ecsCredentialsLoader, source);
        return source;
    }

    private void alterDefinitionSource(SourceType type, BasicCredentialsLoader loader, CredentialsDefinitionSource newSource){
        try{
            Field field;
            switch (type) {
                case AWS:
                    field = loader.getClass().getSuperclass().getDeclaredField("definitionSource");
                    break;
                case ECS:
                    field = loader.getClass().getDeclaredField("definitionSource");
                    break;
                default:
                    throw new RuntimeException("crap");
            }

            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(loader, newSource);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
