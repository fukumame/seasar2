/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.extension.jdbc.meta;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import junit.framework.TestCase;

import org.seasar.extension.jdbc.EntityMeta;
import org.seasar.extension.jdbc.JoinColumnMeta;
import org.seasar.extension.jdbc.PropertyMeta;
import org.seasar.extension.jdbc.RelationshipType;
import org.seasar.extension.jdbc.entity.Aaa;
import org.seasar.extension.jdbc.entity.Bbb;
import org.seasar.extension.jdbc.exception.BothMappedByAndJoinColumnRuntimeException;
import org.seasar.extension.jdbc.exception.JoinColumnNameAndReferencedColumnNameMandatoryRuntimeException;
import org.seasar.extension.jdbc.exception.JoinColumnNotAllowedRuntimeException;
import org.seasar.extension.jdbc.exception.MappedByMandatoryRuntimeException;
import org.seasar.extension.jdbc.exception.MappedByNotIdenticalRuntimeException;
import org.seasar.extension.jdbc.exception.MappedByPropertyNotFoundRuntimeException;
import org.seasar.extension.jdbc.exception.NonRelationshipRuntimeException;
import org.seasar.extension.jdbc.exception.OneToManyNotGenericsRuntimeException;
import org.seasar.extension.jdbc.exception.OneToManyNotListRuntimeException;
import org.seasar.extension.jdbc.exception.RelationshipNotEntityRuntimeException;
import org.seasar.framework.convention.impl.PersistenceConventionImpl;

/**
 * @author higa
 * 
 */
public class PropertyMetaFactoryImplTest extends TestCase {

	@Transient
	private PropertyMetaFactoryImpl factory;

	@SuppressWarnings("unused")
	private transient String hoge;

	@SuppressWarnings("unused")
	@Version
	private Integer version;

	@SuppressWarnings("unused")
	@OneToOne
	@JoinColumn(name = "bbb_id", referencedColumnName = "id")
	private Bbb bbb;

	@SuppressWarnings("unused")
	@OneToOne
	@JoinColumns( { @JoinColumn, @JoinColumn })
	private Bbb bbb2;

	private EntityMeta entityMeta;

	@Override
	protected void setUp() {
		entityMeta = new EntityMeta("Aaa");
		entityMeta.setEntityClass(Aaa.class);
		factory = new PropertyMetaFactoryImpl();
		PersistenceConventionImpl convention = new PersistenceConventionImpl();
		factory.setPersistenceConvention(convention);
		ColumnMetaFactoryImpl cmFactory = new ColumnMetaFactoryImpl();
		cmFactory.setPersistenceConvention(convention);
		factory.setColumnMetaFactory(cmFactory);
	}

	/**
	 * @throws Exception
	 */
	public void testId() throws Exception {
		Field field = Aaa.class.getDeclaredField("id");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertTrue(propertyMeta.isId());
	}

	/**
	 * @throws Exception
	 */
	public void testId_noid() throws Exception {
		Field field = Aaa.class.getDeclaredField("name");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertFalse(propertyMeta.isId());
	}

	/**
	 * @throws Exception
	 */
	public void testTransient() throws Exception {
		Field field = getClass().getDeclaredField("factory");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertTrue(propertyMeta.isTransient());
	}

	/**
	 * @throws Exception
	 */
	public void testTransient_modifier() throws Exception {
		Field field = getClass().getDeclaredField("hoge");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertTrue(propertyMeta.isTransient());
	}

	/**
	 * @throws Exception
	 */
	public void testTransient_notransient() throws Exception {
		Field field = Aaa.class.getDeclaredField("name");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertFalse(propertyMeta.isTransient());
	}

	/**
	 * @throws Exception
	 */
	public void testColumnMeta() throws Exception {
		Field field = Aaa.class.getDeclaredField("id");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertNotNull(propertyMeta.getColumnMeta());
	}

	/**
	 * @throws Exception
	 */
	public void testField() throws Exception {
		Field field = Aaa.class.getDeclaredField("id");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertSame(field, propertyMeta.getField());
	}

	/**
	 * @throws Exception
	 */
	public void testName() throws Exception {
		Field field = Aaa.class.getDeclaredField("id");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertEquals("id", propertyMeta.getName());
	}

	/**
	 * @throws Exception
	 */
	public void testPropertyClass() throws Exception {
		Field field = Aaa.class.getDeclaredField("id");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertEquals(Integer.class, propertyMeta.getPropertyClass());
	}

	/**
	 * @throws Exception
	 */
	public void testVersion() throws Exception {
		Field field = getClass().getDeclaredField("version");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertTrue(propertyMeta.isVersion());
	}

	/**
	 * @throws Exception
	 */
	public void testVersion_noannotation() throws Exception {
		Field field = Aaa.class.getDeclaredField("id");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertFalse(propertyMeta.isVersion());
	}

	/**
	 * @throws Exception
	 */
	public void testJoinColumnMeta() throws Exception {
		Field field = getClass().getDeclaredField("bbb");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertEquals(1, propertyMeta.getJoinColumnMetaList().size());
		JoinColumnMeta joinColumnMeta = propertyMeta.getJoinColumnMetaList()
				.get(0);
		assertEquals("bbb_id", joinColumnMeta.getName());
		assertEquals("id", joinColumnMeta.getReferencedColumnName());
	}

