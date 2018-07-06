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
import com.sphenon.basics.locating.*;

import com.sphenon.engines.generator.*;

import java.io.BufferedReader;
import java.util.Date;
import java.util.Vector;

/**
   A wrapper around a template which stores it's reader
   and creates it therefore only once. The class is used
   by the BootstrapParser, since it reads the first line
   of the template and passes it to another TemplateParser
   which shall not see the first line.
 */

public class Class_TemplateWrapper implements Template {

    // Construction -------------------------------------------------------------------

    public Class_TemplateWrapper(CallContext context, Template template) {
        this.template = template;
    }

    // Attributes ---------------------------------------------------------------------

    protected Template template;

    public Template getTemplate (CallContext context) {
        return this.template;
    }

    public void setTemplate (CallContext context, Template template) {
        this.template = template;
    }

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    protected BufferedReader reader;

    public BufferedReader getReader (CallContext context) {
        if (this.reader == null) {
            this.reader = this.template.getReader(context);
        }
        return this.reader;
    }

    public String getClassName(CallContext context) {
        return this.template.getClassName(context);
    }

    public String getPackageName(CallContext context) {
        return this.template.getPackageName(context);
    }

    public String getFullClassName(CallContext context) {
        return this.template.getFullClassName(context);
    }

    public Vector<String> getPackageChilds(CallContext context) {
        return this.template.getPackageChilds(context);
    }

    public long getLastModification(CallContext context) {
        return this.template.getLastModification(context);
    }

    public Locator tryGetOrigin(CallContext context) {
        return this.template.tryGetOrigin(context);
    }

    public String getTemplateTypeAlias(CallContext context) {
        return this.template.getTemplateTypeAlias(context);
    }
}
