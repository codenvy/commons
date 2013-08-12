/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.commons.lang;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/** A collection of utilities for encoding URLs. */

public class URLEncodedUtils {

    private static final String PARAMETER_SEPARATOR  = "&";
    private static final String NAME_VALUE_SEPARATOR = "=";

    /**
     * Returns Map<String, Set<String>>  as built from the
     * URI's query portion. For example, a URI of
     * http://example.org/path/to/file?a=1&b=2&c=3&c=4 would return a Map three
     * key is a name name of parameter Set<String> is a values, a={1},
     * one for b={2} and two for c={3,4}.
     *
     * <p/>
     * This is typically useful while parsing an HTTP PUT.
     *
     * @param uri
     *         uri to parse
     * @param encoding
     *         encoding to use while parsing the query
     */
    public static Map<String, Set<String>> parse(final URI uri, final String encoding) {
        Map<String, Set<String>> result = Collections.emptyMap();
        final String query = uri.getRawQuery();
        if (query != null && query.length() > 0) {
            result = new HashMap<>();
            parse(result, new Scanner(query), encoding);
        }
        return result;
    }

    /**
     * Adds all parameters within the Scanner to the list of
     * <code>parameters</code>, as encoded by <code>encoding</code>. For
     * example, a scanner containing the string <code>a=1&b=2&c=3</code> would
     * add the Map<String, Set<String>>  a=1, b=2, and c=3 to the
     * list of parameters.
     *
     * @param parameters
     *         List to add parameters to.
     * @param scanner
     *         Input that contains the parameters to parse.
     * @param encoding
     *         Encoding to use when decoding the parameters.
     */
    public static void parse(final Map<String, Set<String>> parameters, final Scanner scanner, final String encoding) {
        scanner.useDelimiter(PARAMETER_SEPARATOR);
        while (scanner.hasNext()) {
            final String[] nameValue = scanner.next().split(NAME_VALUE_SEPARATOR);
            if (nameValue.length == 0 || nameValue.length > 2)
                throw new IllegalArgumentException("bad parameter");

            final String name = decode(nameValue[0], encoding);
            String value = null;
            if (nameValue.length == 2)
                value = decode(nameValue[1], encoding);

            Set<String> values = parameters.get(name);
            if (values == null) {
                values = new LinkedHashSet<>();
                parameters.put(name, values);
            }
            if (value != null) {
                values.add(value);
            }
        }
    }

    /**
     * Returns a String that is suitable for use as an <code>application/x-www-form-urlencoded</code>
     * list of parameters in an HTTP PUT or HTTP POST.
     *
     * @param parameters
     *         The parameters to include.
     * @param encoding
     *         The encoding to use.
     */
    public static String format(final Map<String, Set<String>> parameters, final String encoding) {
        final StringBuilder result = new StringBuilder();

        for (Map.Entry<String, Set<String>> parameter : parameters.entrySet()) {
            final String encodedName = encode(parameter.getKey(), encoding);
            final Set<String> values = parameter.getValue();
            if (values == null || values.size() == 0) {
                result.append(encodedName);
                result.append(NAME_VALUE_SEPARATOR);
                result.append("");
            } else {
                for (String value : values) {
                    final String encodedValue = value != null ? encode(value, encoding) : "";
                    if (result.length() > 0)
                        result.append(PARAMETER_SEPARATOR);
                    result.append(encodedName);
                    result.append(NAME_VALUE_SEPARATOR);
                    result.append(encodedValue);
                }
            }
        }
        return result.toString();
    }

    private static String decode(final String content, final String encoding) {
        try {
            return URLDecoder.decode(content,
                                     encoding != null ? encoding : "UTF-8");
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    private static String encode(final String content, final String encoding) {
        try {
            return URLEncoder.encode(content,
                                     encoding != null ? encoding : "UTF-8");
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

}
