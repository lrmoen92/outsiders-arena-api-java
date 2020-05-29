package org.outsiders.arena.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages={"org.outsiders.arena.repository"})
public class CassandraConfig
  extends AbstractCassandraConfiguration
{
  protected String getKeyspaceName()
  {
    return "outsiders_arena";
  }
}
