/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.library;

import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.util.Thawed;

import java.lang.reflect.Constructor;

import static com.github.fge.jsonschema.messages.ValidationConfigurationMessages.*;

public final class LibraryBuilder
    implements Thawed<Library>
{
    final DictionaryBuilder<SyntaxChecker> syntaxCheckers;
    final DictionaryBuilder<Digester> digesters;
    final DictionaryBuilder<Constructor<? extends KeywordValidator>> validators;
    final DictionaryBuilder<FormatAttribute> formatAttributes;

    LibraryBuilder()
    {
        syntaxCheckers = Dictionary.newBuilder();
        digesters = Dictionary.newBuilder();
        validators = Dictionary.newBuilder();
        formatAttributes = Dictionary.newBuilder();
    }

    LibraryBuilder(final Library library)
    {
        syntaxCheckers = library.syntaxCheckers.thaw();
        digesters = library.digesters.thaw();
        validators = library.validators.thaw();
        formatAttributes = library.formatAttributes.thaw();
    }

    LibraryBuilder addKeyword(final Keyword keyword)
    {
        /*
         * The only optional element of a keyword is its validator class
         */
        final String name = keyword.name;
        removeKeyword(name);

        syntaxCheckers.addEntry(name, keyword.syntaxChecker);
        digesters.addEntry(name, keyword.digester);
        if (keyword.constructor != null)
            validators.addEntry(name, keyword.constructor);
        return this;
    }

    LibraryBuilder removeKeyword(final String name)
    {
        if (name == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NULL_NAME));
        syntaxCheckers.removeEntry(name);
        digesters.removeEntry(name);
        validators.removeEntry(name);
        return this;
    }

    LibraryBuilder addFormatAttribute(final String name,
        final FormatAttribute attribute)
    {
        removeFormatAttribute(name);
        if (attribute == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NULL_ATTRIBUTE));
        formatAttributes.addEntry(name, attribute);
        return this;
    }

    LibraryBuilder removeFormatAttribute(final String name)
    {
        if (name == null)
            throw new ValidationConfigurationError(new ProcessingMessage()
                .message(NULL_FORMAT));
        formatAttributes.removeEntry(name);
        return this;
    }

    @Override
    public Library freeze()
    {
        return new Library(this);
    }
}