	/**
	 * @throws Exception
	 */
	public void testJoinColumnsMeta() throws Exception {
		Field field = getClass().getDeclaredField("bbb");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertEquals(1, propertyMeta.getJoinColumnMetaList().size());
		JoinColumnMeta joinColumnMeta = propertyMeta.getJoinColumnMetaList()
				.get(0);
		assertEquals("bbb_id", joinColumnMeta.getName());
		assertEquals("id", joinColumnMeta.getReferencedColumnName());
	}

	/**
	 * @throws Exception
	 */
	public void testJoinColumnsMeta_empty() throws Exception {
		entityMeta.setEntityClass(getClass());
		entityMeta.setName("MyTest");
		Field field = getClass().getDeclaredField("bbb2");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (JoinColumnNameAndReferencedColumnNameMandatoryRuntimeException e) {
			System.out.println(e);
			assertEquals("MyTest", e.getEntityName());
			assertEquals("bbb2", e.getPropertyName());
		}
	}

	/**
	 * @throws Exception
	 * 
	 */
	public void testJoinColumnMeta_noentity() throws Exception {
		Field field = Aaa.class.getDeclaredField("id");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertEquals(0, propertyMeta.getJoinColumnMetaList().size());
	}

	/**
	 * @throws Exception
	 */
	public void testOneToOne() throws Exception {
		Field field = MyAaa.class.getDeclaredField("bbb");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertEquals(RelationshipType.ONE_TO_ONE, propertyMeta
				.getRelationshipType());
		assertEquals(MyBbb.class, propertyMeta.getRelationshipClass());
		assertEquals(1, propertyMeta.getJoinColumnMetaList().size());
	}

	/**
	 * @throws Exception
	 */
	public void testOneToOneInverse() throws Exception {
		entityMeta.setName("MyBbb");
		entityMeta.setEntityClass(MyBbb.class);
		Field field = MyBbb.class.getDeclaredField("aaa");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertEquals(RelationshipType.ONE_TO_ONE, propertyMeta
				.getRelationshipType());
		assertEquals(MyAaa.class, propertyMeta.getRelationshipClass());
		assertEquals("bbb", propertyMeta.getMappedBy());
	}

