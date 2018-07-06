package com.sphenon.engines.generator.classes;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.tracking.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.javacode.*;
import com.sphenon.basics.expression.*;

import com.sphenon.basics.configuration.annotations.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.returncodes.*;
import com.sphenon.engines.generator.tom.*;

import java.io.BufferedReader;

public class BootstrapParser implements TemplateParser {

    // Configuration ------------------------------------------------------------------

    @Configuration public interface Config {
        String getTemplateTypeAlias(CallContext context, String alias_name);
    }
    static Config config = Configuration_BootstrapParser.get(RootContext.getInitialisationContext());

    // --------------------------------------------------------------------------------

    public BootstrapParser(CallContext context, Template template) {
        this.template = template;
    }

    public long getLastModification(CallContext context) throws InvalidTemplateSyntax {
        TemplateParser tp = this.getTemplateParser(context);
        return tp.getLastModification(context);
    }

    public String getId(CallContext context) throws InvalidTemplateSyntax {
        TemplateParser tp = this.getTemplateParser(context);
        return tp.getId(context);
    }

    public Origin getOrigin (CallContext context) {
        try {
            return this.getTemplateParser(context).getOrigin(context);
        } catch (InvalidTemplateSyntax its) {
            CustomaryContext.create((Context)context).throwPreConditionViolation(context, its, "exception unexpected here");
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
    }

    // --------------------------------------------------------------------------------

    static protected RegularExpression template_type_re = new RegularExpression("(?<![A-Za-z0-9])G-([0-9\\.]+)-([A-Za-z0-9_]+)-([0-9\\.]+)(?![A-Za-z0-9])");

    protected Template template;

    protected Template optionally_wrapped;
    protected TemplateParser template_parser;
    protected String[] matches;

    protected TemplateParser getTemplateParser (CallContext context) throws InvalidTemplateSyntax {

        if (this.optionally_wrapped == null) {
            String firstline = null;

            optionally_wrapped = template;

            String alias = template.getTemplateTypeAlias(context);
            String tta;
            boolean has_alias = (alias != null && alias.length() != 0);
            if (has_alias) {
                firstline = config.getTemplateTypeAlias(context, alias);            
            } else {
                
                // this will store the reader, so that the next TemplateParser
                // does not see the first line which is processed here
                optionally_wrapped = new Class_TemplateWrapper(context, template);
                
                BufferedReader reader = optionally_wrapped.getReader(context);
                try {
                    firstline = reader.readLine();
                } catch (java.io.IOException ioe) {
                    CustomaryContext.create((Context)context).throwEnvironmentFailure(context, "Could not read (first line of) template '%(template)'", "template", template.getFullClassName(context));
                    throw (ExceptionEnvironmentFailure) null; // compiler insists
                }
            }
            
            matches = template_type_re.tryGetMatches(context, firstline);
            if (matches == null) {
                if (has_alias) {
                    InvalidTemplateSyntax.createAndThrow(context, "Could not identify template type, first line of template '%(template)' does not contain type descriptor 'G-GVersion-TVariant-TVersion' : '%(line)'", "line", firstline, "template", template.getFullClassName(context));
                    throw (InvalidTemplateSyntax) null;
                } else {
                    InvalidTemplateSyntax.createAndThrow(context, "Could not identify template type, alias '%(alias)' definition for template '%(template)' does not contain type descriptor 'G-GVersion-TVariant-TVersion' : '%(line)'", "alias", alias, "line", firstline, "template", template.getFullClassName(context));
                    throw (InvalidTemplateSyntax) null;
                }
            }
            
            // template.setSignature(context, new Class_Signature(context, matches[3], template));

            TemplateParserRegistry tpr = TemplateParserRegistry.get(context);
            this.template_parser = tpr.getParser(context, matches[0], matches[1], matches[2]);
        }
        return this.template_parser;
    }

    protected boolean already_parsed = false;

    public TOMNode parse (CallContext context, Template template, JavaCodeManager java_code_manager, TOMNode current_node) throws InvalidTemplateSyntax {
        if (already_parsed) {
            this.optionally_wrapped = null;
        }
        TemplateParser tp = this.getTemplateParser(context);
        current_node = tp.parse(context, this.optionally_wrapped, java_code_manager, current_node);
        this.already_parsed = true;
        return current_node;
    }
}
