/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.SelectResults;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.repository.sample.User;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Integration Tests for {@link GemfireTemplate}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.query.SelectResults
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class GemfireTemplateIntegrationTests extends IntegrationTestsSupport {

	private static GemFireCluster gemFireCluster;
	private ClassPathXmlApplicationContext applicationContext;

	@BeforeClass
	public static void startCluster() {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1).withConfiguration("server-*", container -> container.withStartupAttempts(3));;

		gemFireCluster.acceptLicense().start();
		gemFireCluster.getContainers().values().forEach(container -> container.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"));
		gemFireCluster.gfshBuilder().build().run("create region --name=Users --type=REPLICATE");
		gemFireCluster.gfshBuilder().build().run("deploy --jar=/testJar.jar");
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	protected static final String DEFAULT_GEMFIRE_LOG_LEVEL = "error";

	protected static final List<User> TEST_USERS = new ArrayList<>(9);

	static {
		TEST_USERS.add(newUser("jonDoe"));
		TEST_USERS.add(newUser("janeDoe", false));
		TEST_USERS.add(newUser("pieDoe", false));
		TEST_USERS.add(newUser("cookieDoe"));
		TEST_USERS.add(newUser("jackHandy"));
		TEST_USERS.add(newUser("mandyHandy", false));
		TEST_USERS.add(newUser("randyHandy", false));
		TEST_USERS.add(newUser("sandyHandy"));
		TEST_USERS.add(newUser("imaPigg"));
	}

	@Autowired
	private ClientCache gemfireCache;

	@Autowired
	private GemfireTemplate usersTemplate;

	@Autowired
	@Qualifier("Users")
	private Region<String, User> users;

	private static User newUser(String username) {
		return newUser(username, true);
	}

	private static User newUser(String username, Boolean active) {
		return newUser(username, String.format("%1$s@companyx.com", username), Instant.now(), active);
	}

	private static User newUser(String username, String email, Instant since, Boolean active) {

		User user = new User(username);

		user.setActive(Boolean.TRUE.equals(active));
		user.setEmail(email);
		user.setSince(since);

		return user;
	}

	private String getKey(User user) {
		return user != null ? user.getUsername() : null;
	}

	private User getUser(String username) {

		for (User user : TEST_USERS) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}

		return null;
	}

	private List<User> getUsers(String... usernames) {

		List<String> usernameList = Arrays.asList(usernames);
		List<User> users = new ArrayList<>(usernames.length);

		for (User user : TEST_USERS) {
			if (usernameList.contains(user.getUsername())) {
				users.add(user);
			}
		}

		return users;
	}

	private Map<String, User> getUsersAsMap(String... usernames) {
		return getUsersAsMap(getUsers(usernames));
	}

	private Map<String, User> getUsersAsMap(User... users) {
		return getUsersAsMap(Arrays.asList(users));
	}

	private Map<String, User> getUsersAsMap(Iterable<User> users) {

		Map<String, User> userMap = new HashMap<>();

		for (User user : users) {
			userMap.put(getKey(user), user);
		}

		return userMap;
	}

	private void assertNullEquals(Object value1, Object value2) {
		Assertions.assertThat(Objects.equals(value1, value2)).isTrue();
	}

	@Before
	public void setup() {

		Assertions.assertThat(this.users).isNotNull();

		if (this.users.isEmpty()) {
			for (User user : TEST_USERS) {
				this.users.put(getKey(user), user);
			}

			Assertions.assertThat(this.users.size()).isEqualTo(TEST_USERS.size());
		}
	}

	@Test
	public void containsKey() {

		Assertions.assertThat(this.usersTemplate.containsKey(getKey(getUser("jonDoe")))).isTrue();
		Assertions.assertThat(this.usersTemplate.containsKey("dukeNukem")).isFalse();
	}

	@Test
	public void containsKeyOnServer() {
		Assertions.assertThat(this.usersTemplate.containsKeyOnServer(getKey(getUser("jackHandy")))).isTrue();
		Assertions.assertThat(this.usersTemplate.containsKeyOnServer("maxPayne")).isFalse();
	}

	@Test
	public void containsValue() {

		Assertions.assertThat(this.usersTemplate.containsValue(getUser("pieDoe"))).isTrue();
		Assertions.assertThat(this.usersTemplate.containsValue(newUser("pieDough"))).isFalse();
	}

	@Test
	public void containsValueForKey() {

		Assertions.assertThat(this.usersTemplate.containsValueForKey(getKey(getUser("cookieDoe")))).isTrue();
		Assertions.assertThat(this.usersTemplate.containsValueForKey("chocolateChipCookieDoe")).isFalse();
	}

	@Test
	public void create() {

		User bartSimpson = newUser("bartSimpson");

		this.usersTemplate.create(getKey(bartSimpson), bartSimpson);

		Assertions.assertThat(this.users.containsKey(getKey(bartSimpson))).isTrue();
		Assertions.assertThat(this.users.containsValueForKey(getKey(bartSimpson))).isTrue();
		Assertions.assertThat(this.users.containsValue(bartSimpson)).isTrue();
		Assertions.assertThat(this.users.get(getKey(bartSimpson))).isEqualTo(bartSimpson);
	}

	@Test
	public void get() {

		String key = getKey(getUser("imaPigg"));

		Assertions.assertThat(this.usersTemplate.<Object, Object>get(key)).isEqualTo(this.users.get(key));
		assertNullEquals(this.users.get("mrT"), this.usersTemplate.get("mrT"));
	}

	@Test
	public void put() {

		User peterGriffon = newUser("peterGriffon");

		Assertions.assertThat(this.usersTemplate.put(getKey(peterGriffon), peterGriffon)).isNull();
		Assertions.assertThat(this.users.get(getKey(peterGriffon))).isEqualTo(peterGriffon);
	}

	@Test
	public void putIfAbsent() {

		User stewieGriffon = newUser("stewieGriffon");

		Assertions.assertThat(this.users.containsValue(stewieGriffon)).isFalse();
		Assertions.assertThat(this.usersTemplate.putIfAbsent(getKey(stewieGriffon), stewieGriffon)).isNull();
		Assertions.assertThat(this.users.containsValue(stewieGriffon)).isTrue();
		Assertions.assertThat(this.usersTemplate.putIfAbsent(getKey(stewieGriffon), newUser("megGriffon"))).isEqualTo(stewieGriffon);
		Assertions.assertThat(this.users.get(getKey(stewieGriffon))).isEqualTo(stewieGriffon);
	}

	@Test
	public void remove() {

		User mandyHandy = this.users.get(getKey(getUser("mandyHandy")));

		Assertions.assertThat(mandyHandy).isNotNull();
		Assertions.assertThat(this.usersTemplate.<Object, Object>remove(getKey(mandyHandy))).isEqualTo(mandyHandy);
		Assertions.assertThat(this.users.containsKey(getKey(mandyHandy))).isFalse();
		Assertions.assertThat(this.users.containsValue(mandyHandy)).isFalse();
		Assertions.assertThat(this.users.containsKey("loisGriffon")).isFalse();
		Assertions.assertThat(this.usersTemplate.<Object, Object>remove("loisGriffon")).isNull();
		Assertions.assertThat(this.users.containsKey("loisGriffon")).isFalse();
	}

	@Test
	public void replace() {

		User randyHandy = this.users.get(getKey(getUser("randyHandy")));
		User lukeFluke = newUser("lukeFluke");
		User chrisGriffon = newUser("chrisGriffon");

		Assertions.assertThat(randyHandy).isNotNull();
		Assertions.assertThat(this.usersTemplate.replace(getKey(randyHandy), lukeFluke)).isEqualTo(randyHandy);
		Assertions.assertThat(this.users.get(getKey(randyHandy))).isEqualTo(lukeFluke);
		Assertions.assertThat(this.users.containsValue(randyHandy)).isFalse();
		Assertions.assertThat(this.users.containsValue(chrisGriffon)).isFalse();
		Assertions.assertThat(this.usersTemplate.replace(getKey(chrisGriffon), chrisGriffon)).isNull();
		Assertions.assertThat(this.users.containsValue(chrisGriffon)).isFalse();
	}

	@Test
	public void replaceOldValueWithNewValue() {

		User jackHandy = getUser("jackHandy");
		User imaPigg = getUser("imaPigg");

		Assertions.assertThat(this.users.containsValue(jackHandy)).isTrue();
		Assertions.assertThat(this.usersTemplate.replace(getKey(jackHandy), null, imaPigg)).isFalse();
		Assertions.assertThat(this.users.containsValue(jackHandy)).isTrue();
		Assertions.assertThat(this.users.get(getKey(jackHandy))).isEqualTo(jackHandy);
		Assertions.assertThat(this.usersTemplate.replace(getKey(jackHandy), jackHandy, imaPigg)).isTrue();
		Assertions.assertThat(this.users.containsValue(jackHandy)).isFalse();
		Assertions.assertThat(this.users.get(getKey(jackHandy))).isEqualTo(imaPigg);
	}

	@Test
	public void getAllReturnsNoResults() {

		List<String> keys = Arrays.asList("keyOne", "keyTwo", "keyThree");

		Map<String, User> users = this.usersTemplate.getAll(keys);

		Assertions.assertThat(users).isNotNull();
		Assertions.assertThat(users).isEqualTo(this.users.getAll(keys));
	}

	@Test
	public void getAllReturnsResults() {

		Map<String, User> users = this.usersTemplate.getAll(Arrays.asList(
			getKey(getUser("jonDoe")), getKey(getUser("pieDoe"))));

		Assertions.assertThat(users).isNotNull();
		Assertions.assertThat(users).isEqualTo(getUsersAsMap(getUser("jonDoe"), getUser("pieDoe")));
	}

	@Test
	public void putAll() {

		User batMan = newUser("batMan");
		User spiderMan = newUser("spiderMan");
		User superMan = newUser("superMan");

		Map<String, User> userMap = getUsersAsMap(batMan, spiderMan, superMan);

		Assertions.assertThat(this.users.keySet().containsAll(userMap.keySet())).isFalse();
		Assertions.assertThat(this.users.values().containsAll(userMap.values())).isFalse();

		this.usersTemplate.putAll(userMap);

		Assertions.assertThat(this.users.keySet().containsAll(userMap.keySet())).isTrue();
		Assertions.assertThat(this.users.values().containsAll(userMap.values())).isTrue();
	}

	@Test
	public void query() {

		SelectResults<User> queryResults = this.usersTemplate.query("username LIKE '%Doe'");

		Assertions.assertThat(queryResults).isNotNull();

		List<User> usersFound = queryResults.asList();

		Assertions.assertThat(usersFound).isNotNull();
		Assertions.assertThat(usersFound.size()).isEqualTo(4);
		Assertions.assertThat(usersFound.containsAll(getUsers("jonDoe", "janeDoe", "pieDoe", "cookieDoe"))).isTrue();
	}

	@Test
	public void find() {

		SelectResults<User> findResults =
			this.usersTemplate.find("SELECT u FROM /Users u WHERE u.username LIKE $1 AND u.active = $2", "%Doe", true);

		Assertions.assertThat(findResults).isNotNull();

		List<User> usersFound = findResults.asList();

		Assertions.assertThat(usersFound).isNotNull();
		Assertions.assertThat(usersFound.size()).isEqualTo(2);
		Assertions.assertThat(usersFound.containsAll(getUsers("jonDoe", "cookieDoe"))).isTrue();
	}

	// The following query is syntactically correct but does NOT work!!!
	// "SELECT keys FROM /Users u, u.keySet keys WHERE u.active = false ORDER BY u.username ASC"
	@Test
	public void findKeys() {

		User mandyHandy = getUser("mandyHandy");

		this.users.put(mandyHandy.getUsername(), mandyHandy);

		String query = "SELECT u.key FROM /Users.entrySet u WHERE u.value.active = false ORDER BY u.value.username ASC";

		SelectResults<String> results = this.usersTemplate.find(query);

		Assertions.assertThat(results).isNotNull();
		Assertions.assertThat(results).hasSize(4);
		Assertions.assertThat(results.asList()).containsExactly("janeDoe", "mandyHandy", "pieDoe", "randyHandy");
	}

	@Test
	public void findLimitedKeys() {

		String query = "SELECT u.key"
			+ " FROM /Users.entrySet u"
			+ " WHERE u.value.active = false"
			+ " AND u.value.username LIKE '%Doe'"
			+ " ORDER BY u.value.username ASC"
			+ " LIMIT 1";

		SelectResults<String> results = this.usersTemplate.find(query);

		Assertions.assertThat(results).isNotNull();
		Assertions.assertThat(results).hasSize(1);
		Assertions.assertThat(results.asList()).containsExactly("janeDoe");
	}

	@Test
	public void findUniqueReturnsResult() {

		User jonDoe =
			this.usersTemplate.findUnique("SELECT u FROM /Users u WHERE u.username = $1", "jonDoe");

		Assertions.assertThat(jonDoe).isNotNull();
		Assertions.assertThat(jonDoe).isEqualTo(getUser("jonDoe"));
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void findUniqueReturnsNoResult() {
		this.usersTemplate.findUnique("SELECT u FROM /Users u WHERE u.username = $1", "benDover");
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void findUniqueReturnsTooManyResults() {
		this.usersTemplate.findUnique("SELECT u FROM /Users u WHERE u.username LIKE $1", "%Doe");
	}

	@Configuration
	static class GemfireTemplateConfiguration {

		Properties gemfireProperties() {

			Properties gemfireProperties = new Properties();

			gemfireProperties.setProperty("name", applicationName());
			gemfireProperties.setProperty("log-level", logLevel());

			return gemfireProperties;
		}

		String applicationName() {
			return GemfireTemplateIntegrationTests.class.getName();
		}

		String logLevel() {
			return System.getProperty("gemfire.log-level", DEFAULT_GEMFIRE_LOG_LEVEL);
		}

		@Bean
		ClientCacheFactoryBean gemfireCache() {

			ClientCacheFactoryBean gemfireCache = new ClientCacheFactoryBean();
			gemfireCache.setPoolName("server-pool");
			gemfireCache.setClose(false);
			gemfireCache.setProperties(gemfireProperties());

			return gemfireCache;
		}

		@Bean(name = "Users")
		ClientRegionFactoryBean<String, User> usersRegion(ClientCache gemfireCache) {

			ClientRegionFactoryBean<String, User> usersRegion = new ClientRegionFactoryBean<>();

			usersRegion.setCache(gemfireCache);
			usersRegion.setPoolName("server-pool");
			usersRegion.setShortcut(ClientRegionShortcut.LOCAL);
			usersRegion.setPersistent(false);

			return usersRegion;
		}

		@Bean
		GemfireTemplate usersTemplate(@Qualifier("Users") Region<Object, Object> users) {
			return new GemfireTemplate(users);
		}

		@Bean("server-pool")
		PoolFactoryBean pool() {
			PoolFactoryBean pool = new PoolFactoryBean();
			pool.setLocators(new ConnectionEndpoint[] { new ConnectionEndpoint("localhost", gemFireCluster.getLocatorPort()) });
			return pool;
		}
	}
}
