/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.snapshot;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.snapshot.SnapshotFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.data.gemfire.snapshot.event.ExportSnapshotApplicationEvent;
import org.springframework.data.gemfire.snapshot.event.ImportSnapshotApplicationEvent;
import org.springframework.data.gemfire.snapshot.event.SnapshotApplicationEvent;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.data.gemfire.tests.util.ThreadUtils;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Integration Tests testing the effects of the {@link SnapshotServiceFactoryBean} using Spring
 * {@link ApplicationEvent ApplicationEvents} to trigger imports and exports of cache {@link Region} data.
 *
 * @author John Blum
 * @see Test
 * @see Region
 * @see SnapshotServiceFactoryBean
 * @see ExportSnapshotApplicationEvent
 * @see ImportSnapshotApplicationEvent
 * @see SnapshotApplicationEvent
 * @see IntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.7.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class SnapshotApplicationEventTriggeredImportsExportsIntegrationTests extends IntegrationTestsSupport {

	protected static final AtomicLong ID_SEQUENCE = new AtomicLong(0L);

	protected static File snapshotsDirectory;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	@Qualifier("Doe")
	private Region<Long, Person> doe;

	@Autowired
	@Qualifier("EveryoneElse")
	private Region<Long, Person> everyoneElse;

	@Autowired
	@Qualifier("Handy")
	private Region<Long, Person> handy;

	@Autowired
	@Qualifier("People")
	private Region<Long, Person> people;

	@BeforeClass
	public static void setupSnapshotDirectoryAndFiles() throws Exception {

		snapshotsDirectory = new File(new File(new File(FileSystemUtils.WORKING_DIRECTORY, "gemfire"),
			"data"), "snapshots");

		assertThat(snapshotsDirectory.isDirectory() || snapshotsDirectory.mkdirs()).isTrue();

		File peopleSnapshotFile = new File(snapshotsDirectory, "people-snapshot.gfd");
		File nonHandyNonDoeSnapshotFile = new File(snapshotsDirectory, "nonHandyNonDoePeople-snapshot.gfd");

		assertThat(peopleSnapshotFile.isFile() || peopleSnapshotFile.createNewFile()).isTrue();
		assertThat(nonHandyNonDoeSnapshotFile.isFile() || nonHandyNonDoeSnapshotFile.createNewFile()).isTrue();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		//FileSystemUtils.deleteRecursive(snapshotsDirectory.getParentFile().getParentFile());
	}

	private void assertPeople(Region<Long, Person> targetRegion, Person... people) {

		assertThat(targetRegion.size()).isEqualTo(people.length);

		stream(nullSafeArray(people, Person.class))
			.forEach(person -> assertPerson(person, targetRegion.get(person.getId())));
	}

	private void assertPerson(Person expectedPerson, Person actualPerson) {

		assertThat(actualPerson)
			.describedAs(String.format("Expected [%1$s]; but was [%2$s]", expectedPerson, actualPerson))
			.isNotNull();

		assertThat(actualPerson.getId()).isEqualTo(expectedPerson.getId());
		assertThat(actualPerson.getFirstname()).isEqualTo(expectedPerson.getFirstname());
		assertThat(actualPerson.getLastname()).isEqualTo(expectedPerson.getLastname());
	}

	private Person newPerson(String firstName, String lastName) {
		return new Person(ID_SEQUENCE.incrementAndGet(), firstName, lastName);
	}

	private Person put(Region<Long, Person> targetRegion, Person person) {

		targetRegion.putIfAbsent(person.getId(), person);

		return person;
	}

	private void wait(int seconds, int expectedDoeSize, int expectedEveryoneSize, int expectedHandySize) {

		ThreadUtils.timedWait(TimeUnit.SECONDS.toMillis(seconds), 500,
			() -> doe.size() == expectedDoeSize
				|| everyoneElse.size() == expectedEveryoneSize
				|| handy.size() == expectedHandySize);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void exportsTriggeringImportsOnSnapshotApplicationEvents() {

		Person jonDoe = put(people, newPerson("Jon", "Doe"));
		Person janeDoe = put(people, newPerson("Jane", "Doe"));
		Person jackBlack = put(people, newPerson("Jack", "Black"));
		Person jackHandy = put(people, newPerson("Jack", "Handy"));
		Person joeDirt = put(people, newPerson("Joe", "Dirt"));

		SnapshotApplicationEvent<Long, Person> event =
			new ExportSnapshotApplicationEvent<>(this, people.getFullPath());

		eventPublisher.publishEvent(event);

		wait(15, 2, 2, 1);

		assertPeople(doe, jonDoe, janeDoe);
		assertPeople(everyoneElse, jackBlack, joeDirt);
		assertPeople(handy, jackHandy);

		Person cookieDoe = put(people, newPerson("Cookie", "Doe"));
		Person pieDoe = put(people, newPerson("Pie", "Doe"));
		Person sourDoe = put(people, newPerson("Sour", "Doe"));
		Person randyHandy = put(people, newPerson("Randy", "Handy"));
		Person sandyHandy = put(people, newPerson("Sandy", "Handy"));
		Person jackHill = put(people, newPerson("Jack", "Hill"));
		Person jillHill = put(people, newPerson("Jill", "Hill"));

		eventPublisher.publishEvent(event);

		wait(15, 5, 4, 3);

		assertPeople(doe, jonDoe, janeDoe, cookieDoe, pieDoe, sourDoe);
		assertPeople(everyoneElse, jackBlack, joeDirt, jackHill, jillHill);
		assertPeople(handy, jackHandy, randyHandy, sandyHandy);

		Person bobDoe = put(people, newPerson("Bob", "Doe"));
		Person mandyHandy = put(people, newPerson("Mandy", "Handy"));
		Person imaPigg = put(people, newPerson("Ima", "Pigg"));
		Person benDover = put(people, newPerson("Ben", "Dover"));

		eventPublisher.publishEvent(event);

		wait(15, 6, 6, 4);

		assertPeople(doe, jonDoe, janeDoe, cookieDoe, pieDoe, sourDoe, bobDoe);
		assertPeople(everyoneElse, jackBlack, joeDirt, jackHill, jillHill, imaPigg, benDover);
		assertPeople(handy, jackHandy, randyHandy, sandyHandy, mandyHandy);
	}

	static class LastNameSnapshotFilter implements SnapshotFilter<Long, Person> {

		private final String lastName;

		LastNameSnapshotFilter(String lastName) {
			Assert.hasText(lastName, "lastName must be specified");
			this.lastName = lastName;
		}

		protected String getLastName() {
			Assert.state(StringUtils.hasText(this.lastName), "lastName was not properly initialized");
			return this.lastName;
		}

		@Override
		public boolean accept(Map.Entry<Long, Person> entry) {
			return accept(entry.getValue());
		}

		public boolean accept(Person person) {
			return getLastName().equalsIgnoreCase(person.getLastname());
		}
	}

	static class NotLastNameSnapshotFilter extends LastNameSnapshotFilter {

		NotLastNameSnapshotFilter(String lastName) {
			super(lastName);
		}

		@Override
		public boolean accept(final Map.Entry<Long, Person> entry) {
			return !super.accept(entry);
		}
	}

	public static class SnapshotImportsMonitor {

		@Autowired
		private ApplicationEventPublisher eventPublisher;

		private static final Map<File, Long> snapshotFileLastModifiedMap = new ConcurrentHashMap<>(2);

		@Scheduled(fixedDelay = 1000)
		@SuppressWarnings("unchecked")
		public void processSnapshots() {

			boolean triggerEvent = false;

			File[] snapshotFiles = ArrayUtils.nullSafeArray(snapshotsDirectory
				.listFiles(FileSystemUtils.FileOnlyFilter.INSTANCE), File.class);

			for (File snapshotFile : snapshotFiles) {
				triggerEvent |= isUnprocessedSnapshotFile(snapshotFile);
			}

			if (triggerEvent) {
				eventPublisher.publishEvent(new ImportSnapshotApplicationEvent<Long, Person>(this));
			}
		}

		private boolean isUnprocessedSnapshotFile(File snapshotFile) {

			Long lastModified = snapshotFile.lastModified();
			Long previousLastModified = snapshotFileLastModifiedMap.get(snapshotFile);

			previousLastModified = previousLastModified != null ? previousLastModified : lastModified;

			snapshotFileLastModifiedMap.put(snapshotFile, lastModified);

			return previousLastModified < lastModified;
		}
	}
}
