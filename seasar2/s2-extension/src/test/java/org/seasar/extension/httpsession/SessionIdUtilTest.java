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
package org.seasar.extension.httpsession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;
import org.seasar.framework.mock.servlet.MockHttpServletResponseImpl;
import org.seasar.framework.mock.servlet.MockServletContextImpl;

/**
 * @author higa
 * 
 */
public class SessionIdUtilTest extends TestCase {

    public void tearDown() {
        SessionIdUtil.cookieName = SessionIdUtil.SESSION_ID_KEY;
        SessionIdUtil.cookieMaxAge = -1;
        SessionIdUtil.cookiePath = null;
        SessionIdUtil.cookieSecure = null;
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#getSessionIdFromCookie(javax.servlet.http.HttpServletRequest)}
     * .
     */
    public void testGetSessionIdFromCookie() {
        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        assertNull(SessionIdUtil.getSessionIdFromCookie(request));
        request.addCookie(new Cookie("S2SESSIONID", "123"));
        assertEquals("123", SessionIdUtil.getSessionIdFromCookie(request));
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#getSessionIdFromCookie(javax.servlet.http.HttpServletRequest)}
     * .
     */
    public void testGetSessionIdFromCookieWithCustomId() {
        SessionIdUtil.cookieName = "CUSTOMID";
        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        assertNull(SessionIdUtil.getSessionIdFromCookie(request));
        request.addCookie(new Cookie("CUSTOMID", "123"));
        assertEquals("123", SessionIdUtil.getSessionIdFromCookie(request));
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#getSessionIdFromURL(javax.servlet.http.HttpServletRequest)}
     * .
     */
    public void testGetSessionIdFromURI() {
        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html;S2SESSIONID=123");
        assertEquals("123", SessionIdUtil.getSessionIdFromURL(request));
        request = new MockHttpServletRequestImpl(context,
                "hello.html;S2SESSIONID=123?aaa=111");
        assertEquals("123", SessionIdUtil.getSessionIdFromURL(request));
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#getSessionIdFromURL(javax.servlet.http.HttpServletRequest)}
     * .
     */
    public void testGetSessionIdFromURIWithCustomId() {
        SessionIdUtil.cookieName = "CUSTOMID";
        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html;CUSTOMID=123");
        assertEquals("123", SessionIdUtil.getSessionIdFromURL(request));
        request = new MockHttpServletRequestImpl(context,
                "hello.html;CUSTOMID=123?aaa=111");
        assertEquals("123", SessionIdUtil.getSessionIdFromURL(request));
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#getSessionIdFromURL(javax.servlet.http.HttpServletRequest)}
     * .
     */
    public void testGetSessionIdFromURI2() {
        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        assertNull(SessionIdUtil.getSessionIdFromURL(request));
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#getSessionIdFromURL(javax.servlet.http.HttpServletRequest)}
     * .
     */
    public void testGetSessionIdFromURI2WithCustomId() {
        SessionIdUtil.cookieName = "CUSTOMID";
        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        assertNull(SessionIdUtil.getSessionIdFromURL(request));
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#rewriteURL(String, javax.servlet.http.HttpServletRequest)}
     * .
     */
    public void testRewriteURL() {
        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        request.getSession(true);
        String url = SessionIdUtil.rewriteURL("/example/hello.html", request);
        System.out.println(url);
        assertTrue(url.indexOf(SessionIdUtil.SESSION_ID_KEY) >= 0);
        url = SessionIdUtil.rewriteURL("/example/hello.html?aaa=111", request);
        System.out.println(url);
        assertTrue(url.indexOf(SessionIdUtil.SESSION_ID_KEY) < url.indexOf('?'));
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#rewriteURL(String, javax.servlet.http.HttpServletRequest)}
     * .
     */
    public void testRewriteURLWithCustomId() {
        SessionIdUtil.cookieName = "CUSTOMID";
        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        request.getSession(true);
        String url = SessionIdUtil.rewriteURL("/example/hello.html", request);
        System.out.println(url);
        assertTrue(url.indexOf(SessionIdUtil.cookieName) >= 0);
        url = SessionIdUtil.rewriteURL("/example/hello.html?aaa=111", request);
        System.out.println(url);
        assertTrue(url.indexOf(SessionIdUtil.cookieName) < url.indexOf('?'));
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#writeCookie(HttpServletRequest)}
     */
    public void testWriteCookie() {
        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl(
                request);

        SessionIdUtil.writeCookie(request, response, "hoge");
        Cookie[] cookies = response.getCookies();
        assertNotNull(cookies);
        assertEquals(1, cookies.length);
        Cookie cookie = cookies[0];
        assertEquals(SessionIdUtil.SESSION_ID_KEY, cookie.getName());
        assertEquals("/example", cookie.getPath());
        assertEquals(-1, cookie.getMaxAge());
        assertEquals(false, cookie.getSecure());
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#writeCookie(HttpServletRequest)}
     */
    public void testWriteCookieWithCustomId() {
        SessionIdUtil.cookieName = "CUSTOMID";
        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl(
                request);

        SessionIdUtil.writeCookie(request, response, "hoge");
        Cookie[] cookies = response.getCookies();
        assertNotNull(cookies);
        assertEquals(1, cookies.length);
        Cookie cookie = cookies[0];
        assertEquals(SessionIdUtil.cookieName, cookie.getName());
        assertEquals("/example", cookie.getPath());
        assertEquals(-1, cookie.getMaxAge());
        assertEquals(false, cookie.getSecure());
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#writeCookie(HttpServletRequest)}
     */
    public void testWriteCookie_RootContext() {
        MockServletContextImpl context = new MockServletContextImpl("");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl(
                request);

        SessionIdUtil.writeCookie(request, response, "hoge");
        Cookie[] cookies = response.getCookies();
        assertNotNull(cookies);
        assertEquals(1, cookies.length);
        Cookie cookie = cookies[0];
        assertEquals(SessionIdUtil.SESSION_ID_KEY, cookie.getName());
        assertEquals("/", cookie.getPath());
        assertEquals(-1, cookie.getMaxAge());
        assertEquals(false, cookie.getSecure());
    }

    /**
     * Test method for
     * {@link org.seasar.extension.httpsession.SessionIdUtil#writeCookie(HttpServletRequest)}
     */
    public void testWriteCookie_RootContextWithCustomId() {
        SessionIdUtil.cookieName = "CUSTOMID";
        MockServletContextImpl context = new MockServletContextImpl("");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl(
                request);

        SessionIdUtil.writeCookie(request, response, "hoge");
        Cookie[] cookies = response.getCookies();
        assertNotNull(cookies);
        assertEquals(1, cookies.length);
        Cookie cookie = cookies[0];
        assertEquals(SessionIdUtil.cookieName, cookie.getName());
        assertEquals("/", cookie.getPath());
        assertEquals(-1, cookie.getMaxAge());
        assertEquals(false, cookie.getSecure());
    }

    /**
     * 
     */
    public void testWriteCookieWithOptions() {
        SessionIdUtil.cookieName = "MY_SESSION_ID";
        SessionIdUtil.cookieMaxAge = 3600;
        SessionIdUtil.cookiePath = "/";
        SessionIdUtil.cookieSecure = Boolean.TRUE;

        MockServletContextImpl context = new MockServletContextImpl("/example");
        MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(
                context, "hello.html");
        MockHttpServletResponseImpl response = new MockHttpServletResponseImpl(
                request);

        SessionIdUtil.writeCookie(request, response, "hoge");
        Cookie[] cookies = response.getCookies();
        assertNotNull(cookies);
        assertEquals(1, cookies.length);
        Cookie cookie = cookies[0];
        assertEquals("MY_SESSION_ID", cookie.getName());
        assertEquals("/", cookie.getPath());
        assertEquals(3600, cookie.getMaxAge());
        assertEquals(true, cookie.getSecure());
    }

}