	/**
	 * @throws Exception
	 */
	public void testOneToOne_notEntity() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("myDto");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (RelationshipNotEntityRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("myDto", e.getPropertyName());
			assertEquals(MyDto.class, e.getRelationshipClass());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testOneToOne_mappedByPropertyNotFound() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("aaa");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (MappedByPropertyNotFoundRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("aaa", e.getPropertyName());
			assertEquals("xxx", e.getMappedBy());
			assertEquals(MyAaa.class, e.getInverseRelationshipClass());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testOneToOne_mappedByNotIdentical() throws Exception {
		entityMeta.setEntityClass(BadBbb.class);
		entityMeta.setName("BadBbb");
		Field field = BadBbb.class.getDeclaredField("badAaa");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (MappedByNotIdenticalRuntimeException e) {
			System.out.println(e);
			assertEquals("BadBbb", e.getEntityName());
			assertEquals("badAaa", e.getPropertyName());
			assertEquals("badCcc", e.getMappedBy());
			assertEquals(BadAaa.class, e.getInverseRelationshipClass());
			assertEquals("badCcc", e.getInversePropertyName());
			assertEquals(BadCcc.class, e.getInversePropertyClass());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testOneToOne_bothMappedByAndJoinColumn() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("aaa6");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (BothMappedByAndJoinColumnRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("aaa6", e.getPropertyName());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testOneToMany() throws Exception {
		entityMeta.setName("MyBbb");
		entityMeta.setEntityClass(MyBbb.class);
		Field field = MyBbb.class.getDeclaredField("ddds");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertEquals(RelationshipType.ONE_TO_MANY, propertyMeta
				.getRelationshipType());
		assertEquals(MyDdd.class, propertyMeta.getRelationshipClass());
		assertEquals("bbb", propertyMeta.getMappedBy());
	}

	/**
	 * @throws Exception
	 */
	public void testOneToMany_notList() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("aaa2");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (OneToManyNotListRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("aaa2", e.getPropertyName());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testOneToMany_notGenerics() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("aaa3");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (OneToManyNotGenericsRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("aaa3", e.getPropertyName());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testOneToMany_notEntity() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("myDto3");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (RelationshipNotEntityRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("myDto3", e.getPropertyName());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testOneToMany_mappedByPropertyNotFound() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("aaa4");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (MappedByPropertyNotFoundRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("aaa4", e.getPropertyName());
			assertEquals("xxx", e.getMappedBy());
			assertEquals(MyAaa.class, e.getInverseRelationshipClass());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testOneToMany_mappedByNotIdentical() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("aaa5");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (MappedByNotIdenticalRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("aaa5", e.getPropertyName());
			assertEquals("bbb", e.getMappedBy());
			assertEquals(MyAaa.class, e.getInverseRelationshipClass());
			assertEquals("bbb", e.getInversePropertyName());
			assertEquals(MyBbb.class, e.getInversePropertyClass());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testOneToMany_bothMappedByAndJoinColumn() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("aaa7");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (BothMappedByAndJoinColumnRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("aaa7", e.getPropertyName());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testOneToMany_mappedByMandatory() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("aaa8");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (MappedByMandatoryRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("aaa8", e.getPropertyName());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testManyToOne() throws Exception {
		entityMeta.setName("MyDdd");
		entityMeta.setEntityClass(MyDdd.class);
		Field field = MyDdd.class.getDeclaredField("bbb");
		PropertyMeta propertyMeta = factory.createPropertyMeta(field,
				entityMeta);
		assertEquals(RelationshipType.MANY_TO_ONE, propertyMeta
				.getRelationshipType());
		assertEquals(MyBbb.class, propertyMeta.getRelationshipClass());
		assertEquals(1, propertyMeta.getJoinColumnMetaList().size());
	}

	/**
	 * @throws Exception
	 */
	public void testManyToOne_notEntity() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("myDto4");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (RelationshipNotEntityRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("myDto4", e.getPropertyName());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testRelation_dtoNotRelationship() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("myDto2");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (NonRelationshipRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("myDto2", e.getPropertyName());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testRelation_joinColumnNotAllowed() throws Exception {
		entityMeta.setEntityClass(BadAaa2.class);
		entityMeta.setName("BadAaa2");
		Field field = BadAaa2.class.getDeclaredField("id");
		try {
			factory.createPropertyMeta(field, entityMeta);
			fail();
		} catch (JoinColumnNotAllowedRuntimeException e) {
			System.out.println(e);
			assertEquals("BadAaa2", e.getEntityName());
			assertEquals("id", e.getPropertyName());
		}
	}

	@Entity
	private static class MyAaa {
		/**
		 * 
		 */
		@Id
		public Integer id;

		/**
		 * 
		 */
		public Integer bbbId;

		/**
		 * 
		 */
		@OneToOne
		@JoinColumn(name = "BBB_ID", referencedColumnName = "ID")
		public MyBbb bbb;
	}

	@Entity
	private static class MyBbb {
		/**
		 * 
		 */
		@Id
		public Integer id;

		/**
		 * 
		 */
		@OneToOne(mappedBy = "bbb")
		public MyAaa aaa;

		/**
		 * 
		 */
		@OneToMany(mappedBy = "bbb")
		public List<MyDdd> ddds;
	}

	@Entity
	private static class MyDdd {
		/**
		 * 
		 */
		@Id
		public Integer id;

		/**
		 * 
		 */
		public Integer bbbId;

		/**
		 * 
		 */
		@ManyToOne
		@JoinColumn(name = "BBB_ID", referencedColumnName = "ID")
		public MyBbb bbb;
	}

	@Entity
	private static class BadAaa {
		/**
		 * 
		 */
		@Id
		public Integer aaaId;

		/**
		 * 
		 */
		public Integer cccId;

		/**
		 * 
		 */
		@JoinColumn(name = "CCC_ID", referencedColumnName = "CCC_ID")
		public BadCcc badCcc;
	}

	@Entity
	private static class BadAaa2 {
		/**
		 * 
		 */
		@Id
		@JoinColumn
		public Integer id;

		/**
		 * 
		 */
		public Integer cccId;

		/**
		 * 
		 */
		@OneToOne
		public MyDto myDto;

		/**
		 * 
		 */
		public MyDto myDto2;

		/**
		 * 
		 */
		@OneToMany
		public List<MyDto> myDto3;

		/**
		 * 
		 */
		@ManyToOne
		public MyDto myDto4;

		/**
		 * 
		 */
		@OneToOne(mappedBy = "xxx")
		public MyAaa aaa;

		/**
		 * 
		 */
		@OneToMany
		public MyAaa aaa2;

		/**
		 * 
		 */
		@SuppressWarnings("unchecked")
        @OneToMany
		public List aaa3;

		/**
		 * 
		 */
		@OneToMany(mappedBy = "xxx")
		public List<MyAaa> aaa4;

		/**
		 * 
		 */
		@OneToMany(mappedBy = "bbb")
		public List<MyAaa> aaa5;

		/**
		 * 
		 */
		@OneToOne(mappedBy = "bbb")
		@JoinColumn(name = "BBB_ID", referencedColumnName = "ID")
		public MyAaa aaa6;

		/**
		 * 
		 */
		@OneToMany(mappedBy = "bbb")
		@JoinColumn(name = "BBB_ID", referencedColumnName = "ID")
		public List<MyAaa> aaa7;

		/**
		 * 
		 */
		@OneToMany
		public List<MyAaa> aaa8;
	}

	@Entity
	private static class BadBbb {
		/**
		 * 
		 */
		@Id
		public Integer bbbId;

		/**
		 * 
		 */

		@OneToOne(mappedBy = "badCcc")
		public BadAaa badAaa;
	}

	@Entity
	private static class BadCcc {
		/**
		 * 
		 */
		public Integer cccId;
	}

	private static class MyDto {

	}
}