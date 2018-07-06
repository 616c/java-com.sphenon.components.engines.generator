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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.locating.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.returncodes.*;
import com.sphenon.engines.generator.tchandler.classes.*;

public class Class_FormalArgument implements FormalArgument {

    static protected RegularExpression arg_re = new RegularExpression("^(?: *)([^ ].*)(?: +)([A-Za-z0-9_]+)(?: *)$");

    public Class_FormalArgument (CallContext context, String definition, Template template) throws InvalidTemplateSyntax {
        String[] matches = arg_re.tryGetMatches(context, definition);
        if (matches == null) {
            InvalidTemplateSyntax.createAndThrow(context, "Invalid formal parameter definition in template signature '%(definition)'", "definition", definition, "template", template.getFullClassName(context));
            throw (InvalidTemplateSyntax) null;
        }

        this.type_name = matches[0];
        this.argument_name = matches[1];
        this.type = null;
        this.default_value_code = null;
        this.default_value = null;
    }

    public Class_FormalArgument (CallContext context, String type_name, String argument_name, Type type, Object default_value) {
        this.type_name          = type_name;
        this.argument_name      = argument_name;
        this.default_value      = default_value;
        this.default_value_code = null;
        this.type               = type;
    }

    public Class_FormalArgument (CallContext context, TCodeArgument argument) {
        this.type_name          = argument.getType(context);
        this.argument_name      = argument.getName(context);
        this.default_value_code = argument.getDefaultValue(context);
        this.default_value      = null;
        this.type               = null;
    }

    protected String type_name;

    public String getTypeName (CallContext context) {
        return this.type_name;
    }

    protected String argument_name;

    public String getArgumentName (CallContext context) {
        return this.argument_name;
    }

    protected Type type;

    public Type getType (CallContext context) {
        return this.type;
    }

    protected String default_value_code;

    public String getDefaultValueCode (CallContext context) {
        return this.default_value_code;
    }

    public void setDefaultValueCode (CallContext context, String default_value_code) {
        this.default_value_code = default_value_code;
    }

    protected Object default_value;

    public Object getDefaultValue (CallContext context) {
        return this.default_value;
    }

    public void setDefaultValue (CallContext context, Object default_value) {
        this.default_value = default_value;
    }
}
