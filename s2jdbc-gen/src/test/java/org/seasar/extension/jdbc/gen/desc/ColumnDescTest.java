/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.seasar.extension.jdbc.gen.desc;

import junitx.framework.Assert;

import org.junit.Test;
import org.seasar.extension.jdbc.gen.desc.ColumnDesc;

import static org.junit.Assert.*;

/**
 * @author taedium
 * 
 */
public class ColumnDescTest {

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testEquals() throws Exception {
        ColumnDesc columnDesc = new ColumnDesc();
        columnDesc.setName("AAA");
        ColumnDesc columnDesc2 = new ColumnDesc();
        columnDesc2.setName("aaa");
        ColumnDesc columnDesc3 = new ColumnDesc();
        columnDesc3.setName("XXX");

        assertEquals(columnDesc, columnDesc2);
        assertEquals(columnDesc.hashCode(), columnDesc2.hashCode());
        Assert.assertNotEquals(columnDesc, columnDesc3);
        Assert.assertNotEquals(columnDesc.hashCode(), columnDesc3.hashCode());
    }
}
