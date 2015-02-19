/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.inject;

import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeConverter;

/** @author andrew00x */
public class StringArrayConverter extends AbstractModule implements TypeConverter {
    @Override
    public Object convert(String value, TypeLiteral<?> toType) {
        Iterable<String> strings = Splitter.on(",").split(value);
        return FluentIterable.from(strings).toArray(String.class);
    }

    @Override
    protected void configure() {
        convertToTypes(Matchers.only(TypeLiteral.get(String[].class)), this);
    }
}
