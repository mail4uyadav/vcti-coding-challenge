package com.vcti.ct.SRVServices.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraCqlClusterFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig extends AbstractCassandraConfiguration {

	@Value("${spring.data.cassandra.contact-points}")
	private String contactPoints;

	@Value("${spring.data.cassandra.port}")
	private int port;

	@Value("${spring.data.cassandra.keyspace-name}")
	private String keySpace;

	@Value("${spring.data.cassandra.username}")
	private String userName;

	@Value("${spring.data.cassandra.password}")
	private String password;

	@Value("${spring.data.cassandra.scheduler-table}")
	private String schedulerTable;

	@Value("${spring.data.cassandra.obj-result-table}")
	private String objResultTable;

	@Value("${spring.data.cassandra.subj-result-table}")
	private String subjResultTable;

	
	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	@Override
	protected String getKeyspaceName() {
		return keySpace;
	}

	@Override
	protected String getContactPoints() {
		return contactPoints;
	}

	@Bean
	@Override
	public CassandraCqlClusterFactoryBean cluster() {
		CassandraCqlClusterFactoryBean bean = new CassandraCqlClusterFactoryBean();
		bean.setKeyspaceCreations(getKeyspaceCreations());
		bean.setContactPoints(getContactPoints());
		bean.setUsername(getUserName());
		bean.setPassword(getPassword());
		return bean;
	}

	@Override
	protected int getPort() {
		return port;
	}

	@Override
	public SchemaAction getSchemaAction() {
		return SchemaAction.CREATE_IF_NOT_EXISTS;
	}

	protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
		return Collections.singletonList(CreateKeyspaceSpecification.createKeyspace(getKeyspaceName()).ifNotExists());
	}

	@Override
	protected List<String> getStartupScripts() {
		List<String> startupScriptList = new ArrayList<String>();
		// Create Queries
		startupScriptList.addAll(getListOfCreateQuery());
		return startupScriptList;
	}

	private List<String> getListOfCreateQuery() {
		List<String> createQueryList = new ArrayList<String>();
		// Question Table
		createQueryList.add("CREATE TABLE IF NOT EXISTS " + getKeyspaceName() + "." + schedulerTable
				+ "(id text PRIMARY KEY,qid text, assigneduid text, assigneruid text)");

		createQueryList.add("CREATE TABLE IF NOT EXISTS " + getKeyspaceName() + "." + objResultTable
				+ "(userid text, qId text, selectedoption text, PRIMARY KEY (userid, qId))");

		createQueryList.add("CREATE TABLE IF NOT EXISTS " + getKeyspaceName() + "." + subjResultTable
				+ "(userid text, qId text, program blob, consolidatedoutput text, PRIMARY KEY (userid, qId))");

		return createQueryList;
	}

}
