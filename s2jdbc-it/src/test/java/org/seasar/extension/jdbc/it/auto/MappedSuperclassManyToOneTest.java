/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.extension.jdbc.it.auto;

import java.util.List;

import org.junit.runner.RunWith;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.JoinType;
import org.seasar.extension.jdbc.it.entity.ConcreateEmployee;
import org.seasar.framework.unit.Seasar2;

import static org.junit.Assert.*;

/**
 * @author taedium
 * 
 */
@RunWith(Seasar2.class)
public class MappedSuperclassManyToOneTest {

    private JdbcManager jdbcManager;

    /**
     * 
     * @throws Exception
     */
    public void testLeftOuterJoin_fetch() throws Exception {
        List<ConcreateEmployee> list =
            jdbcManager.from(ConcreateEmployee.class).leftOuterJoin(
                "department").getResultList();
        assertEquals(14, list.size());
        for (ConcreateEmployee e : list) {
            assertNotNull(e);
            assertNotNull(e.department);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testLeftOuterJoin() throws Exception {
        List<ConcreateEmployee> list =
            jdbcManager.from(ConcreateEmployee.class).leftOuterJoin(
                "department",
                false).getResultList();
        assertEquals(14, list.size());
        for (ConcreateEmployee e : list) {
            assertNotNull(e);
            assertNull(e.department);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testInnerJoin_fetch() throws Exception {
        List<ConcreateEmployee> list =
            jdbcManager.from(ConcreateEmployee.class).join(
                "department",
                JoinType.INNER).getResultList();
        assertEquals(14, list.size());
        for (ConcreateEmployee e : list) {
            assertNotNull(e);
            assertNotNull(e.department);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testInnerJoin() throws Exception {
        List<ConcreateEmployee> list =
            jdbcManager.from(ConcreateEmployee.class).join(
                "department",
                JoinType.INNER,
                false).getResultList();
        assertEquals(14, list.size());
        for (ConcreateEmployee e : list) {
            assertNotNull(e);
            assertNull(e.department);
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void testInnerJoin_fetch_self() throws Exception {
        List<ConcreateEmployee> list =
            jdbcManager.from(ConcreateEmployee.class).join(
                "manager",
                JoinType.INNER).getResultList();
        assertEquals(13, list.size());
        for (ConcreateEmployee e : list) {
            assertNotNull(e);
            assertNotNull(e.manager);
        }
    }

}