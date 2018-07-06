package com.sphenon.engines.generator;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.engines.factorysite.gates.*;
import com.sphenon.engines.factorysite.returncodes.*;

import com.sphenon.engines.generator.returncodes.*;

public class TemplateParserRegistry {

    static public TemplateParserRegistry get(CallContext context) {

        // ToDo: registry aus GeneratorContext holen

        return new TemplateParserRegistry (context);
    }

    public TemplateParserRegistry (CallContext context) {
    }

    public TemplateParser getParser (CallContext context, String generator_version, String template_variant, String template_version) {
        String parser_aggregate = "ctn://Class<Aggregate>/com/sphenon/engines/generator/parsers/TP-"+generator_version+"-"+template_variant+"-"+template_version+";class=com.sphenon.engines.generator.TemplateParser";
        Object o = null;
        try {
            return (TemplateParser) (o = FactorySiteGate.createObject(context, parser_aggregate));
        } catch (InvalidCTN ictn) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, ictn, "Cannot create template parser, ctn is invalid '%(ctn)'", "ctn", parser_aggregate);
            throw (ExceptionConfigurationError) null; // compiler insists
        } catch (ClassCastException cce) {
            CustomaryContext.create(Context.create(context)).throwConfigurationError(context, cce, "Cannot create template parser, ctn '%(ctn)' is valid, but does not denote a TemplateParser, but a '%(type)'", "ctn", parser_aggregate, "type", o.getClass().getName());
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }
}
